package com.res;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class App extends JFrame {
    // In-memory "Database"
    private List<String[]> reports = new ArrayList<>();
    private final String VALID_STUDENT = "212345";
    private final String VALID_STUDENT_PIN = "1234";
    private final String VALID_ADMIN = "admin";
    private final String VALID_ADMIN_PIN = "adminpin";

    private JPanel cardPanel;
    private CardLayout cardLayout = new CardLayout();
    private DefaultTableModel adminTableModel; // Keep track of the table model

    public App() {
        setTitle("Residence Management System");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardPanel = new JPanel(cardLayout);
        cardPanel.add(createLoginPanel(), "Login");
        
        add(cardPanel);
        setVisible(true);
    }

    // --- LOGIN PANEL ---
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JLabel titleLabel = new JLabel("Welcome to RES-MGMT");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0; panel.add(new JLabel("Username/Student ID:"), gbc);
        JTextField userField = new JTextField(15);
        gbc.gridx = 1; panel.add(userField, gbc);

        gbc.gridy = 2; gbc.gridx = 0; panel.add(new JLabel("PIN/Password:"), gbc);
        JPasswordField pinField = new JPasswordField(15);
        gbc.gridx = 1; panel.add(pinField, gbc);

        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> authenticate(userField.getText(), new String(pinField.getPassword())));
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2; panel.add(loginBtn, gbc);

        return panel;
    }

    private void authenticate(String user, String pin) {
        if (user.equals(VALID_STUDENT) && pin.equals(VALID_STUDENT_PIN)) {
            cardPanel.add(createReportingForm(user), "Student");
            cardLayout.show(cardPanel, "Student");
        } else if (user.equals(VALID_ADMIN) && pin.equals(VALID_ADMIN_PIN)) {
            showAdminPanel();
            cardLayout.show(cardPanel, "Admin");
        } else {
            JOptionPane.showMessageDialog(this, "Login Failed!");
        }
    }

    // --- STUDENT REPORTING FORM (Reused from previous code) ---
    private JPanel createReportingForm(String studentNum) {
        // ... (Code for student form remains the same as previous turn) ...
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel header = new JLabel("Submit Maintenance Report (Logged in as " + studentNum + ")", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(header, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        
        JComboBox<String> blockBox = new JComboBox<>(new String[]{"Block A", "Block B", "Block C"});
        JComboBox<String> roomBox = new JComboBox<>(new String[]{"Room 101", "Room 102", "Room 201", "Room 202"});
        JComboBox<String> issueBox = new JComboBox<>(new String[]{"Broken Light", "Leaking Pipe", "Door Lock Fault", "No Hot Water"});
        JTextArea descArea = new JTextArea(5, 20);
        descArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        formPanel.add(new JLabel("Block:")); formPanel.add(blockBox);
        formPanel.add(new JLabel("Room:")); formPanel.add(roomBox);
        formPanel.add(new JLabel("Issue:")); formPanel.add(issueBox);
        formPanel.add(new JLabel("Details:")); formPanel.add(new JScrollPane(descArea));

        JButton submitBtn = new JButton("Submit Report");
        submitBtn.setBackground(new Color(76, 175, 80)); // Material Green
        submitBtn.setForeground(Color.WHITE);
        submitBtn.addActionListener(e -> submitReport(studentNum, (String)blockBox.getSelectedItem(), (String)roomBox.getSelectedItem(), (String)issueBox.getSelectedItem(), descArea.getText()));
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> cardLayout.show(cardPanel, "Login"));

        JPanel southPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        southPanel.add(logoutBtn);
        southPanel.add(submitBtn);

        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void submitReport(String sNum, String block, String room, String issue, String desc) {
        // Data format: StudentNo, Block, Room, Issue, Description, Status
        String[] report = {sNum, block, room, issue, desc, "Pending"};
        reports.add(report);
        JOptionPane.showMessageDialog(this, "Report Logged Successfully!");
        // If admin panel is visible, refresh table
        if (adminTableModel != null) {
            adminTableModel.addRow(report);
        }
    }

    // --- MANAGEMENT (ADMIN) PANEL ---
    private void showAdminPanel() {
        JPanel adminPanel = new JPanel(new BorderLayout());
        JLabel adminHeader = new JLabel("Management Dashboard", SwingConstants.CENTER);
        adminHeader.setFont(new Font("Arial", Font.BOLD, 18));
        adminPanel.add(adminHeader, BorderLayout.NORTH);

        String[] columnNames = {"Student No.", "Block", "Room", "Issue", "Description", "Status"};
        adminTableModel = new DefaultTableModel(columnNames, 0);

        for (String[] report : reports) {
            adminTableModel.addRow(report);
        }
        
        JTable reportTable = new JTable(adminTableModel);
        JScrollPane scrollPane = new JScrollPane(reportTable);
        adminPanel.add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Logout");
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "Login"));
        
        JButton resolveButton = new JButton("Mark Selected as Resolved");
        resolveButton.addActionListener(e -> resolveReport(reportTable));

        JPanel southPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        southPanel.add(backButton);
        southPanel.add(resolveButton);
        
        adminPanel.add(southPanel, BorderLayout.SOUTH);

        cardPanel.add(adminPanel, "Admin");
    }

    private void resolveReport(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            // Update the status column (index 5) in both the table model and the underlying list
            adminTableModel.setValueAt("Resolved", selectedRow, 5);
            reports.get(selectedRow)[5] = "Resolved";
            JOptionPane.showMessageDialog(this, "Report status updated.");
        } else {
            JOptionPane.showMessageDialog(this, "Please select a report to resolve.");
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App());
    }
}

