package com.mashreq.automation.driver;

import com.mashreq.automation.config.ConfigManager;
import com.mashreq.automation.config.FrameworkConstants;
import com.microsoft.playwright.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Factory for creating Playwright browser and page instances
 * Handles browser launch configuration and page initialization
 */
public class PlaywrightFactory {

    private static final Logger logger = LogManager.getLogger(PlaywrightFactory.class);
    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;

    /**
     * Initialize Playwright
     */
    public static synchronized void initPlaywright() {
        if (playwright == null) {
            playwright = Playwright.create();
            logger.info("Playwright initialized successfully");
        }
    }

    /**
     * Launch browser based on configuration
     */
    public synchronized Browser launchBrowser() {
        if (browser != null) {
            logger.warn("Browser already launched, returning existing instance");
            return browser;
        }

        initPlaywright();
        ConfigManager configManager = ConfigManager.getInstance();

        String browserType = configManager.getBrowser();
        boolean headless = configManager.isHeadless();
        int slowmo = configManager.getSlowMo();

        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setSlowMo(slowmo);

        try {
            switch (browserType.toLowerCase()) {
                case FrameworkConstants.BROWSER_CHROMIUM:
                    logger.info("Launching Chromium browser");
                    browser = playwright.chromium().launch(launchOptions);
                    break;
                case FrameworkConstants.BROWSER_FIREFOX:
                    logger.info("Launching Firefox browser");
                    browser = playwright.firefox().launch(launchOptions);
                    break;
                case FrameworkConstants.BROWSER_WEBKIT:
                    logger.info("Launching WebKit browser");
                    browser = playwright.webkit().launch(launchOptions);
                    break;
                case FrameworkConstants.BROWSER_EDGE:
                    logger.info("Launching Edge browser");
                    browser = playwright.chromium().launch(
                            launchOptions.setChannel("msedge")
                    );
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported browser: " + browserType);
            }

            logger.info("Browser launched successfully: {}", browserType);
        } catch (Exception e) {
            logger.error("Failed to launch browser", e);
            throw new RuntimeException("Browser launch failed", e);
        }

        return browser;
    }

    /**
     * Create a new browser context
     */
    public BrowserContext createContext() {
        if (browser == null) {
            launchBrowser();
        }

        BrowserContext.NewContextOptions contextOptions = new BrowserContext.NewContextOptions()
                .setViewportSize(FrameworkConstants.VIEWPORT_WIDTH, FrameworkConstants.VIEWPORT_HEIGHT)
                .setIgnoreHTTPSErrors(true);

        context = browser.newContext(contextOptions);
        logger.info("Browser context created");
        return context;
    }

    /**
     * Create a new page within context
     */
    public Page createPage() {
        if (context == null) {
            createContext();
        }

        page = context.newPage();
        page.setDefaultTimeout(FrameworkConstants.EXPLICIT_WAIT * 1000);
        page.setDefaultNavigationTimeout(FrameworkConstants.PAGE_LOAD_TIMEOUT * 1000);

        logger.info("New page created");
        return page;
    }

    /**
     * Get current page
     */
    public Page getPage() {
        if (page == null) {
            createPage();
        }
        return page;
    }

    /**
     * Navigate to URL
     */
    public void navigateToUrl(String url) {
        try {
            Page currentPage = getPage();
            currentPage.navigate(url);
            logger.info("Navigated to: {}", url);
        } catch (PlaywrightException e) {
            logger.error("Navigation failed to URL: {}", url, e);
            throw new RuntimeException("Navigation failed", e);
        }
    }

    /**
     * Close current page
     */
    public void closePage() {
        if (page != null) {
            page.close();
            page = null;
            logger.info("Page closed");
        }
    }

    /**
     * Close browser context
     */
    public void closeContext() {
        if (context != null) {
            context.close();
            context = null;
            logger.info("Browser context closed");
        }
    }

    /**
     * Close browser
     */
    public static synchronized void closeBrowser() {
        if (browser != null) {
            browser.close();
            browser = null;
            logger.info("Browser closed");
        }
    }

    /**
     * Close Playwright
     */
    public static synchronized void closePlaywright() {
        if (playwright != null) {
            playwright.close();
            playwright = null;
            logger.info("Playwright closed");
        }
    }

    /**
     * Cleanup all resources
     */
    public void cleanup() {
        closePage();
        closeContext();
        closeBrowser();
        closePlaywright();
        logger.info("All Playwright resources cleaned up");
    }
}
