package ui.admin;

import dao.StudentDAO;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import models.Student;
import ui.UIHelper;

public class ViewStudentsPanel extends JPanel {

    private final StudentDAO studentDAO = new StudentDAO();

    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField, courseField, branchField, sectionField;

    public ViewStudentsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIHelper.BG_LIGHT);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        buildUI();
        loadStudents();
    }

    private void buildUI() {
        // ── Header ───────────────────────────────────────────────────
        JPanel top = new JPanel(new BorderLayout(10, 8));
        top.setOpaque(false);
        top.add(UIHelper.headerLabel("Manage Students"), BorderLayout.NORTH);

        // Filter bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterBar.setOpaque(false);

        filterBar.add(UIHelper.formLabel("Search (name/user):"));
        searchField = UIHelper.textField();
        searchField.setPreferredSize(new Dimension(150, 30));
        filterBar.add(searchField);

        filterBar.add(UIHelper.formLabel("Course:"));
        courseField = UIHelper.textField();
        courseField.setPreferredSize(new Dimension(100, 30));
        filterBar.add(courseField);

        filterBar.add(UIHelper.formLabel("Branch:"));
        branchField = UIHelper.textField();
        branchField.setPreferredSize(new Dimension(100, 30));
        filterBar.add(branchField);

        filterBar.add(UIHelper.formLabel("Section:"));
        sectionField = UIHelper.textField();
        sectionField.setPreferredSize(new Dimension(60, 30));
        filterBar.add(sectionField);

        JButton filterBtn = UIHelper.primaryButton("Filter");
        JButton clearBtn = UIHelper.accentButton("Clear");
        filterBar.add(filterBtn);
        filterBar.add(clearBtn);
        top.add(filterBar, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        // ── Table ────────────────────────────────────────────────────
        String[] cols = { "ID", "Full Name", "Username", "Course", "Branch", "Section", "CGPA", "Status" };
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(model);
        UIHelper.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(60);
        table.getColumnModel().getColumn(6).setPreferredWidth(60);
        table.getColumnModel().getColumn(7).setPreferredWidth(80);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(UIHelper.BORDER_COLOR));
        add(sp, BorderLayout.CENTER);

        // ── Detail view ──────────────────────────────────────────────
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        south.setOpaque(false);
        JButton viewBtn = UIHelper.primaryButton("View Full Details");
        JButton deleteBtn = UIHelper.dangerButton("Delete");
        south.add(viewBtn);
        south.add(deleteBtn);
        add(south, BorderLayout.SOUTH);

        // ── Listeners ────────────────────────────────────────────────
        filterBtn.addActionListener(e -> applyFilter());
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            courseField.setText("");
            branchField.setText("");
            sectionField.setText("");
            loadStudents();
        });
        viewBtn.addActionListener(e -> viewStudentDetails());
        deleteBtn.addActionListener(e -> deleteStudent());
    }

    private void loadStudents() {
        model.setRowCount(0);
        for (Student s : studentDAO.getAll())
            addRow(s);
        if (model.getRowCount() == 0)
            model.addRow(new Object[] { "", "No students found", "", "", "", "", "", "" });
    }

    private void applyFilter() {
        model.setRowCount(0);
        String search = searchField.getText().trim().toLowerCase();
        String course = courseField.getText().trim();
        String branch = branchField.getText().trim();
        String section = sectionField.getText().trim();

        List<Student> students = studentDAO.filter(
                course.isEmpty() ? null : course,
                branch.isEmpty() ? null : branch,
                section.isEmpty() ? null : section);

        for (Student s : students) {
            if (!search.isEmpty() &&
                    !s.getFullName().toLowerCase().contains(search) &&
                    !s.getUsername().toLowerCase().contains(search))
                continue;
            addRow(s);
        }
        if (model.getRowCount() == 0)
            model.addRow(new Object[] { "", "No matching students", "", "", "", "", "", "" });
    }

    private void addRow(Student s) {
        model.addRow(new Object[] {
                s.getId(), s.getFullName(), s.getUsername(),
                s.getCourse() != null ? s.getCourse() : "",
                s.getBranch() != null ? s.getBranch() : "",
                s.getSection() != null ? s.getSection() : "",
                s.getCgpa(), s.getStatus()
        });
    }

    private void deleteStudent() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UIHelper.error(this, "Please select a student.");
            return;
        }
        Object idObj = model.getValueAt(row, 0);
        if (idObj == null || idObj.toString().isEmpty())
            return;
        Student s = studentDAO.getById(Integer.parseInt(idObj.toString()));

        if (UIHelper.confirm(this, "Delete student '" + s.getFullName()
                + "'?\nAll associated applications will also be removed.")) {
            if (studentDAO.delete(s.getId())) {
                UIHelper.success(this, "Student deleted.");
                loadStudents();
            } else {
                UIHelper.error(this, "Failed to delete student.");
            }
        }
    }

    private void viewStudentDetails() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UIHelper.error(this, "Please select a student.");
            return;
        }
        Object idObj = model.getValueAt(row, 0);
        if (idObj == null || idObj.toString().isEmpty())
            return;
        Student s = studentDAO.getById(Integer.parseInt(idObj.toString()));
        if (s == null) {
            UIHelper.error(this, "Student not found.");
            return;
        }

        String info = "Full Name  : " + s.getFullName() + "\n" +
                "Username   : " + s.getUsername() + "\n" +
                "Email      : " + s.getEmail() + "\n" +
                "Phone      : " + (s.getPhone() != null ? s.getPhone() : "N/A") + "\n" +
                "Course     : " + (s.getCourse() != null ? s.getCourse() : "N/A") + "\n" +
                "Branch     : " + (s.getBranch() != null ? s.getBranch() : "N/A") + "\n" +
                "Section    : " + (s.getSection() != null ? s.getSection() : "N/A") + "\n" +
                "CGPA       : " + s.getCgpa() + "\n" +
                "Status     : " + s.getStatus() + "\n" +
                "Resume     : " + (s.getResumePath() != null ? s.getResumePath() : "Not uploaded") + "\n" +
                "Registered : " + (s.getCreatedAt() != null ? s.getCreatedAt().toString().substring(0, 10) : "N/A");

        JTextArea ta = new JTextArea(info);
        ta.setEditable(false);
        ta.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JOptionPane.showMessageDialog(this, new JScrollPane(ta),
                "Student Details – " + s.getFullName(), JOptionPane.INFORMATION_MESSAGE);
    }
}
