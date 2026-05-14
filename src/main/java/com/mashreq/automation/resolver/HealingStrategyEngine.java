package com.mashreq.automation.resolver;

import com.mashreq.automation.metadata.ElementMetadata;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Map;

/**
 * AI-ready healing strategy engine for locator recovery
 * Implements multiple healing strategies for broken locators
 */
public class HealingStrategyEngine {

    private static final Logger logger = LogManager.getLogger(HealingStrategyEngine.class);

    /**
     * Attempt to heal broken locator using multiple strategies
     *
     * @param page Current page instance
     * @param metadata Element metadata with healing strategies
     * @param screenName Screen identifier
     * @param elementName Element identifier
     * @return Healed locator or null
     */
    public Locator heal(Page page, ElementMetadata metadata, String screenName, String elementName) {
        logger.warn("Attempting to heal locator: {}.{}", screenName, elementName);

        // Strategy 1: Text-based healing
        Locator healed = healByText(page, metadata);
        if (healed != null && isLocatorValid(page, healed)) {
            logger.warn("Healing Strategy 1 (Text-based) succeeded");
            return healed;
        }

        // Strategy 2: Attribute-based healing
        healed = healByAttributes(page, metadata);
        if (healed != null && isLocatorValid(page, healed)) {
            logger.warn("Healing Strategy 2 (Attribute-based) succeeded");
            return healed;
        }

        // Strategy 3: Partial XPath healing
        healed = healByPartialXPath(page, metadata);
        if (healed != null && isLocatorValid(page, healed)) {
            logger.warn("Healing Strategy 3 (Partial XPath) succeeded");
            return healed;
        }

        // Strategy 4: Role-based healing (accessibility)
        healed = healByRole(page, metadata);
        if (healed != null && isLocatorValid(page, healed)) {
            logger.warn("Healing Strategy 4 (Role-based) succeeded");
            return healed;
        }

        // Strategy 5: ADF component healing
        if (metadata.getAdfComponentId() != null) {
            healed = healAdfComponent(page, metadata);
            if (healed != null && isLocatorValid(page, healed)) {
                logger.warn("Healing Strategy 5 (ADF Component) succeeded");
                return healed;
            }
        }

        logger.error("All healing strategies failed for: {}.{}", screenName, elementName);
        return null;
    }

    /**
     * Strategy 1: Heal by text content
     */
    private Locator healByText(Page page, ElementMetadata metadata) {
        if (metadata.getText() == null) {
            return null;
        }
        try {
            String text = metadata.getText();
            return page.locator("text=" + text);
        } catch (Exception e) {
            logger.debug("Text-based healing failed", e);
            return null;
        }
    }

    /**
     * Strategy 2: Heal by common attributes
     */
    private Locator healByAttributes(Page page, ElementMetadata metadata) {
        try {
            if (metadata.getId() != null) {
                return page.locator("[id='" + metadata.getId() + "']");
            }
            if (metadata.getName() != null) {
                return page.locator("[name='" + metadata.getName() + "']");
            }
            if (metadata.getCss() != null) {
                // Try partial CSS matching
                return page.locator(metadata.getCss());
            }
        } catch (Exception e) {
            logger.debug("Attribute-based healing failed", e);
        }
        return null;
    }

    /**
     * Strategy 3: Heal by partial XPath
     */
    private Locator healByPartialXPath(Page page, ElementMetadata metadata) {
        try {
            if (metadata.getXpath() != null) {
                String xpath = metadata.getXpath();
                // Extract element type from XPath
                if (xpath.contains("@id")) {
                    String[] parts = xpath.split("@id='|'");
                    if (parts.length > 1) {
                        return page.locator("[id*='" + parts[1] + "']");
                    }
                }
                if (xpath.contains("@class")) {
                    String[] parts = xpath.split("@class='|'");
                    if (parts.length > 1) {
                        return page.locator("[class*='" + parts[1] + "']");
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Partial XPath healing failed", e);
        }
        return null;
    }

    /**
     * Strategy 4: Heal by role (accessibility)
     */
    private Locator healByRole(Page page, ElementMetadata metadata) {
        try {
            if (metadata.getType() != null) {
                switch (metadata.getType().toLowerCase()) {
                    case "button":
                        if (metadata.getText() != null) {
                            return page.locator("button:has-text(\"" + metadata.getText() + "\")");
                        }
                        break;
                    case "input":
                        if (metadata.getName() != null) {
                            return page.locator("input[name='" + metadata.getName() + "']");
                        }
                        break;
                    case "link":
                        if (metadata.getText() != null) {
                            return page.locator("a:has-text(\"" + metadata.getText() + "\")");
                        }
                        break;
                }
            }
        } catch (Exception e) {
            logger.debug("Role-based healing failed", e);
        }
        return null;
    }

    /**
     * Strategy 5: Heal ADF (Oracle Forms) components
     */
    private Locator healAdfComponent(Page page, ElementMetadata metadata) {
        try {
            String adfId = metadata.getAdfComponentId();
            // ADF-specific selector patterns
            return page.locator("[id*='" + adfId + "']");
        } catch (Exception e) {
            logger.debug("ADF component healing failed", e);
        }
        return null;
    }

    /**
     * Validate if locator is valid
     */
    private boolean isLocatorValid(Page page, Locator locator) {
        try {
            return locator.count() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get healing statistics (for MCP reporting)
     */
    public Map<String, Object> getHealingStats() {
        return Map.of(
                "strategies_implemented", 5,
                "priority", "text > attributes > xpath > role > adf",
                "adf_support", true
        );
    }
}
