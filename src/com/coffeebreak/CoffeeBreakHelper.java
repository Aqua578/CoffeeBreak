package com.coffeebreak;

import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.table.DefaultTableModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;




public class CoffeeBreakHelper {
    // Holds the running total for the current order
    private double totalAmount = 0.0;
    // Discount rate for Senior/PWD
    private static final double DISCOUNT_RATE = 0.20;

    // Database connection info for PostgreSQL
    private static final String DB_URL = "jdbc:postgresql://localhost:5433/Brew";
    private static final String DB_USER = "postgres";
    private static final String DB_PASS = "0000";

    // Admin authentication
    private static String ADMIN_PASSWORD_HASH = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8"; // Default: "password"
    private static final String ADMIN_PASSWORD_SALT = "CoffeeBreakSalt2025"; // Fixed salt for simplicity

    // Maps for menu subcategories and prices
    private Map<String, String[]> menuSubcategories = new HashMap<>();
    private Map<String, double[]> categoryPrices = new HashMap<>();

    // Constructor: initializes menu data and ensures sales table exists
    public CoffeeBreakHelper() {
        initializeMenuData();
        createSalesTableIfNotExists();
    }


    // Populates menu subcategories and prices for each category
    private void initializeMenuData() {
        menuSubcategories.put("Amerikano", new String[]{"Original Coffee", "Caramel", "Chocolate", "Blanca"});
        menuSubcategories.put("Boba Bliss", new String[]{"Probiotea Pearl", "Chocolate", "Cookies and Cream", "Hokkaido", "Matcha", "Salted Caramel", "Okinawa", "WinterMLN"});
        menuSubcategories.put("Fruit Tea", new String[]{"Apple Berry", "Green Apple", "Lychee", "Strawberry", "Tropical Berries"});
        menuSubcategories.put("Hot Drinks", new String[]{"Blanca", "Caramel", "Hot Choco", "Matcha"});
        menuSubcategories.put("Iced Latte", new String[]{"Probio Latte", "Caramel Macchiato", "Chocolate", "Matcha", "Matcha Blanca", "Mocha Blanca", "Salted Caramel"});
        menuSubcategories.put("Non Caffeine", new String[]{"Cookies and Cream", "Iced Choco", "Probiotea Blanca", "Probioberry"});
        menuSubcategories.put("Special Series", new String[]{"Crumbs and Cream", "Double Choco Bliss", "Midnight Mocha", "Snowy Mocha", "Spanish Latte", "Tropical Tisoy", "Red Velvet Coffee", "Red Velvet Milktea w/Creampuff", "Thai Tea Temptation", "Thai Espresso", "Nam Thai"});
        menuSubcategories.put("Siomai King", new String[]{"Chicken", "Spicy Chicken", "Pork", "Beef", "Japanese"});
        menuSubcategories.put("French Fries", new String[]{"Regular Fries", "Cheese Fries", "BBQ Fries", "Sour Cream Fries"});
        menuSubcategories.put("Croffles (Premium)", new String[]{"Nutella", "Strawberry", "Blueberry", "Ube"});
        menuSubcategories.put("Croffles (Classic)", new String[]{"Plain", "Cinnamon", "Sugar Coated"});
        menuSubcategories.put("Combo", new String[]{"Coffee + Croffle", "Coffee + Siomai", "Coffee + Fries", "Special + Croffle", "Special + Siomai"});

        categoryPrices.put("Amerikano", new double[]{29, 39});
        categoryPrices.put("Boba Bliss", new double[]{39, 49});
        categoryPrices.put("Fruit Tea", new double[]{39, 49});
        categoryPrices.put("Hot Drinks", new double[]{39, 39});
        categoryPrices.put("Iced Latte", new double[]{39, 49});
        categoryPrices.put("Non Caffeine", new double[]{39, 49});
        categoryPrices.put("Special Series", new double[]{55, 55});
        categoryPrices.put("Siomai King", new double[]{50, 75});
        categoryPrices.put("French Fries", new double[]{35, 50});
        categoryPrices.put("Croffles (Premium)", new double[]{70 ,95});
        categoryPrices.put("Croffles (Classic)", new double[]{50, 75});
        categoryPrices.put("Combo", new double[]{99, 149});
    }

