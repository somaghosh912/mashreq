package com.mashreq.automation.driver;

import com.microsoft.playwright.Page;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages Playwright driver instances using ThreadLocal
 * Ensures thread-safe driver management for parallel test execution
 */
public class DriverManager {

    private static final Logger logger = LogManager.getLogger(DriverManager.class);
    private static final ThreadLocal<PlaywrightFactory> factory = ThreadLocal.withInitial(PlaywrightFactory::new);
    private static final ThreadLocal<Page> pageThreadLocal = ThreadLocal.withInitial(() -> {
        try {
            return factory.get().createPage();
        } catch (Exception e) {
            logger.error("Failed to initialize page", e);
            throw new RuntimeException("Page initialization failed", e);
        }
    });

    /**
     * Get or create page for current thread
     */
    public static Page getPage() {
        Page page = pageThreadLocal.get();
        if (page == null) {
            page = factory.get().createPage();
            pageThreadLocal.set(page);
        }
        return page;
    }

    /**
     * Set page for current thread
     */
    public static void setPage(Page page) {
        pageThreadLocal.set(page);
    }

    /**
     * Get driver factory for current thread
     */
    public static PlaywrightFactory getFactory() {
        return factory.get();
    }

    /**
     * Close page for current thread
     */
    public static void closePage() {
        Page page = pageThreadLocal.get();
        if (page != null) {
            page.close();
            pageThreadLocal.remove();
            logger.info("Page closed for thread: {}", Thread.currentThread().getId());
        }
    }

    /**
     * Close all resources for current thread
     */
    public static void cleanup() {
        try {
            PlaywrightFactory playwrightFactory = factory.get();
            if (playwrightFactory != null) {
                playwrightFactory.cleanup();
            }
        } finally {
            pageThreadLocal.remove();
            factory.remove();
            logger.info("Driver cleanup completed for thread: {}", Thread.currentThread().getId());
        }
    }
}
