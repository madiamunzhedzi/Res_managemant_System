package com.res;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class App extends JFrame {
    // In-memory "Databases"
    private Map<String, String[]> students = new HashMap<>(); // ID -> {Name, PIN, Block, Room}
    private List<String[]> reports = new ArrayList<>(); // StudentNo, Block, Room, Issue, Description, Status
    private List<String> availableIssues = new ArrayList<>();
    // Blocks mapped to their list of rooms
    private Map<String, List<String>> campusMap = new HashMap<>();

    private JPanel cardPanel;
    private CardLayout cardLayout = new CardLayout();
    private DefaultTableModel adminReportsTableModel;
    private JComboBox<String> studentFormBlockBox, studentFormRoomBox, studentFormIssueBox;

    public App() {
        // Initialize some dummy data
        students.put("212345", new String[]{"John Doe", "1234", "Block A", "Room 101"});
        campusMap.put("Block A", new ArrayList<>(List.of("Room 101", "Room 102")));
        campusMap.put("Block B", new ArrayList<>(List.of("Room 201", "Room 202")));
        availableIssues.addAll(List.of("Broken Light", "Leaking Pipe", "Door Lock Fault", "No Hot Water"));

        setTitle("Residence Management System");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardPanel = new JPanel(cardLayout);
        cardPanel.add(createLoginPanel(), "Login");
        
        add(cardPanel);
        setVisible(true);
    }

    // --- LOGIN PANEL (Uses same method to authenticate both types) ---
    private JPanel createLoginPanel() {
        // ... (Login panel aesthetics code from previous turn remains the same) ...
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
        if (students.containsKey(user) && students.get(user)[1].equals(pin)) {
            cardPanel.add(createReportingForm(user), "Student");
            cardLayout.show(cardPanel, "Student");
        } else if (user.equals("admin") && pin.equals("adminpin")) {
            showAdminPanel();
            cardLayout.show(cardPanel, "Admin");
        } else {
            JOptionPane.showMessageDialog(this, "Login Failed!");
        }
    }

    // --- STUDENT REPORTING FORM (Updated to use dynamic data) ---
    private JPanel createReportingForm(String studentNum) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel header = new JLabel("Submit Maintenance Report", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(header, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        
        // Populate JComboBoxes dynamically from our in-memory data
        studentFormBlockBox = new JComboBox<>(campusMap.keySet().toArray(new String[0]));
        // Room box content changes based on block selection - advanced logic left out for brevity
        studentFormRoomBox = new JComboBox<>(new String[]{"Select Room After Selecting Block"}); 
        studentFormIssueBox = new JComboBox<>(availableIssues.toArray(new String[0]));
        JTextArea descArea = new JTextArea(5, 20);
        descArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        formPanel.add(new JLabel("Block:")); formPanel.add(studentFormBlockBox);
        formPanel.add(new JLabel("Room:")); formPanel.add(studentFormRoomBox);
        formPanel.add(new JLabel("Issue:")); formPanel.add(studentFormIssueBox);
        formPanel.add(new JLabel("Details:")); formPanel.add(new JScrollPane(descArea));

        JButton submitBtn = new JButton("Submit Report");
        submitBtn.addActionListener(e -> submitReport(studentNum, (String)studentFormBlockBox.getSelectedItem(), (String)studentFormRoomBox.getSelectedItem(), (String)studentFormIssueBox.getSelectedItem(), descArea.getText()));
        
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
        String[] report = {sNum, block, room, issue, desc, "Pending"};
        reports.add(report);
        JOptionPane.showMessageDialog(this, "Report Logged Successfully!");
        if (adminReportsTableModel != null) {
            adminReportsTableModel.addRow(report);
        }
    }
    
    // --- MANAGEMENT (ADMIN) PANEL (With Tabs for Management) ---
    private void showAdminPanel() {
        JPanel adminPanel = new JPanel(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("View Reports", createReportsManagementTab());
        tabbedPane.addTab("Manage Students", createStudentManagementTab());
        tabbedPane.addTab("Manage Campus Map", createCampusManagementTab());
        tabbedPane.addTab("Manage Issue Types", createIssueManagementTab());
        
        adminPanel.add(tabbedPane, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> cardLayout.show(cardPanel, "Login"));
        adminPanel.add(logoutButton, BorderLayout.SOUTH);

        cardPanel.add(adminPanel, "Admin");
    }

    // Helper for updating JComboBoxes across the UI
    private void updateComboBoxes() {
        // This is a simple way to force refresh UI components across different panels
        if (studentFormBlockBox != null) studentFormBlockBox.setModel(new DefaultComboBoxModel<>(campusMap.keySet().toArray(new String[0])));
        if (studentFormIssueBox != null) studentFormIssueBox.setModel(new DefaultComboBoxModel<>(availableIssues.toArray(new String[0])));
    }

    // --- ADMIN TABS (View/Edit Reports) ---
    private JPanel createReportsManagementTab() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnNames = {"Student No.", "Block", "Room", "Issue", "Description", "Status"};
        adminReportsTableModel = new DefaultTableModel(columnNames, 0);

        for (String[] report : reports) {
            adminReportsTableModel.addRow(report);
        }
        
        JTable reportTable = new JTable(adminReportsTableModel);
        JScrollPane scrollPane = new JScrollPane(reportTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton resolveButton = new JButton("Mark Selected as Resolved");
        resolveButton.addActionListener(e -> {
            int selectedRow = reportTable.getSelectedRow();
            if (selectedRow != -1) {
                adminReportsTableModel.setValueAt("Resolved", selectedRow, 5);
                reports.get(selectedRow)[5] = "Resolved";
            }
        });
        panel.add(resolveButton, BorderLayout.SOUTH);

        return panel;
    }
    
    // --- ADMIN TABS (Manage Students) ---
    private JPanel createStudentManagementTab() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JTextField idField = new JTextField(), nameField = new JTextField(), pinField = new JTextField();
        // Simplified: Block/Room assignment for new students would need robust UI selection

        panel.add(new JLabel("Student ID:")); panel.add(idField);
        panel.add(new JLabel("Full Name:")); panel.add(nameField);
        panel.add(new JLabel("PIN:")); panel.add(pinField);
        
        JButton addButton = new JButton("Add Student");
        addButton.addActionListener(e -> {
            if (!idField.getText().isEmpty() && !students.containsKey(idField.getText())) {
                students.put(idField.getText(), new String[]{nameField.getText(), pinField.getText(), "N/A", "N/A"});
                JOptionPane.showMessageDialog(this, "Student Added: " + nameField.getText());
            }
        });
        panel.add(addButton);
        
        JButton removeButton = new JButton("Remove Student (by ID)");
        removeButton.addActionListener(e -> {
            if (students.containsKey(idField.getText())) {
                students.remove(idField.getText());
                JOptionPane.showMessageDialog(this, "Student Removed: " + idField.getText());
            }
        });
        panel.add(removeButton);

        return panel;
    }

    // --- ADMIN TABS (Manage Campus Map - Add/Subtract Rooms/Blocks) ---
    private JPanel createCampusManagementTab() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JComboBox<String> blockSelector = new JComboBox<>(campusMap.keySet().toArray(new String[0]));
        JTextField blockNameField = new JTextField(), roomNameField = new JTextField();

        panel.add(new JLabel("Block Name (Add New):")); panel.add(blockNameField);
        JButton addBlockBtn = new JButton("Add Block");
        addBlockBtn.addActionListener(e -> {
            if (!blockNameField.getText().isEmpty() && !campusMap.containsKey(blockNameField.getText())) {
                campusMap.put(blockNameField.getText(), new ArrayList<>());
                updateComboBoxes();
                JOptionPane.showMessageDialog(this, "Block Added.");
            }
        });
        panel.add(addBlockBtn);
        
        panel.add(new JLabel("Select Block:")); panel.add(blockSelector);
        panel.add(new JLabel("Room Name (Add/Sub):")); panel.add(roomNameField);
        
        JButton addRoomBtn = new JButton("Add Room");
        addRoomBtn.addActionListener(e -> {
            String block = (String)blockSelector.getSelectedItem();
            if (block != null && !roomNameField.getText().isEmpty()) {
                campusMap.get(block).add(roomNameField.getText());
                updateComboBoxes();
                JOptionPane.showMessageDialog(this, "Room Added.");
            }
        });
        panel.add(addRoomBtn);

        JButton subRoomBtn = new JButton("Subtract Room");
        subRoomBtn.addActionListener(e -> {
            String block = (String)blockSelector.getSelectedItem();
            if (block != null && campusMap.get(block).remove(roomNameField.getText())) {
                updateComboBoxes();
                JOptionPane.showMessageDialog(this, "Room Removed.");
            }
        });
        panel.add(subRoomBtn);

        return panel;
    }

    // --- ADMIN TABS (Manage Issues) ---
    private JPanel createIssueManagementTab() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JTextField issueField = new JTextField();
        
        panel.add(new JLabel("Issue Name (Add/Sub):")); panel.add(issueField);
        
        JButton addIssueBtn = new JButton("Add Issue Type");
        addIssueBtn.addActionListener(e -> {
            if (!issueField.getText().isEmpty() && !availableIssues.contains(issueField.getText())) {
                availableIssues.add(issueField.getText());
                updateComboBoxes();
                JOptionPane.showMessageDialog(this, "Issue Added.");
            }
        });
        panel.add(addIssueBtn);

        JButton subIssueBtn = new JButton("Subtract Issue Type");
        subIssueBtn.addActionListener(e -> {
            if (availableIssues.remove(issueField.getText())) {
                updateComboBoxes();
                JOptionPane.showMessageDialog(this, "Issue Removed.");
            }
        });
        panel.add(subIssueBtn);
        return panel;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App());
    }
}


