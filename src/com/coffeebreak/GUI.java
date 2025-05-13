package com.coffeebreak;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUI {
    // Main application frame and key UI components
    private JFrame frame;
    private JLabel dateLabel;
    private JLabel priceLabel;
    private JPanel orderDetailsPanel;
    private JLabel nameLabel = new JLabel("Name");
    private JLabel codeLabel = new JLabel("Code");
    private JLabel qtyLabel = new JLabel("Qty");
    private JLabel priceHeaderLabel = new JLabel("Price");
    private CoffeeBreakHelper helper = new CoffeeBreakHelper(); // For program logic

    // Constructor: sets up the main window and shows it
    public GUI() {
        prepareFrame();
        JPanel mainPanel = createMainPanel();
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // Sets up the main JFrame properties
    private void prepareFrame() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Brew");
        frame.setSize(1280, 720);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());
    }

    // Builds the main content panel with header, content, and action areas
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
        panel.setBackground(Color.WHITE);

        panel.add(createHeaderWrapper(), BorderLayout.NORTH);
        panel.add(createMainContentPanel(), BorderLayout.CENTER);
        panel.add(createActionPanel(), BorderLayout.SOUTH);

        return panel;
    }

    // Wrapper for the order header and order panel * Groups relevant components tgt
    private JPanel createHeaderWrapper() {
        JPanel headerWrapper = new JPanel();
        headerWrapper.setLayout(new BoxLayout(headerWrapper, BoxLayout.Y_AXIS));
        headerWrapper.setBackground(Color.WHITE);

        headerWrapper.add(createOrderHeaderPanel()); // Shows app title and date
        headerWrapper.add(createOrderPanel());       // Shows "ORDER INVOICE" label

        return headerWrapper;
    }

    // Top header: shows app title and current date
    private JPanel createOrderHeaderPanel() {
        JPanel orderheaderPanel = new JPanel(new BorderLayout());
        orderheaderPanel.setBackground(new Color(220, 220, 220));
        orderheaderPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("COFFEE BREAK");
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 18));
        orderheaderPanel.add(titleLabel, BorderLayout.WEST);

        dateLabel = new JLabel(helper.getCurrentDate());
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        orderheaderPanel.add(dateLabel, BorderLayout.EAST);

        return orderheaderPanel;
    }

    // Shows "ORDER INVOICE" label on the left
    private JPanel createOrderPanel() {
        JPanel orderPanel = new JPanel(new BorderLayout());
        orderPanel.setBackground(Color.WHITE);

        JLabel orderLabel = new JLabel("<html>ORDER<br>INVOICE</html>");
        orderLabel.setFont(new Font("Poppins", Font.BOLD, 32));
        orderLabel.setHorizontalAlignment(SwingConstants.LEFT);
        orderPanel.add(orderLabel, BorderLayout.WEST);

        return orderPanel;
    }

    // Main content: order details and menu buttons
    private JPanel createMainContentPanel() {
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.X_AXIS));
        mainContentPanel.setBackground(Color.WHITE);

        orderDetailsPanel = createOrderDetailsPanel(); // Shows order items and total
        JPanel buttonsWrapperPanel = createButtonsWrapperPanel(); // Menu buttons

        mainContentPanel.add(orderDetailsPanel);
        mainContentPanel.add(Box.createHorizontalGlue());
        mainContentPanel.add(buttonsWrapperPanel);

        return mainContentPanel;
    }

    // Panel for order details: header, items, and total
    private JPanel createOrderDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panel.setPreferredSize(new Dimension(350, 300));

        panel.add(createHeaderPanel(), BorderLayout.NORTH); // Column headers
        panel.add(createItemsScrollPane(), BorderLayout.CENTER); // Order items
        panel.add(createTotalPanel(), BorderLayout.SOUTH); // Total price

        return panel;
    }

    // Header for order details (column names)
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 60, 0));
        headerPanel.setBackground(Color.LIGHT_GRAY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        headerPanel.setPreferredSize(new Dimension(350, 30));
    
        nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        codeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        qtyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        priceHeaderLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    
        headerPanel.add(nameLabel);
        headerPanel.add(codeLabel);
        headerPanel.add(qtyLabel);
        headerPanel.add(priceHeaderLabel);
    
        return headerPanel;
    }

    // Scrollable area for order items
    private JScrollPane createItemsScrollPane() {
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Color.WHITE);

        return scrollPane;
    }

    // Panel showing the total price at the bottom of order details
    private JPanel createTotalPanel() {
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(Color.WHITE);
        totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel totalLabel = new JLabel("TOTAL");
        totalLabel.setFont(new Font("Poppins", Font.BOLD, 20));
        priceLabel = new JLabel("₱0.00");
        priceLabel.setFont(new Font("Poppins", Font.BOLD, 20));
        priceLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        totalPanel.add(totalLabel, BorderLayout.WEST);
        totalPanel.add(priceLabel, BorderLayout.EAST);

        return totalPanel;
    }

    // Wrapper for all menu buttons (drinks, food, etc.)
    private JPanel createButtonsWrapperPanel() {
        JPanel buttonsWrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonsWrapperPanel.setBackground(Color.WHITE);

        JPanel buttonsPanel = new JPanel(new GridLayout(2, 7, 10, 10));
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.setPreferredSize(new Dimension(700, 220));

        String[] buttonLabels = helper.createButtonLabels();
        for (String label : buttonLabels) {
            JButton button = createBlackButton(label);
            button.setPreferredSize(new Dimension(80, 80));
            // Each button adds an item to the order when clicked
            button.addActionListener(e -> helper.handleButtonClick(label, orderDetailsPanel, priceLabel));
            buttonsPanel.add(button);
        }

        buttonsWrapperPanel.add(buttonsPanel);
        return buttonsWrapperPanel;
    }

    // Panel for action buttons: Cancel and Pay
    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton cancelButton = createCancelButton();
        JButton payButton = createPayButton();

        actionPanel.add(cancelButton);
        actionPanel.add(payButton);

        return actionPanel;
    }

    // Cancel button: clears the order and resets the total
    private JButton createCancelButton() {
        JButton cancelButton;
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/resources/trash-icon.png"));
            Image image = originalIcon.getImage();
            Image scaledImage = image.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            cancelButton = createColoredButton("", new Color(255, 75, 75));
            cancelButton.setIcon(scaledIcon);
        } catch (Exception e) {
            cancelButton = createColoredButton("\uD83D\uDDD1", new Color(255, 75, 75));
        }

        cancelButton.addActionListener(e -> {
            // Remove all items from the order
            JScrollPane scrollPane = (JScrollPane) orderDetailsPanel.getComponent(1);
            JPanel itemsPanel = (JPanel) scrollPane.getViewport().getView();
            itemsPanel.removeAll();
            itemsPanel.revalidate();
            itemsPanel.repaint();
            priceLabel.setText("₱0.00");
            helper.setTotalAmount(0.0);
        });

        return cancelButton;
    }

    // Pay button: processes payment and resets order on success
    private JButton createPayButton() {
        JButton payButton = createColoredButton("PAY", new Color(120, 200, 120));
        payButton.addActionListener(e -> {
            double currentTotal = helper.getTotalAmount();
            if (currentTotal <= 0) {
                JOptionPane.showMessageDialog(frame, "No items to pay for!", "Empty Order", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Process payment and show result
            boolean paymentSuccessful = helper.processPayment(currentTotal, orderDetailsPanel);
            if (paymentSuccessful) {
                JOptionPane.showMessageDialog(frame, "Payment successful!");
                priceLabel.setText("₱0.00");
                helper.setTotalAmount(0.0);
                // Clear order items after successful payment
                JScrollPane scrollPane = (JScrollPane) orderDetailsPanel.getComponent(1);
                JPanel itemsPanel = (JPanel) scrollPane.getViewport().getView();
                itemsPanel.removeAll();
                itemsPanel.revalidate();
                itemsPanel.repaint();
            } else {
                JOptionPane.showMessageDialog(frame, "Payment failed. Please try again.", "Payment Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        return payButton;
    }

    // Creates a black menu button with rounded corners and hover effect
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

    // Creates a colored button with rounded corners and hover effect
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
}