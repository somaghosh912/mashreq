package com.mashreq.automation.engine;

import com.mashreq.automation.config.FrameworkConstants;
import com.mashreq.automation.resolver.SmartLocatorResolver;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.PlaywrightException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;

/**
 * Generic Action Engine
 * Universal element interaction layer supporting all common UI actions
 * Eliminates need for action-specific methods
 */
public class GenericActionEngine {

    private static final Logger logger = LogManager.getLogger(GenericActionEngine.class);
    private final Page page;
    private final SmartLocatorResolver locatorResolver;

    public GenericActionEngine(Page page) {
        this.page = page;
        this.locatorResolver = new SmartLocatorResolver();
    }

    /**
     * Click element by qualified name
     */
    public void click(String qualifiedName) {
        try {
            logger.info("Clicking element: {}", qualifiedName);
            Locator locator = locatorResolver.resolveElement(page, qualifiedName);
            locator.click();
            logger.debug("Element clicked successfully: {}", qualifiedName);
        } catch (Exception e) {
            logger.error("Click action failed for: {}", qualifiedName, e);
            throw new RuntimeException("Click failed", e);
        }
    }

    /**
     * Type text in input element
     */
    public void type(String qualifiedName, String text) {
        try {
            logger.info("Typing in element: {} -> {}", qualifiedName, text);
            Locator locator = locatorResolver.resolveElement(page, qualifiedName);
            locator.fill(text);
            logger.debug("Text typed successfully in: {}", qualifiedName);
        } catch (Exception e) {
            logger.error("Type action failed for: {}", qualifiedName, e);
            throw new RuntimeException("Type failed", e);
        }
    }

    /**
     * Clear input field
     */
    public void clear(String qualifiedName) {
        try {
            logger.info("Clearing element: {}", qualifiedName);
            Locator locator = locatorResolver.resolveElement(page, qualifiedName);
            locator.clear();
            logger.debug("Element cleared: {}", qualifiedName);
        } catch (Exception e) {
            logger.error("Clear action failed for: {}", qualifiedName, e);
            throw new RuntimeException("Clear failed", e);
        }
    }

    /**
     * Select dropdown option by visible text
     */
    public void selectByVisibleText(String qualifiedName, String text) {
        try {
            logger.info("Selecting option in dropdown: {} -> {}", qualifiedName, text);
            Locator locator = locatorResolver.resolveElement(page, qualifiedName);
            locator.selectOption(text);
            logger.debug("Option selected: {}", text);
        } catch (Exception e) {
            logger.error("Select action failed for: {}", qualifiedName, e);
            throw new RuntimeException("Select failed", e);
        }
    }

    /**
     * Select dropdown option by value attribute
     */
    public void selectByValue(String qualifiedName, String value) {
        try {
            logger.info("Selecting option by value: {} -> {}", qualifiedName, value);
            Locator locator = locatorResolver.resolveElement(page, qualifiedName);
            locator.selectOption(value);
            logger.debug("Option selected by value: {}", value);
        } catch (Exception e) {
            logger.error("Select by value failed for: {}", qualifiedName, e);
            throw new RuntimeException("Select by value failed", e);
        }
    }

    /**
     * Check checkbox
     */
    public void check(String qualifiedName) {
        try {
            logger.info("Checking checkbox: {}", qualifiedName);
            Locator locator = locatorResolver.resolveElement(page, qualifiedName);
            locator.check();
            logger.debug("Checkbox checked: {}", qualifiedName);
        } catch (Exception e) {
            logger.error("Check action failed for: {}", qualifiedName, e);
            throw new RuntimeException("Check failed", e);
        }
    }

    /**
     * Uncheck checkbox
     */
    public void uncheck(String qualifiedName) {
        try {
            logger.info("Unchecking checkbox: {}", qualifiedName);
            Locator locator = locatorResolver.resolveElement(page, qualifiedName);
            locator.uncheck();
            logger.debug("Checkbox unchecked: {}", qualifiedName);
        } catch (Exception e) {
            logger.error("Uncheck action failed for: {}", qualifiedName, e);
            throw new RuntimeException("Uncheck failed", e);
        }
    }

    /**
     * Hover over element
     */
    public void hover(String qualifiedName) {
        try {
            logger.info("Hovering over element: {}", qualifiedName);
            Locator locator = locatorResolver.resolveElement(page, qualifiedName);
            locator.hover();
            logger.debug("Hovered over element: {}", qualifiedName);
        } catch (Exception e) {
            logger.error("Hover action failed for: {}", qualifiedName, e);
            throw new RuntimeException("Hover failed", e);
        }
    }

