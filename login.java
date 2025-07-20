import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class login implements ActionListener {
    JLabel log, user, pas;
    JTextField mail;
    JPasswordField ent_pass;
    JButton lo, clear;

    JFrame fr;

    public login() {
        fr = new JFrame("Login");
        log = new JLabel("Enter Your Details");
        log.setBounds(190, 110, 200, 70);

        user = new JLabel("Enter your Username");
        user.setBounds(120, 200, 200, 30);
        mail = new JTextField("");
        mail.setBounds(270, 200, 150, 35);

        pas = new JLabel("Enter your Password");
        pas.setBounds(120, 250, 200, 30);
        ent_pass = new JPasswordField("");
        ent_pass.setBounds(270, 250, 150, 35);

        lo = new JButton("Login");
        lo.setBounds(170, 320, 90, 30);
        lo.addActionListener(this);

        clear = new JButton("Clear");
        clear.setBounds(270, 320, 90, 30);
        clear.addActionListener(e -> {
            mail.setText("");
            ent_pass.setText("");
        });

        JButton registerBtn = new JButton("Register");
registerBtn.setBounds(170, 360, 200, 30);
registerBtn.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        // Close the login window
        ((JFrame) SwingUtilities.getWindowAncestor(registerBtn)).dispose();
        // Open the register window
        new register();
    }
});
fr.add(registerBtn);


        fr.add(lo);
        fr.add(clear);
        fr.add(log);
        fr.add(user);
        fr.add(mail);
        fr.add(pas);
        fr.add(ent_pass);

        fr.setLayout(null);
        fr.setSize(500, 500);
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fr.setVisible(true);
    }

    public static void main(String[] args) {
        new login();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String username = mail.getText();
        String password = new String(ent_pass.getPassword());

        if (validateLogin(username, password)) {
            fr.dispose(); // Close login window
            new HomePage(username); // Open Home Page
        } else {
            JOptionPane.showMessageDialog(fr, "Invalid login. Try again.");
        }
    }

    private boolean validateLogin(String username, String password) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/finance_db", "root", "root")) {
            PreparedStatement pst = con.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();
            return rs.next(); // If user found
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(fr, "Database error: " + ex.getMessage());
            return false;
        }
    }
}
