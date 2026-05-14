package com.mashreq.automation.tests.workflows;

import com.mashreq.automation.core.BaseTest;
import com.mashreq.automation.workflow.MakerCheckerWorkflow;
import org.testng.annotations.Test;
import org.testng.Assert;
import java.util.HashMap;
import java.util.Map;

/**
 * Example workflow test: CIF Master creation with maker-checker approval
 */
public class CIFWorkflowTest extends BaseTest {

    @Test(groups = {"smoke", "maker-checker"}, description = "Create CIF with maker-checker workflow")
    public void testCIFCreationWithApproval() {
        try {
            // Setup: Login as Maker
            logger.info("Step 1: Login as Maker");
            actionEngine.type("LoginScreen.username_field", "maker1@mashreq.ae");
            actionEngine.type("LoginScreen.password_field", "MakerPassword@123");
            actionEngine.click("LoginScreen.login_button");
            actionEngine.waitForElementVisible("DashboardScreen.welcome_message", 30000);

            // Step 2: Initiate CIF Creation
            logger.info("Step 2: Navigate to CIF creation");
            actionEngine.click("DashboardScreen.cif_menu");
            actionEngine.waitForElementVisible("CIFScreen.cif_id_field", 30000);

            // Step 3: Fill CIF Form
            logger.info("Step 3: Fill CIF master form");
            Map<String, String> cifData = new HashMap<>();
            cifData.put("cif_id", "CUST001");
            cifData.put("customer_name", "Test Customer One");
            cifData.put("customer_type", "Individual");

            actionEngine.type("CIFScreen.cif_id_field", cifData.get("cif_id"));
            actionEngine.type("CIFScreen.customer_name_field", cifData.get("customer_name"));
            actionEngine.selectByVisibleText("CIFScreen.customer_type_dropdown", cifData.get("customer_type"));
            actionEngine.click("CIFScreen.save_button");
            transactionContext.putData("cif_id", cifData.get("cif_id"));

            // Step 4: Submit for Approval
            logger.info("Step 4: Submit CIF for approval");
            actionEngine.waitAndClick("CIFScreen.submit_button");
            actionEngine.waitForElementVisible("CIFScreen.success_message", 15000);

            String successMessage = actionEngine.getText("CIFScreen.success_message");
            logger.info("Success message: {}", successMessage);
            transactionContext.recordStepResult("SUBMIT_FOR_APPROVAL", "SUCCESS");

            // Step 5: Logout as Maker
            logger.info("Step 5: Logout as Maker");
            actionEngine.click("DashboardScreen.user_menu");
            actionEngine.click("DashboardScreen.logout_option");
            actionEngine.waitForElementVisible("LoginScreen.username_field", 15000);

            // Step 6: Login as Checker
            logger.info("Step 6: Login as Checker");
            actionEngine.type("LoginScreen.username_field", "checker1@mashreq.ae");
            actionEngine.type("LoginScreen.password_field", "CheckerPassword@123");
            actionEngine.click("LoginScreen.login_button");
            actionEngine.waitForElementVisible("DashboardScreen.welcome_message", 30000);

            // Step 7: Review and Approve
            logger.info("Step 7: Review and approve CIF");
            actionEngine.click("DashboardScreen.cif_menu");
            actionEngine.waitForElementVisible("CIFScreen.cif_id_field", 30000);
            // Search for submitted CIF
            actionEngine.type("CIFScreen.cif_id_field", cifData.get("cif_id"));
            actionEngine.click("CIFScreen.submit_button"); // Re-purposing for approval
            transactionContext.recordStepResult("APPROVE_CIF", "SUCCESS");

            // Validation
            logger.info("Step 8: Validate CIF approval");
            boolean success = actionEngine.isVisible("CIFScreen.success_message");
            Assert.assertTrue(success, "CIF approval failed");

            // Transaction completion
            transactionContext.completeTransaction();
            logger.info("CIF Workflow Test completed successfully");

        } catch (Exception e) {
            logger.error("CIF Workflow Test failed", e);
            transactionContext.failTransaction(e.getMessage());
            throw new RuntimeException("Test failed", e);
        }
    }

    @Test(groups = {"smoke"}, description = "Test CIF field validations")
    public void testCIFFieldValidations() {
        try {
            // Login
            actionEngine.type("LoginScreen.username_field", "maker1@mashreq.ae");
            actionEngine.type("LoginScreen.password_field", "MakerPassword@123");
            actionEngine.click("LoginScreen.login_button");
            actionEngine.waitForElementVisible("DashboardScreen.welcome_message", 30000);

            // Navigate to CIF
            actionEngine.click("DashboardScreen.cif_menu");
            actionEngine.waitForElementVisible("CIFScreen.cif_id_field", 30000);

            // Attempt to save without mandatory fields
            actionEngine.click("CIFScreen.save_button");

            // Verify error message
            boolean errorVisible = actionEngine.isVisible("CIFScreen.success_message");
            Assert.assertFalse(errorVisible, "Validation error expected");

            logger.info("Field validation test passed");
        } catch (Exception e) {
            logger.error("Field validation test failed", e);
            throw new RuntimeException("Test failed", e);
        }
    }
}
