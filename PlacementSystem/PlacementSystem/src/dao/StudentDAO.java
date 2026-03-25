package dao;

import db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Student;

public class StudentDAO {

    private Connection conn() {
        return DBConnection.getConnection();
    }

    /** Register a new student. Returns generated id or -1 on failure. */
    public int register(Student s) {
        String sql = "INSERT INTO students (full_name,username,email,password,phone,course,branch,section,cgpa) " +
                "VALUES (?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getFullName());
            ps.setString(2, s.getUsername());
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getPassword());
            ps.setString(5, s.getPhone());
            ps.setString(6, s.getCourse());
            ps.setString(7, s.getBranch());
            ps.setString(8, s.getSection());
            ps.setDouble(9, s.getCgpa());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("StudentDAO.register: " + e.getMessage());
        }
        return -1;
    }

    /** Login – returns Student if credentials match, else null. */
    public Student login(String usernameOrEmail, String password) {
        String sql = "SELECT * FROM students WHERE (username=? OR email=?) AND password=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, usernameOrEmail);
            ps.setString(2, usernameOrEmail);
            ps.setString(3, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return map(rs);
        } catch (SQLException e) {
            System.err.println("StudentDAO.login: " + e.getMessage());
        }
        return null;
    }

    /** Update profile details and (optionally) resume path. */
    public boolean updateProfile(Student s) {
        String sql = "UPDATE students SET full_name=?,phone=?,course=?,branch=?,section=?,cgpa=?,resume_path=? WHERE id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, s.getFullName());
            ps.setString(2, s.getPhone());
            ps.setString(3, s.getCourse());
            ps.setString(4, s.getBranch());
            ps.setString(5, s.getSection());
            ps.setDouble(6, s.getCgpa());
            ps.setString(7, s.getResumePath());
            ps.setInt(8, s.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("StudentDAO.updateProfile: " + e.getMessage());
        }
        return false;
    }

    /** Reset password by email. */
    public boolean resetPassword(String email, String newPassword) {
        String sql = "UPDATE students SET password=? WHERE email=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("StudentDAO.resetPassword: " + e.getMessage());
        }
        return false;
    }

    /** Check if a username already exists. */
    public boolean usernameExists(String username) {
        try (PreparedStatement ps = conn().prepareStatement("SELECT id FROM students WHERE username=?")) {
            ps.setString(1, username);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    /** Check if an email already exists. */
    public boolean emailExists(String email) {
        try (PreparedStatement ps = conn().prepareStatement("SELECT id FROM students WHERE email=?")) {
            ps.setString(1, email);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    /** Get student by id. */
    public Student getById(int id) {
        try (PreparedStatement ps = conn().prepareStatement("SELECT * FROM students WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return map(rs);
        } catch (SQLException e) {
            System.err.println("StudentDAO.getById: " + e.getMessage());
        }
        return null;
    }

    /** Get all students (admin). */
    public List<Student> getAll() {
        List<Student> list = new ArrayList<>();
        try (Statement st = conn().createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM students ORDER BY full_name")) {
            while (rs.next())
                list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("StudentDAO.getAll: " + e.getMessage());
        }
        return list;
    }

    /**
     * Filter students by course, branch, section (admin). Pass null/"" to skip a
     * filter.
     */
    public List<Student> filter(String course, String branch, String section) {
        StringBuilder sb = new StringBuilder("SELECT * FROM students WHERE 1=1");
        List<String> params = new ArrayList<>();
        if (course != null && !course.isEmpty()) {
            sb.append(" AND course=?");
            params.add(course);
        }
        if (branch != null && !branch.isEmpty()) {
            sb.append(" AND branch=?");
            params.add(branch);
        }
        if (section != null && !section.isEmpty()) {
            sb.append(" AND section=?");
            params.add(section);
        }
        sb.append(" ORDER BY full_name");
        List<Student> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++)
                ps.setString(i + 1, params.get(i));
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("StudentDAO.filter: " + e.getMessage());
        }
        return list;
    }

    private Student map(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("id"));
        s.setFullName(rs.getString("full_name"));
        s.setUsername(rs.getString("username"));
        s.setEmail(rs.getString("email"));
        s.setPassword(rs.getString("password"));
        s.setPhone(rs.getString("phone"));
        s.setCourse(rs.getString("course"));
        s.setBranch(rs.getString("branch"));
        s.setSection(rs.getString("section"));
        s.setCgpa(rs.getDouble("cgpa"));
        s.setResumePath(rs.getString("resume_path"));
        s.setStatus(rs.getString("status"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        return s;
    }

    public boolean delete(int id) {
        try (PreparedStatement ps = conn().prepareStatement("DELETE FROM students WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("StudentDAO.delete: " + e.getMessage());
        }
        return false;
    }

}
