package com.mashreq.automation.config;

/**
 * Framework-wide constants for FLEXCUBE enterprise automation
 * Centralized configuration for timeouts, paths, and system properties
 */
public class FrameworkConstants {

    // ============= TIMEOUTS =============
    public static final int EXPLICIT_WAIT = 30;
    public static final int IMPLICIT_WAIT = 10;
    public static final int FLUENT_WAIT = 5;
    public static final int PAGE_LOAD_TIMEOUT = 60;
    public static final int TRANSACTION_TIMEOUT = 300;

    // ============= PATHS =============
    public static final String RESOURCES_PATH = "src/main/resources/";
    public static final String CONFIG_PATH = RESOURCES_PATH + "config/";
    public static final String METADATA_PATH = RESOURCES_PATH + "metadata/";
    public static final String TESTDATA_PATH = RESOURCES_PATH + "testdata/";
    public static final String SCREENSHOTS_PATH = "reports/screenshots/";
    public static final String VIDEOS_PATH = "reports/videos/";
    public static final String EXTENT_REPORT_PATH = "reports/extent-reports/";
    public static final String LOGS_PATH = "logs/";

    // ============= BROWSER CONFIGURATIONS =============
    public static final String BROWSER_CHROMIUM = "chromium";
    public static final String BROWSER_FIREFOX = "firefox";
    public static final String BROWSER_WEBKIT = "webkit";
    public static final String BROWSER_EDGE = "edge";

    // ============= VIEWPORT SIZES =============
    public static final int VIEWPORT_WIDTH = 1920;
    public static final int VIEWPORT_HEIGHT = 1080;

    // ============= RETRY CONFIGURATION =============
    public static final int MAX_RETRY_ATTEMPTS = 3;
    public static final int RETRY_TIMEOUT_MILLIS = 2000;

    // ============= LOGGING LEVELS =============
    public static final String LOG_LEVEL_INFO = "INFO";
    public static final String LOG_LEVEL_DEBUG = "DEBUG";
    public static final String LOG_LEVEL_ERROR = "ERROR";

    // ============= WORKFLOW CONSTANTS =============
    public static final String WORKFLOW_CONTEXT_KEY = "WORKFLOW_CONTEXT";
    public static final String TRANSACTION_CONTEXT_KEY = "TRANSACTION_CONTEXT";
    public static final String USER_CONTEXT_KEY = "USER_CONTEXT";
    public static final String ROLE_MAKER = "ROLE_MAKER";
    public static final String ROLE_CHECKER = "ROLE_CHECKER";
    public static final String ROLE_AUTHORIZER = "ROLE_AUTHORIZER";

    // ============= ADF SYNCHRONIZATION =============
    public static final int ADF_SYNC_TIMEOUT = 15;
    public static final String ADF_FORM_READY_CONDITION = "return oracle.Forms.isConnected();";
    public static final String ADF_LOADING_MASK = "(//div[@id='processbar'])[1]";

    // ============= ELEMENT TYPES =============
    public enum ElementType {
        INPUT, BUTTON, DROPDOWN, CHECKBOX, RADIO, TEXTAREA, LINK, TABLE, FORM
    }

    // ============= WAIT CONDITIONS =============
    public enum WaitCondition {
        VISIBLE, CLICKABLE, PRESENT, INVISIBLE, STALENESS, ATTRIBUTE_CHANGE
    }

    // ============= TEST PARAMETERS =============
    public static final String PARAM_ENV = "env";
    public static final String PARAM_BROWSER = "browser";
    public static final String PARAM_HEADLESS = "headless";
    public static final String PARAM_DEBUG = "debug";
    public static final String PARAM_SLOWMO = "slowmo";
    public static final String PARAM_SCREENSHOT = "screenshot";
    public static final String PARAM_VIDEO = "video";

    // ============= DEFAULT VALUES =============
    public static final String DEFAULT_ENV = "qa";
    public static final String DEFAULT_BROWSER = BROWSER_CHROMIUM;
    public static final boolean DEFAULT_HEADLESS = true;
    public static final int DEFAULT_SLOWMO = 0;
    public static final boolean DEFAULT_SCREENSHOT = true;
    public static final boolean DEFAULT_VIDEO = false;

    // ============= REGEX PATTERNS =============
    public static final String PHONE_REGEX = "^[+]?[0-9]{10,}$";
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    public static final String AMOUNT_REGEX = "^[0-9]{1,20}([.,][0-9]{1,2})?$";
    public static final String ACCOUNT_REGEX = "^[0-9]{10,20}$";
}
