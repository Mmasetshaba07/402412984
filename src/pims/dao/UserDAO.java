package pims.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import pims.db.DBConnection;
import pims.model.User;

public class UserDAO {

    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username=? AND password=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("user_id"), rs.getString("username"),
                            rs.getString("password"), rs.getString("role"),
                            rs.getString("full_name"));
                }
            }
        }
        return null;
    }

    public List<User> getAllCashiers() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role='Cashier' ORDER BY full_name";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new User(rs.getInt("user_id"), rs.getString("username"),
                        rs.getString("password"), rs.getString("role"),
                        rs.getString("full_name")));
            }
        }
        return list;
    }

    public boolean addCashier(String username, String password, String fullName) throws SQLException {
        String sql = "INSERT INTO users (username, password, role, full_name) VALUES (?,?, 'Cashier', ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, fullName);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id=? AND role='Cashier'";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }
}