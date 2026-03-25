package ui.student;

import dao.ApplicationDAO;
import dao.JobDAO;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import models.Job;
import models.Student;
import ui.UIHelper;

public class JobListPanel extends JPanel {

    private final Student student;
    private final JobDAO jobDAO = new JobDAO();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private JComboBox<String> typeFilter;

    public JobListPanel(Student student) {
        this.student = student;
        setLayout(new BorderLayout(10, 10));
        setBackground(UIHelper.BG_LIGHT);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        buildUI();
        loadJobs();
    }

    private void buildUI() {
        // ── Title + search bar ───────────────────────────────────────
        JPanel top = new JPanel(new BorderLayout(10, 5));
        top.setOpaque(false);

        JLabel title = UIHelper.headerLabel("Available Job & Placement Offers");
        top.add(title, BorderLayout.NORTH);

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterBar.setOpaque(false);

        filterBar.add(UIHelper.formLabel("Search:"));
        searchField = UIHelper.textField();
        searchField.setPreferredSize(new Dimension(200, 30));
        filterBar.add(searchField);

        filterBar.add(UIHelper.formLabel("Type:"));
        typeFilter = new JComboBox<>(new String[] { "All", "Job", "Placement" });
        typeFilter.setFont(UIHelper.FONT_BODY);
        filterBar.add(typeFilter);

        JButton searchBtn = UIHelper.primaryButton("Search");
        JButton refreshBtn = UIHelper.accentButton("Refresh");
        filterBar.add(searchBtn);
        filterBar.add(refreshBtn);

        top.add(filterBar, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        // ── Table ────────────────────────────────────────────────────
        String[] cols = { "ID", "Company", "Job Title", "Type", "Salary", "Location", "Deadline", "Status" };
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(model);
        UIHelper.styleTable(table);
        table.setColumnSelectionAllowed(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(UIHelper.BORDER_COLOR));
        add(sp, BorderLayout.CENTER);

        // ── Detail + apply panel ─────────────────────────────────────
        JPanel south = new JPanel(new BorderLayout(10, 0));
        south.setOpaque(false);

        // Detail area
        JTextArea detailArea = new JTextArea(6, 40);
        detailArea.setEditable(false);
        detailArea.setFont(UIHelper.FONT_BODY);
        detailArea.setForeground(UIHelper.TEXT_SECONDARY);
        JScrollPane detailScroll = UIHelper.scrollableTextArea(detailArea);
        south.add(detailScroll, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);
        JButton viewBtn = UIHelper.primaryButton("View Details");
        JButton applyBtn = UIHelper.accentButton("Apply");
        JButton removeBtn = UIHelper.dangerButton("Remove Application");
        btnPanel.add(viewBtn);
        btnPanel.add(applyBtn);
        btnPanel.add(removeBtn);
        south.add(btnPanel, BorderLayout.EAST);
        add(south, BorderLayout.SOUTH);

        // ── Listeners ────────────────────────────────────────────────
        searchBtn.addActionListener(e -> loadJobs());
        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            typeFilter.setSelectedIndex(0);
            loadJobs();
        });
        typeFilter.addActionListener(e -> loadJobs());

        viewBtn.addActionListener(e -> {
            Job job = getSelectedJob();
            if (job == null)
                return;
            detailArea.setText(buildDetail(job));
        });

        applyBtn.addActionListener(e -> applyForJob(detailArea));
        removeBtn.addActionListener(e -> removeApplication());
    }

    private void loadJobs() {
        model.setRowCount(0);
        String search = searchField.getText().trim().toLowerCase();
        String type = (String) typeFilter.getSelectedItem();

        List<Job> jobs = jobDAO.getAvailableJobs();
        for (Job j : jobs) {
            if (!search.isEmpty() &&
                    !j.getTitle().toLowerCase().contains(search) &&
                    !j.getCompanyName().toLowerCase().contains(search))
                continue;
            if (!"All".equals(type) && !type.equals(j.getJobType()))
                continue;

            model.addRow(new Object[] {
                    j.getId(), j.getCompanyName(), j.getTitle(),
                    j.getJobType(), j.getSalary(), j.getLocation(),
                    j.getDeadline(), j.getStatus()
            });
        }
        if (model.getRowCount() == 0)
            model.addRow(new Object[] { "", "No jobs found", "", "", "", "", "", "" });
    }

    private Job getSelectedJob() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UIHelper.error(this, "Please select a job first.");
            return null;
        }
        Object idObj = model.getValueAt(row, 0);
        if (idObj == null || idObj.toString().isEmpty())
            return null;
        int id = Integer.parseInt(idObj.toString());
        return jobDAO.getById(id);
    }

    private String buildDetail(Job j) {
        return "== " + j.getTitle() + " ==\n" +
                "Company    : " + j.getCompanyName() + "\n" +
                "Type       : " + j.getJobType() + "\n" +
                "Salary     : " + j.getSalary() + "\n" +
                "Location   : " + j.getLocation() + "\n" +
                "Deadline   : " + j.getDeadline() + "\n" +
                "Eligibility: " + j.getEligibility() + "\n" +
                "Criteria   : " + j.getCriteria() + "\n\n" +
                "Description:\n" + j.getDescription();
    }

    private void applyForJob(JTextArea detailArea) {
        Job job = getSelectedJob();
        if (job == null)
            return;

        if (applicationDAO.hasApplied(student.getId(), job.getId())) {
            UIHelper.error(this, "You have already applied for this job.");
            return;
        }

        JTextArea noteArea = new JTextArea(5, 30);
        noteArea.setLineWrap(true);
        JScrollPane noteSp = new JScrollPane(noteArea);
        int opt = JOptionPane.showConfirmDialog(this,
                new Object[] { "Application Letter (optional):", noteSp },
                "Apply for: " + job.getTitle(),
                JOptionPane.OK_CANCEL_OPTION);

        if (opt == JOptionPane.OK_OPTION) {
            int appId = applicationDAO.apply(student.getId(), job.getId(), noteArea.getText().trim());
            if (appId > 0) {
                UIHelper.success(this, "Application submitted successfully!");
                detailArea.setText("");
            } else {
                UIHelper.error(this, "Failed to submit application.");
            }
        }
    }

    private void removeApplication() {
        Job job = getSelectedJob();
        if (job == null)
            return;
        if (!applicationDAO.hasApplied(student.getId(), job.getId())) {
            UIHelper.error(this, "You have not applied for this job.");
            return;
        }
        if (UIHelper.confirm(this, "Remove your application for '" + job.getTitle() + "'?")) {
            if (applicationDAO.remove(student.getId(), job.getId())) {
                UIHelper.success(this, "Application removed.");
            } else {
                UIHelper.error(this, "Failed to remove application.");
            }
        }
    }
}
