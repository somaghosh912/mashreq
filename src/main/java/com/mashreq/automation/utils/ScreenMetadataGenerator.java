package com.mashreq.automation.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.mashreq.automation.metadata.ElementMetadata;
import com.mashreq.automation.metadata.ScreenMetadata;
import com.mashreq.automation.metadata.ComponentMetadata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for generating initial draft metadata and test data
 * Supports screen URL analysis and image-based element detection
 * 
 * Usage:
 *   ScreenMetadataGenerator generator = new ScreenMetadataGenerator();
 *   generator.generateFromURL("https://example.com/transfer", "TransferScreen", "Payment");
 *   generator.generateFromImage("/path/to/screen.png", "TransferScreen", "Payment");
 */
public class ScreenMetadataGenerator {

    private static final Logger logger = LogManager.getLogger(ScreenMetadataGenerator.class);
    private final ObjectMapper yamlMapper;
    private final ObjectMapper jsonMapper;
    private final AIElementDetector elementDetector;
    private final TestDataGenerator testDataGenerator;

    public ScreenMetadataGenerator() {
        YAMLFactory yamlFactory = new YAMLFactory()
            .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        this.yamlMapper = new ObjectMapper(yamlFactory);
        this.jsonMapper = new ObjectMapper();
        this.elementDetector = new AIElementDetector();
        this.testDataGenerator = new TestDataGenerator();
    }

    /**
     * Generate metadata from screen URL
     *
     * @param screenUrl The URL to analyze
     * @param screenName Screen identifier (e.g., "TransferScreen")
     * @param module Module name (e.g., "Payments")
     * @return Generated metadata map
     */
    public Map<String, Object> generateFromURL(String screenUrl, String screenName, String module) {
        logger.info("Generating metadata from URL: {} for screen: {}", screenUrl, screenName);
        
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("screen", screenName);
        metadata.put("module", module);
        metadata.put("description", "Auto-generated from URL: " + screenUrl);
        metadata.put("url_pattern", extractUrlPattern(screenUrl));
        metadata.put("load_wait_condition", "//input, //select, //button");
        metadata.put("adf_enabled", false);
        
        // Generate basic elements structure
        Map<String, Object> elements = generateBasicElements(screenUrl);
        metadata.put("elements", elements);
        
        metadata.put("components", new LinkedHashMap<>());
        metadata.put("validation_elements", new LinkedHashMap<>());
        
        logger.info("Generated {} elements for screen: {}", elements.size(), screenName);
        return metadata;
    }

    /**
     * Generate metadata from screen image using AI-based element detection
     *
     * @param imagePath Path to screen screenshot/image
     * @param screenName Screen identifier
     * @param module Module name
     * @return Generated metadata map
     */
    public Map<String, Object> generateFromImage(String imagePath, String screenName, String module) {
        logger.info("Generating metadata from image: {} for screen: {}", imagePath, screenName);
        
        try {
            // Detect elements from image using AI
            List<DetectedElement> detectedElements = elementDetector.detectElementsFromImage(imagePath);
            
            Map<String, Object> metadata = new LinkedHashMap<>();
            metadata.put("screen", screenName);
            metadata.put("module", module);
            metadata.put("description", "Auto-generated from image: " + imagePath);
            metadata.put("url_pattern", "*/" + screenName.toLowerCase() + "*");
            metadata.put("load_wait_condition", "//input, //select, //button");
            metadata.put("adf_enabled", false);
            
            // Convert detected elements to metadata format
            Map<String, Object> elements = convertDetectedElements(detectedElements);
            metadata.put("elements", elements);
            
            metadata.put("components", generateComponentsFromElements(elements));
            metadata.put("validation_elements", new LinkedHashMap<>());
            
            logger.info("Detected and generated {} elements from image", detectedElements.size());
            return metadata;
        } catch (Exception e) {
            logger.error("Failed to generate metadata from image: {}", imagePath, e);
            return generateFromURL("*/" + screenName.toLowerCase() + "*", screenName, module);
        }
    }

    /**
     * Save generated metadata to YAML file
     *
     * @param metadata Metadata map to save
     * @param screenName Screen name
     * @param outputPath Output directory path
     * @return File path of saved metadata
     */
    public String saveMetadataYAML(Map<String, Object> metadata, String screenName, String outputPath) {
        try {
            String fileName = outputPath + File.separator + screenName + ".yaml";
            
            // Create output directory if not exists
            new File(outputPath).mkdirs();
            
            // Write YAML
            FileWriter writer = new FileWriter(fileName);
            yamlMapper.writeValue(writer, metadata);
            writer.close();
            
            logger.info("Metadata saved to: {}", fileName);
            return fileName;
        } catch (IOException e) {
            logger.error("Failed to save metadata YAML", e);
            throw new RuntimeException("Failed to save metadata", e);
        }
    }

