package com.mashreq.automation.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Retry utility for resilient test execution
 * Implements exponential backoff and configurable retry logic
 */
public class RetryUtil {

    private static final Logger logger = LogManager.getLogger(RetryUtil.class);

    /**
     * Retry operation with exponential backoff
     */
    public static <T> T retryWithBackoff(RetryableOperation<T> operation, int maxAttempts, long initialDelayMs) throws Exception {
        int attempt = 0;
        long delay = initialDelayMs;

        while (attempt < maxAttempts) {
            try {
                logger.debug("Executing operation, attempt: {}/{}", attempt + 1, maxAttempts);
                return operation.execute();
            } catch (Exception e) {
                attempt++;
                if (attempt >= maxAttempts) {
                    logger.error("Operation failed after {} attempts", maxAttempts, e);
                    throw e;
                }
                logger.warn("Operation failed, retrying in {}ms (Attempt {}/{})", delay, attempt, maxAttempts);
                Thread.sleep(delay);
                delay *= 2; // Exponential backoff
            }
        }
        throw new RuntimeException("Operation failed after " + maxAttempts + " attempts");
    }

    /**
     * Functional interface for retryable operations
     */
    @FunctionalInterface
    public interface RetryableOperation<T> {
        T execute() throws Exception;
    }
}
