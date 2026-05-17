package com.mashreq.automation.tests.runners;

import com.mashreq.automation.config.ConfigManager;
import com.mashreq.automation.config.FrameworkConstants;
import com.mashreq.automation.driver.DriverManager;
import com.mashreq.automation.listeners.ExtentListener;
import com.mashreq.automation.listeners.RetryListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlPackage;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.io.File;
import java.util.*;

/**
 * Comprehensive Test Runner Class
 * 
 * Runs all test cases with programmatic configuration
 * Replaces testng.xml with Java-based configuration
 * 
 * Usage:
 *   java -cp target/classes:target/test-classes TestRunner
 *   java -cp target/classes:target/test-classes TestRunner -env=qa -browser=chromium
 * 
 * Maven:
 *   mvn exec:java -Dexec.mainClass="com.mashreq.automation.tests.runners.TestRunner"
 *   mvn exec:java -Dexec.mainClass="com.mashreq.automation.tests.runners.TestRunner" -Denv=qa -Dbrowser=chromium
 */
public class TestRunner {

    private static final Logger logger = LogManager.getLogger(TestRunner.class);
    
    private static final String TEST_PACKAGE = "com.mashreq.automation.tests.workflows";
    private static final String SCREENSHOT_PATH = "target/screenshots/";
    private static final String VIDEO_PATH = "target/videos/";

    // Configuration properties
    private String environment;
    private String browser;
    private int threadCount;
    private boolean headless;
    private int slowmo;
    private boolean parallelExecution;
    private List<String> testGroups;

    /**
     * Default constructor with default configuration
     */
    public TestRunner() {
        this.environment = System.getProperty("env", "qa");
        this.browser = System.getProperty("browser", "chromium");
        this.threadCount = Integer.parseInt(System.getProperty("threads", "4"));
        this.headless = Boolean.parseBoolean(System.getProperty("headless", "true"));
        this.slowmo = Integer.parseInt(System.getProperty("slowmo", "0"));
        this.parallelExecution = Boolean.parseBoolean(System.getProperty("parallel", "true"));
        this.testGroups = parseTestGroups(System.getProperty("groups", "smoke,regression,maker-checker"));
    }

    /**
     * Constructor with custom configuration
     */
    public TestRunner(String environment, String browser, int threadCount) {
        this.environment = environment;
        this.browser = browser;
        this.threadCount = threadCount;
        this.headless = true;
        this.slowmo = 0;
        this.parallelExecution = true;
        this.testGroups = Arrays.asList("smoke", "regression", "maker-checker");
    }

    /**
     * Main entry point - run all tests
     */
    public static void main(String[] args) {
        try {
            logger.info("========== MASHREQ TEST RUNNER STARTED ==========");
            
            // Parse command line arguments if provided
            TestRunner runner = new TestRunner();
            runner.parseCommandLineArgs(args);
            
            // Execute tests
            runner.executeTests();
            
            logger.info("========== MASHREQ TEST RUNNER COMPLETED ==========");
        } catch (Exception e) {
            logger.error("Test execution failed", e);
            System.exit(1);
        }
    }

