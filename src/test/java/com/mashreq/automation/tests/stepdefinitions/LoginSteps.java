package com.mashreq.automation.tests.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import com.mashreq.automation.engine.GenericActionEngine;
import com.mashreq.automation.transaction.TransactionContext;
import com.microsoft.playwright.Page;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

/**
 * Login step definitions for BDD scenarios
 */
public class LoginSteps {

    private static final Logger logger = LogManager.getLogger(LoginSteps.class);
    private GenericActionEngine actionEngine;
    private TransactionContext transactionContext;

    public LoginSteps(com.mashreq.automation.tests.hooks.Hooks hooks) {
        this.actionEngine = hooks.getActionEngine();
        this.transactionContext = hooks.getTransactionContext();
    }

    @Given("User navigates to login page")
    public void userNavigatesToLoginPage() {
        logger.info("User navigates to login page");
        // Page is already navigated in Hooks
        actionEngine.waitForElementVisible("LoginScreen.username_field", 30000);
    }

    @When("User enters valid credentials")
    public void userEntersValidCredentials(io.cucumber.datatable.DataTable dataTable) {
        logger.info("Entering valid credentials");
        java.util.Map<String, String> credentials = dataTable.asMap();
        actionEngine.type("LoginScreen.username_field", credentials.get("username"));
        actionEngine.type("LoginScreen.password_field", credentials.get("password"));
        transactionContext.putData("username", credentials.get("username"));
    }

    @When("User enters invalid credentials")
    public void userEntersInvalidCredentials(io.cucumber.datatable.DataTable dataTable) {
        logger.info("Entering invalid credentials");
        java.util.Map<String, String> credentials = dataTable.asMap();
        actionEngine.type("LoginScreen.username_field", credentials.get("username"));
        actionEngine.type("LoginScreen.password_field", credentials.get("password"));
    }

    @When("User clicks login button")
    public void userClicksLoginButton() {
        logger.info("Clicking login button");
        actionEngine.click("LoginScreen.login_button");
    }

    @Then("Dashboard should be displayed")
    public void dashboardShouldBeDisplayed() {
        logger.info("Verifying dashboard is displayed");
        actionEngine.waitForElementVisible("DashboardScreen.welcome_message", 30000);
        Assert.assertTrue(actionEngine.isVisible("DashboardScreen.welcome_message"), "Dashboard not displayed");
    }

    @Then("Error message \"([^\"]*)\" should be displayed")
    public void errorMessageShouldBeDisplayed(String expectedMessage) {
        logger.info("Verifying error message: {}", expectedMessage);
        actionEngine.waitForElementVisible("LoginScreen.error_message", 15000);
        String actualMessage = actionEngine.getText("LoginScreen.error_message");
        Assert.assertTrue(actualMessage.contains(expectedMessage), "Expected message not found");
    }

    @Then("User should remain on login page")
    public void userShouldRemainOnLoginPage() {
        logger.info("Verifying user is on login page");
        Assert.assertTrue(actionEngine.isVisible("LoginScreen.username_field"), "Not on login page");
    }
}
