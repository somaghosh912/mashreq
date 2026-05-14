package com.mashreq.automation.validations;

import com.microsoft.playwright.Page;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.HashMap;
import java.util.Map;

/**
 * Multi-layer validation orchestrator
 * Coordinates UI, API, and database validations
 */
public class ValidationOrchestrator {

    private static final Logger logger = LogManager.getLogger(ValidationOrchestrator.class);
    private final UIValidation uiValidation;
    private final APIValidation apiValidation;
    private final DBValidation dbValidation;
    private Map<String, Boolean> validationResults;

    public ValidationOrchestrator(Page page) {
        this.uiValidation = new UIValidation(page);
        this.apiValidation = new APIValidation();
        this.dbValidation = new DBValidation();
        this.validationResults = new HashMap<>();
        logger.info("Validation Orchestrator initialized");
    }

    /**
     * Perform comprehensive validation
     */
    public boolean validateEnd2End(String testName, Map<String, Object> expectations) {
        logger.info("Starting end-to-end validation: {}", testName);
        boolean allPassed = true;

        try {
            // UI Validation
            boolean uiPassed = uiValidation.validate(expectations);
            validationResults.put(testName + "_UI", uiPassed);
            logger.info("UI Validation: {}", uiPassed ? "PASSED" : "FAILED");
            allPassed = allPassed && uiPassed;

            // API Validation
            boolean apiPassed = apiValidation.validate(expectations);
            validationResults.put(testName + "_API", apiPassed);
            logger.info("API Validation: {}", apiPassed ? "PASSED" : "FAILED");
            allPassed = allPassed && apiPassed;

            // Database Validation
            boolean dbPassed = dbValidation.validate(expectations);
            validationResults.put(testName + "_DB", dbPassed);
            logger.info("Database Validation: {}", dbPassed ? "PASSED" : "FAILED");
            allPassed = allPassed && dbPassed;

            return allPassed;
        } catch (Exception e) {
            logger.error("Validation orchestration failed", e);
            return false;
        }
    }

    /**
     * Get validation results
     */
    public Map<String, Boolean> getValidationResults() {
        return new HashMap<>(validationResults);
    }

    /**
     * Get validation report
     */
    public Map<String, Object> getValidationReport() {
        Map<String, Object> report = new HashMap<>();
        report.put("total_validations", validationResults.size());
        report.put("passed", validationResults.values().stream().filter(v -> v).count());
        report.put("failed", validationResults.values().stream().filter(v -> !v).count());
        report.put("details", validationResults);
        return report;
    }
}
