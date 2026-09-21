package pims.dao;

import java.sql.*;
import java.util.List;
import pims.db.DBConnection;
import pims.model.SaleItem;

public class SaleDAO {

    /** Saves a sale + its items in one transaction, reduces stock, returns generated saleId. */
    public int createSale(List<SaleItem> items, int userId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        conn.setAutoCommit(false);
        try {
            double total = 0;
            for (SaleItem it : items) total += it.getLineTotal();

            int saleId;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO sales (total_amount, user_id) VALUES (?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setDouble(1, total);
                ps.setInt(2, userId);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    saleId = keys.getInt(1);
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO sale_items (sale_id, medicine_id, quantity_sold, price_at_sale) VALUES (?,?,?,?)")) {
                for (SaleItem it : items) {
                    ps.setInt(1, saleId);
                    ps.setInt(2, it.getMedicineId());
                    ps.setInt(3, it.getQuantitySold());
                    ps.setDouble(4, it.getPriceAtSale());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE medicines SET quantity_in_stock = quantity_in_stock - ? WHERE medicine_id=? AND quantity_in_stock >= ?")) {
                for (SaleItem it : items) {
                    ps.setInt(1, it.getQuantitySold());
                    ps.setInt(2, it.getMedicineId());
                    ps.setInt(3, it.getQuantitySold());
                    if (ps.executeUpdate() == 0)
                        throw new SQLException("Insufficient stock for: " + it.getMedicineName());
                }
            }

            conn.commit();
            return saleId;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
}