    /**
     * Double click element
     */
    public void doubleClick(String qualifiedName) {
        try {
            logger.info("Double clicking element: {}", qualifiedName);
            Locator locator = locatorResolver.resolveElement(page, qualifiedName);
            locator.dblclick();
            logger.debug("Element double clicked: {}", qualifiedName);
        } catch (Exception e) {
            logger.error("Double click action failed for: {}", qualifiedName, e);
            throw new RuntimeException("Double click failed", e);
        }
    }

    /**
     * Right click (context menu) element
     */
    public void rightClick(String qualifiedName) {
        try {
            logger.info("Right clicking element: {}", qualifiedName);
            Locator locator = locatorResolver.resolveElement(page, qualifiedName);
            locator.click(new Locator.ClickOptions().setButton("right"));
            logger.debug("Element right clicked: {}", qualifiedName);
        } catch (Exception e) {
            logger.error("Right click action failed for: {}", qualifiedName, e);
            throw new RuntimeException("Right click failed", e);
        }
    }

    /**
     * Get element text
     */
    public String getText(String qualifiedName) {
        try {
            logger.debug("Getting text from element: {}", qualifiedName);
            Locator locator = locatorResolver.resolveElement(page, qualifiedName);
            String text = locator.textContent();
            logger.debug("Text retrieved: {}", text);
            return text;
        } catch (Exception e) {
            logger.error("Get text action failed for: {}", qualifiedName, e);
            throw new RuntimeException("Get text failed", e);
        }
    }

    /**
     * Get element attribute value
     */
    public String getAttribute(String qualifiedName, String attributeName) {
        try {
            logger.debug("Getting attribute: {} from element: {}", attributeName, qualifiedName);
            Locator locator = locatorResolver.resolveElement(page, qualifiedName);
            String value = locator.getAttribute(attributeName);
            logger.debug("Attribute value: {}", value);
            return value;
        } catch (Exception e) {
            logger.error("Get attribute action failed for: {}", qualifiedName, e);
            throw new RuntimeException("Get attribute failed", e);
        }
    }

    /**
     * Check if element is visible
     */
    public boolean isVisible(String qualifiedName) {
        try {
            logger.debug("Checking visibility of element: {}", qualifiedName);
            Locator locator = locatorResolver.resolveElement(page, qualifiedName);
            boolean visible = locator.isVisible();
            logger.debug("Element visibility: {}", visible);
            return visible;
        } catch (Exception e) {
            logger.debug("Element not visible: {}", qualifiedName);
            return false;
        }
    }

    /**
     * Check if element is enabled
     */
    public boolean isEnabled(String qualifiedName) {
        try {
            logger.debug("Checking if element is enabled: {}", qualifiedName);
            Locator locator = locatorResolver.resolveElement(page, qualifiedName);
            boolean enabled = locator.isEnabled();
            logger.debug("Element enabled status: {}", enabled);
            return enabled;
        } catch (Exception e) {
            logger.debug("Element is not enabled: {}", qualifiedName);
            return false;
        }
    }

    /**
     * Check if element is checked (for checkboxes/radios)
     */
    public boolean isChecked(String qualifiedName) {
        try {
            logger.debug("Checking if element is checked: {}", qualifiedName);
            Locator locator = locatorResolver.resolveElement(page, qualifiedName);
            boolean checked = locator.isChecked();
            logger.debug("Element checked status: {}", checked);
            return checked;
        } catch (Exception e) {
            logger.debug("Element is not checked: {}", qualifiedName);
            return false;
        }
    }

    /**
     * Wait and click (ensure element is visible before clicking)
     */
    public void waitAndClick(String qualifiedName) {
        try {
            logger.info("Wait and clicking element: {}", qualifiedName);
            Locator locator = locatorResolver.resolveElement(page, qualifiedName);
            locator.waitFor();
            locator.click();
            logger.debug("Element waited and clicked: {}", qualifiedName);
        } catch (Exception e) {
            logger.error("Wait and click action failed for: {}", qualifiedName, e);
            throw new RuntimeException("Wait and click failed", e);
        }
    }

