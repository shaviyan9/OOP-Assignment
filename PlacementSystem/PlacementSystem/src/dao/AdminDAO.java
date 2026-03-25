package dao;

import db.DBConnection;
import models.Admin;

import java.sql.*;

public class AdminDAO {

    private Connection conn() { return DBConnection.getConnection(); }

    public Admin login(String username, String password) {
        String sql = "SELECT * FROM admins WHERE username=? AND password=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Admin a = new Admin();
                a.setId(rs.getInt("id"));
                a.setUsername(rs.getString("username"));
                a.setEmail(rs.getString("email"));
                a.setPassword(rs.getString("password"));
                return a;
            }
        } catch (SQLException e) {
            System.err.println("AdminDAO.login: " + e.getMessage());
        }
        return null;
    }
}
