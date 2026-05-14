package com.mashreq.automation.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.HashMap;
import java.util.Map;

/**
 * Runtime data store for maintaining dynamic values during test execution
 * Thread-safe storage for test-specific data
 */
public class RuntimeDataStore {

    private static final Logger logger = LogManager.getLogger(RuntimeDataStore.class);
    private static final ThreadLocal<Map<String, Object>> dataStore = ThreadLocal.withInitial(HashMap::new);

    /**
     * Store data
     */
    public static void store(String key, Object value) {
        dataStore.get().put(key, value);
        logger.debug("Data stored: {} = {}", key, value);
    }

    /**
     * Retrieve data
     */
    public static Object retrieve(String key) {
        return dataStore.get().get(key);
    }

    /**
     * Retrieve as string
     */
    public static String retrieveAsString(String key) {
        Object value = dataStore.get().get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * Check if key exists
     */
    public static boolean exists(String key) {
        return dataStore.get().containsKey(key);
    }

    /**
     * Clear all data
     */
    public static void clear() {
        dataStore.get().clear();
        logger.debug("Runtime data store cleared");
    }

    /**
     * Get all data
     */
    public static Map<String, Object> getAll() {
        return new HashMap<>(dataStore.get());
    }

    /**
     * Remove key
     */
    public static void remove(String key) {
        dataStore.get().remove(key);
        logger.debug("Data removed: {}", key);
    }
}