    /**
     * Wait and type (ensure element is visible before typing)
     */
    public void waitAndType(String qualifiedName, String text) {
        try {
            logger.info("Wait and typing in element: {} -> {}", qualifiedName, text);
            Locator locator = locatorResolver.resolveElement(page, qualifiedName);
            locator.waitFor();
            locator.fill(text);
            logger.debug("Text waited and typed: {}", qualifiedName);
        } catch (Exception e) {
            logger.error("Wait and type action failed for: {}", qualifiedName, e);
            throw new RuntimeException("Wait and type failed", e);
        }
    }

    /**
     * Scroll element into view
     */
    public void scrollIntoView(String qualifiedName) {
        try {
            logger.info("Scrolling element into view: {}", qualifiedName);
            Locator locator = locatorResolver.resolveElement(page, qualifiedName);
            locator.scrollIntoViewIfNeeded();
            logger.debug("Element scrolled into view: {}", qualifiedName);
        } catch (Exception e) {
            logger.error("Scroll action failed for: {}", qualifiedName, e);
            throw new RuntimeException("Scroll failed", e);
        }
    }

    /**
     * Focus element
     */
    public void focus(String qualifiedName) {
        try {
            logger.info("Focusing on element: {}", qualifiedName);
            Locator locator = locatorResolver.resolveElement(page, qualifiedName);
            locator.focus();
            logger.debug("Element focused: {}", qualifiedName);
        } catch (Exception e) {
            logger.error("Focus action failed for: {}", qualifiedName, e);
            throw new RuntimeException("Focus failed", e);
        }
    }

    /**
     * Press keyboard key
     */
    public void press(String qualifiedName, String key) {
        try {
            logger.info("Pressing key '{}' on element: {}", key, qualifiedName);
            Locator locator = locatorResolver.resolveElement(page, qualifiedName);
            locator.press(key);
            logger.debug("Key pressed on element: {}", qualifiedName);
        } catch (Exception e) {
            logger.error("Press action failed for: {}", qualifiedName, e);
            throw new RuntimeException("Press failed", e);
        }
    }

    /**
     * Get element count
     */
    public int getElementCount(String qualifiedName) {
        try {
            logger.debug("Getting element count for: {}", qualifiedName);
            int count = locatorResolver.getElementCount(page, qualifiedName.split("\\.")[0], qualifiedName.split("\\.")[1]);
            logger.debug("Element count: {}", count);
            return count;
        } catch (Exception e) {
            logger.error("Get element count failed for: {}", qualifiedName, e);
            return 0;
        }
    }

    /**
     * Wait for element visibility
     */
    public void waitForElementVisible(String qualifiedName, int timeoutMs) {
        try {
            logger.info("Waiting for element visible: {} (timeout: {}ms)", qualifiedName, timeoutMs);
            String[] parts = qualifiedName.split("\\.");
            locatorResolver.waitForElement(page, parts[0], parts[1], timeoutMs);
            logger.debug("Element is visible: {}", qualifiedName);
        } catch (Exception e) {
            logger.error("Wait for visible action failed for: {}", qualifiedName, e);
            throw new RuntimeException("Wait for visible failed", e);
        }
    }

    /**
     * Execute JavaScript in page context
     */
    public Object executeScript(String script, Object... args) {
        try {
            logger.debug("Executing script: {}", script);
            Object result = page.evaluate(script, args);
            logger.debug("Script execution result: {}", result);
            return result;
        } catch (Exception e) {
            logger.error("Script execution failed", e);
            throw new RuntimeException("Script execution failed", e);
        }
    }

    /**
     * Get page title
     */
    public String getPageTitle() {
        return page.title();
    }

    /**
     * Get page URL
     */
    public String getPageUrl() {
        return page.url();
    }

    /**
     * Refresh page
     */
    public void refreshPage() {
        try {
            logger.info("Refreshing page");
            page.reload();
            logger.debug("Page refreshed");
        } catch (Exception e) {
            logger.error("Page refresh failed", e);
            throw new RuntimeException("Page refresh failed", e);
        }
    }

    /**
     * Navigate back
     */
    public void navigateBack() {
        try {
            logger.info("Navigating back");
            page.goBack();
            logger.debug("Navigated back");
        } catch (Exception e) {
            logger.error("Navigation back failed", e);
            throw new RuntimeException("Navigation back failed", e);
        }
    }

    /**
     * Navigate forward
     */
    public void navigateForward() {
        try {
            logger.info("Navigating forward");
            page.goForward();
            logger.debug("Navigated forward");
        } catch (Exception e) {
            logger.error("Navigation forward failed", e);
            throw new RuntimeException("Navigation forward failed", e);
        }
    }
}
