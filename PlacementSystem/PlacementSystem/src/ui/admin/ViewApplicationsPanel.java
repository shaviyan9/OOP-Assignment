package ui.admin;

import dao.ApplicationDAO;
import models.Application;
import ui.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ViewApplicationsPanel extends JPanel {

    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    private JTable            table;
    private DefaultTableModel model;
    private JComboBox<String> statusFilter;
    private JTextField        searchField;

    public ViewApplicationsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIHelper.BG_LIGHT);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        buildUI();
        loadApplications();
    }

    private void buildUI() {
        JPanel top = new JPanel(new BorderLayout(10, 8));
        top.setOpaque(false);
        top.add(UIHelper.headerLabel("All Job Applications"), BorderLayout.NORTH);

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterBar.setOpaque(false);

        filterBar.add(UIHelper.formLabel("Search (student/job):"));
        searchField = UIHelper.textField();
        searchField.setPreferredSize(new Dimension(180, 30));
        filterBar.add(searchField);

        filterBar.add(UIHelper.formLabel("Status:"));
        statusFilter = new JComboBox<>(new String[]{"All", "pending", "accepted", "rejected"});
        statusFilter.setFont(UIHelper.FONT_BODY);
        filterBar.add(statusFilter);

        JButton filterBtn  = UIHelper.primaryButton("Filter");
        JButton refreshBtn = UIHelper.accentButton("Refresh");
        filterBar.add(filterBtn);
        filterBar.add(refreshBtn);
        top.add(filterBar, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        // ── Table ────────────────────────────────────────────────────
        String[] cols = {"App ID", "Student", "Job Title", "Company", "Applied On", "Status"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UIHelper.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(55);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(160);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);

        // Row colour by status
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                if (!sel) {
                    String status = model.getValueAt(row, 5) != null ? model.getValueAt(row, 5).toString() : "";
                    switch (status) {
                        case "accepted": c.setBackground(new Color(225, 255, 235)); break;
                        case "rejected": c.setBackground(new Color(255, 230, 230)); break;
                        default:         c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(UIHelper.BORDER_COLOR));
        add(sp, BorderLayout.CENTER);

        // ── South: counts + update status ────────────────────────────
        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);

        JPanel updateRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        updateRow.setOpaque(false);
        JButton acceptBtn = UIHelper.accentButton("Mark Accepted");
        JButton rejectBtn = UIHelper.dangerButton("Mark Rejected");
        updateRow.add(acceptBtn);
        updateRow.add(rejectBtn);
        south.add(updateRow, BorderLayout.EAST);

        // Summary label
        JLabel countLabel = new JLabel("Loading...");
        countLabel.setFont(UIHelper.FONT_SMALL);
        countLabel.setForeground(UIHelper.TEXT_SECONDARY);
        countLabel.setBorder(new EmptyBorder(6, 5, 0, 0));
        south.add(countLabel, BorderLayout.WEST);
        add(south, BorderLayout.SOUTH);

        // ── Listeners ────────────────────────────────────────────────
        filterBtn.addActionListener(e -> applyFilter(countLabel));
        refreshBtn.addActionListener(e -> { searchField.setText(""); statusFilter.setSelectedIndex(0); loadApplications(); updateCount(countLabel); });
        acceptBtn.addActionListener(e -> updateStatus("accepted", countLabel));
        rejectBtn.addActionListener(e -> updateStatus("rejected", countLabel));
    }

    private void loadApplications() {
        model.setRowCount(0);
        for (Application a : applicationDAO.getAll()) addRow(a);
        if (model.getRowCount() == 0)
            model.addRow(new Object[]{"", "No applications found", "", "", "", ""});
    }

    private void applyFilter(JLabel countLabel) {
        model.setRowCount(0);
        String search = searchField.getText().trim().toLowerCase();
        String status = (String) statusFilter.getSelectedItem();

        for (Application a : applicationDAO.getAll()) {
            boolean matchStatus = "All".equals(status) || status.equals(a.getStatus());
            boolean matchSearch = search.isEmpty() ||
                    a.getStudentName().toLowerCase().contains(search) ||
                    a.getJobTitle().toLowerCase().contains(search) ||
                    a.getCompanyName().toLowerCase().contains(search);
            if (matchStatus && matchSearch) addRow(a);
        }
        if (model.getRowCount() == 0)
            model.addRow(new Object[]{"", "No matching applications", "", "", "", ""});
        updateCount(countLabel);
    }

    private void addRow(Application a) {
        model.addRow(new Object[]{
            a.getId(), a.getStudentName(), a.getJobTitle(),
            a.getCompanyName(),
            a.getAppliedAt() != null ? a.getAppliedAt().toString().substring(0, 10) : "",
            a.getStatus()
        });
    }

    private void updateStatus(String newStatus, JLabel countLabel) {
        int row = table.getSelectedRow();
        if (row < 0) { UIHelper.error(this, "Please select an application."); return; }
        Object idObj = model.getValueAt(row, 0);
        if (idObj == null || idObj.toString().isEmpty()) return;
        int appId = Integer.parseInt(idObj.toString());

        if (UIHelper.confirm(this, "Mark this application as '" + newStatus + "'?")) {
            if (applicationDAO.updateStatus(appId, newStatus)) {
                UIHelper.success(this, "Status updated to: " + newStatus);
                loadApplications();
                updateCount(countLabel);
            } else {
                UIHelper.error(this, "Update failed.");
            }
        }
    }

    private void updateCount(JLabel countLabel) {
        int total = 0, pending = 0, accepted = 0, rejected = 0;
        for (int r = 0; r < model.getRowCount(); r++) {
            Object s = model.getValueAt(r, 5);
            if (s == null) continue;
            total++;
            switch (s.toString()) {
                case "accepted": accepted++; break;
                case "rejected": rejected++; break;
                case "pending":  pending++;  break;
            }
        }
        countLabel.setText(String.format(
            "Total: %d  |  Pending: %d  |  Accepted: %d  |  Rejected: %d",
            total, pending, accepted, rejected));
    }
}
