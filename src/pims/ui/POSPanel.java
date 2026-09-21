package pims.ui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import pims.dao.MedicineDAO;
import pims.dao.SaleDAO;
import pims.model.Medicine;
import pims.model.SaleItem;
import pims.model.User;

public class POSPanel extends JPanel {

    private final MedicineDAO medDAO = new MedicineDAO();
    private final SaleDAO saleDAO = new SaleDAO();
    private final User cashier;

    private final JTextField searchF = new JTextField(20);
    private final JTable stockTable = new JTable(new DefaultTableModel(
            new String[]{"ID","Name","Price","Stock"}, 0));
    private final DefaultTableModel cartModel = new DefaultTableModel(
            new String[]{"Medicine","Qty","Unit Price","Line Total"}, 0);
    private final JTable cartTable = new JTable(cartModel);
    private final JLabel totalLbl = new JLabel("Total: R0.00");
    private final List<SaleItem> cart = new ArrayList<>();

    public POSPanel(User cashier) {
        this.cashier = cashier;
        setLayout(new BorderLayout(8,8));

        JPanel searchP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchP.add(new JLabel("Search medicine:"));
        searchP.add(searchF);
        JButton searchB = new JButton("Search");
        JButton allB = new JButton("Show All");
        searchP.add(searchB); searchP.add(allB);
        add(searchP, BorderLayout.NORTH);

        JPanel left = new JPanel(new BorderLayout());
        left.setBorder(BorderFactory.createTitledBorder("Available Medicines"));
        left.add(new JScrollPane(stockTable), BorderLayout.CENTER);
        JButton addToCartB = new JButton("Add to Cart →");
        left.add(addToCartB, BorderLayout.SOUTH);

        JPanel right = new JPanel(new BorderLayout());
        right.setBorder(BorderFactory.createTitledBorder("Cart"));
        right.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JPanel cartBtns = new JPanel();
        JButton removeB   = new JButton("Remove Item");
        JButton clearB    = new JButton("Clear Cart");
        JButton checkoutB = new JButton("Checkout");
        cartBtns.add(removeB); cartBtns.add(clearB); cartBtns.add(checkoutB);
        right.add(cartBtns, BorderLayout.SOUTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 8, 8));
        center.add(left); center.add(right);
        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        bottom.add(totalLbl);
        add(bottom, BorderLayout.SOUTH);

        searchB.addActionListener(e -> doSearch());
        allB.addActionListener(e -> loadAll());
        addToCartB.addActionListener(e -> addToCart());
        removeB.addActionListener(e -> removeFromCart());
        clearB.addActionListener(e -> { cart.clear(); cartModel.setRowCount(0); updateTotal(); });
        checkoutB.addActionListener(e -> checkout());

        loadAll();
    }

    private void loadAll() {
        try {
            ((DefaultTableModel) stockTable.getModel()).setRowCount(0);
            for (Medicine m : medDAO.getAll()) addRow(m);
        } catch (Exception ex) { err(ex); }
    }

    private void doSearch() {
        try {
            ((DefaultTableModel) stockTable.getModel()).setRowCount(0);
            for (Medicine m : medDAO.search(searchF.getText().trim())) addRow(m);
        } catch (Exception ex) { err(ex); }
    }

    private void addRow(Medicine m) {
        ((DefaultTableModel) stockTable.getModel()).addRow(new Object[]{
                m.getMedicineId(), m.getName(), m.getPrice(), m.getQuantityInStock()});
    }

    private void addToCart() {
        int r = stockTable.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Select a medicine."); return; }

        int id = (int) stockTable.getValueAt(r, 0);
        String name = (String) stockTable.getValueAt(r, 1);
        double price = ((Number) stockTable.getValueAt(r, 2)).doubleValue();
        int stock = (int) stockTable.getValueAt(r, 3);

        String input = JOptionPane.showInputDialog(this, "Quantity for " + name + ":", "1");
        if (input == null) return;
        int qty;
        try { qty = Integer.parseInt(input.trim()); }
        catch (NumberFormatException e) { JOptionPane.showMessageDialog(this, "Invalid quantity."); return; }
        if (qty <= 0) { JOptionPane.showMessageDialog(this, "Quantity must be > 0."); return; }

        int already = 0;
        for (SaleItem it : cart) if (it.getMedicineId() == id) already += it.getQuantitySold();
        if (qty + already > stock) {
            JOptionPane.showMessageDialog(this, "Only " + stock + " in stock (cart already has " + already + ").");
            return;
        }

        SaleItem item = new SaleItem(id, name, qty, price);
        cart.add(item);
        cartModel.addRow(new Object[]{name, qty, price, item.getLineTotal()});
        updateTotal();
    }

    private void removeFromCart() {
        int r = cartTable.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Select a cart item."); return; }
        cart.remove(r);
        cartModel.removeRow(r);
        updateTotal();
    }

    private void updateTotal() {
        double total = 0;
        for (SaleItem it : cart) total += it.getLineTotal();
        totalLbl.setText(String.format("Total: R%.2f", total));
    }

    private void checkout() {
        if (cart.isEmpty()) { JOptionPane.showMessageDialog(this, "Cart is empty."); return; }
        try {
            int saleId = saleDAO.createSale(cart, cashier.getUserId());
            new BillFrame(saleId, cart, cashier).setVisible(true);
            cart.clear(); cartModel.setRowCount(0); updateTotal();
            loadAll();
        } catch (Exception ex) { err(ex); }
    }

    private void err(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}