package ui.student;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import models.Student;
import ui.UIHelper;

/**
 * Student main window. Uses a JTabbedPane so children are accessible
 * as separate "frames" (tabs), satisfying the ≥4-frame requirement.
 */
public class StudentDashboard extends JFrame {

    private final Student student;
    private final JFrame  loginFrame;

    public StudentDashboard(Student student, JFrame loginFrame) {
        super("Student Portal – " + student.getFullName());
        this.student    = student;
        this.loginFrame = loginFrame;
        setSize(980, 680);
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
        topBar.setBackground(UIHelper.PRIMARY);
        topBar.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel brand = new JLabel("Placement System  |  Student Portal");
        brand.setFont(UIHelper.FONT_SUBTITLE);
        brand.setForeground(Color.WHITE);

        JPanel userInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userInfo.setOpaque(false);
        JLabel nameLabel = new JLabel("Welcome, " + student.getFullName());
        nameLabel.setFont(UIHelper.FONT_BODY);
        nameLabel.setForeground(new Color(200, 220, 255));

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

        // ── Tabbed pane ─────────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.LEFT);
        tabs.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        //tabs.setFont(UIHelper.FONT_BOLD);
        tabs.setBackground(UIHelper.BG_LIGHT);

        tabs.addTab("🏠  Dashboard",      buildDashboardTab());
        tabs.addTab("📋  Browse Jobs",    new JobListPanel(student));
        tabs.addTab("📝  My Applications",new ApplicationStatusPanel(student));
        tabs.addTab("👤  My Profile",     new UpdateProfilePanel(student));
        tabs.addTab("🔒  Privacy Policy", new PrivacyPolicyPanel());

        root.add(tabs, BorderLayout.CENTER);
        setContentPane(root);
    }

    // ── Dashboard overview tab ───────────────────────────────────────
    private JPanel buildDashboardTab() {
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
        JLabel hi = new JLabel("Hello, " + student.getFullName());
        hi.setFont(UIHelper.FONT_TITLE);
        hi.setForeground(UIHelper.PRIMARY);
        JLabel sub = new JLabel("Use the tabs on the left to navigate the portal.");
        sub.setFont(UIHelper.FONT_BODY);
        sub.setForeground(UIHelper.TEXT_SECONDARY);
        welcome.add(hi);
        welcome.add(Box.createVerticalStrut(8));
        welcome.add(sub);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weighty = 0.2;
        p.add(welcome, gbc);

        // Info cards
        String[][] cards = {
            {"Course",   student.getCourse()   != null ? student.getCourse()   : "N/A"},
            {"Branch",   student.getBranch()   != null ? student.getBranch()   : "N/A"},
            {"Section",  student.getSection()  != null ? student.getSection()  : "N/A"},
            {"CGPA",     String.valueOf(student.getCgpa())},
            {"Status",   student.getStatus()   != null ? student.getStatus()   : "active"},
            {"Email",    student.getEmail()},
        };

        gbc.gridwidth = 1; gbc.weighty = 0.15;
        for (int i = 0; i < cards.length; i++) {
            gbc.gridx = i % 2;
            gbc.gridy = 1 + i / 2;
            JPanel card = UIHelper.cardPanel();
            card.setLayout(new BorderLayout(5, 5));
            JLabel lbl = UIHelper.formLabel(cards[i][0]);
            JLabel val = new JLabel(cards[i][1]);
            val.setFont(UIHelper.FONT_BODY);
            val.setForeground(UIHelper.TEXT_SECONDARY);
            card.add(lbl, BorderLayout.NORTH);
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
