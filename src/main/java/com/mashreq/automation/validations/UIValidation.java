package com.mashreq.automation.validations;

import com.microsoft.playwright.Page;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Map;

/**
 * UI Layer Validation
 * Validates visual elements, text, and UI state
 */
public class UIValidation {

    private static final Logger logger = LogManager.getLogger(UIValidation.class);
    private final Page page;

    public UIValidation(Page page) {
        this.page = page;
    }

    /**
     * Validate UI expectations
     */
    public boolean validate(Map<String, Object> expectations) {
        logger.info("Starting UI validation");

        try {
            // Validate page title
            if (expectations.containsKey("title")) {
                String expectedTitle = expectations.get("title").toString();
                String actualTitle = page.title();
                if (!actualTitle.equals(expectedTitle)) {
                    logger.warn("Title validation failed. Expected: {}, Actual: {}", expectedTitle, actualTitle);
                    return false;
                }
            }

            // Validate page URL
            if (expectations.containsKey("url")) {
                String expectedUrl = expectations.get("url").toString();
                String actualUrl = page.url();
                if (!actualUrl.contains(expectedUrl)) {
                    logger.warn("URL validation failed. Expected: {}, Actual: {}", expectedUrl, actualUrl);
                    return false;
                }
            }

            logger.info("UI validation passed");
            return true;
        } catch (Exception e) {
            logger.error("UI validation error", e);
            return false;
        }
    }
}
