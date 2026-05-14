package com.mashreq.automation.tests.workflows;

import com.mashreq.automation.core.BaseTest;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 * Example test: Fund Transfer workflow
 */
public class FundTransferWorkflowTest extends BaseTest {

    @Test(groups = {"regression", "fund-transfer"}, description = "Execute domestic fund transfer")
    public void testDomesticFundTransfer() {
        try {
            // Login
            logger.info("Logging in for fund transfer test");
            actionEngine.type("LoginScreen.username_field", "maker1@mashreq.ae");
            actionEngine.type("LoginScreen.password_field", "MakerPassword@123");
            actionEngine.click("LoginScreen.login_button");
            actionEngine.waitForElementVisible("DashboardScreen.welcome_message", 30000);

            // Navigate to Fund Transfer
            logger.info("Navigating to Fund Transfer menu");
            actionEngine.click("DashboardScreen.fund_transfer_menu");

            // Fill transfer details
            logger.info("Filling fund transfer details");
            // Implementation would continue here with actual screen interactions

            logger.info("Fund transfer test completed");
        } catch (Exception e) {
            logger.error("Fund transfer test failed", e);
            throw new RuntimeException("Test failed", e);
        }
    }
}
