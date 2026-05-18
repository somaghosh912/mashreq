package com.mashreq.automation.listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RetryListener - TestNG IRetryAnalyzer implementation
 * Automatically retries failed tests based on configured retry count
 */
public class RetryListener implements IRetryAnalyzer {
    
    private static final Logger logger = LoggerFactory.getLogger(RetryListener.class);
    
    private static final int MAX_RETRY_COUNT = 2;
    private int retryCount = 0;
    
    @Override
    public boolean retry(ITestResult result) {
        // Don't retry if test passed
        if (result.isSuccess()) {
            return false;
        }
        
        // Check if we've reached max retry attempts
        if (retryCount < MAX_RETRY_COUNT) {
            retryCount++;
            String testName = result.getName();
            Throwable throwable = result.getThrowable();
            
            logger.warn("Retrying test '{}' - Attempt {} of {}", 
                testName, retryCount, MAX_RETRY_COUNT);
            
            if (throwable != null) {
                logger.warn("Test failed with: {}", throwable.getMessage());
            }
            
            return true; // Retry the test
        } else {
            logger.error("Test '{}' failed after {} retry attempts", 
                result.getName(), MAX_RETRY_COUNT);
            retryCount = 0; // Reset for next test
            return false; // Don't retry
        }
    }
    
    /**
     * Get the maximum retry count
     */
    public static int getMaxRetryCount() {
        return MAX_RETRY_COUNT;
    }
    
    /**
     * Get current retry count
     */
    public int getRetryCount() {
        return retryCount;
    }
    
    /**
     * Reset retry count
     */
    public void resetRetryCount() {
        retryCount = 0;
    }
}
