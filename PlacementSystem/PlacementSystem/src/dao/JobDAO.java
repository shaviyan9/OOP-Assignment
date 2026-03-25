package dao;

import db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Job;

public class JobDAO {

    private Connection conn() {
        return DBConnection.getConnection();
    }

    public int postJob(Job j) {
        String sql = "INSERT INTO jobs (company_id,title,description,eligibility,criteria,salary,location,deadline,job_type,status) "
                +
                "VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, j.getCompanyId());
            ps.setString(2, j.getTitle());
            ps.setString(3, j.getDescription());
            ps.setString(4, j.getEligibility());
            ps.setString(5, j.getCriteria());
            ps.setString(6, j.getSalary());
            ps.setString(7, j.getLocation());
            ps.setDate(8, j.getDeadline());
            ps.setString(9, j.getJobType());
            ps.setString(10, j.getStatus() != null ? j.getStatus() : "available");
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("JobDAO.postJob: " + e.getMessage());
        }
        return -1;
    }

    public boolean updateJob(Job j) {
        String sql = "UPDATE jobs SET title=?,description=?,eligibility=?,criteria=?,salary=?,location=?,deadline=?,status=? WHERE id=? AND company_id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, j.getTitle());
            ps.setString(2, j.getDescription());
            ps.setString(3, j.getEligibility());
            ps.setString(4, j.getCriteria());
            ps.setString(5, j.getSalary());
            ps.setString(6, j.getLocation());
            ps.setDate(7, j.getDeadline());
            ps.setString(8, j.getStatus());
            ps.setInt(9, j.getId());
            ps.setInt(10, j.getCompanyId());


            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("JobDAO.updateJob: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteJob(int jobId, int companyId) {
        String sql = "DELETE FROM jobs WHERE id=? AND company_id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, jobId);
            ps.setInt(2, companyId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("JobDAO.deleteJob: " + e.getMessage());
        }
        return false;
    }

    /** All available jobs (student view). */
    public List<Job> getAvailableJobs() {
        return queryJobs("SELECT j.*, c.company_name FROM jobs j " +
                "JOIN companies c ON j.company_id=c.id " +
                "WHERE j.status='available' ORDER BY j.created_at DESC");
    }

    /** All jobs for a specific company. */
    public List<Job> getJobsByCompany(int companyId) {
        String sql = "SELECT j.*, c.company_name FROM jobs j " +
                "JOIN companies c ON j.company_id=c.id " +
                "WHERE j.company_id=? ORDER BY j.created_at DESC";
        return queryWithId(sql, companyId);
    }

    /** Filter jobs by status for company. */
    public List<Job> filterJobsByStatus(int companyId, String status) {
        String sql = "SELECT j.*, c.company_name FROM jobs j " +
                "JOIN companies c ON j.company_id=c.id " +
                "WHERE j.company_id=? AND j.status=? ORDER BY j.created_at DESC";
        List<Job> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, companyId);
            ps.setString(2, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("JobDAO.filterJobsByStatus: " + e.getMessage());
        }
        return list;
    }

    public Job getById(int id) {
        String sql = "SELECT j.*, c.company_name FROM jobs j " +
                "JOIN companies c ON j.company_id=c.id WHERE j.id=?";
        List<Job> result = queryWithId(sql, id);
        return result.isEmpty() ? null : result.get(0);
    }

    /** All jobs (admin). */
    public List<Job> getAll() {
        return queryJobs("SELECT j.*, c.company_name FROM jobs j " +
                "JOIN companies c ON j.company_id=c.id ORDER BY j.created_at DESC");
    }

    // ---------- helpers ----------

    private List<Job> queryJobs(String sql) {
        List<Job> list = new ArrayList<>();
        try (Statement st = conn().createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("JobDAO.queryJobs: " + e.getMessage());
        }
        return list;
    }

    private List<Job> queryWithId(String sql, int id) {
        List<Job> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("JobDAO.queryWithId: " + e.getMessage());
        }
        return list;
    }

    private Job map(ResultSet rs) throws SQLException {
        Job j = new Job();
        j.setId(rs.getInt("id"));
        j.setCompanyId(rs.getInt("company_id"));
        j.setCompanyName(rs.getString("company_name"));
        j.setTitle(rs.getString("title"));
        j.setDescription(rs.getString("description"));
        j.setEligibility(rs.getString("eligibility"));
        j.setCriteria(rs.getString("criteria"));
        j.setSalary(rs.getString("salary"));
        j.setLocation(rs.getString("location"));
        j.setDeadline(rs.getDate("deadline"));
        j.setJobType(rs.getString("job_type"));
        j.setStatus(rs.getString("status"));
        j.setCreatedAt(rs.getTimestamp("created_at"));
        return j;
    }
}
