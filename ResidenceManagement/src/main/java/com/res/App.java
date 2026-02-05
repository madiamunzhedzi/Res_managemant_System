package com.res;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class App extends JFrame {
    // In-memory "Database"
    private List<String[]> reports = new ArrayList<>();
    private final String VALID_STUDENT = "212345"; // Test Student Number
    private final String VALID_PIN = "1234";      // Test PIN

    public App() {
        setTitle("Res Management System (Demo Mode)");
        setSize(400, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        showLogin();
    }

    private void showLogin() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));
        JTextField userField = new JTextField();
        JPasswordField pinField = new JPasswordField();
        
        panel.add(new JLabel("Student Number (Try: 212345):"));
        panel.add(userField);
        panel.add(new JLabel("PIN (Try: 1234):"));
        panel.add(pinField);
        
        int result = JOptionPane.showConfirmDialog(null, panel, "Login", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String user = userField.getText();
            String pin = new String(pinField.getPassword());
            
            if (user.equals(VALID_STUDENT) && pin.equals(VALID_PIN)) {
                showReportingForm(user);
            } else {
                JOptionPane.showMessageDialog(this, "Wrong details! Try again.");
                showLogin();
            }
        }
    }

    private void showReportingForm(String studentNum) {
        getContentPane().removeAll();
        setLayout(new BorderLayout(15, 15));

        JPanel formPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        
        // Block & Room Selection
        JComboBox<String> blockBox = new JComboBox<>(new String[]{"Block A", "Block B", "Block C"});
        JComboBox<String> roomBox = new JComboBox<>(new String[]{"Room 101", "Room 102", "Room 201", "Room 202"});
        
        // Issue Selection
        JComboBox<String> issueBox = new JComboBox<>(new String[]{"Broken Light", "Leaking Pipe", "Door Lock Fault", "No Hot Water"});
        JTextArea descArea = new JTextArea(4, 20);
        descArea.setBorder(BorderFactory.createTitledBorder("Describe what is wrong:"));

        formPanel.add(new JLabel("Select your Block:"));
        formPanel.add(blockBox);
        formPanel.add(new JLabel("Select your Room:"));
        formPanel.add(roomBox);
        formPanel.add(new JLabel("What is the issue?"));
        formPanel.add(issueBox);
        formPanel.add(new JScrollPane(descArea));

        JButton submitBtn = new JButton("Submit Report");
        submitBtn.setBackground(Color.GREEN);

        submitBtn.addActionListener(e -> {
            // Save to memory
            String[] report = {
                studentNum, 
                (String)blockBox.getSelectedItem(), 
                (String)roomBox.getSelectedItem(), 
                (String)issueBox.getSelectedItem(), 
                descArea.getText()
            };
            reports.add(report);
            
            JOptionPane.showMessageDialog(this, "Success! Report saved for " + studentNum);
            System.out.println("Total reports logged: " + reports.size());
            descArea.setText(""); // Clear form
        });

        add(new JLabel("Student: " + studentNum, SwingConstants.CENTER), BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(submitBtn, BorderLayout.SOUTH);
        
        revalidate();
        repaint();
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App());
    }
}
