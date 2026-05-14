package com.mashreq.automation.resolver;

import com.mashreq.automation.metadata.ElementMetadata;
import com.mashreq.automation.metadata.ScreenMetadataRepository;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.PlaywrightException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;
import java.util.Map;

/**
 * Smart locator resolver with AI-ready self-healing capabilities
 * Attempts multiple strategies to locate elements even after UI changes
 */
public class SmartLocatorResolver {

    private static final Logger logger = LogManager.getLogger(SmartLocatorResolver.class);
    private final ScreenMetadataRepository metadataRepository;
    private final HealingStrategyEngine healingEngine;

    public SmartLocatorResolver() {
        this.metadataRepository = ScreenMetadataRepository.getInstance();
        this.healingEngine = new HealingStrategyEngine();
    }

    /**
     * Resolve element using qualified name (ScreenName.ElementName)
     *
     * @param page Current page instance
     * @param qualifiedName Qualified element name
     * @return Locator object
     */
    public Locator resolveElement(Page page, String qualifiedName) {
        String[] parts = qualifiedName.split("\\.");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid qualified name format. Use: ScreenName.ElementName");
        }

        String screenName = parts[0];
        String elementName = parts[1];

        return resolveElement(page, screenName, elementName);
    }

    /**
     * Resolve element with healing fallback
     *
     * @param page Current page instance
     * @param screenName Screen identifier
     * @param elementName Element identifier
     * @return Locator object
     */
    public Locator resolveElement(Page page, String screenName, String elementName) {
        logger.debug("Resolving element: {}.{}", screenName, elementName);

        try {
            // Load metadata
            ElementMetadata metadata = metadataRepository.getElementMetadata(screenName, elementName);
            if (metadata == null) {
                throw new RuntimeException("Element metadata not found: " + screenName + "." + elementName);
            }

            // Try primary locator
            Locator locator = getPrimaryLocator(page, metadata);
            if (locator != null && isLocatorValid(page, locator)) {
                logger.debug("Element resolved with primary locator: {}", elementName);
                return locator;
            }

            // Try fallback locators
            locator = tryFallbackLocators(page, metadata);
            if (locator != null) {
                logger.warn("Element resolved with fallback locator: {}", elementName);
                return locator;
            }

            // Attempt healing
            locator = healingEngine.heal(page, metadata, screenName, elementName);
            if (locator != null) {
                logger.warn("Element healed and resolved: {}", elementName);
                return locator;
            }

            throw new RuntimeException("Unable to resolve element after healing attempts: " + screenName + "." + elementName);
        } catch (Exception e) {
            logger.error("Element resolution failed: {}.{}", screenName, elementName, e);
            throw new RuntimeException("Element resolution failed", e);
        }
    }

    /**
     * Get primary locator from metadata
     */
    private Locator getPrimaryLocator(Page page, ElementMetadata metadata) {
        if (metadata.getXpath() != null) {
            return page.locator("xpath=" + metadata.getXpath());
        }
        if (metadata.getCss() != null) {
            return page.locator(metadata.getCss());
        }
        if (metadata.getId() != null) {
            return page.locator("id=" + metadata.getId());
        }
        if (metadata.getText() != null) {
            return page.locator("text=" + metadata.getText());
        }
        return null;
    }

    /**
     * Try fallback locators
     */
    private Locator tryFallbackLocators(Page page, ElementMetadata metadata) {
        if (metadata.getFallbackLocators() == null || metadata.getFallbackLocators().isEmpty()) {
            return null;
        }

        for (Map.Entry<String, String> fallback : metadata.getFallbackLocators().entrySet()) {
            try {
                Locator locator = page.locator(fallback.getValue());
                if (isLocatorValid(page, locator)) {
                    logger.debug("Fallback locator matched: {}", fallback.getKey());
                    return locator;
                }
            } catch (Exception e) {
                logger.debug("Fallback locator failed: {}", fallback.getKey());
            }
        }
        return null;
    }

    /**
     * Validate if locator is valid and findable
     */
    private boolean isLocatorValid(Page page, Locator locator) {
        try {
            return locator.count() > 0;
        } catch (PlaywrightException e) {
            return false;
        }
    }

    /**
     * Wait for element visibility
     */
    public void waitForElement(Page page, String screenName, String elementName, int timeoutMs) {
        Locator locator = resolveElement(page, screenName, elementName);
        try {
            locator.waitFor(new Locator.WaitForOptions().setTimeout(timeoutMs));
            logger.debug("Element is visible: {}.{}", screenName, elementName);
        } catch (PlaywrightException e) {
            logger.error("Element visibility wait failed: {}.{}", screenName, elementName, e);
            throw new RuntimeException("Element not visible: " + screenName + "." + elementName, e);
        }
    }

    /**
     * Find all matching elements
     */
    public List<Locator> findAllElements(Page page, String screenName, String elementName) {
        Locator locator = resolveElement(page, screenName, elementName);
        int count = locator.count();
        List<Locator> locators = new java.util.ArrayList<>();
        for (int i = 0; i < count; i++) {
            locators.add(locator.nth(i));
        }
        return locators;
    }

    /**
     * Get locator count
     */
    public int getElementCount(Page page, String screenName, String elementName) {
        Locator locator = resolveElement(page, screenName, elementName);
        return locator.count();
    }
}
