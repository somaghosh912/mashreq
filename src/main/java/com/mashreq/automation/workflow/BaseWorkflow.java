package com.mashreq.automation.workflow;

import com.mashreq.automation.transaction.TransactionContext;
import com.mashreq.automation.config.FrameworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.List;

/**
 * Base workflow class for orchestrating business processes
 * Supports maker-checker patterns and complex transaction flows
 */
public abstract class BaseWorkflow {

    protected static final Logger logger = LogManager.getLogger(BaseWorkflow.class);
    protected TransactionContext transactionContext;
    protected String workflowName;
    protected List<String> executionSteps;
    protected String currentMaker;
    protected String currentChecker;

    public BaseWorkflow(String workflowName) {
        this.workflowName = workflowName;
        this.executionSteps = new ArrayList<>();
        this.transactionContext = TransactionContext.newTransaction();
        transactionContext.setWorkflowId(workflowName);
        logger.info("Workflow initialized: {}", workflowName);
    }

    /**
     * Set maker user for the workflow
     */
    public BaseWorkflow asMaker(String userId, String role) {
        this.currentMaker = userId;
        transactionContext.setUser(userId, role);
        logger.info("Maker set for workflow: {}", userId);
        return this;
    }

    /**
     * Set checker user for the workflow
     */
    public BaseWorkflow asChecker(String userId, String role) {
        this.currentChecker = userId;
        transactionContext.setUser(userId, role);
        logger.info("Checker set for workflow: {}", userId);
        return this;
    }

    /**
     * Add step to execution plan
     */
    protected void addStep(String stepName) {
        executionSteps.add(stepName);
        logger.debug("Step added to workflow: {}", stepName);
    }

    /**
     * Record step execution
     */
    protected void recordStepExecution(String stepName, String result) {
        transactionContext.recordStepResult(stepName, result);
        logger.info("Step executed: {} -> {}", stepName, result);
    }

    /**
     * Execute workflow (abstract - to be implemented by subclasses)
     */
    public abstract void execute() throws Exception;

    /**
     * Get execution summary
     */
    public void printExecutionSummary() {
        logger.info("\n========== WORKFLOW EXECUTION SUMMARY ==========");
        logger.info("Workflow: {}", workflowName);
        logger.info("Status: {}", transactionContext.getStatus());
        logger.info("Steps Executed: {}", executionSteps.size());
        executionSteps.forEach(step -> logger.info("  - {}", step));
        logger.info("Transaction Details:");
        logger.info("  - Transaction ID: {}", transactionContext.getTransactionId());
        logger.info("  - Current User: {}", transactionContext.getCurrentUser());
        logger.info("  - Current Role: {}", transactionContext.getCurrentRole());
        logger.info("============================================");
    }

    /**
     * Rollback workflow (for failure scenarios)
     */
    public abstract void rollback() throws Exception;

    /**
     * Cleanup resources
     */
    public void cleanup() {
        TransactionContext.clearContext();
        logger.info("Workflow cleanup completed: {}", workflowName);
    }

    // Getters
    public TransactionContext getTransactionContext() {
        return transactionContext;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public List<String> getExecutionSteps() {
        return new ArrayList<>(executionSteps);
    }
}
