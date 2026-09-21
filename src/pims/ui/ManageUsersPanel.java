package pims.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import pims.dao.UserDAO;
import pims.model.User;

public class ManageUsersPanel extends JPanel {

    private final UserDAO dao = new UserDAO();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID","Username","Full Name"}, 0);
    private final JTable table = new JTable(model);

    private final JTextField userF = new JTextField();
    private final JTextField passF = new JTextField();
    private final JTextField nameF = new JTextField();

    public ManageUsersPanel() {
        setLayout(new BorderLayout(8,8));

        JPanel form = new JPanel(new GridLayout(2, 4, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Create Cashier Account"));
        form.add(new JLabel("Username")); form.add(userF);
        form.add(new JLabel("Password")); form.add(passF);
        form.add(new JLabel("Full Name")); form.add(nameF);
        form.add(new JLabel()); form.add(new JLabel());

        JPanel btns = new JPanel();
        JButton addB = new JButton("Add Cashier");
        JButton delB = new JButton("Delete");
        JButton refB = new JButton("Refresh");
        btns.add(addB); btns.add(delB); btns.add(refB);

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.CENTER);
        top.add(btns, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        addB.addActionListener(e -> addUser());
        delB.addActionListener(e -> deleteUser());
        refB.addActionListener(e -> refresh());

        refresh();
    }

    private void refresh() {
        try {
            model.setRowCount(0);
            for (User u : dao.getAllCashiers())
                model.addRow(new Object[]{u.getUserId(), u.getUsername(), u.getFullName()});
        } catch (Exception ex) { err(ex); }
    }

    private void addUser() {
        try {
            if (userF.getText().isBlank() || passF.getText().isBlank() || nameF.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "All fields required.");
                return;
            }
            dao.addCashier(userF.getText().trim(), passF.getText().trim(), nameF.getText().trim());
            userF.setText(""); passF.setText(""); nameF.setText("");
            refresh();
        } catch (Exception ex) { err(ex); }
    }

    private void deleteUser() {
        int r = table.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Select a cashier."); return; }
        int id = (int) model.getValueAt(r, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete cashier ID " + id + "?",
                "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try { dao.deleteUser(id); refresh(); }
        catch (Exception ex) { err(ex); }
    }

    private void err(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}