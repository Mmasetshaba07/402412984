package pims.ui;

import java.awt.*;
import javax.swing.*;
import pims.model.User;

public class CashierDashboard extends JFrame {

    public CashierDashboard(User cashier) {
        setTitle("Pharmacy Information System - Cashier (" + cashier.getFullName() + ")");
        setSize(1000, 640);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });

        JPanel top = new JPanel(new BorderLayout());
        JLabel welcome = new JLabel("  Welcome, " + cashier.getFullName() + " (Cashier)");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 14));
        top.add(welcome, BorderLayout.WEST);
        top.add(logout, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);
        add(new POSPanel(cashier), BorderLayout.CENTER);
    }
}