import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class HomePage extends JFrame {
    private String username;
    private int userId;

    public HomePage(String username) {
        this.username = username;
        setTitle("Personal Finance Tracker - Home");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        getUserId();

        JLabel welcome = new JLabel("Welcome, " + username, SwingConstants.CENTER);
        welcome.setFont(new Font("Arial", Font.BOLD, 20));
        add(welcome, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        JButton btnAddTransaction = new JButton("➕ Add Transaction");
        JButton btnViewEarnings = new JButton("📈 View Earnings");
        JButton btnViewExpenses = new JButton("📉 View Expenses");
        JButton btnViewAll = new JButton("🧾 View All Transactions");
        JButton btnSummary = new JButton("📊 View Summary");
        JButton btnLogout = new JButton("🚪 Logout");

        buttonPanel.add(btnAddTransaction);
        buttonPanel.add(btnViewEarnings);
        buttonPanel.add(btnViewExpenses);
        buttonPanel.add(btnViewAll);
        buttonPanel.add(btnSummary);
        buttonPanel.add(btnLogout);

        add(buttonPanel, BorderLayout.CENTER);

        // Button actions
        btnAddTransaction.addActionListener(e -> openTransactionDialog());
        btnViewEarnings.addActionListener(e -> openTransactionView("income"));
        btnViewExpenses.addActionListener(e -> openTransactionView("expense"));
        btnViewAll.addActionListener(e -> openTransactionView("all"));
        btnSummary.addActionListener(e -> showSummary());
        btnLogout.addActionListener(e -> {
            dispose();
            new login();
        });

        setVisible(true);
    }

    private void getUserId() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/finance_db", "root", "root")) {
            PreparedStatement pst = con.prepareStatement("SELECT id FROM users WHERE username = ?");
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                userId = rs.getInt("id");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error fetching user ID: " + ex.getMessage());
        }
    }

    private void openTransactionDialog() {
        JTextField amountField = new JTextField();
        JTextField descriptionField = new JTextField();
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"income", "expense"});
        JTextField dateField = new JTextField("");

        Object[] message = {
            "Amount:", amountField,
            "Description:", descriptionField,
            "Type:", typeBox,
            "Date (DD-MM-YYYY):", dateField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Transaction", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/finance_db", "root", "root")) {
                PreparedStatement pst = con.prepareStatement("INSERT INTO transactions (user_id, type, amount, description, date) VALUES (?, ?, ?, ?, ?)");
                pst.setInt(1, userId);
                pst.setString(2, (String) typeBox.getSelectedItem());
                pst.setDouble(3, Double.parseDouble(amountField.getText()));
                pst.setString(4, descriptionField.getText());
                pst.setString(5, dateField.getText());
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Transaction added successfully.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding transaction: " + ex.getMessage());
            }
        }
    }

    private void openTransactionView(String type) {
        JFrame viewFrame = new JFrame("Transactions - " + (type.equals("all") ? "All" : type));
        viewFrame.setSize(600, 400);
        viewFrame.setLocationRelativeTo(this);

        DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Date", "Type", "Amount", "Description"}, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        viewFrame.add(scrollPane);

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/finance_db", "root", "root")) {
            String query = "SELECT * FROM transactions WHERE user_id = ?";
            if (!type.equals("all")) {
                query += " AND type = ?";
            }
            query += " ORDER BY date DESC";

            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, userId);
            if (!type.equals("all")) {
                pst.setString(2, type);
            }
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String date = rs.getString("date");
                String t = rs.getString("type");
                String desc = rs.getString("description");
                double amt = rs.getDouble("amount");
                tableModel.addRow(new Object[]{date, t, "Rs. " + amt, desc});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error fetching transactions: " + ex.getMessage());
        }

        viewFrame.setVisible(true);
    }

    private void showSummary() {
        double income = 0, expense = 0;

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/finance_db", "root", "root")) {
            PreparedStatement pst = con.prepareStatement("SELECT type, amount FROM transactions WHERE user_id = ?");
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String type = rs.getString("type");
                double amt = rs.getDouble("amount");
                if (type.equals("income")) income += amt;
                else expense += amt;
            }

            double balance = income - expense;
            String message = String.format(
                "📈 Total Income: Rs. %.2f\n📉 Total Expenses: Rs. %.2f\n💰 Available Balance: Rs. %.2f",
                income, expense, balance
            );
            JOptionPane.showMessageDialog(this, message, "Financial Summary", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error showing summary: " + ex.getMessage());
        }
    }
}
