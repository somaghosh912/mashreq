package com.mashreq.automation.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Test Data Generator for creating realistic test data for automation testing
 * Supports generation of various data types based on element names and types
 */
public class TestDataGenerator {

    private static final Logger logger = LogManager.getLogger(TestDataGenerator.class);
    
    private static final Map<String, Object> DEFAULT_TEST_DATA = new HashMap<>();
    
    static {
        // Initialize default test data mappings
        DEFAULT_TEST_DATA.put("username", "testuser123");
        DEFAULT_TEST_DATA.put("email", "test.user@example.com");
        DEFAULT_TEST_DATA.put("password", "TestPassword@123");
        DEFAULT_TEST_DATA.put("search", "search_term");
        DEFAULT_TEST_DATA.put("account", "1234567890");
        DEFAULT_TEST_DATA.put("reference", "REF123456789");
        DEFAULT_TEST_DATA.put("amount", "1000.00");
        DEFAULT_TEST_DATA.put("date", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        DEFAULT_TEST_DATA.put("status", "Active");
        DEFAULT_TEST_DATA.put("country", "United States");
        DEFAULT_TEST_DATA.put("state", "California");
        DEFAULT_TEST_DATA.put("type", "Default");
    }

    /**
     * Generate test data for a screen based on element definitions
     *
     * @param screenName Screen identifier
     * @param elements Element definitions from metadata
     * @return Generated test data map
     */
    public Map<String, Object> generateTestData(String screenName, Map<String, Object> elements) {
        logger.info("Generating test data for screen: {}", screenName);
        
        Map<String, Object> testData = new LinkedHashMap<>();
        
        // Add screen metadata
        testData.put("screen_name", screenName);
        testData.put("generated_at", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        testData.put("generated_by", "TestDataGenerator");
        
        // Generate test data for each element
        Map<String, Object> testDataValues = new LinkedHashMap<>();
        
        if (elements != null && !elements.isEmpty()) {
            for (Map.Entry<String, Object> entry : elements.entrySet()) {
                String elementName = entry.getKey();
                Object elementValue = entry.getValue();
                
                String testValue = generateValueForElement(elementName, elementValue);
                testDataValues.put(elementName, testValue);
            }
        }
        
        testData.put("test_data", testDataValues);
        
        // Add test scenarios
        testData.put("test_scenarios", generateTestScenarios(screenName));
        
        // Add validation data
        testData.put("validation_data", generateValidationData(testDataValues));
        
        logger.info("Generated test data with {} test values for screen: {}", testDataValues.size(), screenName);
        return testData;
    }

    /**
     * Generate appropriate test value for an element
     */
    private String generateValueForElement(String elementName, Object elementDefinition) {
        String lowerName = elementName.toLowerCase();
        
        // Check for specific patterns in element name
        if (lowerName.contains("username") || lowerName.contains("user")) {
            return generateUsername();
        } else if (lowerName.contains("email")) {
            return generateEmail();
        } else if (lowerName.contains("password") || lowerName.contains("pwd")) {
            return generatePassword();
        } else if (lowerName.contains("phone") || lowerName.contains("mobile")) {
            return generatePhoneNumber();
        } else if (lowerName.contains("date") || lowerName.contains("dob")) {
            return generateDate();
        } else if (lowerName.contains("amount") || lowerName.contains("price") || lowerName.contains("value")) {
            return generateAmount();
        } else if (lowerName.contains("account") || lowerName.contains("accountnumber")) {
            return generateAccountNumber();
        } else if (lowerName.contains("reference") || lowerName.contains("refnumber")) {
            return generateReferenceNumber();
        } else if (lowerName.contains("search")) {
            return generateSearchTerm();
        } else if (lowerName.contains("field")) {
            return generateGenericText();
        } else if (lowerName.contains("dropdown") || lowerName.contains("select")) {
            return generateSelectOption(lowerName);
        } else if (lowerName.contains("button") || lowerName.contains("checkbox") || lowerName.contains("radio")) {
            return "true";
        } else {
            // Default fallback
            return generateGenericText();
        }
    }

    /**
     * Generate username
     */
    private String generateUsername() {
        String[] prefixes = {"test", "user", "admin", "demo"};
        String prefix = prefixes[ThreadLocalRandom.current().nextInt(prefixes.length)];
        int suffix = ThreadLocalRandom.current().nextInt(1000, 9999);
        return prefix + suffix;
    }

    /**
     * Generate email address
     */
    private String generateEmail() {
        String username = generateUsername();
        String[] domains = {"example.com", "test.com", "demo.org", "automation.io"};
        String domain = domains[ThreadLocalRandom.current().nextInt(domains.length)];
        return username + "@" + domain;
    }

    /**
     * Generate password
     */
    private String generatePassword() {
        // Password with uppercase, lowercase, number, and special character
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(ThreadLocalRandom.current().nextInt(chars.length())));
        }
        return password.toString();
    }

