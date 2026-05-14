package com.mashreq.automation.transaction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Runtime transaction context for maintaining state during workflow execution
 * Manages maker-checker transaction data and dynamic values
 */
public class TransactionContext {

    private static final Logger logger = LogManager.getLogger(TransactionContext.class);
    private static final ThreadLocal<TransactionContext> contextThreadLocal = ThreadLocal.withInitial(TransactionContext::new);

    private String transactionId;
    private String workflowId;
    private String currentUser;
    private String currentRole;
    private Map<String, Object> transactionData;
    private Map<String, Object> runtimeData;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Map<String, String> stepResults;

    /**
     * Private constructor
     */
    private TransactionContext() {
        this.transactionId = UUID.randomUUID().toString();
        this.transactionData = new HashMap<>();
        this.runtimeData = new HashMap<>();
        this.stepResults = new HashMap<>();
        this.startTime = LocalDateTime.now();
        this.status = "INITIATED";
        logger.debug("Transaction context created: {}", transactionId);
    }

    /**
     * Get thread-local transaction context
     */
    public static TransactionContext getCurrentContext() {
        return contextThreadLocal.get();
    }

    /**
     * Initialize new transaction
     */
    public static TransactionContext newTransaction() {
        TransactionContext context = new TransactionContext();
        contextThreadLocal.set(context);
        logger.info("New transaction initiated: {}", context.transactionId);
        return context;
    }

    /**
     * Set transaction ID (for tracking)
     */
    public TransactionContext setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    /**
     * Set workflow ID
     */
    public TransactionContext setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
        logger.debug("Workflow ID set: {}", workflowId);
        return this;
    }

    /**
     * Set current user and role
     */
    public TransactionContext setUser(String userId, String role) {
        this.currentUser = userId;
        this.currentRole = role;
        logger.info("User set in transaction context: {} (Role: {})", userId, role);
        return this;
    }

    /**
     * Add transaction data
     */
    public TransactionContext putData(String key, Object value) {
        this.transactionData.put(key, value);
        logger.debug("Transaction data added: {} = {}", key, value);
        return this;
    }

    /**
     * Get transaction data
     */
    public Object getData(String key) {
        return transactionData.get(key);
    }

    /**
     * Get transaction data as string
     */
    public String getDataAsString(String key) {
        Object value = transactionData.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * Get all transaction data
     */
    public Map<String, Object> getAllData() {
        return new HashMap<>(transactionData);
    }

    /**
     * Add runtime data (dynamic values discovered during execution)
     */
    public TransactionContext putRuntimeData(String key, Object value) {
        this.runtimeData.put(key, value);
        logger.debug("Runtime data added: {} = {}", key, value);
        return this;
    }

    /**
     * Get runtime data
     */
    public Object getRuntimeData(String key) {
        return runtimeData.get(key);
    }

    /**
     * Record step result
     */
    public TransactionContext recordStepResult(String stepName, String result) {
        this.stepResults.put(stepName, result);
        logger.info("Step result recorded: {} -> {}", stepName, result);
        return this;
    }

    /**
     * Get step result
     */
    public String getStepResult(String stepName) {
        return stepResults.get(stepName);
    }

    /**
     * Update transaction status
     */
    public TransactionContext setStatus(String status) {
        this.status = status;
        logger.info("Transaction status updated: {}", status);
        return this;
    }

    /**
     * Complete transaction
     */
    public void completeTransaction() {
        this.endTime = LocalDateTime.now();
        this.status = "COMPLETED";
        logger.info("Transaction completed: {} (Duration: {} ms)",
                transactionId,
                java.time.temporal.ChronoUnit.MILLIS.between(startTime, endTime));
    }

    /**
     * Fail transaction
     */
    public void failTransaction(String reason) {
        this.endTime = LocalDateTime.now();
        this.status = "FAILED";
        this.putData("failure_reason", reason);
        logger.error("Transaction failed: {} (Reason: {})", transactionId, reason);
    }

    /**
     * Check if transaction is for maker
     */
    public boolean isMakerRole() {
        return "ROLE_MAKER".equals(currentRole);
    }

    /**
     * Check if transaction is for checker
     */
    public boolean isCheckerRole() {
        return "ROLE_CHECKER".equals(currentRole);
    }

    /**
     * Get transaction summary
     */
    public Map<String, Object> getTransactionSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("transactionId", transactionId);
        summary.put("workflowId", workflowId);
        summary.put("user", currentUser);
        summary.put("role", currentRole);
        summary.put("status", status);
        summary.put("startTime", startTime);
        summary.put("endTime", endTime);
        summary.put("dataItems", transactionData.size());
        summary.put("stepCount", stepResults.size());
        return summary;
    }

    /**
     * Clear context (cleanup after transaction)
     */
    public static void clearContext() {
        contextThreadLocal.remove();
        logger.debug("Transaction context cleared");
    }

    // Getters
    public String getTransactionId() {
        return transactionId;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public String getCurrentRole() {
        return currentRole;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}
