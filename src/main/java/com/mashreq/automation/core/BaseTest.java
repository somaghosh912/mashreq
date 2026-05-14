package com.mashreq.automation.core;

import com.mashreq.automation.config.ConfigManager;
import com.mashreq.automation.config.EnvironmentManager;
import com.mashreq.automation.driver.DriverManager;
import com.mashreq.automation.engine.GenericActionEngine;
import com.mashreq.automation.transaction.TransactionContext;
import com.microsoft.playwright.Page;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;

/**
 * Base test class for all automated tests
 * Provides common setup/teardown and framework utilities
 */
public abstract class BaseTest {

    protected static final Logger logger = LogManager.getLogger(BaseTest.class);
    protected Page page;
    protected GenericActionEngine actionEngine;
    protected TransactionContext transactionContext;
    protected ConfigManager configManager;
    protected EnvironmentManager environmentManager;
    protected String testName;
    protected long testStartTime;

    @BeforeMethod(alwaysRun = true)
    public void setUp(org.testng.ITestResult testResult) {
        try {
            this.testName = testResult.getMethod().getMethodName();
            this.testStartTime = System.currentTimeMillis();

            logger.info("\n========== TEST SETUP ==========");
            logger.info("Test Name: {}", testName);

            // Initialize managers
            configManager = ConfigManager.getInstance();
            environmentManager = EnvironmentManager.getInstance();

            // Load configuration
            String environment = System.getProperty("env", "qa");
            configManager.loadConfig(environment);

            // Initialize driver and page
            page = DriverManager.getPage();
            actionEngine = new GenericActionEngine(page);

            // Initialize transaction context
            transactionContext = TransactionContext.newTransaction();

            // Navigate to application URL
            String appUrl = configManager.getApplicationUrl();
            logger.info("Navigating to: {}", appUrl);
            page.navigate(appUrl);

            logger.info("Test setup completed");
        } catch (Exception e) {
            logger.error("Test setup failed", e);
            throw new RuntimeException("Test setup failed", e);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(org.testng.ITestResult testResult) {
        try {
            long testEndTime = System.currentTimeMillis();
            long duration = testEndTime - testStartTime;

            logger.info("\n========== TEST TEARDOWN ==========");
            logger.info("Test Name: {}", testName);
            logger.info("Status: {}", testResult.isSuccess() ? "PASSED" : "FAILED");
            logger.info("Duration: {} ms", duration);

            if (!testResult.isSuccess() && testResult.getThrowable() != null) {
                logger.error("Test Failure Reason: {}", testResult.getThrowable().getMessage());
            }

            // Cleanup
            TransactionContext.clearContext();
            DriverManager.cleanup();

            logger.info("Test teardown completed");
        } catch (Exception e) {
            logger.error("Test teardown error", e);
        }
    }

    /**
     * Get test execution duration
     */
    protected long getTestDuration() {
        return System.currentTimeMillis() - testStartTime;
    }
}
