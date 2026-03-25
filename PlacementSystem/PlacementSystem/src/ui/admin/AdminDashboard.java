package ui.admin;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import models.Admin;
import ui.UIHelper;

public class AdminDashboard extends JFrame {

    private final Admin  admin;
    private final JFrame loginFrame;

    public AdminDashboard(Admin admin, JFrame loginFrame) {
        super("Admin Portal – Placement System");
        this.admin      = admin;
        this.loginFrame = loginFrame;
        setSize(1100, 720);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        UIHelper.centre(this);
        buildUI();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) { logout(); }
        });
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIHelper.BG_LIGHT);

        // ── Top bar ─────────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(15, 40, 100));
        topBar.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel brand = new JLabel(" Placement System  |  Administrator Panel");
        brand.setFont(UIHelper.FONT_SUBTITLE);
        brand.setForeground(Color.WHITE);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        JLabel adminLabel = new JLabel("Admin: " + admin.getUsername());
        adminLabel.setFont(UIHelper.FONT_BODY);
        adminLabel.setForeground(new Color(180, 200, 255));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(UIHelper.FONT_SMALL);
        logoutBtn.setBackground(UIHelper.DANGER);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(new EmptyBorder(5, 12, 5, 12));
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> logout());

        right.add(adminLabel);
        right.add(logoutBtn);
        topBar.add(brand, BorderLayout.WEST);
        topBar.add(right, BorderLayout.EAST);
        root.add(topBar, BorderLayout.NORTH);

        // ── Tabs ─────────────────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.LEFT);
        //tabs.setFont(UIHelper.FONT_BOLD);
        tabs.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));


        tabs.addTab("🏠  Overview",          buildOverviewTab());
        tabs.addTab("🏢  Manage Companies",  new ManageCompaniesPanel());
        tabs.addTab("👥  Manage Students",     new ViewStudentsPanel());
        tabs.addTab("📄  Job Applications",  new ViewApplicationsPanel());

        root.add(tabs, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel buildOverviewTab() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UIHelper.BG_LIGHT);
        p.setBorder(new EmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;

        // Welcome card
        JPanel welcome = UIHelper.cardPanel();
        welcome.setLayout(new BoxLayout(welcome, BoxLayout.Y_AXIS));
        JLabel hi = new JLabel("Administrator Dashboard");
        hi.setFont(UIHelper.FONT_TITLE);
        hi.setForeground(UIHelper.PRIMARY);
        JLabel sub = new JLabel("Manage companies, students, and placement applications from the tabs on the left.");
        sub.setFont(UIHelper.FONT_BODY);
        sub.setForeground(UIHelper.TEXT_SECONDARY);
        welcome.add(hi);
        welcome.add(Box.createVerticalStrut(8));
        welcome.add(sub);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3; gbc.weighty = 0.25;
        p.add(welcome, gbc);

        // Quick-action cards
        String[][] quickCards = {
            {"🏢", "Manage Companies", "Add, update or remove companies"},
            {"👥", "Manage Students",    "Filter students by course/branch/section"},
            {"📄", "Applications",     "Review all job & placement applications"},
        };
        gbc.gridwidth = 1; gbc.weighty = 0.3;
        for (int i = 0; i < quickCards.length; i++) {
            gbc.gridx = i; gbc.gridy = 1;
            JPanel card = UIHelper.cardPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            JLabel icon = new JLabel(quickCards[i][0]);
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
            JLabel title = new JLabel(quickCards[i][1]);
            title.setFont(UIHelper.FONT_SUBTITLE);
            title.setForeground(UIHelper.PRIMARY);
            JLabel desc = new JLabel("<html><small>" + quickCards[i][2] + "</small></html>");
            desc.setFont(UIHelper.FONT_SMALL);
            desc.setForeground(UIHelper.TEXT_SECONDARY);
            card.add(icon); card.add(Box.createVerticalStrut(6));
            card.add(title); card.add(Box.createVerticalStrut(4));
            card.add(desc);
            p.add(card, gbc);
        }
        return p;
    }

    private void logout() {
        if (UIHelper.confirm(this, "Are you sure you want to logout?")) {
            loginFrame.setVisible(true);
            dispose();
        }
    }
}
