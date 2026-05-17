package com.mashreq.automation.tests.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.datatable.DataTable;
import com.mashreq.automation.engine.GenericActionEngine;
import com.mashreq.automation.transaction.TransactionContext;
import com.mashreq.automation.validations.ValidationOrchestrator;
import com.microsoft.playwright.Page;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import java.util.HashMap;
import java.util.Map;

/**
 * Enhanced CIF Step Definitions with Multi-Layer Validation
 * Demonstrates how to use ValidationOrchestrator for comprehensive testing
 */
public class CIFStepsWithValidation {

    private static final Logger logger = LogManager.getLogger(CIFStepsWithValidation.class);
    private GenericActionEngine actionEngine;
    private TransactionContext transactionContext;
    private ValidationOrchestrator validationOrchestrator;  // ✅ ADD THIS
    private Page page;

    public CIFStepsWithValidation(com.mashreq.automation.tests.hooks.Hooks hooks) {
        this.actionEngine = hooks.getActionEngine();
        this.transactionContext = hooks.getTransactionContext();
        this.page = hooks.getPage();  // ✅ GET PAGE FROM HOOKS
        this.validationOrchestrator = new ValidationOrchestrator(page);  // ✅ INITIALIZE
    }

    @Given("User \"([^\"]*)\" is logged in with \"([^\"]*)\"")
    public void userIsLoggedInWithRole(String userId, String role) {
        logger.info("Logging in user: {} with role: {}", userId, role);
        
        // Perform login actions
        actionEngine.type("LoginScreen.username_field", userId);
        actionEngine.type("LoginScreen.password_field", "Password@123");
        actionEngine.click("LoginScreen.login_button");
        actionEngine.waitForElementVisible("DashboardScreen.welcome_message", 30000);
        
        // Store login data
        transactionContext.setUser(userId, role);
        
        // ✅ VALIDATE UI AFTER LOGIN
        Map<String, Object> loginExpectations = new HashMap<>();
        loginExpectations.put("element_visible", "DashboardScreen.welcome_message");
        loginExpectations.put("user_role", role);
        
        boolean loginValidated = validationOrchestrator.validateEnd2End(
            "LOGIN_" + userId, 
            loginExpectations
        );
        
        logger.info("Login validation result: {}", loginValidated);
        Assert.assertTrue(loginValidated, "Login validation failed for user: " + userId);
    }

    @When("User initiates CIF creation for customer \"([^\"]*)\"")
    public void userInitiatesCIFCreation(String customerId) {
        logger.info("Initiating CIF creation for customer: {}", customerId);
        
        actionEngine.click("DashboardScreen.cif_menu");
        actionEngine.waitForElementVisible("CIFScreen.cif_id_field", 30000);
        transactionContext.putData("customer_id", customerId);
        
        // ✅ VALIDATE CIF SCREEN LOADED
        Map<String, Object> screenExpectations = new HashMap<>();
        screenExpectations.put("element_visible", "CIFScreen.cif_id_field");
        screenExpectations.put("element_visible", "CIFScreen.customer_name_field");
        screenExpectations.put("element_visible", "CIFScreen.customer_type_dropdown");
        
        boolean screenValidated = validationOrchestrator.validateEnd2End(
            "CIF_SCREEN_LOAD_" + customerId,
            screenExpectations
        );
        
        Assert.assertTrue(screenValidated, "CIF screen validation failed");
    }

    @When("User fills CIF details:")
    public void userFillsCIFDetails(DataTable dataTable) {
        logger.info("Filling CIF details");
        Map<String, String> cifData = dataTable.asMap();
        
        // Perform data entry
        for (Map.Entry<String, String> entry : cifData.entrySet()) {
            logger.debug("Setting {}: {}", entry.getKey(), entry.getValue());
            switch (entry.getKey()) {
                case "Customer Name":
                    actionEngine.type("CIFScreen.customer_name_field", entry.getValue());
                    break;
                case "Customer Type":
                    actionEngine.selectByVisibleText("CIFScreen.customer_type_dropdown", entry.getValue());
                    break;
                default:
                    logger.warn("Unknown field: {}", entry.getKey());
            }
            transactionContext.putData(entry.getKey(), entry.getValue());
        }
        
        // ✅ VALIDATE DATA ENTRY
        Map<String, Object> dataEntryExpectations = new HashMap<>();
        dataEntryExpectations.putAll(cifData);  // Validate all entered data
        dataEntryExpectations.put("form_valid", true);
        
        boolean dataValidated = validationOrchestrator.validateEnd2End(
            "CIF_DATA_ENTRY",
            dataEntryExpectations
        );
        
        logger.info("Data entry validation result: {}", dataValidated);
    }

    @When("User saves the CIF")
    public void userSavesTheCIF() {
        logger.info("Saving CIF");
        actionEngine.click("CIFScreen.save_button");
        actionEngine.waitForElementVisible("CIFScreen.success_message", 15000);
    }

