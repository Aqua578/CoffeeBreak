package com.coffeebreak.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 * Manages database configuration for the Coffee Break application.
 * Responsible for loading, saving, and providing access to database connection properties.
 */
public class DatabaseConfig {
    private static final String CONFIG_DIR = System.getProperty("user.home") + "/.coffeebreak";
    private static final String CONFIG_FILE = CONFIG_DIR + "/db.properties";
    
    // Default database configuration
    private static final String DEFAULT_DB_URL = "jdbc:postgresql://localhost:5433/Brew";
    private static final String DEFAULT_DB_USER = "postgres";
    private static final String DEFAULT_DB_PASS = "";

    private Properties properties;
    private static DatabaseConfig instance;

    /**
     * Private constructor to enforce singleton pattern.
     * Loads database configuration from properties file or creates with defaults if not found.
     */
    private DatabaseConfig() {
        properties = new Properties();
        loadConfig();
    }

    /**
     * Gets the singleton instance of DatabaseConfig.
     * @return The DatabaseConfig instance
     */
    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    /**
     * Loads configuration from properties file.
     * Creates default configuration if file doesn't exist.
     */
    private void loadConfig() {
        Path configDir = Paths.get(CONFIG_DIR);
        Path configFile = Paths.get(CONFIG_FILE);

        try {
            // Create config directory if it doesn't exist
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
            }

            // Create default config file if it doesn't exist
            if (!Files.exists(configFile)) {
                createDefaultConfig();
            }

            // Load properties from file
            try (InputStream input = new FileInputStream(CONFIG_FILE)) {
                properties.load(input);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                null,
                "Error loading database configuration: " + e.getMessage() +
                "\nUsing default settings.",
                "Configuration Error",
                JOptionPane.WARNING_MESSAGE
            );
            setDefaults();
        }
    }

    /**
     * Creates a default configuration file with default values.
     */
    private void createDefaultConfig() throws IOException {
        setDefaults();

        try (FileOutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, "Coffee Break Database Configuration");
        }
    }

    /**
     * Sets default values in the properties object.
     */
    private void setDefaults() {
        properties.setProperty("db.url", DEFAULT_DB_URL);
        properties.setProperty("db.user", DEFAULT_DB_USER);
        properties.setProperty("db.password", DEFAULT_DB_PASS);
    }

    /**
     * Gets the database URL.
     * @return The database URL
     */
    public String getDbUrl() {
        return properties.getProperty("db.url", DEFAULT_DB_URL);
    }

    /**
     * Gets the database username.
     * @return The database username
     */
    public String getDbUser() {
        return properties.getProperty("db.user", DEFAULT_DB_USER);
    }

    /**
     * Gets the database password.
     * @return The database password
     */
    public String getDbPassword() {
        return properties.getProperty("db.password", DEFAULT_DB_PASS);
    }

    /**
     * Updates database configuration properties.
     * @param url Database URL
     * @param user Database username
     * @param password Database password
     * @return true if successful, false otherwise
     */
    public boolean updateConfig(String url, String user, String password) {
        properties.setProperty("db.url", url);
        properties.setProperty("db.user", user);
        properties.setProperty("db.password", password);

        try (FileOutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, "Coffee Break Database Configuration");
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                null,
                "Error saving database configuration: " + e.getMessage(),
                "Configuration Error",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }
}