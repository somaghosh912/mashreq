package com.mashreq.automation.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * Component metadata for reusable UI components
 * Defines composite elements that can be reused across multiple screens
 */
public class ComponentMetadata {

    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private String type; // e.g., "table", "form", "modal", "menu"

    @JsonProperty("description")
    private String description;

    @JsonProperty("root_selector")
    private String rootSelector;

    @JsonProperty("child_elements")
    private Map<String, ElementMetadata> childElements;

    @JsonProperty("actions")
    private Map<String, ComponentAction> actions;

    @JsonProperty("properties")
    private Map<String, Object> properties;

    // Constructors
    public ComponentMetadata() {}

    public ComponentMetadata(String name, String type) {
        this.name = name;
        this.type = type;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRootSelector() {
        return rootSelector;
    }

    public void setRootSelector(String rootSelector) {
        this.rootSelector = rootSelector;
    }

    public Map<String, ElementMetadata> getChildElements() {
        return childElements;
    }

    public void setChildElements(Map<String, ElementMetadata> childElements) {
        this.childElements = childElements;
    }

    public Map<String, ComponentAction> getActions() {
        return actions;
    }

    public void setActions(Map<String, ComponentAction> actions) {
        this.actions = actions;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    /**
     * Component action definition
     */
    public static class ComponentAction {
        @JsonProperty("name")
        public String name;

        @JsonProperty("description")
        public String description;

        @JsonProperty("action_type")
        public String actionType; // e.g., "click", "type", "select"

        @JsonProperty("parameters")
        public Map<String, String> parameters;

        public ComponentAction() {}

        public ComponentAction(String name, String actionType) {
            this.name = name;
            this.actionType = actionType;
        }
    }

    @Override
    public String toString() {
        return "ComponentMetadata{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
