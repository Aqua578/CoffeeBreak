package com.coffeebreak;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Component;
import java.util.*;

/**
 * Helper class for the Coffee Break application
 * Contains business logic and helper functions
 */
public class CoffeeBreakHelper {
    private double totalAmount = 0.0;
    private static final double DISCOUNT_RATE = 0.20; // 20% discount for Senior/PWD
    
    // Data structure to store menu items and their subcategories
    private Map<String, String[]> menuSubcategories = new HashMap<>();
    private Map<String, double[]> categoryPrices = new HashMap<>();
    
    public CoffeeBreakHelper() {
        initializeMenuData();
    }
    
    private void initializeMenuData() {
        // Initialize menu subcategories
        menuSubcategories.put("Amerikano", new String[]{"Original Coffee", "Caramel", "Chocolate", "Blanca"});
        menuSubcategories.put("Boba Bliss", new String[]{"Probiotea Pearl", "Chocolate", "Cookies and Cream", "Hokkaido", "Matcha", "Salted Caramel", "Okinawa", "Wintermelon"});
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
        
        // Initialize prices for categories
        // Format: {Petite/Mini price, Venti/Large price}
        categoryPrices.put("Amerikano", new double[]{29, 39});
        categoryPrices.put("Boba Bliss", new double[]{39, 49});
        categoryPrices.put("Fruit Tea", new double[]{39, 49});
        categoryPrices.put("Hot Drinks", new double[]{39, 39}); // Hot drinks only have one size (12oz)
        categoryPrices.put("Iced Latte", new double[]{39, 49});
        categoryPrices.put("Non Caffeine", new double[]{39, 49});
        categoryPrices.put("Special Series", new double[]{55, 55}); // Special series only has Venti size
        // Food items with Mini/Large prices
        categoryPrices.put("Siomai King", new double[]{50, 75});
        categoryPrices.put("French Fries", new double[]{35, 50});
        categoryPrices.put("Croffles (Premium)", new double[]{70 ,95});
        categoryPrices.put("Croffles (Classic)", new double[]{50, 75});
        categoryPrices.put("Combo", new double[]{99, 149});
    }
    
    public String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        return dateFormat.format(new Date());
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
        
        String selectedSubcategory = (String) JOptionPane.showInputDialog(
            null,
            "Select " + buttonLabel + " Type:",
            "Type Selection",
            JOptionPane.QUESTION_MESSAGE,
            null,
            subcategories,
            subcategories[0]
        );
        
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
        
        int sizeChoice = JOptionPane.showOptionDialog(
            null,
            "Choose size for " + selectedSubcategory + ":",
            "Size Selection",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            sizeOptions,
            sizeOptions[0]
        );
        
        if (sizeChoice == JOptionPane.CLOSED_OPTION || sizeChoice == -1) {
            return;
        }
        
        String selectedSize;
        double price;
        
        if (buttonLabel.equals("Hot Drinks")) {
            selectedSize = "12oz";
            price = prices[0];
        } else if (buttonLabel.equals("Special Series")) {
            selectedSize = "Venti";
            price = prices[0];
        } else {
            selectedSize = sizeChoice == 0 ? (isDrinkItem ? "Petite" : "Mini") : (isDrinkItem ? "Venti" : "Large");
            price = prices[sizeChoice];
        }
        
        String itemDescription = selectedSubcategory + " - " + selectedSize;
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
        int paymentChoice = JOptionPane.showOptionDialog(
            null,
            "Total Amount: ₱" + String.format("%.2f", finalAmount) + "\nSelect payment method:",
            "Payment Method",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            paymentOptions,
            paymentOptions[0]
        );

        if (paymentChoice == JOptionPane.CLOSED_OPTION) {
            return false;
        }

