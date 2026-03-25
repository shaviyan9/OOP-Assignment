package ui.company;

import dao.JobDAO;
import java.awt.*;
import java.sql.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import models.Company;
import models.Job;
import ui.UIHelper;

public class CompanyJobListPanel extends JPanel {

    private final Company company;
    private final JobDAO jobDAO = new JobDAO();

    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> statusFilter;

    public CompanyJobListPanel(Company company) {
        this.company = company;
        setLayout(new BorderLayout(10, 10));
        setBackground(UIHelper.BG_LIGHT);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        buildUI();
        loadJobs();
    }

    private void buildUI() {
        // ── Header ───────────────────────────────────────────────────
        JPanel top = new JPanel(new BorderLayout(10, 5));
        top.setOpaque(false);
        top.add(UIHelper.headerLabel("My Job Posts"), BorderLayout.NORTH);

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterBar.setOpaque(false);
        filterBar.add(UIHelper.formLabel("Filter by Status:"));
        statusFilter = new JComboBox<>(new String[] { "All", "available", "secured", "closed" });
        statusFilter.setFont(UIHelper.FONT_BODY);
        filterBar.add(statusFilter);
        JButton filterBtn = UIHelper.primaryButton("Filter");
        JButton refreshBtn = UIHelper.accentButton("Refresh");
        filterBar.add(filterBtn);
        filterBar.add(refreshBtn);
        top.add(filterBar, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        // ── Table ────────────────────────────────────────────────────
        String[] cols = { "ID", "Title", "Type", "Location", "Deadline", "Status" };
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(model);
        UIHelper.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(UIHelper.BORDER_COLOR));
        add(sp, BorderLayout.CENTER);

        // ── Action buttons ───────────────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);
        JButton editBtn = UIHelper.primaryButton("Edit Selected");
        JButton deleteBtn = UIHelper.dangerButton("Delete Selected");
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // ── Listeners ────────────────────────────────────────────────
        filterBtn.addActionListener(e -> loadJobs());
        refreshBtn.addActionListener(e -> {
            statusFilter.setSelectedIndex(0);
            loadJobs();
        });
        editBtn.addActionListener(e -> editSelectedJob());
        deleteBtn.addActionListener(e -> deleteSelectedJob());
    }

    private void loadJobs() {
        model.setRowCount(0);
        String status = (String) statusFilter.getSelectedItem();
        List<Job> jobs = "All".equals(status)
                ? jobDAO.getJobsByCompany(company.getId())
                : jobDAO.filterJobsByStatus(company.getId(), status);
        for (Job j : jobs) {
            model.addRow(new Object[] {
                    j.getId(), j.getTitle(), j.getJobType(),
                    j.getLocation(), j.getDeadline(), j.getStatus()
            });
        }
        if (model.getRowCount() == 0)
            model.addRow(new Object[] { "", "No job posts found", "", "", "", "" });
    }

    private Job getSelectedJob() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UIHelper.error(this, "Please select a job.");
            return null;
        }
        Object idObj = model.getValueAt(row, 0);
        if (idObj == null || idObj.toString().isEmpty())
            return null;
        return jobDAO.getById(Integer.parseInt(idObj.toString()));
    }

    private void editSelectedJob() {
        Job job = getSelectedJob();
        if (job == null)
            return;
        showEditDialog(job);
    }

    private void showEditDialog(Job job) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Edit Job: " + job.getTitle(), true);
        dialog.setSize(480, 500);
        UIHelper.centre(dialog);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIHelper.CARD_BG);
        form.setBorder(new EmptyBorder(20, 25, 20, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 0;

        JTextField titleField = UIHelper.textField();
        titleField.setText(job.getTitle());
        JTextField salaryField = UIHelper.textField();
        salaryField.setText(job.getSalary());
        JTextField locationField = UIHelper.textField();
        locationField.setText(job.getLocation());
        JTextField deadlineField = UIHelper.textField();
        deadlineField.setText(job.getDeadline() != null ? job.getDeadline().toString() : "");
        JTextArea descArea = new JTextArea(3, 20);
        descArea.setText(job.getDescription());
        JTextArea eligArea = new JTextArea(2, 20);
        eligArea.setText(job.getEligibility());
        JTextArea critArea = new JTextArea(2, 20);
        critArea.setText(job.getCriteria());
        JComboBox<String> statusBox = new JComboBox<>(new String[] { "available", "secured", "closed" });
        statusBox.setSelectedItem(job.getStatus());

        Object[][] rows = {
                { "Title", titleField },
                { "Salary", salaryField },
                { "Location", locationField },
                { "Deadline (YYYY-MM-DD)", deadlineField },
                { "Status", statusBox },
                { "Description", UIHelper.scrollableTextArea(descArea) },
                { "Eligibility", UIHelper.scrollableTextArea(eligArea) },
                { "Criteria", UIHelper.scrollableTextArea(critArea) },
        };
        for (int i = 0; i < rows.length; i++) {
            gbc.gridy = i;
            gbc.gridx = 0;
            gbc.weightx = 0;
            form.add(UIHelper.formLabel((String) rows[i][0]), gbc);
            gbc.gridx = 1;
            gbc.weightx = 1;
            form.add((Component) rows[i][1], gbc);
        }

        JButton saveBtn = UIHelper.primaryButton("Save Changes");
        gbc.gridy = rows.length;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(14, 5, 5, 5);
        form.add(saveBtn, gbc);

        dialog.add(new JScrollPane(form));

        

        saveBtn.addActionListener(e -> {
            
            try {
                job.setTitle(titleField.getText().trim());
                job.setSalary(salaryField.getText().trim());
                job.setLocation(locationField.getText().trim());
                job.setDeadline(Date.valueOf(deadlineField.getText().trim()));
                job.setStatus((String) statusBox.getSelectedItem());
                job.setDescription(descArea.getText().trim());
                job.setEligibility(eligArea.getText().trim());
                job.setCriteria(critArea.getText().trim());
                if (jobDAO.updateJob(job)) {
                    UIHelper.success(dialog, "Job updated successfully.");
                    loadJobs();
                    dialog.dispose();
                } else {
                    UIHelper.error(dialog, "Update failed.");
                }
            } catch (IllegalArgumentException ex) {
                UIHelper.error(dialog, "Invalid date format. Use YYYY-MM-DD.");
            }
        });
        dialog.setVisible(true);
    }

    private void deleteSelectedJob() {
        Job job = getSelectedJob();
        if (job == null)
            return;
        if (UIHelper.confirm(this, "Delete job post '" + job.getTitle() + "'? This cannot be undone.")) {
            if (jobDAO.deleteJob(job.getId(), company.getId())) {
                UIHelper.success(this, "Job deleted.");
                loadJobs();
            } else {
                UIHelper.error(this, "Failed to delete job.");
            }
        }
    }
}