    // Creates the sales table in the database if it doesn't exist
    private void createSalesTableIfNotExists() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS sales (" +
                    "id SERIAL PRIMARY KEY, " +
                    "ref VARCHAR(32), " +
                    "amount DOUBLE PRECISION, " +
                    "date TIMESTAMP, " +
                    "payment_method VARCHAR(20)" +
                    ")";
            stmt.execute(sql);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Inserts a sale record into the database
    private void insertSale(String ref, double amount, LocalDateTime date, String paymentMethod) {
        String sql = "INSERT INTO sales (ref, amount, date, payment_method) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ref);
            ps.setDouble(2, amount);
            ps.setTimestamp(3, Timestamp.valueOf(date));
            ps.setString(4, paymentMethod);
            ps.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to record sale: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Returns the current date as a formatted string
    public String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        return dateFormat.format(new java.util.Date());
    }

    // Returns the main menu button labels
    public String[] createButtonLabels() {
        return new String[] {
            "Amerikano", "Boba Bliss", "Fruit Tea", "Hot Drinks", "Iced Latte", "Non Caffeine", "Special Series", 
            " ", "Menu", "Combo", "Siomai King", "French Fries", "Croffles (Premium)", "Croffles (Classic)"
        };
    }

    // Checks if a menu item is a drink (for size options)
    private boolean isDrink(String itemName) {
        String[] drinkItems = {
            "Amerikano", "Boba Bliss", "Fruit Tea", "Hot Drinks", 
            "Iced Latte", "Non Caffeine", "Special Series"
        };
        for (String drink : drinkItems) {
            if (drink.equals(itemName)) {
                return true;
            }
        }
        return false;
    }

    // Loads the Poppins font from resources, falls back to Arial if not found
    private Font getPoppinsFont(float size, int style) {
        try {
            java.io.InputStream is = getClass().getResourceAsStream("/com/coffeebreak/resources/fonts/Poppins-Regular.ttf");
            if (is == null) return new Font("Arial", style, (int)size);
            Font font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(style, size);
            return font;
        } catch (FontFormatException | IOException e) {
            return new Font("Arial", style, (int)size);
        }
    }

    // Creates a custom black button with rounded corners and hover/press effects
    private JButton createBlackButton(String text) {
        JButton button = new JButton("<html><center>" + text + "</center></html>") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(new Color(20, 20, 20));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(40, 40, 40));
                } else {
                    g2.setColor(Color.BLACK);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Poppins", Font.PLAIN, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(80, 80));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(40, 40, 40));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.BLACK);
            }
        });
        button.setContentAreaFilled(false);
        return button;
    }

    // Creates a colored button with rounded corners and hover/press effects
    private JButton createColoredButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(color.brighter());
                } else {
                    g2.setColor(color);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(120, 50));
        return button;
    }

    // Centers a window/dialog on the screen
    private void centerWindow(Window window) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = window.getSize();
        int x = (screenSize.width - windowSize.width) / 2;
        int y = (screenSize.height - windowSize.height) / 2;
        window.setLocation(x, y);
    }

    // Shows a custom dialog for selecting an option (returns selected string)
    private String showCustomOptionDialog(String title, String[] options) {
        JDialog dialog = new JDialog((JFrame) null, title, true);

        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(Color.WHITE);
        outerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel panel = new JPanel();
        int columns = Math.min(options.length, 4);
        panel.setLayout(new GridLayout(0, columns, 10, 10));
        panel.setBackground(Color.WHITE);

        final String[] selected = {null};

        for (String option : options) {
            JButton btn = createBlackButton(option);
            btn.setPreferredSize(new Dimension(80, 80));
            btn.addActionListener(e -> {
                selected[0] = option;
                dialog.dispose();
            });
            panel.add(btn);
        }

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 12));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        outerPanel.add(titleLabel, BorderLayout.NORTH);
        outerPanel.add(panel, BorderLayout.CENTER);

        dialog.getContentPane().add(outerPanel);
        dialog.pack();
        centerWindow(dialog);
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);

        return selected[0];
    }

    // Shows a custom dialog for selecting an option (returns selected index)
    private int showCustomOptionDialogIndex(String title, String[] options, int defaultIndex) {
        JDialog dialog = new JDialog((JFrame) null, title, true);

        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(Color.WHITE);
        outerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel panel = new JPanel();
        int columns = Math.min(options.length, 4);
        panel.setLayout(new GridLayout(0, columns, 10, 10));
        panel.setBackground(Color.WHITE);

        final int[] selected = {-1};

        for (int i = 0; i < options.length; i++) {
            String option = options[i];
            JButton btn = createBlackButton(option);
            btn.setPreferredSize(new Dimension(80, 80));
            final int idx = i;
            btn.addActionListener(e -> {
                selected[0] = idx;
                dialog.dispose();
            });
            panel.add(btn);
        }

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        outerPanel.add(titleLabel, BorderLayout.NORTH);
        outerPanel.add(panel, BorderLayout.CENTER);

        dialog.getContentPane().add(outerPanel);
        dialog.pack();
        centerWindow(dialog);
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);

        return selected[0];
    }

    // Handles logic when a menu button is clicked: subcategory, size, price, and adds to order panel
    public void handleButtonClick(String buttonLabel, JPanel orderPanel, JLabel priceLabel) {
        if (buttonLabel.trim().isEmpty()) {
            return;
        }

        // Special case for Menu button - show menu panel with admin panel button
        if (buttonLabel.equals("Menu")) {
            showMenuPanel();
            return;
        }

        // Get subcategories for the selected category
        String[] subcategories = menuSubcategories.get(buttonLabel);

        if (subcategories == null || subcategories.length == 0) {
            JOptionPane.showMessageDialog(null, "No options available for " + buttonLabel, 
                                     "No Options", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Show dialog to select subcategory
        String selectedSubcategory = showCustomOptionDialog(buttonLabel, subcategories);

        if (selectedSubcategory == null) {
            return;
        }

        // Get prices for the selected category
        double[] prices = categoryPrices.get(buttonLabel);

        if (prices == null) {
            JOptionPane.showMessageDialog(null, "Price not found for " + buttonLabel, 
                                     "Price Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Determine size options based on category
        String[] sizeOptions;
        boolean isDrinkItem = isDrink(buttonLabel);

        if (buttonLabel.equals("Hot Drinks")) {
            sizeOptions = new String[]{"12oz (₱" + prices[0] + ")"};
        } else if (buttonLabel.equals("Special Series")) {
            sizeOptions = new String[]{"Venti (₱" + prices[0] + ")"};
        } else if (buttonLabel.equals("French Fries")) {
            sizeOptions = new String[]{"Mini (₱" + prices[0] + ")", "Jumbo (₱" + prices[1] + ")"};
        } else if (isDrinkItem) {
            sizeOptions = new String[]{"Petite (₱" + prices[0] + ")", "Venti (₱" + prices[1] + ")"};
        } else {
            sizeOptions = new String[]{"Mini (₱" + prices[0] + ")", "Giant (₱" + prices[1] + ")"};
        }

        // Show dialog to select size
        String selectedSizeOption = showCustomOptionDialog("Choose size for " + selectedSubcategory, sizeOptions);

        if (selectedSizeOption == null) {
            return;
        }

        // Find the index of the selected size
        int sizeChoice = -1;
        for (int i = 0; i < sizeOptions.length; i++) {
            if (sizeOptions[i].equals(selectedSizeOption)) {
                sizeChoice = i;
                break;
            }
        }
        if (sizeChoice == -1) return;

        // Determine size label and price
        String selectedSize;
        double price;

        // Separation for Hot Drinks and Special Series
        if (buttonLabel.equals("Hot Drinks")) {
            selectedSize = "12oz";
            price = prices[0];
        } else if (buttonLabel.equals("Special Series")) {
            selectedSize = "Venti";
            price = prices[0];
        } else {
            selectedSize = sizeChoice == 0 ? (isDrinkItem ? "Petite" : "Mini") : (isDrinkItem ? "Venti" : (buttonLabel.equals("French Fries") ? "Jumbo" : "Large"));
            price = prices[sizeChoice];
        }

        // Add the selected item to the order panel
        JScrollPane scrollPane = (JScrollPane) orderPanel.getComponent(1);
        JPanel itemsPanel = (JPanel) scrollPane.getViewport().getView();

        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.Y_AXIS));
        wrapperPanel.setBackground(Color.WHITE);
        wrapperPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        itemPanel.setPreferredSize(new Dimension(350, 30));
        itemPanel.setMaximumSize(new Dimension(350, 30));
        itemPanel.setBackground(Color.WHITE);

        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.setPreferredSize(new Dimension(100, 30));
        namePanel.setBackground(Color.WHITE);

        JPanel codePanel = new JPanel(new BorderLayout());
        codePanel.setPreferredSize(new Dimension(60, 30));
        codePanel.setBackground(Color.WHITE);

        JPanel qtyPanel = new JPanel(new BorderLayout());
        qtyPanel.setPreferredSize(new Dimension(65, 30));
        qtyPanel.setBackground(Color.WHITE);

        JPanel pricePanel = new JPanel(new BorderLayout());
        pricePanel.setPreferredSize(new Dimension(55, 30));
        pricePanel.setBackground(Color.WHITE);

        JLabel itemNameLabel = new JLabel(selectedSubcategory, SwingConstants.LEFT);
        JLabel itemCodeLabel = new JLabel((buttonLabel.substring(0, 3) + sizeChoice).toUpperCase(), SwingConstants.LEFT);
        JLabel itemQtyLabel = new JLabel("1", SwingConstants.CENTER);
        JLabel itemPriceLabel = new JLabel("₱" + String.format("%.2f", price), SwingConstants.RIGHT);

        Font poppins10 = getPoppinsFont(12f, Font.BOLD);
        itemNameLabel.setFont(poppins10);
        itemCodeLabel.setFont(poppins10);
        itemQtyLabel.setFont(poppins10);
        itemPriceLabel.setFont(poppins10);

        namePanel.add(itemNameLabel);
        codePanel.add(itemCodeLabel);
        qtyPanel.add(itemQtyLabel);
        pricePanel.add(itemPriceLabel);

        itemPanel.add(namePanel);
        itemPanel.add(codePanel);
        itemPanel.add(qtyPanel);
        itemPanel.add(pricePanel);

        wrapperPanel.add(itemPanel);
        itemsPanel.add(wrapperPanel);

        itemsPanel.revalidate();
        itemsPanel.repaint();

        // Update total and price label
        addToTotal(price);
        priceLabel.setText("₱" + String.format("%.2f", getTotalAmount()));
    }

    // Shows the menu panel with a black "Admin Panel" button
    private void showMenuPanel() {
        JDialog dialog = new JDialog((JFrame) null, "Menu", true);

        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(Color.WHITE);
        outerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("Menu", SwingConstants.CENTER);
        titleLabel.setFont(getPoppinsFont(12f, Font.BOLD));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        outerPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        centerPanel.setBackground(Color.WHITE);

        // Use the same format as in GUI.java for the Admin Panel button
        String[] adminLabels = {"Admin Panel"};
        for (String label : adminLabels) {
            JButton button = createBlackButton(label);
            button.setPreferredSize(new Dimension(100, 100));
            button.addActionListener(e -> {
                dialog.dispose();
                showAdminPanel();
            });
            centerPanel.add(button);
        }

        outerPanel.add(centerPanel, BorderLayout.CENTER);

        dialog.getContentPane().add(outerPanel);
        dialog.pack();
        dialog.setSize(300, dialog.getHeight());
        centerWindow(dialog);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    // Shows the admin panel with sales data from the database, after password check
    public void showAdminPanel() {
        // Show password dialog first
        JPasswordField pwd = new JPasswordField();
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(new JLabel("Enter admin password:"), BorderLayout.NORTH);
        panel.add(pwd, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
            null,
            panel,
            "Admin Access",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String password = new String(pwd.getPassword());
        if (!verifyPassword(password)) {
            JOptionPane.showMessageDialog(null, "Incorrect password.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create main dialog
        JDialog dialog = new JDialog((JFrame) null, "Admin Panel - Sales Reports", true);
        dialog.setUndecorated(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JLabel header = new JLabel("Sales Reports", SwingConstants.CENTER);
        header.setFont(getPoppinsFont(22f, Font.BOLD));
        header.setForeground(Color.BLACK);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(header, BorderLayout.NORTH);

        // Report selection panel
        JPanel reportPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        reportPanel.setBackground(Color.WHITE);

        // Date picker for reports
        JDatePickerImpl datePicker = createDatePicker();
        reportPanel.add(new JLabel("Select Date:"));
        reportPanel.add(datePicker);

        // Report type buttons
        JButton dailyButton = createColoredButton("Daily", new Color(60, 60, 60));
        JButton weeklyButton = createColoredButton("Weekly", new Color(60, 60, 60));
        JButton monthlyButton = createColoredButton("Monthly", new Color(60, 60, 60));
        JButton yearlyButton = createColoredButton("Yearly", new Color(60, 60, 60));

        reportPanel.add(dailyButton);
        reportPanel.add(weeklyButton);
        reportPanel.add(monthlyButton);
        reportPanel.add(yearlyButton);

        mainPanel.add(reportPanel, BorderLayout.NORTH);

        // Table panel
        String[] columnNames = {"Reference ID", "Amount", "Date", "Payment Method"};
        JTable table = new JTable(new Object[0][4], columnNames);
        table.setFont(getPoppinsFont(14f, Font.PLAIN));
        table.setRowHeight(28);
        table.getTableHeader().setFont(getPoppinsFont(15f, Font.BOLD));
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button actions
        dailyButton.addActionListener(e -> {
            LocalDate selectedDate = getSelectedDate(datePicker);
            table.setModel(new DefaultTableModel(
                fetchDailySalesData(selectedDate), columnNames));
        });

        weeklyButton.addActionListener(e -> {
            LocalDate selectedDate = getSelectedDate(datePicker);
            table.setModel(new DefaultTableModel(
                fetchWeeklySalesData(selectedDate), columnNames));
        });

        monthlyButton.addActionListener(e -> {
            LocalDate selectedDate = getSelectedDate(datePicker);
            table.setModel(new DefaultTableModel(
                fetchMonthlySalesData(selectedDate.getYear(), selectedDate.getMonthValue()), 
                columnNames));
        });

        yearlyButton.addActionListener(e -> {
            LocalDate selectedDate = getSelectedDate(datePicker);
            table.setModel(new DefaultTableModel(
                fetchYearlySalesData(selectedDate.getYear()), columnNames));
        });

        // Bottom button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        // Change password button
        JButton changePasswordButton = createColoredButton("Change Password", new Color(60, 60, 60));
        changePasswordButton.addActionListener(e -> showChangePasswordDialog());

        // Close button
        JButton closeButton = createColoredButton("Close", new Color(60, 60, 60));
        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(changePasswordButton);
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(mainPanel);
        dialog.pack();
        centerWindow(dialog);
        dialog.setVisible(true);
    }

    // Helper method to create a date picker
    private JDatePickerImpl createDatePicker() {
        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        return new JDatePickerImpl(datePanel, new DateLabelFormatter());
    }

    // Helper method to get selected date from picker
    private LocalDate getSelectedDate(JDatePickerImpl datePicker) {
        Date selected = (Date) datePicker.getModel().getValue();
        return selected != null ? selected.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate() : LocalDate.now();
    }

    // Date formatter for the picker
    private class DateLabelFormatter extends AbstractFormatter {
        private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        @Override
        public Object stringToValue(String text) throws ParseException {
            if (text == null || text.trim().isEmpty()) {
                return null;
            }
            return LocalDate.parse(text, dateFormatter);
        }

        @Override
        public String valueToString(Object value) throws ParseException {
            if (value == null) {
                return "";
            }
            if (value instanceof LocalDate) {
                return dateFormatter.format((LocalDate) value);
            }
            return "";
        }
    }
    // Add these methods to the CoffeeBreakHelper class

    private Object[][] fetchDailySalesData(LocalDate date) {
        List<Object[]> rows = new ArrayList<>();
        String sql = """
            SELECT ref, amount, date, payment_method 
            FROM sales 
            WHERE DATE(date) = ? 
            ORDER BY date DESC
            """;
            
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, java.sql.Date.valueOf(date));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    rows.add(createSalesRow(rs));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to fetch daily sales: " + e.getMessage(), 
                "DB Error", JOptionPane.ERROR_MESSAGE);
        }
        return rows.toArray(new Object[0][]);
    }

    private Object[][] fetchWeeklySalesData(LocalDate startDate) {
    List<Object[]> rows = new ArrayList<>();
    String sql = """
        SELECT ref, amount, date, payment_method 
        FROM sales 
        WHERE date::date >= ?::date 
        AND date::date < (?::date + INTERVAL '1 week')::date
        ORDER BY date DESC
        """;
        
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
        pstmt.setDate(1, java.sql.Date.valueOf(startDate));
        pstmt.setDate(2, java.sql.Date.valueOf(startDate));
            
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                rows.add(createSalesRow(rs));
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, 
            "Failed to fetch weekly sales: " + e.getMessage(), 
            "DB Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    return rows.toArray(new Object[0][]);
}

    private Object[][] fetchMonthlySalesData(int year, int month) {
        List<Object[]> rows = new ArrayList<>();
        String sql = """
            SELECT ref, amount, date, payment_method 
            FROM sales 
            WHERE EXTRACT(YEAR FROM date) = ? AND EXTRACT(MONTH FROM date) = ?
            ORDER BY date DESC
            """;
            
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, year);
            pstmt.setInt(2, month);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    rows.add(createSalesRow(rs));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to fetch monthly sales: " + e.getMessage(), 
                "DB Error", JOptionPane.ERROR_MESSAGE);
        }
        return rows.toArray(new Object[0][]);
    }

    private Object[][] fetchYearlySalesData(int year) {
        List<Object[]> rows = new ArrayList<>();
        String sql = """
            SELECT ref, amount, date, payment_method 
            FROM sales 
            WHERE EXTRACT(YEAR FROM date) = ?
            ORDER BY date DESC
            """;
            
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, year);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    rows.add(createSalesRow(rs));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to fetch yearly sales: " + e.getMessage(), 
                "DB Error", JOptionPane.ERROR_MESSAGE);
        }
        return rows.toArray(new Object[0][]);
    }

    // Helper method to create a row from ResultSet
    private Object[] createSalesRow(ResultSet rs) throws SQLException {
        String ref = rs.getString("ref");
        double amount = rs.getDouble("amount");
        Timestamp date = rs.getTimestamp("date");
        String paymentMethod = rs.getString("payment_method");
        
        return new Object[]{
            ref,
            String.format("₱%.2f", amount),
            date.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            paymentMethod
        };
    }

    // Fetches sales data from the database for the admin panel
    private Object[][] fetchSalesData() {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT ref, amount, date, payment_method FROM sales ORDER BY date DESC";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String ref = rs.getString("ref");
                double amount = rs.getDouble("amount");
                Timestamp date = rs.getTimestamp("date");
                String paymentMethod = rs.getString("payment_method");
                rows.add(new Object[]{
                    ref,
                    String.format("₱%.2f", amount),
                    date.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    paymentMethod
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to fetch sales data: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
        return rows.toArray(new Object[0][]);
    }

    // Handles the payment process, including discount, payment method, and validation
    public boolean processPayment(double amount, JPanel orderDetailsPanel) {
        if (amount <= 0) {
            return false;
        }

        // Apply discount if applicable
        double finalAmount = applyDiscount(amount, orderDetailsPanel);

        // Payment method selection
        String[] paymentOptions = {"Cash", "Credit Card", "GCash"};
        int paymentChoice = showCustomOptionDialogIndex("Select payment method", paymentOptions, 0);

        if (paymentChoice == -1) {
            return false;
        }

        String paymentMethod = paymentOptions[paymentChoice];
        String refNumber = generateReferenceNumber();
        LocalDateTime now = LocalDateTime.now();
        boolean paymentSuccess = false;
        double cashAmount = 0;

        // Handle each payment method
        switch (paymentChoice) {
            case 0: // Cash
                String cashInput = JOptionPane.showInputDialog(
                    null,
                    "Total Amount: ₱" + String.format("%.2f", finalAmount) + 
                    "\nEnter cash amount:",
                    "Cash Payment",
                    JOptionPane.PLAIN_MESSAGE
                );

                if (cashInput == null) return false;

                try {
                    cashAmount = Double.parseDouble(cashInput);
                    if (cashAmount < finalAmount) {
                        JOptionPane.showMessageDialog(null, 
                            "Insufficient cash amount!", 
                            "Payment Error", 
                            JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                    paymentSuccess = true;
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, 
                        "Invalid amount entered!", 
                        "Input Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                break;

            case 1: // Credit Card
                String cardNumber;
                while (true) {
                    cardNumber = JOptionPane.showInputDialog(
                        null, "Enter credit card number (16 digits):", "Credit Card Payment", JOptionPane.PLAIN_MESSAGE);
                    if (cardNumber == null) return false;
                    if (cardNumber.matches("^(4[0-9]{12}(?:[0-9]{3})?|5[1-5][0-9]{14}|3[47][0-9]{13}|6(?:011|5[0-9]{2})[0-9]{12}|[0-9]{16})$")) break;
                    JOptionPane.showMessageDialog(null, "Invalid card number!", "Payment Error", JOptionPane.ERROR_MESSAGE);
                }

                String cvv;
                while (true) {
                    cvv = JOptionPane.showInputDialog(
                        null, "Enter CVV (3 or 4 digits):", "Credit Card Payment", JOptionPane.PLAIN_MESSAGE);
                    if (cvv == null) return false;
                    if (cvv.matches("^\\d{3,4}$")) break;
                    JOptionPane.showMessageDialog(null, "Invalid CVV!", "Payment Error", JOptionPane.ERROR_MESSAGE);
                }
                paymentSuccess = true;
                break;

            case 2: // GCash
                String phoneNumber;
                while (true) {
                    phoneNumber = JOptionPane.showInputDialog(
                        null, "Enter GCash number (09XXXXXXXXX):", "GCash Payment", JOptionPane.PLAIN_MESSAGE);
                    if (phoneNumber == null) return false;
                    if (phoneNumber.matches("^09\\d{9}$")) break;
                    JOptionPane.showMessageDialog(null, "Invalid GCash number!", "Payment Error", JOptionPane.ERROR_MESSAGE);
                }

                String pin;
                while (true) {
                    pin = JOptionPane.showInputDialog(
                        null, "Please Enter OTP (4 digits):", "GCash Payment", JOptionPane.PLAIN_MESSAGE);
                    if (pin == null) return false;
                    if (pin.matches("^\\d{4}$")) break;
                    JOptionPane.showMessageDialog(null, "Invalid OTP!", "Payment Error", JOptionPane.ERROR_MESSAGE);
                }
                paymentSuccess = true;
                break;
        }

        // If payment is successful, generate and show receipt, and record sale
        if (paymentSuccess) {
            String receipt = generateReceiptWithDiscount(orderDetailsPanel, amount, finalAmount, cashAmount, refNumber);
            // Insert into database before showing receipt
            insertSale(refNumber, finalAmount, now, paymentMethod);
            showStyledReceipt(receipt);
            return true;
        }
        return false;
    }

    // Shows a styled receipt dialog with black background and white text
    public void showStyledReceipt(String receiptText) {
        JDialog dialog = new JDialog((JFrame) null, "Receipt", true);
        dialog.setUndecorated(true);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.BLACK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JLabel header = new JLabel("COFFEE BREAK", SwingConstants.CENTER);
        header.setFont(getPoppinsFont(26f, Font.BOLD));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(header, BorderLayout.NORTH);

        // Receipt area
        JPanel receiptPanel = new JPanel(new BorderLayout());
        receiptPanel.setBackground(Color.BLACK);
        receiptPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        receiptPanel.setPreferredSize(new Dimension(500, 350));

        JTextArea receiptArea = new JTextArea(receiptText);
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        receiptArea.setForeground(Color.WHITE);
        receiptArea.setBackground(Color.BLACK);
        receiptArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        receiptArea.setCaretPosition(0);

        // Center text in JTextArea (works best with monospaced font)
        receiptArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Wrap in a panel with FlowLayout to help center
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centerPanel.setBackground(Color.BLACK);
        centerPanel.add(receiptArea);

        receiptPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(receiptPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        buttonPanel.setBackground(Color.BLACK);

        JButton backButton = createColoredButton("Back", new Color(60, 60, 60));
        JButton confirmButton = createColoredButton("Confirm", Color.WHITE);
        confirmButton.setForeground(Color.BLACK);
        backButton.setFont(getPoppinsFont(16f, Font.BOLD));
        confirmButton.setFont(getPoppinsFont(16f, Font.BOLD));
        buttonPanel.add(backButton);
        buttonPanel.add(confirmButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        backButton.addActionListener(e -> dialog.dispose());
        confirmButton.addActionListener(e -> dialog.dispose());

        dialog.setContentPane(mainPanel);
        dialog.pack();
        centerWindow(dialog);
        dialog.setVisible(true);
    }

    // Applies discount if Senior/PWD is selected, with ID verification and breakdown
    public double applyDiscount(double amount, JPanel orderPanel) {
        String[] discountOptions = {"Senior Citizen", "PWD", "No Discount"};
        int discountChoice = showCustomOptionDialogIndex("Select discount type (if applicable)", discountOptions, 2);

        if (discountChoice == -1 || discountChoice == 2) {
            return amount;
        }

        while (true) {
            String idNumber = JOptionPane.showInputDialog(
                null,
                "Enter " + discountOptions[discountChoice] + " ID Number:",
                "Discount Verification",
                JOptionPane.PLAIN_MESSAGE
            );

            // If user cancels, go back to discount selection
            if (idNumber == null) {
                return applyDiscount(amount, orderPanel);
            }

            if (idNumber.trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                    null,
                    "Please enter a valid input for the " + discountOptions[discountChoice] + " ID.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE
                );
                continue;
            }

            double discount = amount * DISCOUNT_RATE;
            double discountedAmount = amount - discount;

            StringBuilder discountBreakdown = new StringBuilder();
            discountBreakdown.append("Discount Breakdown:\n\n");
            discountBreakdown.append(String.format("Original Amount: ₱%.2f\n", amount));
            discountBreakdown.append(String.format("Discount (20%%): ₱%.2f\n", discount));
            discountBreakdown.append(String.format("Final Amount: ₱%.2f", discountedAmount));

            JOptionPane.showMessageDialog(
                null,
                discountBreakdown.toString(),
                discountOptions[discountChoice] + " Discount Applied",
                JOptionPane.INFORMATION_MESSAGE
            );

            return discountedAmount;
        }
    }

    // Generates a receipt string, including discount and change if applicable
    public String generateReceiptWithDiscount(JPanel orderPanel, double originalAmount, double discountedAmount, double cashAmount, String refNumber) {
        StringBuilder receipt = new StringBuilder();
        LocalDateTime now = LocalDateTime.now();

        receipt.append("         COFFEE BREAK         \n");
        receipt.append("    Your Daily Coffee Fix     \n");
        receipt.append("====================================\n");
        receipt.append("Ref #: ").append(refNumber).append("\n");
        receipt.append("Date: ").append(now.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))).append("\n");
        receipt.append("Time: ").append(now.format(DateTimeFormatter.ofPattern("HH:mm:ss"))).append("\n");
        receipt.append("------------------------------------\n");
        receipt.append(String.format("%-20s %8s\n", "ITEM", "AMOUNT"));
        receipt.append("------------------------------------\n");

        JScrollPane scrollPane = (JScrollPane) orderPanel.getComponent(1);
        JPanel itemsPanel = (JPanel) scrollPane.getViewport().getView();

        for (java.awt.Component wrapperComp : itemsPanel.getComponents()) {
            JPanel wrapperPanel = (JPanel) wrapperComp;
            JPanel itemPanel = (JPanel) wrapperPanel.getComponent(0);

            JPanel namePanel = (JPanel) itemPanel.getComponent(0);
            JPanel pricePanel = (JPanel) itemPanel.getComponent(3);

            String itemName = ((JLabel) namePanel.getComponent(0)).getText();
            String itemPrice = ((JLabel) pricePanel.getComponent(0)).getText();

            receipt.append(String.format("%-20s %8s\n", itemName, itemPrice));
        }

        receipt.append("------------------------------------\n");
        receipt.append(String.format("%-20s %8s\n", "SUBTOTAL", "₱" + String.format("%.2f", originalAmount)));
        if (originalAmount != discountedAmount) {
            receipt.append(String.format("%-20s %8s\n", "DISCOUNT (20%)", "₱" + String.format("%.2f", originalAmount - discountedAmount)));
            receipt.append(String.format("%-20s %8s\n", "TOTAL", "₱" + String.format("%.2f", discountedAmount)));
        }
        if (cashAmount > 0) {
            receipt.append(String.format("%-20s %8s\n", "CASH", "₱" + String.format("%.2f", cashAmount)));
            receipt.append(String.format("%-20s %8s\n", "CHANGE", "₱" + String.format("%.2f", cashAmount - discountedAmount)));
        }
        receipt.append("====================================\n");
        receipt.append("         Thank You!           \n");
        receipt.append("      Please Come Again!      \n");
        receipt.append("====================================\n");

        return receipt.toString();
    }

    // Generates a unique reference number for each transaction
    private String generateReferenceNumber() {
        LocalDateTime now = LocalDateTime.now();
        Random random = new Random();
        String dateStr = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomStr = String.format("%04d", random.nextInt(10000));
        return "CB" + dateStr + randomStr;
    }

    // Hash a password using SHA-256 with salt
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String saltedPassword = password + ADMIN_PASSWORD_SALT;
            byte[] hash = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Fallback to basic security if SHA-256 is not available
            return String.valueOf((password + ADMIN_PASSWORD_SALT).hashCode());
        }
    }

    // Verify if entered password matches stored hash
    private boolean verifyPassword(String enteredPassword) {
        String enteredHash = hashPassword(enteredPassword);
        return ADMIN_PASSWORD_HASH.equals(enteredHash);
    }

    // Change admin password
    private void changeAdminPassword(String newPassword) {
        ADMIN_PASSWORD_HASH = hashPassword(newPassword);
    }

    // Shows a dialog for changing the admin password
    private void showChangePasswordDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Current password field
        JPasswordField currentPwdField = new JPasswordField(15);
        panel.add(new JLabel("Current Password:"));
        panel.add(currentPwdField);

        // New password field
        JPasswordField newPwdField = new JPasswordField(15);
        panel.add(new JLabel("New Password:"));
        panel.add(newPwdField);

        // Confirm new password field
        JPasswordField confirmPwdField = new JPasswordField(15);
        panel.add(new JLabel("Confirm New Password:"));
        panel.add(confirmPwdField);

        int result = JOptionPane.showConfirmDialog(
            null, panel, "Change Admin Password",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        // Get passwords from fields
        String currentPwd = new String(currentPwdField.getPassword());
        String newPwd = new String(newPwdField.getPassword());
        String confirmPwd = new String(confirmPwdField.getPassword());

        // Validate inputs
        if (currentPwd.isEmpty() || newPwd.isEmpty() || confirmPwd.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "All fields are required.",
                "Missing Information",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verify current password
        if (!verifyPassword(currentPwd)) {
            JOptionPane.showMessageDialog(null,
                "Current password is incorrect.",
                "Authentication Failed",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Verify passwords match
        if (!newPwd.equals(confirmPwd)) {
            JOptionPane.showMessageDialog(null,
                "New passwords don't match.",
                "Password Mismatch",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check password strength
        if (newPwd.length() < 8) {
            JOptionPane.showMessageDialog(null,
                "Password must be at least 8 characters long.",
                "Weak Password",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Change the password
        changeAdminPassword(newPwd);

        JOptionPane.showMessageDialog(null,
            "Admin password changed successfully.",
            "Password Changed",
            JOptionPane.INFORMATION_MESSAGE);
    }

    public void addToTotal(double amount) {
        this.totalAmount += amount;
    }

    // Returns the current total amount for the order
    public double getTotalAmount() {
        return totalAmount;
    }

    // Sets the total amount (used for resetting or updating)
    public void setTotalAmount(double amount) {
        this.totalAmount = amount;
    }
}