    /**
     * Parse command line arguments
     */
    private void parseCommandLineArgs(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("-env=")) {
                this.environment = arg.substring(5);
            } else if (arg.startsWith("-browser=")) {
                this.browser = arg.substring(9);
            } else if (arg.startsWith("-threads=")) {
                this.threadCount = Integer.parseInt(arg.substring(9));
            } else if (arg.startsWith("-headless=")) {
                this.headless = Boolean.parseBoolean(arg.substring(10));
            } else if (arg.startsWith("-groups=")) {
                this.testGroups = parseTestGroups(arg.substring(8));
            }
        }
        logConfiguration();
    }

    /**
     * Log current configuration
     */
    private void logConfiguration() {
        logger.info("\n========== TEST CONFIGURATION ==========");
        logger.info("Environment: {}", environment);
        logger.info("Browser: {}", browser);
        logger.info("Thread Count: {}", threadCount);
        logger.info("Headless Mode: {}", headless);
        logger.info("Slowmo (ms): {}", slowmo);
        logger.info("Parallel Execution: {}", parallelExecution);
        logger.info("Test Groups: {}", testGroups);
        logger.info("========== CONFIGURATION END ==========\n");
    }

    /**
     * Execute all tests
     */
    public void executeTests() {
        try {
            // Create TestNG instance
            TestNG testng = new TestNG();
            testng.setOutputDirectory("target/test-reports");
            
            // Create and configure suite
            XmlSuite xmlSuite = createTestSuite();
            
            // Set suite to TestNG
            List<XmlSuite> suites = new ArrayList<>();
            suites.add(xmlSuite);
            testng.setXmlSuites(suites);
            
            // Add listeners
            testng.addListener(new ExtentListener());
            testng.addListener(new RetryListener());
            
            // Run tests
            logger.info("Starting test execution...");
            testng.run();
            
            if (testng.hasFailure()) {
                logger.warn("Some tests failed");
                System.exit(1);
            } else {
                logger.info("All tests passed successfully");
            }
        } catch (Exception e) {
            logger.error("Error during test execution", e);
            throw new RuntimeException("Test execution failed", e);
        }
    }

    /**
     * Create TestNG suite with all configuration
     */
    private XmlSuite createTestSuite() {
        XmlSuite suite = new XmlSuite();
        suite.setName("Mashreq FLEXCUBE Enterprise Suite");
        suite.setParallel(parallelExecution ? XmlSuite.ParallelMode.METHODS : XmlSuite.ParallelMode.NONE);
        suite.setThreadCount(threadCount);
        suite.setVerbose(2);
        
        // Add listeners
        suite.addListener(ExtentListener.class.getName());
        suite.addListener(RetryListener.class.getName());
        
        // Create tests for each group
        List<XmlTest> tests = new ArrayList<>();
        
        if (testGroups.contains("smoke")) {
            tests.add(createXmlTest("Smoke Tests", "smoke"));
        }
        if (testGroups.contains("regression")) {
            tests.add(createXmlTest("Regression Tests", "regression"));
        }
        if (testGroups.contains("maker-checker")) {
            tests.add(createXmlTest("Maker-Checker Workflows", "maker-checker"));
        }
        if (testGroups.contains("critical")) {
            tests.add(createXmlTest("Critical Path", "critical"));
        }
        
        // Add tests to suite
        suite.setTests(tests);
        
        return suite;
    }

    /**
     * Create individual XML test with group and parameters
     */
    private XmlTest createXmlTest(String testName, String groupName) {
        XmlTest test = new XmlTest(new XmlSuite());
        test.setName(testName);
        
        // Set parameters
        Map<String, String> parameters = new HashMap<>();
        parameters.put("env", environment);
        parameters.put("browser", browser);
        parameters.put("headless", String.valueOf(headless));
        parameters.put("slowmo", String.valueOf(slowmo));
        parameters.put("screenshotPath", SCREENSHOT_PATH);
        parameters.put("videoPath", VIDEO_PATH);
        test.setParameters(parameters);
        
        // Set groups to include
        test.addIncludedGroup(groupName);
        
        // Add test package
        XmlPackage xmlPackage = new XmlPackage(TEST_PACKAGE);
        test.getPackages().add(xmlPackage);
        
        return test;
    }

    /**
     * Parse test groups from comma-separated string
     */
    private List<String> parseTestGroups(String groupsString) {
        List<String> groups = new ArrayList<>();
        if (groupsString != null && !groupsString.isEmpty()) {
            String[] groupArray = groupsString.split(",");
            for (String group : groupArray) {
                groups.add(group.trim());
            }
        }
        return groups;
    }

    /**
     * Run specific test class
     */
    public void runTestClass(Class<?> testClass) {
        TestNG testng = new TestNG();
        testng.setOutputDirectory("target/test-reports");
        
        XmlSuite xmlSuite = new XmlSuite();
        xmlSuite.setName("Test Suite");
        xmlSuite.setParallel(XmlSuite.ParallelMode.METHODS);
        xmlSuite.setThreadCount(threadCount);
        
        XmlTest xmlTest = new XmlTest(xmlSuite);
        xmlTest.setName("Test");
        
        // Set parameters
        Map<String, String> parameters = new HashMap<>();
        parameters.put("env", environment);
        parameters.put("browser", browser);
        xmlTest.setParameters(parameters);
        
        // Add test class
        XmlClass xmlClass = new XmlClass(testClass);
        xmlTest.setClasses(Arrays.asList(xmlClass));
        
        testng.setXmlSuites(Arrays.asList(xmlSuite));
        testng.addListener(new ExtentListener());
        testng.addListener(new RetryListener());
        
        logger.info("Running test class: {}", testClass.getName());
        testng.run();
    }

    /**
     * Run tests by group
     */
    public void runTestsByGroup(String... groups) {
        this.testGroups = Arrays.asList(groups);
        executeTests();
    }

    /**
     * Get configuration
     */
    public String getEnvironment() { return environment; }
    public String getBrowser() { return browser; }
    public int getThreadCount() { return threadCount; }
    public boolean isHeadless() { return headless; }
    public List<String> getTestGroups() { return testGroups; }

    /**
     * Set configuration
     */
    public void setEnvironment(String environment) { this.environment = environment; }
    public void setBrowser(String browser) { this.browser = browser; }
    public void setThreadCount(int threadCount) { this.threadCount = threadCount; }
    public void setHeadless(boolean headless) { this.headless = headless; }
}