    /**
     * Generate phone number
     */
    private String generatePhoneNumber() {
        StringBuilder phone = new StringBuilder("+1-");
        for (int i = 0; i < 10; i++) {
            phone.append(ThreadLocalRandom.current().nextInt(10));
            if (i == 2 || i == 5) {
                phone.append("-");
            }
        }
        return phone.toString();
    }

    /**
     * Generate date
     */
    private String generateDate() {
        LocalDate date = LocalDate.now().minusDays(ThreadLocalRandom.current().nextInt(1, 365));
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Generate amount
     */
    private String generateAmount() {
        double amount = Math.round(ThreadLocalRandom.current().nextDouble(100, 99999) * 100.0) / 100.0;
        return String.format("%.2f", amount);
    }

    /**
     * Generate account number
     */
    private String generateAccountNumber() {
        StringBuilder account = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            account.append(ThreadLocalRandom.current().nextInt(10));
        }
        return account.toString();
    }

    /**
     * Generate reference number
     */
    private String generateReferenceNumber() {
        return "REF" + System.currentTimeMillis() % 1000000000;
    }

    /**
     * Generate search term
     */
    private String generateSearchTerm() {
        String[] terms = {"transaction", "payment", "transfer", "account", "report", "statement", "history", "balance"};
        return terms[ThreadLocalRandom.current().nextInt(terms.length)];
    }

    /**
     * Generate generic text
     */
    private String generateGenericText() {
        String[] samples = {"Sample Text", "Test Value", "Demo Data", "Automation Test", "QA Testing"};
        return samples[ThreadLocalRandom.current().nextInt(samples.length)];
    }

    /**
     * Generate select option based on element name
     */
    private String generateSelectOption(String elementName) {
        if (elementName.contains("status")) {
            String[] statuses = {"Active", "Inactive", "Pending", "Approved", "Rejected"};
            return statuses[ThreadLocalRandom.current().nextInt(statuses.length)];
        } else if (elementName.contains("country")) {
            String[] countries = {"United States", "Canada", "United Kingdom", "Australia", "India"};
            return countries[ThreadLocalRandom.current().nextInt(countries.length)];
        } else if (elementName.contains("state") || elementName.contains("region")) {
            String[] states = {"California", "Texas", "Florida", "New York", "Washington"};
            return states[ThreadLocalRandom.current().nextInt(states.length)];
        } else if (elementName.contains("type") || elementName.contains("category")) {
            String[] types = {"Type A", "Type B", "Type C", "Default", "Standard"};
            return types[ThreadLocalRandom.current().nextInt(types.length)];
        } else if (elementName.contains("filter")) {
            String[] filters = {"All", "Recent", "Popular", "Trending", "Featured"};
            return filters[ThreadLocalRandom.current().nextInt(filters.length)];
        } else {
            return "Option 1";
        }
    }

