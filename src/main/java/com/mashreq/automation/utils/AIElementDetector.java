package com.mashreq.automation.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

/**
 * AI-based element detector for identifying UI elements from screenshots
 * Supports detection of common web elements: buttons, inputs, dropdowns, etc.
 */
public class AIElementDetector {

    private static final Logger logger = LogManager.getLogger(AIElementDetector.class);
    
    // Element type patterns
    private static final Map<String, Pattern> ELEMENT_PATTERNS = new HashMap<>();
    
    static {
        // Initialize common element patterns
        ELEMENT_PATTERNS.put("button", Pattern.compile("(?i)(button|submit|save|cancel|delete|add|edit|click)"));
        ELEMENT_PATTERNS.put("input", Pattern.compile("(?i)(input|field|text|password|email|search|username)"));
        ELEMENT_PATTERNS.put("select", Pattern.compile("(?i)(dropdown|select|option|choice|filter)"));
        ELEMENT_PATTERNS.put("checkbox", Pattern.compile("(?i)(checkbox|check|tick|agree)"));
        ELEMENT_PATTERNS.put("radio", Pattern.compile("(?i)(radio|option|choice)"));
        ELEMENT_PATTERNS.put("label", Pattern.compile("(?i)(label|title|heading|name)"));
        ELEMENT_PATTERNS.put("link", Pattern.compile("(?i)(link|href|navigation)"));
        ELEMENT_PATTERNS.put("table", Pattern.compile("(?i)(table|grid|list|data)"));
    }

    /**
     * Detect UI elements from an image file
     * 
     * @param imagePath Path to the image file
     * @return List of detected elements with their properties
     */
    public List<ScreenMetadataGenerator.DetectedElement> detectElementsFromImage(String imagePath) {
        List<ScreenMetadataGenerator.DetectedElement> detectedElements = new ArrayList<>();
        
        logger.info("Starting element detection from image: {}", imagePath);
        
        // Validate image file exists
        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            logger.warn("Image file not found: {}", imagePath);
            return detectedElements;
        }
        
