package com.mashreq.automation.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages runtime environment variables and system properties
 * Provides overrides for configuration values via system properties or environment variables
 */
public class EnvironmentManager {

    private static final Logger logger = LogManager.getLogger(EnvironmentManager.class);
    private static EnvironmentManager instance;
    private Map<String, String> environmentVariables;

    private EnvironmentManager() {
        this.environmentVariables = new HashMap<>(System.getenv());
    }

    /**
     * Get singleton instance
     */
    public static synchronized EnvironmentManager getInstance() {
        if (instance == null) {
            instance = new EnvironmentManager();
        }
        return instance;
    }

    /**
     * Get environment value with priority:
     * 1. System property
     * 2. Environment variable
     * 3. Default value
     */
    public String getEnvironmentValue(String key, String defaultValue) {
        // Check system property first
        String systemValue = System.getProperty(key);
        if (systemValue != null) {
            logger.debug("Using system property for {}: {}", key, systemValue);
            return systemValue;
        }

        // Check environment variable
        String envValue = environmentVariables.get(key);
        if (envValue != null) {
            logger.debug("Using environment variable for {}: {}", key, envValue);
            return envValue;
        }

        logger.debug("Using default value for {}: {}", key, defaultValue);
        return defaultValue;
    }

    /**
     * Get environment or default
     */
    public String getEnv(String key) {
        return getEnvironmentValue(key, "");
    }

    /**
     * Set system property
     */
    public void setSystemProperty(String key, String value) {
        System.setProperty(key, value);
        logger.debug("System property set: {} = {}", key, value);
    }

    /**
     * Get environment variable
     */
    public String getEnvironmentVariable(String key) {
        return environmentVariables.get(key);
    }

    /**
     * Check if running in CI/CD environment
     */
    public boolean isCIEnvironment() {
        return System.getenv("CI") != null
                || System.getenv("JENKINS_URL") != null
                || System.getenv("GITHUB_ACTIONS") != null
                || System.getenv("GITLAB_CI") != null;
    }

    /**
     * Get OS name
     */
    public String getOSName() {
        return System.getProperty("os.name");
    }

    /**
     * Check if running on Windows
     */
    public boolean isWindows() {
        return getOSName().toLowerCase().contains("windows");
    }

    /**
     * Check if running on Mac
     */
    public boolean isMac() {
        return getOSName().toLowerCase().contains("mac");
    }

    /**
     * Check if running on Linux
     */
    public boolean isLinux() {
        return getOSName().toLowerCase().contains("linux");
    }

    /**
     * Get Java version
     */
    public String getJavaVersion() {
        return System.getProperty("java.version");
    }

    /**
     * Get user home directory
     */
    public String getUserHome() {
        return System.getProperty("user.home");
    }
}