        switch (paymentChoice) {
            case 0: // Cash
                String cashInput = JOptionPane.showInputDialog(
                    "Total Amount: ₱" + String.format("%.2f", finalAmount) + 
                    "\nEnter cash amount:"
                );
                
                if (cashInput == null) return false;
                
                try {
                    double cashAmount = Double.parseDouble(cashInput);
                    if (cashAmount < finalAmount) {
                        JOptionPane.showMessageDialog(null, 
                            "Insufficient cash amount!", 
                            "Payment Error", 
                            JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                    
                    String receipt = generateReceiptWithDiscount(orderDetailsPanel, amount, finalAmount, cashAmount);
                    JTextArea receiptArea = new JTextArea(receipt);
                    receiptArea.setBackground(new Color(240, 240, 240));
                    JScrollPane receiptPane = new JScrollPane(receiptArea);
                    receiptPane.setBackground(new Color(240, 240, 240));

                    JOptionPane.showMessageDialog(null, 
                        receiptPane,
                        "Receipt",
                        JOptionPane.INFORMATION_MESSAGE);
                    return true;
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, 
                        "Invalid amount entered!", 
                        "Input Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return false;
                }

            case 1: // Credit Card
                String cardNumber = JOptionPane.showInputDialog("Enter credit card number:");
                if (cardNumber == null || !cardNumber.matches("\\d{16}")) {
                    JOptionPane.showMessageDialog(null, 
                        "Invalid card number!", 
                        "Payment Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                
                String cvv = JOptionPane.showInputDialog("Enter CVV:");
                if (cvv == null || !cvv.matches("\\d{3}")) {
                    JOptionPane.showMessageDialog(null, 
                        "Invalid CVV!", 
                        "Payment Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                
                String receipt = generateReceiptWithDiscount(orderDetailsPanel, amount, finalAmount, 0);
                JTextArea receiptArea = new JTextArea(receipt);
                receiptArea.setBackground(new Color(240, 240, 240));
                JScrollPane receiptPane = new JScrollPane(receiptArea);
                receiptPane.setBackground(new Color(240, 240, 240));

                JOptionPane.showMessageDialog(null, 
                    receiptPane,
                    "Receipt",
                    JOptionPane.INFORMATION_MESSAGE);
                return true;

            case 2: // GCash
                String phoneNumber = JOptionPane.showInputDialog("Enter GCash number:");
                if (phoneNumber == null || !phoneNumber.matches("09\\d{9}")) {
                    JOptionPane.showMessageDialog(null, 
                        "Invalid phone number!", 
                        "Payment Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                
                String pin = JOptionPane.showInputDialog("Enter MPIN:");
                if (pin == null || !pin.matches("\\d{4}")) {
                    JOptionPane.showMessageDialog(null, 
                        "Invalid MPIN!", 
                        "Payment Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                
                receipt = generateReceiptWithDiscount(orderDetailsPanel, amount, finalAmount, 0);
                receiptArea = new JTextArea(receipt);
                receiptArea.setBackground(new Color(240, 240, 240));
                receiptPane = new JScrollPane(receiptArea);
                receiptPane.setBackground(new Color(240, 240, 240));

                JOptionPane.showMessageDialog(null, 
                    receiptPane,
                    "Receipt",
                    JOptionPane.INFORMATION_MESSAGE);
                return true;

            default:
                return false;
        }
    }
    
    public double applyDiscount(double amount, JPanel orderPanel) {
        String[] discountOptions = {"Senior Citizen", "PWD", "No Discount"};
        int discountChoice = JOptionPane.showOptionDialog(
            null,
            "Select discount type (if applicable):",
            "Discount Selection",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            discountOptions,
            discountOptions[2]
        );

        if (discountChoice == JOptionPane.CLOSED_OPTION || discountChoice == 2) {
            return amount;
        }

        String idNumber = JOptionPane.showInputDialog(
            "Enter " + discountOptions[discountChoice] + " ID Number:"
        );

        if (idNumber == null || idNumber.trim().isEmpty()) {
            return amount;
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

    public String generateReceiptWithDiscount(JPanel orderPanel, double originalAmount, 
                                            double discountedAmount, double cashAmount) {
        StringBuilder receipt = new StringBuilder();
        LocalDateTime now = LocalDateTime.now();
        String refNumber = generateReferenceNumber();
        
        receipt.append("===============================\n");
        receipt.append("         COFFEE BREAK         \n");
        receipt.append("    Your Daily Coffee Fix     \n");
        receipt.append("===============================\n");
        receipt.append("Ref #: ").append(refNumber).append("\n");
        receipt.append("Date: ").append(now.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))).append("\n");
        receipt.append("Time: ").append(now.format(DateTimeFormatter.ofPattern("HH:mm:ss"))).append("\n");
        receipt.append("-------------------------------\n");
        receipt.append(String.format("%-20s %8s\n", "ITEM", "AMOUNT"));
        receipt.append("-------------------------------\n");

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

        receipt.append("-------------------------------\n");
        receipt.append(String.format("%-20s %8s\n", "SUBTOTAL", "₱" + String.format("%.2f", originalAmount)));
        if (originalAmount != discountedAmount) {
            receipt.append(String.format("%-20s %8s\n", "DISCOUNT (20%)", 
                "₱" + String.format("%.2f", originalAmount - discountedAmount)));
            receipt.append(String.format("%-20s %8s\n", "TOTAL", "₱" + String.format("%.2f", discountedAmount)));
        }
        if (cashAmount > 0) {
            receipt.append(String.format("%-20s %8s\n", "CASH", "₱" + String.format("%.2f", cashAmount)));
            receipt.append(String.format("%-20s %8s\n", "CHANGE", 
                "₱" + String.format("%.2f", cashAmount - discountedAmount)));
        }
        receipt.append("===============================\n");
        receipt.append("         Thank You!           \n");
        receipt.append("      Please Come Again!      \n");
        receipt.append("===============================\n");

        return receipt.toString();
    }
    
    public String generateReceipt(JPanel orderPanel, double totalAmount, double cashAmount) {
        StringBuilder receipt = new StringBuilder();
        LocalDateTime now = LocalDateTime.now();
        String refNumber = generateReferenceNumber();
        
        receipt.append("===============================\n");
        receipt.append("         COFFEE BREAK         \n");
        receipt.append("    Your Daily Coffee Fix     \n");
        receipt.append("===============================\n");
        receipt.append("Ref #: ").append(refNumber).append("\n");
        receipt.append("Date: ").append(now.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))).append("\n");
        receipt.append("Time: ").append(now.format(DateTimeFormatter.ofPattern("HH:mm:ss"))).append("\n");
        receipt.append("-------------------------------\n");
        receipt.append(String.format("%-20s %8s\n", "ITEM", "AMOUNT"));
        receipt.append("-------------------------------\n");

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

        receipt.append("-------------------------------\n");
        receipt.append(String.format("%-20s %8s\n", "TOTAL", "₱" + String.format("%.2f", totalAmount)));
        if (cashAmount > 0) {
            receipt.append(String.format("%-20s %8s\n", "CASH", "₱" + String.format("%.2f", cashAmount)));
            receipt.append(String.format("%-20s %8s\n", "CHANGE", "₱" + String.format("%.2f", cashAmount - totalAmount)));
        }
        receipt.append("===============================\n");
        receipt.append("         Thank You!           \n");
        receipt.append("      Please Come Again!      \n");
        receipt.append("===============================\n");

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