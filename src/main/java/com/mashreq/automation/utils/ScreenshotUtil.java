package com.mashreq.automation.utils;

import com.mashreq.automation.config.FrameworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Screenshot utility for capturing and managing test evidence
 */
public class ScreenshotUtil {

    private static final Logger logger = LogManager.getLogger(ScreenshotUtil.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    /**
     * Take screenshot with timestamp
     */
    public static String takeScreenshot(com.microsoft.playwright.Page page, String testName) {
        try {
            String timestamp = LocalDateTime.now().format(formatter);
            String filename = testName + "_" + timestamp + ".png";
            String filepath = FrameworkConstants.SCREENSHOTS_PATH + filename;

            File directory = new File(FrameworkConstants.SCREENSHOTS_PATH);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            page.screenshot(new com.microsoft.playwright.Page.ScreenshotOptions()
                    .setPath(new java.io.File(filepath).toPath()));

            logger.info("Screenshot saved: {}", filepath);
            return filepath;
        } catch (Exception e) {
            logger.error("Screenshot capture failed", e);
            return null;
        }
    }

    /**
     * Take full page screenshot
     */
    public static String takeFullPageScreenshot(com.microsoft.playwright.Page page, String testName) {
        try {
            String timestamp = LocalDateTime.now().format(formatter);
            String filename = testName + "_full_" + timestamp + ".png";
            String filepath = FrameworkConstants.SCREENSHOTS_PATH + filename;

            File directory = new File(FrameworkConstants.SCREENSHOTS_PATH);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            page.screenshot(new com.microsoft.playwright.Page.ScreenshotOptions()
                    .setPath(new java.io.File(filepath).toPath())
                    .setFullPage(true));

            logger.info("Full page screenshot saved: {}", filepath);
            return filepath;
        } catch (Exception e) {
            logger.error("Full page screenshot capture failed", e);
            return null;
        }
    }
}