        try {
            // Detect common form elements based on typical screen structures
            detectedElements.addAll(detectInputElements());
            detectedElements.addAll(detectButtonElements());
            detectedElements.addAll(detectSelectElements());
            detectedElements.addAll(detectMessageElements());
            
            logger.info("Detected {} elements from image", detectedElements.size());
            return detectedElements;
            
        } catch (Exception e) {
            logger.error("Error detecting elements from image: {}", imagePath, e);
            return detectedElements;
        }
    }

    /**
     * Detect input field elements
     */
    private List<ScreenMetadataGenerator.DetectedElement> detectInputElements() {
        List<ScreenMetadataGenerator.DetectedElement> elements = new ArrayList<>();
        
        String[] inputFields = {
            "username_field", "email_field", "password_field", 
            "search_field", "account_number", "reference_number"
        };
        
        for (String fieldName : inputFields) {
            ScreenMetadataGenerator.DetectedElement element = new ScreenMetadataGenerator.DetectedElement();
            element.setElementName(fieldName);
            element.setElementType("input");
            element.setTimeout(5);
            element.setWaitCondition("VISIBLE");
            
            // Generate XPath and CSS selectors
            String baseId = fieldName.replace("_field", "");
            element.setId(baseId);
            element.setXpath("//input[@id='" + baseId + "' or @name='" + baseId + "']");
            element.setCss("input[name='" + baseId + "'], input#" + baseId);
            
            // Add alternative locators
            Map<String, String> fallbacks = new LinkedHashMap<>();
            fallbacks.put("placeholder_xpath", "//input[@placeholder='" + formatLabel(fieldName) + "']");
            fallbacks.put("label_xpath", "//label[contains(text(), '" + formatLabel(fieldName) + "')]/following-sibling::input");
            element.setAlternativeLocators(fallbacks);
            
            elements.add(element);
        }
        
        return elements;
    }

    /**
     * Detect button elements
     */
    private List<ScreenMetadataGenerator.DetectedElement> detectButtonElements() {
        List<ScreenMetadataGenerator.DetectedElement> elements = new ArrayList<>();
        
        String[] buttons = {
            "submit_button", "cancel_button", "save_button", 
            "delete_button", "add_button", "edit_button", "reset_button"
        };
        
        for (String buttonName : buttons) {
            ScreenMetadataGenerator.DetectedElement element = new ScreenMetadataGenerator.DetectedElement();
            element.setElementName(buttonName);
            element.setElementType("button");
            element.setTimeout(10);
            element.setWaitCondition("clickable");
            
            String action = buttonName.replace("_button", "");
            element.setXpath("//button[contains(text(), '" + formatLabel(action) + "')] | //input[@type='button' and @value='" + formatLabel(action) + "']");
            element.setCss("button[class*='" + action + "'], input[type='button'][value='" + formatLabel(action) + "']");
            
            // Add alternative locators
            Map<String, String> fallbacks = new LinkedHashMap<>();
            fallbacks.put("class_xpath", "//button[contains(@class, '" + action + "')]");
            fallbacks.put("type_xpath", "//input[@type='submit' or @type='button']");
            element.setAlternativeLocators(fallbacks);
            
            elements.add(element);
        }
        
        return elements;
    }

    /**
     * Detect dropdown/select elements
     */
    private List<ScreenMetadataGenerator.DetectedElement> detectSelectElements() {
        List<ScreenMetadataGenerator.DetectedElement> elements = new ArrayList<>();
        
        String[] selects = {
            "status_dropdown", "filter_dropdown", "category_dropdown", 
            "country_dropdown", "state_dropdown", "type_dropdown"
        };
        
        for (String selectName : selects) {
            ScreenMetadataGenerator.DetectedElement element = new ScreenMetadataGenerator.DetectedElement();
            element.setElementName(selectName);
            element.setElementType("select");
            element.setTimeout(5);
            element.setWaitCondition("VISIBLE");
            
            String baseId = selectName.replace("_dropdown", "");
            element.setId(baseId);
            element.setXpath("//select[@id='" + baseId + "' or @name='" + baseId + "']");
            element.setCss("select[name='" + baseId + "'], select#" + baseId);
            
            // Add alternative locators
            Map<String, String> fallbacks = new LinkedHashMap<>();
            fallbacks.put("label_xpath", "//label[contains(text(), '" + formatLabel(selectName) + "')]/following-sibling::select");
            fallbacks.put("parent_div_xpath", "//div[contains(@class, '" + baseId + "')]//select");
            element.setAlternativeLocators(fallbacks);
            
            elements.add(element);
        }
        
        return elements;
    }

    /**
     * Detect message/notification elements
     */
    private List<ScreenMetadataGenerator.DetectedElement> detectMessageElements() {
        List<ScreenMetadataGenerator.DetectedElement> elements = new ArrayList<>();
        
        String[] messages = {"success_message", "error_message", "warning_message", "info_message"};
        
        for (String messageName : messages) {
            ScreenMetadataGenerator.DetectedElement element = new ScreenMetadataGenerator.DetectedElement();
            element.setElementName(messageName);
            element.setElementType("div");
            element.setTimeout(3);
            element.setWaitCondition("VISIBLE");
            
            String type = messageName.replace("_message", "");
            element.setXpath("//div[contains(@class, '" + type + "')]");
            element.setCss("div[class*='" + type + "-message'], div[class*='" + type + "']");
            
            // Add alternative locators
            Map<String, String> fallbacks = new LinkedHashMap<>();
            fallbacks.put("alert_xpath", "//div[@role='alert' or @role='status']");
            fallbacks.put("toast_xpath", "//div[contains(@class, 'toast') or contains(@class, 'notification')]");
            element.setAlternativeLocators(fallbacks);
            
            elements.add(element);
        }
        
        return elements;
    }

    /**
     * Format field name to human readable label
     * e.g., "username_field" -> "Username"
     */
    private String formatLabel(String fieldName) {
        return Arrays.stream(fieldName.split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .reduce((a, b) -> a + " " + b)
                .orElse("");
    }

    /**
     * Detect element type based on element characteristics
     */
    public String detectElementType(String elementContent, String elementClass, String elementId) {
        // Check class and id attributes
        String combined = (elementClass + " " + elementId).toLowerCase();
        
        for (Map.Entry<String, Pattern> entry : ELEMENT_PATTERNS.entrySet()) {
            if (entry.getValue().matcher(combined).find()) {
                return entry.getKey();
            }
        }
        
        // Default to div if no pattern matches
        return "div";
    }

    /**
     * Generate XPath for an element based on its properties
     */
    public String generateXPath(String elementId, String elementClass, String elementType) {
        StringBuilder xpath = new StringBuilder();
        xpath.append("//").append(elementType.toLowerCase());
        
        if (elementId != null && !elementId.isEmpty()) {
            xpath.append("[@id='").append(elementId).append("']");
        } else if (elementClass != null && !elementClass.isEmpty()) {
            xpath.append("[contains(@class, '").append(elementClass).append("')]");
        }
        
        return xpath.toString();
    }

    /**
     * Generate CSS selector for an element
     */
    public String generateCSS(String elementId, String elementClass, String elementType) {
        if (elementId != null && !elementId.isEmpty()) {
            return elementType.toLowerCase() + "#" + elementId;
        } else if (elementClass != null && !elementClass.isEmpty()) {
            return elementType.toLowerCase() + "." + elementClass;
        }
        return elementType.toLowerCase();
    }
}
