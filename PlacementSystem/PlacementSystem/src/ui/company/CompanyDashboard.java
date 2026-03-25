package ui.company;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import models.Company;
import ui.UIHelper;

public class CompanyDashboard extends JFrame {

    private final Company company;
    private final JFrame  loginFrame;

    public CompanyDashboard(Company company, JFrame loginFrame) {
        super("Company Portal – " + company.getCompanyName());
        this.company    = company;
        this.loginFrame = loginFrame;
        setSize(1050, 700);
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
        topBar.setBackground(UIHelper.PRIMARY_DARK);
        topBar.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel brand = new JLabel("Placement System  |  Company Portal");
        brand.setFont(UIHelper.FONT_SUBTITLE);
        brand.setForeground(Color.WHITE);

        JPanel userInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userInfo.setOpaque(false);
        JLabel nameLabel = new JLabel(company.getCompanyName() + "  |  " + company.getIndustry());
        nameLabel.setFont(UIHelper.FONT_BODY);
        nameLabel.setForeground(new Color(190, 215, 255));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(UIHelper.FONT_SMALL);
        logoutBtn.setBackground(UIHelper.DANGER);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(new EmptyBorder(5, 12, 5, 12));
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> logout());

        userInfo.add(nameLabel);
        userInfo.add(logoutBtn);
        topBar.add(brand, BorderLayout.WEST);
        topBar.add(userInfo, BorderLayout.EAST);
        root.add(topBar, BorderLayout.NORTH);

        // ── Tabs ─────────────────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.LEFT);
        //tabs.setFont(UIHelper.FONT_BOLD);
        tabs.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));

        tabs.addTab("🏠  Dashboard",         buildOverviewTab());
        tabs.addTab("📋  My Job Posts",       new CompanyJobListPanel(company));
        tabs.addTab("➕  Post New Job",       new PostJobPanel(company));
        tabs.addTab("👥  View Applicants",    new ApplicantsPanel(company));
        tabs.addTab("⚙️  Company Profile",    new CompanyProfilePanel(company));

        root.add(tabs, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel buildOverviewTab() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UIHelper.BG_LIGHT);
        p.setBorder(new EmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;

        // Welcome card
        JPanel welcome = UIHelper.cardPanel();
        welcome.setLayout(new BoxLayout(welcome, BoxLayout.Y_AXIS));
        JLabel hi = new JLabel("Welcome, " + company.getCompanyName());
        hi.setFont(UIHelper.FONT_TITLE);
        hi.setForeground(UIHelper.PRIMARY);
        JLabel sub = new JLabel("Use the tabs on the left to manage jobs and applicants.");
        sub.setFont(UIHelper.FONT_BODY);
        sub.setForeground(UIHelper.TEXT_SECONDARY);
        welcome.add(hi);
        welcome.add(Box.createVerticalStrut(8));
        welcome.add(sub);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weighty = 0.3;
        p.add(welcome, gbc);

        // Info cards
        String[][] info = {
            {"Industry",    company.getIndustry()  != null ? company.getIndustry()  : "N/A"},
            {"HR Contact",  company.getHrContact() != null ? company.getHrContact() : "N/A"},
            {"Email",       company.getEmail()},
            {"Website",     company.getWebsite()   != null ? company.getWebsite()   : "N/A"},
        };
        gbc.gridwidth = 1; gbc.weighty = 0.2;
        for (int i = 0; i < info.length; i++) {
            gbc.gridx = i % 2; gbc.gridy = 1 + i / 2;
            JPanel card = UIHelper.cardPanel();
            card.setLayout(new BorderLayout(5, 5));
            card.add(UIHelper.formLabel(info[i][0]), BorderLayout.NORTH);
            JLabel val = new JLabel(info[i][1]);
            val.setFont(UIHelper.FONT_BODY);
            val.setForeground(UIHelper.TEXT_SECONDARY);
            card.add(val, BorderLayout.CENTER);
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
