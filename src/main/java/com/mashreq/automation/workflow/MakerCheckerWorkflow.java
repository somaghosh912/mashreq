package com.mashreq.automation.workflow;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Maker-Checker workflow implementation
 * Specialized workflow for approval-based business processes (common in FLEXCUBE)
 */
public class MakerCheckerWorkflow extends BaseWorkflow {

    private static final Logger logger = LogManager.getLogger(MakerCheckerWorkflow.class);
    private String transactionType;
    private String initiationData;
    private boolean submittedForApproval;
    private boolean approved;
    private String approvalReason;

    public MakerCheckerWorkflow() {
        super("MakerCheckerWorkflow");
        this.submittedForApproval = false;
        this.approved = false;
    }

    /**
     * Initiate transaction as maker
     */
    public MakerCheckerWorkflow initiateTransaction(Object transactionData) {
        addStep("INITIATE_TRANSACTION");
        transactionContext.putData("transaction_data", transactionData);
        logger.info("Transaction initiated: {}", transactionData);
        recordStepExecution("INITIATE_TRANSACTION", "SUCCESS");
        return this;
    }

    /**
     * Submit for approval
     */
    public MakerCheckerWorkflow submitForApproval() {
        addStep("SUBMIT_FOR_APPROVAL");
        submittedForApproval = true;
        transactionContext.putData("submission_status", "PENDING_APPROVAL");
        logger.info("Transaction submitted for approval by: {}", currentMaker);
        recordStepExecution("SUBMIT_FOR_APPROVAL", "SUCCESS");
        return this;
    }

    /**
     * Review transaction as checker
     */
    public MakerCheckerWorkflow reviewTransaction() {
        if (!submittedForApproval) {
            throw new RuntimeException("Transaction not submitted for approval");
        }
        addStep("REVIEW_TRANSACTION");
        transactionContext.putData("review_status", "IN_REVIEW");
        logger.info("Transaction being reviewed by: {}", currentChecker);
        recordStepExecution("REVIEW_TRANSACTION", "SUCCESS");
        return this;
    }

    /**
     * Approve transaction
     */
    public MakerCheckerWorkflow approveWithAuthorization() {
        addStep("APPROVE_TRANSACTION");
        approved = true;
        transactionContext.putData("approval_status", "APPROVED");
        transactionContext.putData("approved_by", currentChecker);
        logger.info("Transaction approved by: {}", currentChecker);
        recordStepExecution("APPROVE_TRANSACTION", "SUCCESS");
        return this;
    }

    /**
     * Reject transaction
     */
    public MakerCheckerWorkflow rejectTransaction(String reason) {
        addStep("REJECT_TRANSACTION");
        transactionContext.putData("approval_status", "REJECTED");
        transactionContext.putData("rejection_reason", reason);
        logger.warn("Transaction rejected by {}: {}", currentChecker, reason);
        recordStepExecution("REJECT_TRANSACTION", "SUCCESS");
        return this;
    }

    /**
     * Execute workflow
     */
    @Override
    public void execute() throws Exception {
        try {
            logger.info("Starting Maker-Checker workflow execution");
            transactionContext.setStatus("EXECUTING");

            // Workflow execution logic
            if (!submittedForApproval) {
                throw new RuntimeException("Workflow incomplete: Transaction not submitted for approval");
            }
            if (!approved) {
                throw new RuntimeException("Workflow incomplete: Transaction not approved");
            }

            transactionContext.setStatus("COMPLETED");
            transactionContext.completeTransaction();
            logger.info("Maker-Checker workflow execution completed successfully");
            printExecutionSummary();
        } catch (Exception e) {
            logger.error("Workflow execution failed", e);
            transactionContext.failTransaction(e.getMessage());
            throw e;
        }
    }

    /**
     * Rollback workflow
     */
    @Override
    public void rollback() throws Exception {
        logger.warn("Rolling back Maker-Checker workflow");
        transactionContext.setStatus("ROLLED_BACK");
        addStep("ROLLBACK");
        recordStepExecution("ROLLBACK", "SUCCESS");
    }

    // Getters
    public boolean isApproved() {
        return approved;
    }

    public boolean isSubmitted() {
        return submittedForApproval;
    }
}
