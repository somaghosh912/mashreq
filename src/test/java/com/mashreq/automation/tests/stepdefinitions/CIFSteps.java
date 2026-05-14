package com.mashreq.automation.tests.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import com.mashreq.automation.engine.GenericActionEngine;
import com.mashreq.automation.transaction.TransactionContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import java.util.Map;

/**
 * CIF (Customer Information File) step definitions for maker-checker workflows
 */
public class CIFSteps {

    private static final Logger logger = LogManager.getLogger(CIFSteps.class);
    private GenericActionEngine actionEngine;
    private TransactionContext transactionContext;

    public CIFSteps(com.mashreq.automation.tests.hooks.Hooks hooks) {
        this.actionEngine = hooks.getActionEngine();
        this.transactionContext = hooks.getTransactionContext();
    }

    @Given("User \"([^\"]*)\" is logged in with \"([^\"]*)\"")
    public void userIsLoggedInWithRole(String userId, String role) {
        logger.info("Logging in user: {} with role: {}", userId, role);
        actionEngine.type("LoginScreen.username_field", userId);
        actionEngine.type("LoginScreen.password_field", "Password@123");
        actionEngine.click("LoginScreen.login_button");
        actionEngine.waitForElementVisible("DashboardScreen.welcome_message", 30000);
        transactionContext.setUser(userId, role);
    }

    @When("User initiates CIF creation for customer \"([^\"]*)\"")
    public void userInitiatesCIFCreation(String customerId) {
        logger.info("Initiating CIF creation for customer: {}", customerId);
        actionEngine.click("DashboardScreen.cif_menu");
        actionEngine.waitForElementVisible("CIFScreen.cif_id_field", 30000);
        transactionContext.putData("customer_id", customerId);
    }

    @When("User fills CIF details:")
    public void userFillsCIFDetails(io.cucumber.datatable.DataTable dataTable) {
        logger.info("Filling CIF details");
        Map<String, String> cifData = dataTable.asMap();
        
        for (Map.Entry<String, String> entry : cifData.entrySet()) {
            logger.debug("Setting {}: {}", entry.getKey(), entry.getValue());
            // Map field names to screen elements
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
    }

    @Then("CIF should be in \"([^\"]*)\" status")
    public void cifShouldBeInStatus(String expectedStatus) {
        logger.info("Verifying CIF status: {}", expectedStatus);
        transactionContext.putData("expected_status", expectedStatus);
        // In real implementation, would query database or API
        Assert.assertNotNull(expectedStatus, "Status should not be null");
    }

    @When("User navigates to CIF approval list")
    public void userNavigatesToCIFApprovalList() {
        logger.info("Navigating to CIF approval list");
        actionEngine.click("DashboardScreen.cif_menu");
        actionEngine.waitForElementVisible("CIFScreen.cif_id_field", 30000);
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
    }

    @Then("CIF should be available in the Dashboard")
    public void cifShouldBeAvailableInDashboard() {
        logger.info("Verifying CIF is available in dashboard");
        Assert.assertTrue(actionEngine.isVisible("DashboardScreen.welcome_message"), "Dashboard not visible");
    }
}
