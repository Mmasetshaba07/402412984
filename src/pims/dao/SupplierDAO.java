package pims.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import pims.db.DBConnection;
import pims.model.Supplier;

public class SupplierDAO {

    public List<Supplier> getAll() throws SQLException {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM suppliers ORDER BY name";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public boolean add(Supplier s) throws SQLException {
        String sql = "INSERT INTO suppliers (name, contact_person, phone, email, address) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, s.getName()); ps.setString(2, s.getContactPerson());
            ps.setString(3, s.getPhone()); ps.setString(4, s.getEmail());
            ps.setString(5, s.getAddress());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Supplier s) throws SQLException {
        String sql = "UPDATE suppliers SET name=?, contact_person=?, phone=?, email=?, address=? WHERE supplier_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, s.getName()); ps.setString(2, s.getContactPerson());
            ps.setString(3, s.getPhone()); ps.setString(4, s.getEmail());
            ps.setString(5, s.getAddress()); ps.setInt(6, s.getSupplierId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM suppliers WHERE supplier_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Supplier map(ResultSet rs) throws SQLException {
        return new Supplier(rs.getInt("supplier_id"), rs.getString("name"),
                rs.getString("contact_person"), rs.getString("phone"),
                rs.getString("email"), rs.getString("address"));
    }
}