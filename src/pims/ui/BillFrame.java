package pims.ui;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import pims.model.SaleItem;
import pims.model.User;

public class BillFrame extends JFrame {

    public BillFrame(int saleId, List<SaleItem> items, User cashier) {
        setTitle("Bill #" + saleId);
        setSize(420, 500);
        setLocationRelativeTo(null);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));

        StringBuilder sb = new StringBuilder();
        sb.append("======= Pharmacy Information System =======\n");
        sb.append("Bill No : ").append(saleId).append("\n");
        sb.append("Date    : ").append(new Date()).append("\n");
        sb.append("Cashier : ").append(cashier.getFullName()).append("\n");
        sb.append("----------------------------------------\n");
        sb.append(String.format("%-18s %4s %8s %9s%n", "Item", "Qty", "Price", "Total"));
        sb.append("----------------------------------------\n");

        double total = 0;
        for (SaleItem it : items) {
            sb.append(String.format("%-18s %4d %8.2f %9.2f%n",
                    trim(it.getMedicineName(), 18),
                    it.getQuantitySold(), it.getPriceAtSale(), it.getLineTotal()));
            total += it.getLineTotal();
        }
        sb.append("----------------------------------------\n");
        sb.append(String.format("GRAND TOTAL: R%.2f%n", total));
        sb.append("========================================\n");
        sb.append("       Thank you. Get well soon!\n");

        area.setText(sb.toString());
        add(new JScrollPane(area), BorderLayout.CENTER);

        JButton saveB = new JButton("Save Bill");
        JButton closeB = new JButton("Close");
        JPanel btns = new JPanel();
        btns.add(saveB); btns.add(closeB);
        add(btns, BorderLayout.SOUTH);

        saveB.addActionListener(e -> {
            try (FileWriter fw = new FileWriter("bill_" + saleId + ".txt")) {
                fw.write(sb.toString());
                JOptionPane.showMessageDialog(this, "Saved as bill_" + saleId + ".txt");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage());
            }
        });
        closeB.addActionListener(e -> dispose());
    }

    private static String trim(String s, int n) {
        return s.length() <= n ? s : s.substring(0, n);
    }
}