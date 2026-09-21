package pims.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import pims.dao.SupplierDAO;
import pims.model.Supplier;

public class ManageSuppliersPanel extends JPanel {

    private final SupplierDAO dao = new SupplierDAO();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID","Name","Contact","Phone","Email","Address"}, 0);
    private final JTable table = new JTable(model);

    private final JTextField nameF = new JTextField();
    private final JTextField contactF = new JTextField();
    private final JTextField phoneF = new JTextField();
    private final JTextField emailF = new JTextField();
    private final JTextField addressF = new JTextField();

    public ManageSuppliersPanel() {
        setLayout(new BorderLayout(8,8));

        JPanel form = new JPanel(new GridLayout(3, 4, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Supplier Details"));
        form.add(new JLabel("Name"));    form.add(nameF);
        form.add(new JLabel("Contact")); form.add(contactF);
        form.add(new JLabel("Phone"));   form.add(phoneF);
        form.add(new JLabel("Email"));   form.add(emailF);
        form.add(new JLabel("Address")); form.add(addressF);
        form.add(new JLabel());          form.add(new JLabel());

        JPanel btns = new JPanel();
        JButton addB = new JButton("Add");
        JButton updB = new JButton("Update");
        JButton delB = new JButton("Delete");
        JButton clrB = new JButton("Clear");
        btns.add(addB); btns.add(updB); btns.add(delB); btns.add(clrB);

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.CENTER);
        top.add(btns, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        addB.addActionListener(e -> add());
        updB.addActionListener(e -> update());
        delB.addActionListener(e -> delete());
        clrB.addActionListener(e -> clear());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) fill();
        });

        refresh();
    }

    private void refresh() {
        try {
            model.setRowCount(0);
            for (Supplier s : dao.getAll())
                model.addRow(new Object[]{s.getSupplierId(), s.getName(), s.getContactPerson(),
                        s.getPhone(), s.getEmail(), s.getAddress()});
        } catch (Exception ex) { err(ex); }
    }

    private void add() {
        try {
            dao.add(new Supplier(0, nameF.getText(), contactF.getText(),
                    phoneF.getText(), emailF.getText(), addressF.getText()));
            refresh(); clear();
        } catch (Exception ex) { err(ex); }
    }

    private void update() {
        int r = table.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Select a supplier."); return; }
        try {
            int id = (int) model.getValueAt(r, 0);
            dao.update(new Supplier(id, nameF.getText(), contactF.getText(),
                    phoneF.getText(), emailF.getText(), addressF.getText()));
            refresh(); clear();
        } catch (Exception ex) { err(ex); }
    }

    private void delete() {
        int r = table.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Select a supplier."); return; }
        int id = (int) model.getValueAt(r, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete supplier ID " + id + "?",
                "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try { dao.delete(id); refresh(); clear(); }
        catch (Exception ex) { err(ex); }
    }

    private void fill() {
        int r = table.getSelectedRow();
        nameF.setText(String.valueOf(model.getValueAt(r,1)));
        contactF.setText(String.valueOf(model.getValueAt(r,2)));
        phoneF.setText(String.valueOf(model.getValueAt(r,3)));
        emailF.setText(String.valueOf(model.getValueAt(r,4)));
        addressF.setText(String.valueOf(model.getValueAt(r,5)));
    }

    private void clear() {
        nameF.setText(""); contactF.setText(""); phoneF.setText("");
        emailF.setText(""); addressF.setText("");
        table.clearSelection();
    }

    private void err(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}