    @When("User submits CIF for approval")
    public void userSubmitsCIFForApproval() {
        logger.info("Submitting CIF for approval");
        actionEngine.click("CIFScreen.submit_button");
        transactionContext.recordStepResult("SUBMIT_FOR_APPROVAL", "SUCCESS");
        
        // ✅ VALIDATE SUBMISSION SUCCESS
        Map<String, Object> submissionExpectations = new HashMap<>();
        submissionExpectations.put("element_visible", "CIFScreen.success_message");
        submissionExpectations.put("status", "PENDING_APPROVAL");
        
        boolean submissionValidated = validationOrchestrator.validateEnd2End(
            "CIF_SUBMISSION",
            submissionExpectations
        );
        
        Assert.assertTrue(submissionValidated, "CIF submission validation failed");
    }

    @Then("CIF should be in \"([^\"]*)\" status")
    public void cifShouldBeInStatus(String expectedStatus) {
        logger.info("Verifying CIF status: {}", expectedStatus);
        transactionContext.putData("expected_status", expectedStatus);
        
        // ✅ MULTI-LAYER VALIDATION
        Map<String, Object> statusExpectations = new HashMap<>();
        statusExpectations.put("cif_status", expectedStatus);
        statusExpectations.put("status_element_visible", true);
        
        // This triggers UI, API, and DB validations
        boolean statusValidated = validationOrchestrator.validateEnd2End(
            "CIF_STATUS_VERIFICATION_" + expectedStatus,
            statusExpectations
        );
        
        // Get comprehensive validation report
        Map<String, Object> validationReport = validationOrchestrator.getValidationReport();
        logger.info("Validation Report: {}", validationReport);
        
        Assert.assertTrue(statusValidated, 
            "CIF status validation failed. Expected: " + expectedStatus + 
            ". Report: " + validationReport);
    }

    @When("User navigates to CIF approval list")
    public void userNavigatesToCIFApprovalList() {
        logger.info("Navigating to CIF approval list");
        actionEngine.click("DashboardScreen.cif_menu");
        actionEngine.waitForElementVisible("CIFScreen.cif_id_field", 30000);
        
        // ✅ VALIDATE APPROVAL LIST SCREEN
        Map<String, Object> approvalListExpectations = new HashMap<>();
        approvalListExpectations.put("element_visible", "CIFScreen.cif_id_field");
        approvalListExpectations.put("page_title_contains", "Approval");
        
        boolean approvalListValidated = validationOrchestrator.validateEnd2End(
            "CIF_APPROVAL_LIST",
            approvalListExpectations
        );
        
        Assert.assertTrue(approvalListValidated, "Approval list validation failed");
    }

    @When("User searches and opens CIF \"([^\"]*)\"")
    public void userSearchesAndOpensCIF(String cifId) {
        logger.info("Searching and opening CIF: {}", cifId);
        actionEngine.type("CIFScreen.cif_id_field", cifId);
    }

    @When("User approves the CIF with authorization")
    public void userApprovesTheCIF() {
        logger.info("Approving CIF");
        actionEngine.click("CIFScreen.submit_button");
        transactionContext.recordStepResult("APPROVE_CIF", "SUCCESS");
        
        // ✅ VALIDATE APPROVAL COMPLETION
        Map<String, Object> approvalExpectations = new HashMap<>();
        approvalExpectations.put("element_visible", "CIFScreen.success_message");
        approvalExpectations.put("cif_status", "APPROVED");
        approvalExpectations.put("authorization_recorded", true);
        
        boolean approvalValidated = validationOrchestrator.validateEnd2End(
            "CIF_APPROVAL",
            approvalExpectations
        );
        
        Assert.assertTrue(approvalValidated, "CIF approval validation failed");
    }

    @Then("CIF should be available in the Dashboard")
    public void cifShouldBeAvailableInDashboard() {
        logger.info("Verifying CIF is available in dashboard");
        
        // ✅ COMPREHENSIVE END-TO-END VALIDATION
        Map<String, Object> dashboardExpectations = new HashMap<>();
        dashboardExpectations.put("element_visible", "DashboardScreen.welcome_message");
        dashboardExpectations.put("cif_in_list", true);
        dashboardExpectations.put("cif_status", "APPROVED");
        dashboardExpectations.put("database_updated", true);  // DB validation
        
        // Execute multi-layer validation
        boolean dashboardValidated = validationOrchestrator.validateEnd2End(
            "CIF_DASHBOARD_AVAILABILITY",
            dashboardExpectations
        );
        
        // Get detailed validation results
        Map<String, Boolean> validationResults = validationOrchestrator.getValidationResults();
        logger.info("Individual validation results: {}", validationResults);
        
        // Generate final report
        Map<String, Object> finalReport = validationOrchestrator.getValidationReport();
        logger.info("Final Validation Report: {}", finalReport);
        
        // Log success count
        long passCount = validationResults.values().stream().filter(v -> v).count();
        logger.info("Passed validations: {}/{}", passCount, validationResults.size());
        
        Assert.assertTrue(dashboardValidated, "Dashboard validation failed");
    }
}
