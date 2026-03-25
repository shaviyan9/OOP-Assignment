package ui.company;

import dao.ApplicationDAO;
import dao.JobDAO;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import models.Application;
import models.Company;
import models.Job;
import ui.UIHelper;

public class ApplicantsPanel extends JPanel {

    private final Company        company;
    private final JobDAO         jobDAO         = new JobDAO();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    private JComboBox<Job>    jobSelector;
    private JTable            table;
    private DefaultTableModel model;
    private JTextField        searchField;

    public ApplicantsPanel(Company company) {
        this.company = company;
        setLayout(new BorderLayout(10, 10));
        setBackground(UIHelper.BG_LIGHT);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        buildUI();
        populateJobSelector();
    }

    private void buildUI() {
        // ── Header ───────────────────────────────────────────────────
        JPanel top = new JPanel(new BorderLayout(10, 8));
        top.setOpaque(false);
        top.add(UIHelper.headerLabel("Applicants for My Jobs"), BorderLayout.NORTH);

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterBar.setOpaque(false);

        filterBar.add(UIHelper.formLabel("Select Job:"));
        jobSelector = new JComboBox<>();
        jobSelector.setFont(UIHelper.FONT_BODY);
        jobSelector.setPreferredSize(new Dimension(280, 30));
        filterBar.add(jobSelector);

        filterBar.add(UIHelper.formLabel("Search:"));
        searchField = UIHelper.textField();
        searchField.setPreferredSize(new Dimension(160, 30));
        filterBar.add(searchField);

        JButton loadBtn    = UIHelper.primaryButton("Load Applicants");
        JButton refreshBtn = UIHelper.accentButton("Refresh");
        filterBar.add(loadBtn);
        filterBar.add(refreshBtn);

        top.add(filterBar, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        // ── Table ────────────────────────────────────────────────────
        String[] cols = {"App ID", "Student Name", "Applied On", "Status", "Application Letter"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UIHelper.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(300);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(UIHelper.BORDER_COLOR));
        add(sp, BorderLayout.CENTER);

        // ── Action buttons ───────────────────────────────────────────
        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        btnPanel.setOpaque(false);

        JButton acceptBtn = UIHelper.accentButton("✔ Accept");
        JButton rejectBtn = UIHelper.dangerButton("✘ Reject");
        JButton viewNoteBtn = UIHelper.primaryButton("View Application Letter");

        btnPanel.add(viewNoteBtn);
        btnPanel.add(acceptBtn);
        btnPanel.add(rejectBtn);
        south.add(btnPanel, BorderLayout.EAST);

        JLabel hint = new JLabel("Select an applicant then click Accept or Reject.");
        hint.setFont(UIHelper.FONT_SMALL);
        hint.setForeground(UIHelper.TEXT_SECONDARY);
        hint.setBorder(new EmptyBorder(6, 5, 0, 0));
        south.add(hint, BorderLayout.WEST);
        add(south, BorderLayout.SOUTH);

        // ── Listeners ────────────────────────────────────────────────
        loadBtn.addActionListener(e -> loadApplicants());
        refreshBtn.addActionListener(e -> { searchField.setText(""); loadApplicants(); });

        acceptBtn.addActionListener(e -> updateApplicationStatus("accepted"));
        rejectBtn.addActionListener(e -> updateApplicationStatus("rejected"));
        viewNoteBtn.addActionListener(e -> viewCoverNote());
    }

    private void populateJobSelector() {
        jobSelector.removeAllItems();
        List<Job> jobs = jobDAO.getJobsByCompany(company.getId());
        for (Job j : jobs) jobSelector.addItem(j);
        if (jobs.isEmpty()) {
            jobSelector.addItem(new Job() {{ setTitle("No jobs posted yet"); }});
        }
    }

    private void loadApplicants() {
        model.setRowCount(0);
        Job selected = (Job) jobSelector.getSelectedItem();
        if (selected == null || selected.getId() == 0) {
            UIHelper.error(this, "Please select a valid job."); return;
        }
        String search = searchField.getText().trim().toLowerCase();
        List<Application> apps = applicationDAO.getByJob(selected.getId());
        for (Application a : apps) {
            if (!search.isEmpty() && !a.getStudentName().toLowerCase().contains(search)) continue;
            model.addRow(new Object[]{
                a.getId(), a.getStudentName(),
                a.getAppliedAt() != null ? a.getAppliedAt().toString().substring(0, 10) : "",
                a.getStatus(),
                a.getCoverNote() != null ? a.getCoverNote().replace("\n", " ") : ""
            });
        }
        if (model.getRowCount() == 0)
            model.addRow(new Object[]{"", "No applicants found", "", "", ""});
    }

    private Application getSelectedApplication() {
        int row = table.getSelectedRow();
        if (row < 0) { UIHelper.error(this, "Please select an applicant."); return null; }
        Object idObj = model.getValueAt(row, 0);
        if (idObj == null || idObj.toString().isEmpty()) return null;
        Application a = new Application();
        a.setId(Integer.parseInt(idObj.toString()));
        a.setStudentName(model.getValueAt(row, 1).toString());
        a.setStatus(model.getValueAt(row, 3).toString());
        a.setCoverNote(model.getValueAt(row, 4).toString());
        return a;
    }

    private void updateApplicationStatus(String newStatus) {
        Application app = getSelectedApplication();
        if (app == null) return;

        String confirmMsg = "accepted".equals(newStatus)
                ? "Accept application from " + app.getStudentName() + "?"
                : "Reject application from " + app.getStudentName() + "?";

        if (UIHelper.confirm(this, confirmMsg)) {
            if (applicationDAO.updateStatus(app.getId(), newStatus)) {
                UIHelper.success(this, "Application " + newStatus + ".");
                loadApplicants();
            } else {
                UIHelper.error(this, "Failed to update status.");
            }
        }
    }

    private void viewCoverNote() {
        Application app = getSelectedApplication();
        if (app == null) return;
        String note = app.getCoverNote();
        JTextArea ta = new JTextArea(note != null && !note.isEmpty() ? note : "(No Application Letter provided)");
        ta.setEditable(false);
        ta.setFont(UIHelper.FONT_BODY);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(400, 200));
        JOptionPane.showMessageDialog(this, sp,
                "Application Letter — " + app.getStudentName(), JOptionPane.INFORMATION_MESSAGE);
    }
}