    /**
     * Generate test scenarios for the screen
     */
    private Map<String, Object> generateTestScenarios(String screenName) {
        Map<String, Object> scenarios = new LinkedHashMap<>();
        
        // Scenario 1: Positive/Happy Path
        Map<String, Object> positiveScenario = new LinkedHashMap<>();
        positiveScenario.put("name", "Positive Test - Valid Data");
        positiveScenario.put("description", "Test with all valid data and expected successful result");
        positiveScenario.put("expected_result", "Success");
        positiveScenario.put("priority", "P0");
        scenarios.put("positive_test", positiveScenario);
        
        // Scenario 2: Negative/Error Handling
        Map<String, Object> negativeScenario = new LinkedHashMap<>();
        negativeScenario.put("name", "Negative Test - Invalid Data");
        negativeScenario.put("description", "Test with invalid data and error handling");
        negativeScenario.put("expected_result", "Error message displayed");
        negativeScenario.put("priority", "P1");
        scenarios.put("negative_test", negativeScenario);
        
        // Scenario 3: Edge Cases
        Map<String, Object> edgeScenario = new LinkedHashMap<>();
        edgeScenario.put("name", "Edge Case Test - Boundary Values");
        edgeScenario.put("description", "Test with boundary and edge case values");
        edgeScenario.put("expected_result", "Handled gracefully");
        edgeScenario.put("priority", "P2");
        scenarios.put("edge_case_test", edgeScenario);
        
        // Scenario 4: Data Validation
        Map<String, Object> validationScenario = new LinkedHashMap<>();
        validationScenario.put("name", "Validation Test - Field Validation");
        validationScenario.put("description", "Test field validation rules");
        validationScenario.put("expected_result", "Validation messages shown");
        validationScenario.put("priority", "P1");
        scenarios.put("validation_test", validationScenario);
        
        return scenarios;
    }

    /**
     * Generate validation data for test assertions
     */
    private Map<String, Object> generateValidationData(Map<String, Object> testDataValues) {
        Map<String, Object> validationData = new LinkedHashMap<>();
        
        // Add validation rules for common fields
        Map<String, Object> rules = new LinkedHashMap<>();
        
        rules.put("email_format", "^[A-Za-z0-9+_.-]+@(.+)$");
        rules.put("username_min_length", 3);
        rules.put("username_max_length", 50);
        rules.put("password_min_length", 8);
        rules.put("amount_min_value", 0.01);
        rules.put("amount_max_value", 999999.99);
        rules.put("phone_format", "^\\+?[1-9]\\d{1,14}$");
        rules.put("account_number_length", 12);
        
        validationData.put("validation_rules", rules);
        
        // Add expected success message patterns
        Map<String, String> successMessages = new LinkedHashMap<>();
        successMessages.put("success", "Success|Completed|Success|Saved|Submitted");
        successMessages.put("confirmation", "Confirmation|Confirm|Confirmed");
        successMessages.put("created", "Created|Added|Created|Registered");
        
        validationData.put("success_messages", successMessages);
        
        // Add expected error message patterns
        Map<String, String> errorMessages = new LinkedHashMap<>();
        errorMessages.put("invalid_input", "Invalid|Error|Required|Missing");
        errorMessages.put("validation_error", "Validation|Invalid|Incorrect|Not valid");
        errorMessages.put("server_error", "Error|Failed|Unable|Server Error");
        
        validationData.put("error_messages", errorMessages);
        
        return validationData;
    }

    /**
     * Generate multiple test datasets (variations)
     */
    public List<Map<String, Object>> generateMultipleDatasets(String screenName, Map<String, Object> elements, int count) {
        logger.info("Generating {} test datasets for screen: {}", count, screenName);
        
        List<Map<String, Object>> datasets = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            Map<String, Object> dataset = generateTestData(screenName, elements);
            // Add dataset index
            ((Map<String, Object>) dataset.get("test_data")).put("_dataset_index", i + 1);
            datasets.add(dataset);
        }
        
        return datasets;
    }

    /**
     * Generate test data for a specific scenario
     */
    public Map<String, Object> generateScenarioData(String screenName, String scenarioName, Map<String, Object> elements) {
        logger.info("Generating test data for scenario: {} on screen: {}", scenarioName, screenName);
        
        Map<String, Object> testData = generateTestData(screenName, elements);
        testData.put("scenario", scenarioName);
        
        return testData;
    }
}