    /**
     * Save generated test data to YAML file
     *
     * @param testData Test data map to save
     * @param screenName Screen name
     * @param outputPath Output directory path
     * @return File path of saved test data
     */
    public String saveTestDataYAML(Map<String, Object> testData, String screenName, String outputPath) {
        try {
            String fileName = outputPath + File.separator + screenName.toLowerCase() + "_testdata.yaml";
            
            // Create output directory if not exists
            new File(outputPath).mkdirs();
            
            // Write YAML
            FileWriter writer = new FileWriter(fileName);
            yamlMapper.writeValue(writer, testData);
            writer.close();
            
            logger.info("Test data saved to: {}", fileName);
            return fileName;
        } catch (IOException e) {
            logger.error("Failed to save test data YAML", e);
            throw new RuntimeException("Failed to save test data", e);
        }
    }

    /**
     * Generate both metadata and test data from URL
     *
     * @param screenUrl Screen URL
     * @param screenName Screen identifier
     * @param module Module name
     * @param outputPath Output directory
     * @return Map containing paths to saved files
     */
    public Map<String, String> generateComplete(String screenUrl, String screenName, String module, String outputPath) {
        logger.info("Generating complete metadata and test data for: {}", screenName);
        
        // Generate metadata
        Map<String, Object> metadata = generateFromURL(screenUrl, screenName, module);
        String metadataPath = saveMetadataYAML(metadata, screenName, outputPath);
        
        // Generate test data
        Map<String, Object> testData = testDataGenerator.generateTestData(screenName, 
            (Map<String, Object>) metadata.get("elements"));
        String testDataPath = saveTestDataYAML(testData, screenName, outputPath);
        
        // Return results
        Map<String, String> results = new LinkedHashMap<>();
        results.put("metadata_file", metadataPath);
        results.put("testdata_file", testDataPath);
        results.put("status", "SUCCESS");
        results.put("message", "Generated metadata and test data for " + screenName);
        
        logger.info("Complete generation finished for: {}", screenName);
        return results;
    }

    /**
     * Generate test data from metadata
     *
     * @param screenName Screen identifier
     * @param elements Elements map from metadata
     * @return Generated test data map
     */
    public Map<String, Object> generateTestData(String screenName, Map<String, Object> elements) {
        return testDataGenerator.generateTestData(screenName, elements);
    }

    /**
     * Extract URL pattern from full URL
     */
    private String extractUrlPattern(String screenUrl) {
        try {
            // Extract path from URL
            Pattern pattern = Pattern.compile("https?://[^/]+(/.*)");
            Matcher matcher = pattern.matcher(screenUrl);
            if (matcher.find()) {
                String path = matcher.group(1);
                return "*" + path.replaceAll("/[^/]+$", "*"); // Convert to pattern
            }
        } catch (Exception e) {
            logger.debug("Could not extract URL pattern", e);
        }
        return "*/screen*";
    }

    /**
     * Generate basic elements from URL analysis
     */
    private Map<String, Object> generateBasicElements(String screenUrl) {
        Map<String, Object> elements = new LinkedHashMap<>();
        
        // Common elements found in most screens
        String[] commonElements = {
            "username_field:input:username",
            "password_field:input:password",
            "email_field:input:email",
            "search_field:input:search",
            "submit_button:button:submit",
            "cancel_button:button:cancel",
            "save_button:button:save",
            "delete_button:button:delete",
            "add_button:button:add",
            "edit_button:button:edit",
            "filter_dropdown:select:filter",
            "status_dropdown:select:status",
            "success_message:div:success",
            "error_message:div:error"
        };
        
        for (String element : commonElements) {
            String[] parts = element.split(":");
            String elementName = parts[0];
            String elementType = parts[1];
            String placeholder = parts[2];
            
            Map<String, Object> elementMeta = createBasicElement(elementName, elementType, placeholder);
            elements.put(elementName, elementMeta);
        }
        
        return elements;
    }

