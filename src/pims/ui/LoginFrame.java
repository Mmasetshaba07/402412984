package pims.ui;

import java.awt.*;
import javax.swing.*;
import pims.dao.UserDAO;
import pims.model.User;

public class LoginFrame extends JFrame {

    private final JTextField usernameField = new JTextField(18);
    private final JPasswordField passwordField = new JPasswordField(18);

    public LoginFrame() {
        setTitle("Pharmacy Information System - Login");
        setSize(400, 240);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Pharmacy Information System");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        add(title, g);

        g.gridwidth = 1;
        g.gridx = 0; g.gridy = 1; add(new JLabel("Username:"), g);
        g.gridx = 1; add(usernameField, g);

        g.gridx = 0; g.gridy = 2; add(new JLabel("Password:"), g);
        g.gridx = 1; add(passwordField, g);

        JButton loginBtn = new JButton("Login");
        JPanel btnPanel = new JPanel();
        btnPanel.add(loginBtn);

        g.gridx = 0; g.gridy = 3; g.gridwidth = 2;
        add(btnPanel, g);

        loginBtn.addActionListener(e -> doLogin());
        getRootPane().setDefaultButton(loginBtn);
    }

    private void doLogin() {
        String u = usernameField.getText().trim();
        String p = new String(passwordField.getPassword());
        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.");
            return;
        }
        try {
            User user = new UserDAO().authenticate(u, p);
            if (user == null) {
                JOptionPane.showMessageDialog(this, "Invalid credentials.",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            dispose();
            if (user.isAdmin()) new AdminDashboard(user).setVisible(true);
            else new CashierDashboard(user).setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}