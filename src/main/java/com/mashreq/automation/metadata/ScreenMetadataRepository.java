package com.mashreq.automation.metadata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.mashreq.automation.config.FrameworkConstants;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Repository for loading and managing screen metadata
 * Central registry of all element locators and definitions
 */
public class ScreenMetadataRepository {

    private static final Logger logger = LogManager.getLogger(ScreenMetadataRepository.class);
    private static ScreenMetadataRepository instance;
    private Map<String, ScreenMetadata> screenRegistry;
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    private ScreenMetadataRepository() {
        this.screenRegistry = new HashMap<>();
    }

    /**
     * Get singleton instance
     */
    public static synchronized ScreenMetadataRepository getInstance() {
        if (instance == null) {
            instance = new ScreenMetadataRepository();
        }
        return instance;
    }

    /**
     * Load screen metadata from YAML file
     *
     * @param screenName Screen identifier (e.g., "LoginScreen")
     * @return ScreenMetadata object
     */
    public ScreenMetadata loadScreenMetadata(String screenName) {
        try {
            // Check if already loaded
            if (screenRegistry.containsKey(screenName)) {
                logger.debug("Screen metadata already loaded: {}", screenName);
                return screenRegistry.get(screenName);
            }

            // Construct file path
            String filePath = FrameworkConstants.METADATA_PATH + screenName + ".yaml";
            File metadataFile = new File(filePath);

            if (!metadataFile.exists()) {
                logger.error("Metadata file not found: {}", filePath);
                throw new RuntimeException("Metadata not found for screen: " + screenName);
            }

            // Parse YAML
            ScreenMetadata screenMetadata = yamlMapper.readValue(metadataFile, ScreenMetadata.class);
            screenRegistry.put(screenName, screenMetadata);

            logger.info("Screen metadata loaded successfully: {} (Elements: {})",
                    screenName,
                    screenMetadata.getElements() != null ? screenMetadata.getElements().size() : 0);

            return screenMetadata;
        } catch (Exception e) {
            logger.error("Failed to load screen metadata: {}", screenName, e);
            throw new RuntimeException("Metadata loading failed for: " + screenName, e);
        }
    }

    /**
     * Get element metadata by screen and element name
     *
     * @param screenName Screen identifier
     * @param elementName Element identifier
     * @return ElementMetadata object
     */
    public ElementMetadata getElementMetadata(String screenName, String elementName) {
        ScreenMetadata screenMetadata = loadScreenMetadata(screenName);
        ElementMetadata elementMetadata = screenMetadata.getElement(elementName);

        if (elementMetadata == null) {
            logger.warn("Element not found in metadata: {}.{}", screenName, elementName);
            return null;
        }

        return elementMetadata;
    }

    /**
     * Get component metadata by screen and component name
     *
     * @param screenName Screen identifier
     * @param componentName Component identifier
     * @return ComponentMetadata object
     */
    public ComponentMetadata getComponentMetadata(String screenName, String componentName) {
        ScreenMetadata screenMetadata = loadScreenMetadata(screenName);
        Map<String, ComponentMetadata> components = screenMetadata.getComponents();

        if (components == null) {
            logger.warn("No components defined for screen: {}", screenName);
            return null;
        }

        return components.get(componentName);
    }

    /**
     * Get all screens in registry
     */
    public Map<String, ScreenMetadata> getAllScreens() {
        return new HashMap<>(screenRegistry);
    }

    /**
     * Check if screen metadata exists
     */
    public boolean screenExists(String screenName) {
        return screenRegistry.containsKey(screenName) ||
                new File(FrameworkConstants.METADATA_PATH + screenName + ".yaml").exists();
    }

    /**
     * Clear registry (useful for testing)
     */
    public void clearRegistry() {
        screenRegistry.clear();
        logger.info("Screen metadata registry cleared");
    }

    /**
     * Get all primary locators for a screen
     */
    public Map<String, String> getPrimaryLocators(String screenName) {
        ScreenMetadata screenMetadata = loadScreenMetadata(screenName);
        Map<String, String> locators = new HashMap<>();

        if (screenMetadata.getElements() != null) {
            screenMetadata.getElements().forEach((key, element) -> {
                String primaryLocator = element.getXpath() != null ? element.getXpath() : element.getCss();
                if (primaryLocator != null) {
                    locators.put(key, primaryLocator);
                }
            });
        }

        return locators;
    }
}
