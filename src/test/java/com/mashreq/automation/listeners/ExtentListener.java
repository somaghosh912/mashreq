package com.mashreq.automation.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.testng.*;
import org.testng.xml.XmlTest;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * ExtentListener - TestNG listener for generating Extent Reports
 * Captures test execution details and generates HTML reports
 */
public class ExtentListener implements ITestListener, ISuiteListener {
    
    private static ExtentReports extentReports;
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    private static final String REPORT_PATH = "target/extent-reports/";
    private static final String REPORT_NAME = "Mashreq_Automation_Report.html";
    
    @Override
    public void onStart(ISuite suite) {
        initializeExtentReports();
    }
    
    @Override
    public void onFinish(ISuite suite) {
        if (extentReports != null) {
            extentReports.flush();
        }
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String className = result.getTestClass().getRealClass().getSimpleName();
        String description = result.getMethod().getDescription();
        
        ExtentTest test = extentReports.createTest(className + " - " + methodName, description);
        
        // Add test parameters
        XmlTest xmlTest = result.getTestContext().getCurrentXmlTest();
        if (xmlTest != null) {
            Map<String, String> parameters = xmlTest.getAllParameters();
            if (!parameters.isEmpty()) {
                test.info("Test Parameters: " + parameters);
            }
        }
        
        extentTest.set(test);
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest test = extentTest.get();
        if (test != null) {
            test.pass("Test passed successfully");
        }
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = extentTest.get();
        if (test != null) {
            test.fail("Test failed with exception:");
            test.fail(result.getThrowable());
        }
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = extentTest.get();
        if (test != null) {
            test.skip("Test skipped");
            if (result.getThrowable() != null) {
                test.skip(result.getThrowable());
            }
        }
    }
    
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        ExtentTest test = extentTest.get();
        if (test != null) {
            test.warning("Test failed but within success percentage");
        }
    }
    
    /**
     * Initialize Extent Reports with configuration
     */
    private void initializeExtentReports() {
        if (extentReports == null) {
            // Create reports directory if it doesn't exist
            File reportDir = new File(REPORT_PATH);
            if (!reportDir.exists()) {
                reportDir.mkdirs();
            }
            
            // Initialize ExtentSparkReporter
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(REPORT_PATH + REPORT_NAME);
            sparkReporter.config().setTheme(Theme.DARK);
            sparkReporter.config().setDocumentTitle("Mashreq Automation Test Report");
            sparkReporter.config().setReportName("Mashreq FLEXCUBE Enterprise Suite - Test Report");
            sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");
            
            // Create ExtentReports instance
            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            
            // Add system information
            extentReports.setSystemInfo("Environment", System.getProperty("env", "QA"));
            extentReports.setSystemInfo("Browser", System.getProperty("browser", "chromium"));
            extentReports.setSystemInfo("OS", System.getProperty("os.name"));
            extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
            extentReports.setSystemInfo("Execution Date", getCurrentDateTime());
        }
    }
    
    /**
     * Get current date and time
     */
    private String getCurrentDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }
    
    /**
     * Get the ExtentTest instance for current thread
     */
    public static ExtentTest getExtentTest() {
        return extentTest.get();
    }
    
    /**
     * Remove ExtentTest from ThreadLocal
     */
    public static void removeExtentTest() {
        extentTest.remove();
    }
}
