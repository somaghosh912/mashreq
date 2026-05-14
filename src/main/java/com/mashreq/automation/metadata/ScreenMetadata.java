package com.mashreq.automation.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * Screen metadata container for entire page/screen definitions
 * Aggregates all element metadata for a screen
 */
public class ScreenMetadata {

    @JsonProperty("screen")
    private String screenName;

    @JsonProperty("module")
    private String module;

    @JsonProperty("description")
    private String description;

    @JsonProperty("url_pattern")
    private String urlPattern;

    @JsonProperty("load_wait_condition")
    private String loadWaitCondition;

    @JsonProperty("adf_enabled")
    private Boolean adfEnabled;

    @JsonProperty("elements")
    private Map<String, ElementMetadata> elements;

    @JsonProperty("components")
    private Map<String, ComponentMetadata> components;

    @JsonProperty("validation_elements")
    private Map<String, String> validationElements;

    // Constructors
    public ScreenMetadata() {}

    public ScreenMetadata(String screenName, String module) {
        this.screenName = screenName;
        this.module = module;
        this.adfEnabled = false;
    }

    // Getters and Setters
    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public String getLoadWaitCondition() {
        return loadWaitCondition;
    }

    public void setLoadWaitCondition(String loadWaitCondition) {
        this.loadWaitCondition = loadWaitCondition;
    }

    public Boolean getAdfEnabled() {
        return adfEnabled != null ? adfEnabled : false;
    }

    public void setAdfEnabled(Boolean adfEnabled) {
        this.adfEnabled = adfEnabled;
    }

    public Map<String, ElementMetadata> getElements() {
        return elements;
    }

    public void setElements(Map<String, ElementMetadata> elements) {
        this.elements = elements;
    }

    public ElementMetadata getElement(String elementName) {
        return elements != null ? elements.get(elementName) : null;
    }

    public Map<String, ComponentMetadata> getComponents() {
        return components;
    }

    public void setComponents(Map<String, ComponentMetadata> components) {
        this.components = components;
    }

    public Map<String, String> getValidationElements() {
        return validationElements;
    }

    public void setValidationElements(Map<String, String> validationElements) {
        this.validationElements = validationElements;
    }

    @Override
    public String toString() {
        return "ScreenMetadata{" +
                "screenName='" + screenName + '\'' +
                ", module='" + module + '\'' +
                ", adfEnabled=" + adfEnabled +
                ", elementCount=" + (elements != null ? elements.size() : 0) +
                '}';
    }
}
