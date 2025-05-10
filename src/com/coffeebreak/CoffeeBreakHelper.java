package com.coffeebreak;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CoffeeBreakHelper {
    private double totalAmount = 0.0;
    private static final double DISCOUNT_RATE = 0.20;

    // Database connection info
    private static final String DB_URL = "jdbc:postgresql://localhost:5433/Brew";
    private static final String DB_USER = "postgres";
    private static final String DB_PASS = "0000";

    private Map<String, String[]> menuSubcategories = new HashMap<>();
    private Map<String, double[]> categoryPrices = new HashMap<>();

    public CoffeeBreakHelper() {
        initializeMenuData();
        createSalesTableIfNotExists();
    }

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

    public String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        return dateFormat.format(new java.util.Date());
    }

    public String[] createButtonLabels() {
        return new String[] {
            "Amerikano", "Boba Bliss", "Fruit Tea", "Hot Drinks", "Iced Latte", "Non Caffeine", "Special Series", 
            " ", "Menu", "Combo", "Siomai King", "French Fries", "Croffles (Premium)", "Croffles (Classic)"
        };
    }

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

    // Center any window/dialog on screen
    private void centerWindow(Window window) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = window.getSize();
        int x = (screenSize.width - windowSize.width) / 2;
        int y = (screenSize.height - windowSize.height) / 2;
        window.setLocation(x, y);
    }

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

        JButton closeBtn = createColoredButton("X", new Color(255, 75, 75));
        closeBtn.setPreferredSize(new Dimension(50, 30));
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        closePanel.setBackground(Color.WHITE);
        closePanel.add(closeBtn);

        outerPanel.add(titleLabel, BorderLayout.NORTH);
        outerPanel.add(panel, BorderLayout.CENTER);
        outerPanel.add(closePanel, BorderLayout.SOUTH);

        dialog.getContentPane().add(outerPanel);
        dialog.pack();
        centerWindow(dialog);
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);

        return selected[0];
    }

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

        JButton closeBtn = createColoredButton("X", new Color(255, 75, 75));
        closeBtn.setPreferredSize(new Dimension(50, 30));
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        closePanel.setBackground(Color.WHITE);
        closePanel.add(closeBtn);

        outerPanel.add(titleLabel, BorderLayout.NORTH);
        outerPanel.add(panel, BorderLayout.CENTER);
        outerPanel.add(closePanel, BorderLayout.SOUTH);

        dialog.getContentPane().add(outerPanel);
        dialog.pack();
        centerWindow(dialog);
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);

        return selected[0];
    }

    public void handleButtonClick(String buttonLabel, JPanel orderPanel, JLabel priceLabel) {
        if (buttonLabel.trim().isEmpty()) {
            return;
        }

        if (buttonLabel.equals("Menu")) {
            JOptionPane.showMessageDialog(null, "Menu options will be displayed here", 
                                     "Menu", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] subcategories = menuSubcategories.get(buttonLabel);

        if (subcategories == null || subcategories.length == 0) {
            JOptionPane.showMessageDialog(null, "No options available for " + buttonLabel, 
                                     "No Options", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String selectedSubcategory = showCustomOptionDialog(buttonLabel, subcategories);

        if (selectedSubcategory == null) {
            return;
        }

        double[] prices = categoryPrices.get(buttonLabel);

        if (prices == null) {
            JOptionPane.showMessageDialog(null, "Price not found for " + buttonLabel, 
                                     "Price Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

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

        String selectedSizeOption = showCustomOptionDialog("Choose size for " + selectedSubcategory, sizeOptions);

        if (selectedSizeOption == null) {
            return;
        }

        int sizeChoice = -1;
        for (int i = 0; i < sizeOptions.length; i++) {
            if (sizeOptions[i].equals(selectedSizeOption)) {
                sizeChoice = i;
                break;
            }
        }
        if (sizeChoice == -1) return;

        String selectedSize;
        double price;

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

        addToTotal(price);
        priceLabel.setText("₱" + String.format("%.2f", getTotalAmount()));
    }

    public boolean processPayment(double amount, JPanel orderDetailsPanel) {
        if (amount <= 0) {
            return false;
        }

        double finalAmount = applyDiscount(amount, orderDetailsPanel);

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
                        null, "Please Enter OTP (6 digits):", "GCash Payment", JOptionPane.PLAIN_MESSAGE);
                    if (pin == null) return false;
                    if (pin.matches("^\\d{4}$")) break;
                    JOptionPane.showMessageDialog(null, "Invalid OTP!", "Payment Error", JOptionPane.ERROR_MESSAGE);
                }
                paymentSuccess = true;
                break;
        }

        if (paymentSuccess) {
            String receipt = generateReceiptWithDiscount(orderDetailsPanel, amount, finalAmount, cashAmount, refNumber);
            // Insert into database before showing receipt
            insertSale(refNumber, finalAmount, now, paymentMethod);
            showStyledReceipt(receipt);
            return true;
        }
        return false;
    }

    // --- Receipt Dialog with Black Main Color and White Text, Centered Content ---
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


            // *** 

            if (idNumber == null) {
            // User cancelled, go back to discount type selection
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

    private String generateReferenceNumber() {
        LocalDateTime now = LocalDateTime.now();
        Random random = new Random();
        String dateStr = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomStr = String.format("%04d", random.nextInt(10000));
        return "CB" + dateStr + randomStr;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double amount) {
        this.totalAmount = amount;
    }

    public void addToTotal(double amount) {
        this.totalAmount += amount;
    }
}