package com.mashreq.automation.tests.hooks;

import io.cucumber.java.Before;
import io.cucumber.java.After;
import com.mashreq.automation.config.ConfigManager;
import com.mashreq.automation.driver.DriverManager;
import com.mashreq.automation.engine.GenericActionEngine;
import com.mashreq.automation.transaction.TransactionContext;
import com.microsoft.playwright.Page;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Cucumber hooks for BDD test execution
 * Manages setup and teardown of each scenario
 */
public class Hooks {

    private static final Logger logger = LogManager.getLogger(Hooks.class);
    private Page page;
    private GenericActionEngine actionEngine;
    private TransactionContext transactionContext;

    @Before(order = 1)
    public void initializeFramework() {
        try {
            logger.info("========== SCENARIO SETUP ==========");

            // Load configuration
            String environment = System.getProperty("env", "qa");
            ConfigManager configManager = ConfigManager.getInstance();
            configManager.loadConfig(environment);
            logger.info("Configuration loaded for environment: {}", environment);

            // Initialize driver and page
            page = DriverManager.getPage();
            actionEngine = new GenericActionEngine(page);

            // Initialize transaction context
            transactionContext = TransactionContext.newTransaction();

            // Navigate to application
            String appUrl = configManager.getApplicationUrl();
            page.navigate(appUrl);
            logger.info("Navigated to: {}", appUrl);
        } catch (Exception e) {
            logger.error("Framework initialization failed", e);
            throw new RuntimeException("Setup failed", e);
        }
    }

    @After(order = 1)
    public void cleanupFramework() {
        try {
            logger.info("========== SCENARIO TEARDOWN ==========");

            // Clear transaction context
            TransactionContext.clearContext();

            // Close browser and cleanup
            DriverManager.cleanup();

            logger.info("Cleanup completed");
        } catch (Exception e) {
            logger.error("Cleanup failed", e);
        }
    }

    // Getters for step definitions
    public Page getPage() {
        return page;
    }

    public GenericActionEngine getActionEngine() {
        return actionEngine;
    }

    public TransactionContext getTransactionContext() {
        return transactionContext;
    }
}
