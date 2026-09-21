package pims.ui;

import pims.dao.ReportDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ReportsPanel extends JPanel {

    private final ReportDAO dao = new ReportDAO();
    private final JTable table = new JTable();

    private final JTextField fromF = new JTextField("2024-01-01", 10);
    private final JTextField toF   = new JTextField("2030-12-31", 10);

    public ReportsPanel() {
        setLayout(new BorderLayout(8,8));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        controls.setBorder(BorderFactory.createTitledBorder("Reports"));

        JButton salesB  = new JButton("Sales Report");
        JButton itemB   = new JButton("Item-Wise");
        JButton lowB    = new JButton("Low Stock");
        JButton expB    = new JButton("Expiry (1 month)");

        controls.add(new JLabel("From:")); controls.add(fromF);
        controls.add(new JLabel("To:"));   controls.add(toF);
        controls.add(salesB); controls.add(itemB);
        controls.add(lowB);   controls.add(expB);

        add(controls, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        salesB.addActionListener(e -> run(() -> dao.getSalesReport(fromF.getText().trim(), toF.getText().trim())));
        itemB .addActionListener(e -> run(dao::getItemWiseReport));
        lowB  .addActionListener(e -> run(dao::getLowStockReport));
        expB  .addActionListener(e -> run(dao::getExpiryReport));
    }

    @FunctionalInterface interface Report { DefaultTableModel get() throws Exception; }

    private void run(Report r) {
        try { table.setModel(r.get()); }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}