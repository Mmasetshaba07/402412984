package pims.ui;

import java.awt.*;
import java.sql.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import pims.dao.MedicineDAO;
import pims.dao.SupplierDAO;
import pims.model.Medicine;
import pims.model.Supplier;

public class ManageMedicinesPanel extends JPanel {

    private final MedicineDAO dao = new MedicineDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID","Name","Company","Type","Price","Stock","Reorder","Expiry","Supplier"}, 0);
    private final JTable table = new JTable(model);

    private final JTextField nameF = new JTextField();
    private final JTextField companyF = new JTextField();
    private final JComboBox<String> typeF = new JComboBox<>(
            new String[]{"Tablet","Capsule","Syrup","Injection","Cream"});
    private final JTextField priceF = new JTextField();
    private final JTextField stockF = new JTextField();
    private final JTextField reorderF = new JTextField();
    private final JTextField expiryF = new JTextField("yyyy-mm-dd");
    private final JComboBox<Supplier> supplierF = new JComboBox<>();

    public ManageMedicinesPanel() {
        setLayout(new BorderLayout(8,8));

        JPanel form = new JPanel(new GridLayout(3, 6, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Medicine Details"));
        form.add(new JLabel("Name"));     form.add(nameF);
        form.add(new JLabel("Company"));  form.add(companyF);
        form.add(new JLabel("Type"));     form.add(typeF);
        form.add(new JLabel("Price"));    form.add(priceF);
        form.add(new JLabel("Stock"));    form.add(stockF);
        form.add(new JLabel("Reorder"));  form.add(reorderF);
        form.add(new JLabel("Expiry"));   form.add(expiryF);
        form.add(new JLabel("Supplier")); form.add(supplierF);
        form.add(new JLabel());           form.add(new JLabel());

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

        addB.addActionListener(e -> addMedicine());
        updB.addActionListener(e -> updateMedicine());
        delB.addActionListener(e -> deleteMedicine());
        clrB.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) fillFormFromRow();
        });

        loadSuppliers();
        refresh();
    }

    private void loadSuppliers() {
        try {
            supplierF.removeAllItems();
            for (Supplier s : supplierDAO.getAll()) supplierF.addItem(s);
        } catch (Exception ex) { showErr(ex); }
    }

    private void refresh() {
        try {
            model.setRowCount(0);
            for (Medicine m : dao.getAll()) {
                model.addRow(new Object[]{m.getMedicineId(), m.getName(), m.getCompany(),
                        m.getMedicineType(), m.getPrice(), m.getQuantityInStock(),
                        m.getReorderLevel(), m.getExpiryDate(), m.getSupplierId()});
            }
        } catch (Exception ex) { showErr(ex); }
    }

    private void addMedicine() {
        try {
            Medicine m = buildFromForm(0);
            if (dao.add(m)) { refresh(); clearForm(); }
        } catch (Exception ex) { showErr(ex); }
    }

    private void updateMedicine() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a medicine."); return; }
        try {
            int id = (int) model.getValueAt(row, 0);
            Medicine m = buildFromForm(id);
            if (dao.update(m)) { refresh(); clearForm(); }
        } catch (Exception ex) { showErr(ex); }
    }

    private void deleteMedicine() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a medicine."); return; }
        int id = (int) model.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete medicine ID " + id + "?",
                "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try { if (dao.delete(id)) { refresh(); clearForm(); } }
        catch (Exception ex) { showErr(ex); }
    }

    private Medicine buildFromForm(int id) {
        Supplier sup = (Supplier) supplierF.getSelectedItem();
        return new Medicine(id,
                nameF.getText().trim(), companyF.getText().trim(),
                (String) typeF.getSelectedItem(),
                Double.parseDouble(priceF.getText().trim()),
                Integer.parseInt(stockF.getText().trim()),
                Integer.parseInt(reorderF.getText().trim()),
                Date.valueOf(expiryF.getText().trim()),
                sup == null ? 0 : sup.getSupplierId());
    }

    private void fillFormFromRow() {
        int r = table.getSelectedRow();
        nameF.setText(String.valueOf(model.getValueAt(r,1)));
        companyF.setText(String.valueOf(model.getValueAt(r,2)));
        typeF.setSelectedItem(String.valueOf(model.getValueAt(r,3)));
        priceF.setText(String.valueOf(model.getValueAt(r,4)));
        stockF.setText(String.valueOf(model.getValueAt(r,5)));
        reorderF.setText(String.valueOf(model.getValueAt(r,6)));
        expiryF.setText(String.valueOf(model.getValueAt(r,7)));
        int supId = Integer.parseInt(String.valueOf(model.getValueAt(r,8)));
        for (int i = 0; i < supplierF.getItemCount(); i++) {
            if (supplierF.getItemAt(i).getSupplierId() == supId) {
                supplierF.setSelectedIndex(i); break;
            }
        }
    }

    private void clearForm() {
        nameF.setText(""); companyF.setText(""); priceF.setText("");
        stockF.setText(""); reorderF.setText(""); expiryF.setText("yyyy-mm-dd");
        typeF.setSelectedIndex(0);
        table.clearSelection();
    }

    private void showErr(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}