package pims.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import pims.db.DBConnection;
import pims.model.Medicine;

public class MedicineDAO {

    public List<Medicine> getAll() throws SQLException {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT * FROM medicines ORDER BY name";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Medicine> search(String keyword) throws SQLException {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT * FROM medicines WHERE name LIKE ? ORDER BY name";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public boolean add(Medicine m) throws SQLException {
        String sql = "INSERT INTO medicines (name, company, medicine_type, price, quantity_in_stock, reorder_level, expiry_date, supplier_id) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            bind(ps, m);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Medicine m) throws SQLException {
        String sql = "UPDATE medicines SET name=?, company=?, medicine_type=?, price=?, quantity_in_stock=?, reorder_level=?, expiry_date=?, supplier_id=? WHERE medicine_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            bind(ps, m);
            ps.setInt(9, m.getMedicineId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM medicines WHERE medicine_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /** Reduce stock after a sale. */
    public boolean reduceStock(int medicineId, int qty) throws SQLException {
        String sql = "UPDATE medicines SET quantity_in_stock = quantity_in_stock - ? WHERE medicine_id=? AND quantity_in_stock >= ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, qty); ps.setInt(2, medicineId); ps.setInt(3, qty);
            return ps.executeUpdate() > 0;
        }
    }

    private void bind(PreparedStatement ps, Medicine m) throws SQLException {
        ps.setString(1, m.getName()); ps.setString(2, m.getCompany());
        ps.setString(3, m.getMedicineType()); ps.setDouble(4, m.getPrice());
        ps.setInt(5, m.getQuantityInStock()); ps.setInt(6, m.getReorderLevel());
        ps.setDate(7, m.getExpiryDate());
        if (m.getSupplierId() > 0) ps.setInt(8, m.getSupplierId());
        else ps.setNull(8, Types.INTEGER);
    }

    private Medicine map(ResultSet rs) throws SQLException {
        return new Medicine(rs.getInt("medicine_id"), rs.getString("name"),
                rs.getString("company"), rs.getString("medicine_type"),
                rs.getDouble("price"), rs.getInt("quantity_in_stock"),
                rs.getInt("reorder_level"), rs.getDate("expiry_date"),
                rs.getInt("supplier_id"));
    }
}
// Medicine DAO
