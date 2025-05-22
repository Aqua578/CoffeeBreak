package com.coffeebreak.config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Dialog for editing database configuration settings.
 * Allows users to modify and test database connection parameters.
 */
public class DatabaseConfigDialog extends JDialog {
    private JTextField urlField;
    private JTextField userField;
    private JPasswordField passwordField;
    private boolean confirmed = false;

    /**
     * Creates a new database configuration dialog.
     * @param parent The parent frame
     */
    public DatabaseConfigDialog(Frame parent) {
        super(parent, "Database Configuration", true);
        initComponents();
        loadCurrentConfig();
        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * Initializes dialog components.
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // URL field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Database URL:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        urlField = new JTextField(30);
        formPanel.add(urlField, gbc);
        
        // Username field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        userField = new JTextField(15);
        formPanel.add(userField, gbc);
        
        // Password field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(15);
        formPanel.add(passwordField, gbc);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton testButton = new JButton("Test Connection");
        testButton.addActionListener(this::testConnection);
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(testButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add panels to dialog
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Loads current configuration values into form fields.
     */
    private void loadCurrentConfig() {
        DatabaseConfig config = DatabaseConfig.getInstance();
        urlField.setText(config.getDbUrl());
        userField.setText(config.getDbUser());
        passwordField.setText(config.getDbPassword());
    }

    /**
     * Tests the database connection with the current field values.
     * @param e The action event
     */
    private void testConnection(ActionEvent e) {
        String url = urlField.getText().trim();
        String user = userField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            JOptionPane.showMessageDialog(
                this,
                "Connection successful!",
                "Test Result",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Connection failed: " + ex.getMessage(),
                "Test Result",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Returns whether the dialog was confirmed.
     * @return true if confirmed, false if cancelled
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * Gets the entered database URL.
     * @return The database URL
     */
    public String getDbUrl() {
        return urlField.getText().trim();
    }

    /**
     * Gets the entered database username.
     * @return The database username
     */
    public String getDbUser() {
        return userField.getText().trim();
    }

    /**
     * Gets the entered database password.
     * @return The database password
     */
    public String getDbPassword() {
        return new String(passwordField.getPassword());
    }
}