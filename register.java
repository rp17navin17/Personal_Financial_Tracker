import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class register implements ActionListener {

    JTextField nam, t_mail;
    JPasswordField t_pass, t_conpass;
    JButton btnSubmit, btnClear, btnLogin;
    JFrame fr;

    public register() {
        fr = new JFrame("Register");

        JLabel res = new JLabel("Register Now");
        res.setBounds(150, 30, 200, 30);

        JLabel name = new JLabel("User Name");
        name.setBounds(50, 80, 100, 30);
        nam = new JTextField();
        nam.setBounds(170, 80, 150, 30);

        JLabel mail = new JLabel("E-Mail");
        mail.setBounds(50, 130, 100, 30);
        t_mail = new JTextField();
        t_mail.setBounds(170, 130, 150, 30);

        JLabel pass = new JLabel("Password");
        pass.setBounds(50, 180, 100, 30);
        t_pass = new JPasswordField();
        t_pass.setBounds(170, 180, 150, 30);

        JLabel conpass = new JLabel("Confirm Password");
        conpass.setBounds(50, 230, 150, 30);
        t_conpass = new JPasswordField();
        t_conpass.setBounds(170, 230, 150, 30);

        btnSubmit = new JButton("Register");
        btnSubmit.setBounds(50, 290, 100, 30);
        btnSubmit.addActionListener(this);

        btnClear = new JButton("Clear");
        btnClear.setBounds(160, 290, 100, 30);
        btnClear.addActionListener(e -> {
            nam.setText("");
            t_mail.setText("");
            t_pass.setText("");
            t_conpass.setText("");
        });

        btnLogin = new JButton("Login");
        btnLogin.setBounds(270, 290, 100, 30);
        btnLogin.addActionListener(e -> {
            fr.dispose();
            new login();
        });

        fr.add(res); fr.add(name); fr.add(nam);
        fr.add(mail); fr.add(t_mail);
        fr.add(pass); fr.add(t_pass);
        fr.add(conpass); fr.add(t_conpass);
        fr.add(btnSubmit); fr.add(btnClear); fr.add(btnLogin);

        fr.setLayout(null);
        fr.setSize(450, 400);
        fr.setLocationRelativeTo(null);
        fr.setVisible(true);
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        new register();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String username = nam.getText().trim();
        String email = t_mail.getText().trim();
        String password = new String(t_pass.getPassword()).trim();
        String confirm = new String(t_conpass.getPassword()).trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(fr, "Please fill all fields.");
            return;
        }

        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(fr, "Passwords do not match.");
            return;
        }

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/finance_db", "root", "root")) {
            String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, email);
            pst.setString(3, password);

            int result = pst.executeUpdate();

            if (result > 0) {
                JOptionPane.showMessageDialog(fr, "Registration successful! Redirecting to login...");
                fr.dispose();
                new login();
            } else {
                JOptionPane.showMessageDialog(fr, "Registration failed.");
            }
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(fr, "Username or email already exists.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(fr, "Error: " + ex.getMessage());
        }
    }
}
