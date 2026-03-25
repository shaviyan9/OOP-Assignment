package dao;

import db.DBConnection;
import models.Company;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompanyDAO {

    private Connection conn() { return DBConnection.getConnection(); }

    public Company login(String email, String password) {
        String sql = "SELECT * FROM companies WHERE email=? AND password=? AND status='active'";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) {
            System.err.println("CompanyDAO.login: " + e.getMessage());
        }
        return null;
    }

    public int add(Company c) {
        String sql = "INSERT INTO companies (company_name,email,password,industry,description,website,hr_contact) " +
                     "VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getCompanyName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getPassword());
            ps.setString(4, c.getIndustry());
            ps.setString(5, c.getDescription());
            ps.setString(6, c.getWebsite());
            ps.setString(7, c.getHrContact());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("CompanyDAO.add: " + e.getMessage());
        }
        return -1;
    }

    public boolean update(Company c) {
        String sql = "UPDATE companies SET company_name=?,email=?,industry=?,description=?,website=?,hr_contact=?,status=? WHERE id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, c.getCompanyName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getIndustry());
            ps.setString(4, c.getDescription());
            ps.setString(5, c.getWebsite());
            ps.setString(6, c.getHrContact());
            ps.setString(7, c.getStatus());
            ps.setInt(8, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("CompanyDAO.update: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int id) {
        try (PreparedStatement ps = conn().prepareStatement("DELETE FROM companies WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("CompanyDAO.delete: " + e.getMessage());
        }
        return false;
    }

    public Company getById(int id) {
        try (PreparedStatement ps = conn().prepareStatement("SELECT * FROM companies WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) {
            System.err.println("CompanyDAO.getById: " + e.getMessage());
        }
        return null;
    }

    public List<Company> getAll() {
        List<Company> list = new ArrayList<>();
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM companies ORDER BY company_name")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("CompanyDAO.getAll: " + e.getMessage());
        }
        return list;
    }

    /** Update only the company's own profile (no status, no password change here). */
    public boolean updateDetails(Company c) {
        String sql = "UPDATE companies SET company_name=?,industry=?,description=?,website=?,hr_contact=? WHERE id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, c.getCompanyName());
            ps.setString(2, c.getIndustry());
            ps.setString(3, c.getDescription());
            ps.setString(4, c.getWebsite());
            ps.setString(5, c.getHrContact());
            ps.setInt(6, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("CompanyDAO.updateDetails: " + e.getMessage());
        }
        return false;
    }

    private Company map(ResultSet rs) throws SQLException {
        Company c = new Company();
        c.setId(rs.getInt("id"));
        c.setCompanyName(rs.getString("company_name"));
        c.setEmail(rs.getString("email"));
        c.setPassword(rs.getString("password"));
        c.setIndustry(rs.getString("industry"));
        c.setDescription(rs.getString("description"));
        c.setWebsite(rs.getString("website"));
        c.setHrContact(rs.getString("hr_contact"));
        c.setStatus(rs.getString("status"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        return c;
    }
}