    /**
     * Create a basic element metadata structure
     */
    private Map<String, Object> createBasicElement(String name, String type, String placeholder) {
        Map<String, Object> element = new LinkedHashMap<>();
        element.put("type", type);
        element.put("timeout", type.equals("button") ? 10 : 5);
        element.put("wait_condition", type.equals("button") ? "clickable" : "VISIBLE");
        
        // Generate sample locators based on type and name
        switch (type) {
            case "input":
                element.put("id", name.replace("_field", ""));
                element.put("css", "input[name='" + name.replace("_field", "") + "']");
                element.put("xpath", "//input[@id='" + name.replace("_field", "") + "']");
                break;
            case "button":
                element.put("css", "button[class*='" + name.replace("_button", "") + "']");
                element.put("xpath", "//button[contains(text(), '" + name.replace("_", " ").toUpperCase() + "')]");
                break;
            case "select":
                element.put("css", "select[name='" + name.replace("_dropdown", "") + "']");
                element.put("xpath", "//select[@name='" + name.replace("_dropdown", "") + "']");
                break;
            case "div":
                element.put("css", "div[class*='" + name.replace("_message", "") + "-message']");
                element.put("xpath", "//div[contains(@class, '" + name.replace("_message", "") + "-message')]");
                break;
        }
        
        // Add fallback locators
        Map<String, String> fallbacks = new LinkedHashMap<>();
        fallbacks.put("text_xpath", "//label[contains(text(), '" + name.replace("_", " ") + "')]");
        element.put("fallback_locators", fallbacks);
        
        return element;
    }

    /**
     * Convert detected elements from AI to metadata format
     */
    private Map<String, Object> convertDetectedElements(List<DetectedElement> detectedElements) {
        Map<String, Object> elements = new LinkedHashMap<>();
        
        for (DetectedElement detected : detectedElements) {
            Map<String, Object> element = new LinkedHashMap<>();
            element.put("type", detected.getElementType());
            element.put("timeout", detected.getTimeout());
            element.put("wait_condition", detected.getWaitCondition());
            
            // Add detected locators
            if (detected.getXpath() != null) {
                element.put("xpath", detected.getXpath());
            }
            if (detected.getCss() != null) {
                element.put("css", detected.getCss());
            }
            if (detected.getId() != null) {
                element.put("id", detected.getId());
            }
            
            // Add fallbacks
            Map<String, String> fallbacks = new LinkedHashMap<>();
            if (detected.getAlternativeLocators() != null) {
                fallbacks.putAll(detected.getAlternativeLocators());
            }
            if (!fallbacks.isEmpty()) {
                element.put("fallback_locators", fallbacks);
            }
            
            elements.put(detected.getElementName(), element);
        }
        
        return elements;
    }

    /**
     * Generate components from elements
     */
    private Map<String, Object> generateComponentsFromElements(Map<String, Object> elements) {
        Map<String, Object> components = new LinkedHashMap<>();
        
        // Group related elements into components
        Map<String, List<String>> componentGroups = new HashMap<>();
        
        for (String elementName : elements.keySet()) {
            String componentType = extractComponentType(elementName);
            componentGroups.computeIfAbsent(componentType, k -> new ArrayList<>()).add(elementName);
        }
        
        // Create component definitions
        for (Map.Entry<String, List<String>> entry : componentGroups.entrySet()) {
            if (entry.getValue().size() > 1) {
                Map<String, Object> component = new LinkedHashMap<>();
                component.put("name", entry.getKey());
                component.put("type", "form");
                component.put("description", "Auto-generated component");
                component.put("child_elements", entry.getValue());
                components.put(entry.getKey(), component);
            }
        }
        
        return components;
    }

    /**
     * Extract component type from element name
     */
    private String extractComponentType(String elementName) {
        // Group elements by common prefixes
        if (elementName.contains("account") || elementName.contains("source")) {
            return "AccountSection";
        } else if (elementName.contains("beneficiary") || elementName.contains("recipient")) {
            return "BeneficiarySection";
        } else if (elementName.contains("amount") || elementName.contains("value")) {
            return "AmountSection";
        } else if (elementName.contains("button")) {
            return "ActionButtons";
        } else if (elementName.contains("message")) {
            return "MessageContainer";
        }
        return "FormSection";
    }

    /**
     * Inner class for detected elements
     */
    public static class DetectedElement {
        private String elementName;
        private String elementType;
        private String xpath;
        private String css;
        private String id;
        private Integer timeout = 5;
        private String waitCondition = "VISIBLE";
        private Map<String, String> alternativeLocators;

        // Getters and Setters
        public String getElementName() { return elementName; }
        public void setElementName(String elementName) { this.elementName = elementName; }

        public String getElementType() { return elementType; }
        public void setElementType(String elementType) { this.elementType = elementType; }

        public String getXpath() { return xpath; }
        public void setXpath(String xpath) { this.xpath = xpath; }

        public String getCss() { return css; }
        public void setCss(String css) { this.css = css; }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public Integer getTimeout() { return timeout; }
        public void setTimeout(Integer timeout) { this.timeout = timeout; }

        public String getWaitCondition() { return waitCondition; }
        public void setWaitCondition(String waitCondition) { this.waitCondition = waitCondition; }

        public Map<String, String> getAlternativeLocators() { return alternativeLocators; }
        public void setAlternativeLocators(Map<String, String> alternativeLocators) {
            this.alternativeLocators = alternativeLocators;
        }
    }
}
