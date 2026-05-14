package com.mashreq.automation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages framework configuration from YAML/JSON files
 * Supports environment-based configuration switching
 */
public class ConfigManager {

    private static final Logger logger = LogManager.getLogger(ConfigManager.class);
    private static ConfigManager instance;
    private Map<String, Object> config;
    private String currentEnvironment;

    private ConfigManager() {
        this.config = new HashMap<>();
    }

    /**
     * Get singleton instance of ConfigManager
     */
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    /**
     * Load configuration from YAML file
     *
     * @param environment Environment name (qa, prod, dev)
     */
    public void loadConfig(String environment) {
        try {
            this.currentEnvironment = environment;
            String configPath = FrameworkConstants.CONFIG_PATH + environment + ".yaml";
            
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            Map<String, Object> envConfig = mapper.readValue(
                new File(configPath),
                Map.class
            );
            
            this.config.putAll(envConfig);
            logger.info("Configuration loaded for environment: {}", environment);
        } catch (Exception e) {
            logger.error("Failed to load configuration", e);
            throw new RuntimeException("Configuration loading failed for: " + environment, e);
        }
    }

    /**
     * Get configuration value by key
     */
    public Object getConfig(String key) {
        Object value = config.get(key);
        if (value == null) {
            logger.warn("Configuration key not found: {}", key);
        }
        return value;
    }

    /**
     * Get string configuration value
     */
    public String getConfigString(String key, String defaultValue) {
        Object value = config.getOrDefault(key, defaultValue);
        return value != null ? value.toString() : defaultValue;
    }

    /**
     * Get integer configuration value
     */
    public int getConfigInt(String key, int defaultValue) {
        Object value = config.getOrDefault(key, defaultValue);
        if (value instanceof Integer) {
            return (Integer) value;
        }
        return defaultValue;
    }

    /**
     * Get boolean configuration value
     */
    public boolean getConfigBoolean(String key, boolean defaultValue) {
        Object value = config.getOrDefault(key, defaultValue);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return defaultValue;
    }

    /**
     * Get application URL based on environment
     */
    public String getApplicationUrl() {
        return getConfigString("application.url", "https://localhost:8080");
    }

    /**
     * Get application timeout
     */
    public int getApplicationTimeout() {
        return getConfigInt("application.timeout", FrameworkConstants.PAGE_LOAD_TIMEOUT);
    }

    /**
     * Get browser configuration
     */
    public String getBrowser() {
        return getConfigString("player.browser", FrameworkConstants.DEFAULT_BROWSER);
    }

    /**
     * Check if headless mode is enabled
     */
    public boolean isHeadless() {
        return getConfigBoolean("player.headless", FrameworkConstants.DEFAULT_HEADLESS);
    }

    /**
     * Get slow motion delay
     */
    public int getSlowMo() {
        return getConfigInt("player.slowmo", FrameworkConstants.DEFAULT_SLOWMO);
    }

    /**
     * Get database configuration
     */
    public Map<String, Object> getDatabaseConfig() {
        Map<String, Object> dbConfig = (Map<String, Object>) config.get("database");
        return dbConfig != null ? dbConfig : new HashMap<>();
    }

    /**
     * Get ADF synchronization configuration
     */
    public boolean isAdfSyncEnabled() {
        return getConfigBoolean("adf.sync.enabled", true);
    }

    /**
     * Get current environment
     */
    public String getCurrentEnvironment() {
        return currentEnvironment;
    }

    /**
     * Get all configurations
     */
    public Map<String, Object> getAllConfigs() {
        return new HashMap<>(config);
    }

    /**
     * Reset configuration (useful for testing)
     */
    public void reset() {
        config.clear();
        currentEnvironment = null;
    }
}
