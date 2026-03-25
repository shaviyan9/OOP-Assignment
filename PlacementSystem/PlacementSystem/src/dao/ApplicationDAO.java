package dao;

import db.DBConnection;
import models.Application;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDAO {

    private Connection conn() { return DBConnection.getConnection(); }

    /** Student applies for a job. Returns generated id or -1. */
    public int apply(int studentId, int jobId, String coverNote) {
        String sql = "INSERT INTO applications (student_id,job_id,cover_note) VALUES (?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, studentId);
            ps.setInt(2, jobId);
            ps.setString(3, coverNote);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("ApplicationDAO.apply: " + e.getMessage());
        }
        return -1;
    }

    /** Check if student already applied. */
    public boolean hasApplied(int studentId, int jobId) {
        try (PreparedStatement ps = conn().prepareStatement(
                "SELECT id FROM applications WHERE student_id=? AND job_id=?")) {
            ps.setInt(1, studentId);
            ps.setInt(2, jobId);
            return ps.executeQuery().next();
        } catch (SQLException e) { return false; }
    }

    /** Remove a job application (student optional feature). */
    public boolean remove(int studentId, int jobId) {
        try (PreparedStatement ps = conn().prepareStatement(
                "DELETE FROM applications WHERE student_id=? AND job_id=?")) {
            ps.setInt(1, studentId);
            ps.setInt(2, jobId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ApplicationDAO.remove: " + e.getMessage());
        }
        return false;
    }

    /** All applications for a specific student. */
    public List<Application> getByStudent(int studentId) {
        String sql = "SELECT a.*, s.full_name AS student_name, j.title AS job_title, c.company_name " +
                     "FROM applications a " +
                     "JOIN students s ON a.student_id=s.id " +
                     "JOIN jobs j ON a.job_id=j.id " +
                     "JOIN companies c ON j.company_id=c.id " +
                     "WHERE a.student_id=? ORDER BY a.applied_at DESC";
        return queryWithId(sql, studentId);
    }

    /** All applicants for a specific job (company view). */
    public List<Application> getByJob(int jobId) {
        String sql = "SELECT a.*, s.full_name AS student_name, j.title AS job_title, c.company_name " +
                     "FROM applications a " +
                     "JOIN students s ON a.student_id=s.id " +
                     "JOIN jobs j ON a.job_id=j.id " +
                     "JOIN companies c ON j.company_id=c.id " +
                     "WHERE a.job_id=? ORDER BY a.applied_at DESC";
        return queryWithId(sql, jobId);
    }

    /** All applications (admin view). */
    public List<Application> getAll() {
        String sql = "SELECT a.*, s.full_name AS student_name, j.title AS job_title, c.company_name " +
                     "FROM applications a " +
                     "JOIN students s ON a.student_id=s.id " +
                     "JOIN jobs j ON a.job_id=j.id " +
                     "JOIN companies c ON j.company_id=c.id " +
                     "ORDER BY a.applied_at DESC";
        List<Application> list = new ArrayList<>();
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("ApplicationDAO.getAll: " + e.getMessage());
        }
        return list;
    }

    /** Company accepts or rejects an application. */
    public boolean updateStatus(int applicationId, String status) {
        try (PreparedStatement ps = conn().prepareStatement(
                "UPDATE applications SET status=? WHERE id=?")) {
            ps.setString(1, status);
            ps.setInt(2, applicationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ApplicationDAO.updateStatus: " + e.getMessage());
        }
        return false;
    }

    private List<Application> queryWithId(String sql, int id) {
        List<Application> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("ApplicationDAO.queryWithId: " + e.getMessage());
        }
        return list;
    }

    private Application map(ResultSet rs) throws SQLException {
        Application a = new Application();
        a.setId(rs.getInt("id"));
        a.setStudentId(rs.getInt("student_id"));
        a.setJobId(rs.getInt("job_id"));
        a.setStudentName(rs.getString("student_name"));
        a.setJobTitle(rs.getString("job_title"));
        a.setCompanyName(rs.getString("company_name"));
        a.setStatus(rs.getString("status"));
        a.setCoverNote(rs.getString("cover_note"));
        a.setAppliedAt(rs.getTimestamp("applied_at"));
        a.setUpdatedAt(rs.getTimestamp("updated_at"));
        return a;
    }
}
