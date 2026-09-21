package pims.dao;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import pims.db.DBConnection;

public class ReportDAO {

    /** Sales Report: every sale in a date range. */
    public DefaultTableModel getSalesReport(String from, String to) throws SQLException {
        DefaultTableModel m = new DefaultTableModel(
                new String[]{"Sale ID","Date","Cashier","Total"}, 0);
        String sql = "SELECT s.sale_id, s.sale_date, u.full_name, s.total_amount " +
                     "FROM sales s LEFT JOIN users u ON s.user_id=u.user_id " +
                     "WHERE DATE(s.sale_date) BETWEEN ? AND ? ORDER BY s.sale_date DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, from); ps.setString(2, to);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    m.addRow(new Object[]{rs.getInt(1), rs.getTimestamp(2),
                            rs.getString(3), rs.getDouble(4)});
                }
            }
        }
        return m;
    }

    /** Item-Wise Report: quantity sold + revenue per medicine. */
    public DefaultTableModel getItemWiseReport() throws SQLException {
        DefaultTableModel m = new DefaultTableModel(
                new String[]{"Medicine","Quantity Sold","Revenue"}, 0);
        String sql = "SELECT me.name, SUM(si.quantity_sold) qty, SUM(si.quantity_sold*si.price_at_sale) rev " +
                     "FROM sale_items si JOIN medicines me ON si.medicine_id=me.medicine_id " +
                     "GROUP BY me.name ORDER BY qty DESC";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) m.addRow(new Object[]{rs.getString(1), rs.getInt(2), rs.getDouble(3)});
        }
        return m;
    }

    /** Low Stock Report: quantity <= reorder level. */
    public DefaultTableModel getLowStockReport() throws SQLException {
        DefaultTableModel m = new DefaultTableModel(
                new String[]{"Medicine","Stock","Reorder Level"}, 0);
        String sql = "SELECT name, quantity_in_stock, reorder_level FROM medicines " +
                     "WHERE quantity_in_stock <= reorder_level ORDER BY quantity_in_stock";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) m.addRow(new Object[]{rs.getString(1), rs.getInt(2), rs.getInt(3)});
        }
        return m;
    }

    /** Expiry Report: items expiring within next 30 days. */
    public DefaultTableModel getExpiryReport() throws SQLException {
        DefaultTableModel m = new DefaultTableModel(
                new String[]{"Medicine","Expiry Date","Stock"}, 0);
        String sql = "SELECT name, expiry_date, quantity_in_stock FROM medicines " +
                     "WHERE expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 1 MONTH) " +
                     "ORDER BY expiry_date";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) m.addRow(new Object[]{rs.getString(1), rs.getDate(2), rs.getInt(3)});
        }
        return m;
    }
}