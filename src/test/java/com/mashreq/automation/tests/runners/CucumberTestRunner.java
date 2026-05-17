package com.mashreq.automation.tests.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * Cucumber Test Runner for BDD test execution
 * Runs Cucumber feature files with TestNG
 */
@CucumberOptions(
    features = "src/test/resources/features",
    glue = {
        "com.mashreq.automation.tests.stepdefinitions",
        "com.mashreq.automation.tests.hooks"
    },
    plugin = {
        "pretty",
        "html:target/cucumber-reports/cucumber.html",
        "json:target/cucumber-reports/cucumber.json",
        "junit:target/cucumber-reports/cucumber.xml"
    },
    monochrome = false,
    tags = "@smoke or @regression",
    dryRun = false,
    strict = true
)
public class CucumberTestRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
