package ui;

import dao.AdminDAO;
import dao.CompanyDAO;
import dao.StudentDAO;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import models.Admin;
import models.Company;
import models.Student;
import ui.admin.AdminDashboard;
import ui.company.CompanyDashboard;
import ui.student.RegisterFrame;
import ui.student.ResetPasswordFrame;
import ui.student.StudentDashboard;

public class LoginFrame extends JFrame {

    private JComboBox<String> roleBox;
    private JTextField userField;
    private JPasswordField passField;

    private final StudentDAO  studentDAO  = new StudentDAO();
    private final CompanyDAO  companyDAO  = new CompanyDAO();
    private final AdminDAO    adminDAO    = new AdminDAO();

    public LoginFrame() {
        super("Placement & Job Recruitment System – Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(440, 540);
        setResizable(true);
        UIHelper.centre(this);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIHelper.BG_LIGHT);

        // ── Banner ──────────────────────────────────────────────────
        JPanel banner = new JPanel(new GridBagLayout());
        banner.setBackground(UIHelper.PRIMARY);
        banner.setBorder(new EmptyBorder(30, 20, 30, 20));

        JLabel title = new JLabel("Placement System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Job & Placement Recruitment Portal");
        sub.setFont(UIHelper.FONT_BODY);
        sub.setForeground(new Color(180, 205, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0; banner.add(title, gbc);
        gbc.gridy = 1; banner.add(sub, gbc);
        root.add(banner, BorderLayout.NORTH);

        // ── Form card ───────────────────────────────────────────────
        JPanel card = UIHelper.cardPanel();
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints fc = new GridBagConstraints();
        fc.insets = new Insets(6, 0, 6, 0);
        fc.fill = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1;
        fc.gridx = 0;

        // Role selector
        fc.gridy = 0;
        card.add(UIHelper.formLabel("Login as"), fc);
        fc.gridy = 1;
        roleBox = new JComboBox<>(new String[]{"Student", "Company", "Admin"});
        roleBox.setFont(UIHelper.FONT_BODY);
        card.add(roleBox, fc);

        // Username / email
        fc.gridy = 2;
        card.add(UIHelper.formLabel("Username / Email"), fc);
        fc.gridy = 3;
        userField = UIHelper.textField();
        card.add(userField, fc);

        // Password
        fc.gridy = 4;
        card.add(UIHelper.formLabel("Password"), fc);
        fc.gridy = 5;
        passField = UIHelper.passwordField();
        card.add(passField, fc);

        // Login button
        fc.gridy = 6;
        fc.insets = new Insets(18, 0, 4, 0);
        JButton loginBtn = UIHelper.primaryButton("Login");
        loginBtn.setPreferredSize(new Dimension(200, 38));
        card.add(loginBtn, fc);

        // Register link
        fc.gridy = 7;
        fc.insets = new Insets(2, 0, 2, 0);
        JButton regBtn = new JButton("New student? Register here");
        regBtn.setFont(UIHelper.FONT_SMALL);
        regBtn.setBorderPainted(false);
        regBtn.setContentAreaFilled(false);
        regBtn.setForeground(UIHelper.PRIMARY);
        regBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.add(regBtn, fc);

        // Forgot password link
        fc.gridy = 8;
        JButton forgotBtn = new JButton("Forgot password?");
        forgotBtn.setFont(UIHelper.FONT_SMALL);
        forgotBtn.setBorderPainted(false);
        forgotBtn.setContentAreaFilled(false);
        forgotBtn.setForeground(UIHelper.TEXT_SECONDARY);
        forgotBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.add(forgotBtn, fc);

        root.add(card, BorderLayout.CENTER);
        setContentPane(root);

        // ── Listeners ───────────────────────────────────────────────
        loginBtn.addActionListener(e -> handleLogin());
        passField.addActionListener(e -> handleLogin());

        regBtn.addActionListener(e -> {
            new RegisterFrame(this).setVisible(true);
            setVisible(false);
        });

        forgotBtn.addActionListener(e -> new ResetPasswordFrame(this).setVisible(true));
    }

    private void handleLogin() {
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword());
        String role = (String) roleBox.getSelectedItem();

        if (user.isEmpty() || pass.isEmpty()) {
            UIHelper.error(this, "Please enter username/email and password.");
            return;
        }

        switch (role) {
            case "Student": {
                Student s = studentDAO.login(user, pass);
                if (s != null) {
                    new StudentDashboard(s, this).setVisible(true);
                    setVisible(false);
                } else {
                    UIHelper.error(this, "Invalid student credentials.");
                }
                break;
            }
            case "Company": {
                Company c = companyDAO.login(user, pass);
                if (c != null) {
                    new CompanyDashboard(c, this).setVisible(true);
                    setVisible(false);
                } else {
                    UIHelper.error(this, "Invalid company credentials.");
                }
                break;
            }
            case "Admin": {
                Admin a = adminDAO.login(user, pass);
                if (a != null) {
                    new AdminDashboard(a, this).setVisible(true);
                    setVisible(false);
                } else {
                    UIHelper.error(this, "Invalid admin credentials.");
                }
                break;
            }
        }
    }
}
