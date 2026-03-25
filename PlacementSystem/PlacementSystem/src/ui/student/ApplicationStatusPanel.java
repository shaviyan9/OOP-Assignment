package ui.student;

import dao.ApplicationDAO;
import models.Application;
import models.Student;
import ui.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ApplicationStatusPanel extends JPanel {

    private final Student        student;
    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    private JTable            table;
    private DefaultTableModel model;

    public ApplicationStatusPanel(Student student) {
        this.student = student;
        setLayout(new BorderLayout(10, 10));
        setBackground(UIHelper.BG_LIGHT);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        buildUI();
        loadApplications();
    }

    private void buildUI() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(UIHelper.headerLabel("My Applications & Status"), BorderLayout.WEST);

        JButton refreshBtn = UIHelper.primaryButton("Refresh");
        refreshBtn.addActionListener(e -> loadApplications());
        top.add(refreshBtn, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        // Summary cards row
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 12, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        String[] labels = {"Total Applied", "Pending", "Accepted", "Rejected"};
        summaryPanel.setLayout(new GridLayout(1, 4, 10, 0));
        for (String lbl : labels) {
            JPanel card = UIHelper.cardPanel();
            card.setLayout(new BorderLayout(5, 5));
            JLabel lTitle = UIHelper.formLabel(lbl);
            JLabel lCount = new JLabel("0");
            lCount.setFont(new Font("Segoe UI", Font.BOLD, 28));
            lCount.setForeground(UIHelper.PRIMARY);
            lCount.setName(lbl);
            card.add(lTitle, BorderLayout.NORTH);
            card.add(lCount, BorderLayout.CENTER);
            summaryPanel.add(card);
        }
        add(summaryPanel, BorderLayout.NORTH);

        // Replace north with a combined panel
        JPanel northCombined = new JPanel(new BorderLayout());
        northCombined.setOpaque(false);
        northCombined.add(top, BorderLayout.NORTH);
        northCombined.add(summaryPanel, BorderLayout.CENTER);
        add(northCombined, BorderLayout.NORTH);

        // Table
        String[] cols = {"#", "Job Title", "Company", "Type", "Applied On", "Status"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UIHelper.styleTable(table);

        // Color rows by status
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                if (!sel) {
                    String status = model.getValueAt(row, 5) != null ?
                                    model.getValueAt(row, 5).toString() : "";
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

        // Legend
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        legend.setOpaque(false);
        addLegend(legend, new Color(225, 255, 235), "Accepted");
        addLegend(legend, new Color(255, 230, 230), "Rejected");
        addLegend(legend, Color.WHITE, "Pending");
        add(legend, BorderLayout.SOUTH);
    }

    private void addLegend(JPanel panel, Color color, String text) {
        JPanel dot = new JPanel();
        dot.setBackground(color);
        dot.setBorder(BorderFactory.createLineBorder(UIHelper.BORDER_COLOR));
        dot.setPreferredSize(new Dimension(16, 16));
        panel.add(dot);
        JLabel lbl = new JLabel(text);
        lbl.setFont(UIHelper.FONT_SMALL);
        lbl.setForeground(UIHelper.TEXT_SECONDARY);
        panel.add(lbl);
    }

    private void loadApplications() {
        model.setRowCount(0);
        List<Application> apps = applicationDAO.getByStudent(student.getId());

        int pending = 0, accepted = 0, rejected = 0;
        int i = 1;
        for (Application a : apps) {
            model.addRow(new Object[]{
                i++, a.getJobTitle(), a.getCompanyName(),
                "", // job type not in Application model - can extend if needed
                a.getAppliedAt() != null ? a.getAppliedAt().toString().substring(0, 10) : "",
                a.getStatus()
            });
            switch (a.getStatus()) {
                case "accepted": accepted++; break;
                case "rejected": rejected++; break;
                default: pending++;
            }
        }
        updateSummary(apps.size(), pending, accepted, rejected);
    }

    private void updateSummary(int total, int pending, int accepted, int rejected) {
        // Walk the north panel hierarchy to update summary labels
        Component northPanel = getComponent(0); // northCombined
        if (!(northPanel instanceof JPanel)) return;
        Component summaryPanel = ((JPanel) northPanel).getComponent(1);
        if (!(summaryPanel instanceof JPanel)) return;

        int[] vals = {total, pending, accepted, rejected};
        Component[] cards = ((JPanel) summaryPanel).getComponents();
        for (int i = 0; i < Math.min(cards.length, vals.length); i++) {
            if (cards[i] instanceof JPanel) {
                for (Component c : ((JPanel) cards[i]).getComponents()) {
                    if (c instanceof JLabel && ((JLabel)c).getFont().getSize() == 28) {
                        ((JLabel)c).setText(String.valueOf(vals[i]));
                    }
                }
            }
        }
    }
}
