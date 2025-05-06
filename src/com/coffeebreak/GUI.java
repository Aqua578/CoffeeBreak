package com.coffeebreak;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

/**
 * Main GUI class for the Coffee Break application
 * This handles all the UI components and their interactions
 */
public class GUI {
    private JLabel dateLabel;
    private JLabel priceLabel;
    private JPanel orderDetailsPanel;
    // Initialize the header labels
    private JLabel nameLabel = new JLabel("Name");
    private JLabel codeLabel = new JLabel("Code");
    private JLabel qtyLabel = new JLabel("Qty");
    private JLabel priceHeaderLabel = new JLabel("Price");
    
    // Create an instance of our helper class
    private CoffeeBreakHelper helper = new CoffeeBreakHelper();
    
    // Add field at the top of GUI class
    private JFrame frame;

    public GUI() {
        this.frame = new JFrame();
        frame.setLayout(new BorderLayout());

        // Main panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
        panel.setBackground(Color.WHITE);

        // Header wrapper to keep everything structured
        JPanel headerWrapper = new JPanel();
        headerWrapper.setLayout(new BoxLayout(headerWrapper, BoxLayout.Y_AXIS));
        headerWrapper.setBackground(Color.WHITE);

        // Header panel (Title & Date)
        JPanel orderheaderPanel = new JPanel(new BorderLayout());
        orderheaderPanel.setBackground(new Color(220, 220, 220));
        orderheaderPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("COFFEE BREAK");
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 18));
        orderheaderPanel.add(titleLabel, BorderLayout.WEST);

        // Set current date
        dateLabel = new JLabel(helper.getCurrentDate());
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        orderheaderPanel.add(dateLabel, BorderLayout.EAST);

        // Separate Panel for Order Invoice to prevent movement
        JPanel orderPanel = new JPanel(new BorderLayout());
        orderPanel.setBackground(Color.WHITE);
        JLabel orderLabel = new JLabel("<html>ORDER<br>INVOICE</html>");
        orderLabel.setFont(new Font("Poppins", Font.BOLD, 32));
        orderLabel.setHorizontalAlignment(SwingConstants.LEFT);
        orderPanel.add(orderLabel, BorderLayout.WEST);

        // Add both header and order label to wrapper
        headerWrapper.add(orderheaderPanel);
        headerWrapper.add(orderPanel);

        // Main content panel (BoxLayout for alignment)
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.X_AXIS));
        mainContentPanel.setBackground(Color.WHITE);

        // Order details panel (left side)
        orderDetailsPanel = new JPanel(new BorderLayout()); // Change to BorderLayout
        orderDetailsPanel.setBackground(Color.WHITE);
        orderDetailsPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        orderDetailsPanel.setPreferredSize(new Dimension(350, 300));

        // Create header panel with grid layout
        JPanel headerPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        headerPanel.setBackground(Color.LIGHT_GRAY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        headerPanel.setPreferredSize(new Dimension(350, 30));

        // Set header labels
        nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        codeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        qtyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        priceHeaderLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        // Add labels directly to header panel
        headerPanel.add(nameLabel);
        headerPanel.add(codeLabel);
        headerPanel.add(qtyLabel);
        headerPanel.add(priceHeaderLabel);

        // Add header to order details panel
        orderDetailsPanel.add(headerPanel, BorderLayout.NORTH);

        // Create panel for the 4 subsections with exact same width as items
        JPanel columnsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        columnsPanel.setPreferredSize(new Dimension(350, 30));
        columnsPanel.setBackground(Color.LIGHT_GRAY);

        // Create fixed-width panels for each column
        JPanel namePanel = new JPanel();
        namePanel.setPreferredSize(new Dimension(140, 25));
        JPanel codePanel = new JPanel();
        codePanel.setPreferredSize(new Dimension(60, 25));
        JPanel qtyPanel = new JPanel();
        qtyPanel.setPreferredSize(new Dimension(90, 25));
        JPanel pricePanel = new JPanel();
        pricePanel.setPreferredSize(new Dimension(65, 25));

        // Set backgrounds
        namePanel.setBackground(Color.LIGHT_GRAY);
        codePanel.setBackground(Color.LIGHT_GRAY);
        qtyPanel.setBackground(Color.LIGHT_GRAY);
        pricePanel.setBackground(Color.LIGHT_GRAY);

        // Add labels to their respective panels
        namePanel.add(nameLabel, BorderLayout.CENTER);
        codePanel.add(codeLabel, BorderLayout.CENTER);
        qtyPanel.add(qtyLabel, BorderLayout.CENTER);
        pricePanel.add(priceHeaderLabel, BorderLayout.CENTER);

        // Add panels to columns panel
        columnsPanel.add(namePanel);
        columnsPanel.add(codePanel);
        columnsPanel.add(qtyPanel);
        columnsPanel.add(pricePanel);

        // Add columns panel to header
        headerPanel.add(columnsPanel, BorderLayout.CENTER);

        // Add header to order details panel before the scroll pane
        orderDetailsPanel.add(headerPanel, BorderLayout.NORTH);

        // Create scrollable panel for order items
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS)); // Use BoxLayout for vertical stacking
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // No padding
        itemsPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Color.WHITE);

        // Create total panel
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

        // Add components to order details panel
        orderDetailsPanel.add(scrollPane, BorderLayout.CENTER);
        orderDetailsPanel.add(totalPanel, BorderLayout.SOUTH);

        // Buttons wrapper panel (right side) - IMPROVED: keep structure but set to WHITE
        JPanel buttonsWrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonsWrapperPanel.setBackground(Color.WHITE); // Changed from default gray to WHITE
        
        // Keep the existing buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 7, 10, 10)); // 2 rows, 7 columns
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.setPreferredSize(new Dimension(700, 220));

        // Get button labels from helper class
        String[] buttonLabels = helper.createButtonLabels();

        // Create buttons and attach action handlers
        for (String label : buttonLabels) {
            JButton button = createBlackButton(label);
            button.setPreferredSize(new Dimension(80, 80)); // Square buttons
            
            // Guy 1's responsibility - handling button clicks
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Call Guy 1's method to handle button actions
                    helper.handleButtonClick(label, orderDetailsPanel, priceLabel);
                }
            });
            
            buttonsPanel.add(button);
        }

        buttonsWrapperPanel.add(buttonsPanel);

        // Aligning panels properly
        mainContentPanel.add(orderDetailsPanel);
        mainContentPanel.add(Box.createHorizontalGlue()); // Pushes buttons to the right
        mainContentPanel.add(buttonsWrapperPanel);

        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton cancelButton;

        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("resources/trash-icon.png"));
            Image image = originalIcon.getImage();
            Image scaledImage = image.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            
            cancelButton = createColoredButton("", new Color(255, 75, 75));
            cancelButton.setIcon(scaledIcon);
        } catch (Exception e) {
            // Fallback to text button if image fails to load
            cancelButton = createColoredButton("\uD83D\uDDD1", new Color(255, 75, 75));
        }

        // Add action listener outside try-catch
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Clear only the items panel
                JScrollPane scrollPane = (JScrollPane) orderDetailsPanel.getComponent(1);
                JPanel itemsPanel = (JPanel) scrollPane.getViewport().getView();
                itemsPanel.removeAll();
                itemsPanel.revalidate();
                itemsPanel.repaint();
                
                // Reset price
                priceLabel.setText("₱0.00");
                helper.setTotalAmount(0.0);
            }
        });
        
        JButton payButton = createColoredButton("PAY", new Color(120, 200, 120));
        
        // Add pay button action listener
        payButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double currentTotal = helper.getTotalAmount();
                if (currentTotal <= 0) {
                    JOptionPane.showMessageDialog(frame, "No items to pay for!", 
                                                "Empty Order", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                boolean paymentSuccessful = helper.processPayment(currentTotal, orderDetailsPanel);
                
                if (paymentSuccessful) {
                    JOptionPane.showMessageDialog(frame, "Payment successful!");
                    // Reset price
                    priceLabel.setText("₱0.00");
                    helper.setTotalAmount(0.0);
                    
                    // Clear only the items panel instead of the whole orderDetailsPanel
                    JScrollPane scrollPane = (JScrollPane) orderDetailsPanel.getComponent(1);
                    JPanel itemsPanel = (JPanel) scrollPane.getViewport().getView();
                    itemsPanel.removeAll();
                    itemsPanel.revalidate();
                    itemsPanel.repaint();
                } else {
                    JOptionPane.showMessageDialog(frame, "Payment failed. Please try again.", 
                                                "Payment Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        actionPanel.add(cancelButton);
        actionPanel.add(payButton);

        // Add components to main panel
        panel.add(headerWrapper, BorderLayout.NORTH);
        panel.add(mainContentPanel, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);

        // Add panel to frame
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Brew");
        frame.setSize(1280, 720);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    // Button Creator
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
        button.setPreferredSize(new Dimension(80, 80)); // Square buttons

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
        button.setPreferredSize(new Dimension(80, 80));
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
}