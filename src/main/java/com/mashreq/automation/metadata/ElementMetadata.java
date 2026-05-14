package com.mashreq.automation.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * Metadata model for UI elements defined in JSON/YAML
 * Represents element locator strategies, types, and synchronization conditions
 */
public class ElementMetadata {

    @JsonProperty("name")
    private String name;

    @JsonProperty("id")
    private String id;

    @JsonProperty("xpath")
    private String xpath;

    @JsonProperty("css")
    private String css;

    @JsonProperty("text")
    private String text;

    @JsonProperty("type")
    private String type;

    @JsonProperty("timeout")
    private Integer timeout;

    @JsonProperty("wait_condition")
    private String waitCondition;

    @JsonProperty("adf_component_id")
    private String adfComponentId;

    @JsonProperty("is_dynamic")
    private Boolean isDynamic;

    @JsonProperty("fallback_locators")
    private Map<String, String> fallbackLocators;

    @JsonProperty("healing_strategies")
    private Map<String, Object> healingStrategies;

    @JsonProperty("validation_rules")
    private Map<String, Object> validationRules;

    // Constructors
    public ElementMetadata() {}

    public ElementMetadata(String name, String xpath) {
        this.name = name;
        this.xpath = xpath;
        this.timeout = 30;
        this.waitCondition = "VISIBLE";
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getTimeout() {
        return timeout != null ? timeout : 30;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getWaitCondition() {
        return waitCondition != null ? waitCondition : "VISIBLE";
    }

    public void setWaitCondition(String waitCondition) {
        this.waitCondition = waitCondition;
    }

    public String getAdfComponentId() {
        return adfComponentId;
    }

    public void setAdfComponentId(String adfComponentId) {
        this.adfComponentId = adfComponentId;
    }

    public Boolean getIsDynamic() {
        return isDynamic != null ? isDynamic : false;
    }

    public void setIsDynamic(Boolean dynamic) {
        isDynamic = dynamic;
    }

    public Map<String, String> getFallbackLocators() {
        return fallbackLocators;
    }

    public void setFallbackLocators(Map<String, String> fallbackLocators) {
        this.fallbackLocators = fallbackLocators;
    }

    public Map<String, Object> getHealingStrategies() {
        return healingStrategies;
    }

    public void setHealingStrategies(Map<String, Object> healingStrategies) {
        this.healingStrategies = healingStrategies;
    }

    public Map<String, Object> getValidationRules() {
        return validationRules;
    }

    public void setValidationRules(Map<String, Object> validationRules) {
        this.validationRules = validationRules;
    }

    @Override
    public String toString() {
        return "ElementMetadata{" +
                "name='" + name + '\'' +
                ", xpath='" + xpath + '\'' +
                ", css='" + css + '\'' +
                ", type='" + type + '\'' +
                ", timeout=" + timeout +
                ", waitCondition='" + waitCondition + '\'' +
                '}';
    }
}
