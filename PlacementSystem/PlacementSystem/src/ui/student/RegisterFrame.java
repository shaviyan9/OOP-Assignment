package ui.student;

import dao.StudentDAO;
import models.Student;
import ui.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegisterFrame extends JFrame {

    private final JFrame parent;
    private final StudentDAO studentDAO = new StudentDAO();

    // Form fields
    private JTextField fullNameField, usernameField, emailField, phoneField;
    private JTextField courseField, branchField, sectionField, cgpaField;
    private JPasswordField passField, confirmPassField;

    public RegisterFrame(JFrame parent) {
        super("Student Registration");
        this.parent = parent;
        setSize(540, 680);
        setResizable(false);
        UIHelper.centre(this);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIHelper.BG_LIGHT);

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(UIHelper.PRIMARY);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel title = new JLabel("Student Registration");
        title.setFont(UIHelper.FONT_TITLE);
        title.setForeground(Color.WHITE);
        header.add(title);
        root.add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIHelper.CARD_BG);
        form.setBorder(new EmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        fullNameField    = UIHelper.textField();
        usernameField    = UIHelper.textField();
        emailField       = UIHelper.textField();
        phoneField       = UIHelper.textField();
        courseField      = UIHelper.textField();
        branchField      = UIHelper.textField();
        sectionField     = UIHelper.textField();
        cgpaField        = UIHelper.textField();
        passField        = UIHelper.passwordField();
        confirmPassField = UIHelper.passwordField();

        Object[][] rows = {
            {"Full Name*",       fullNameField},
            {"Username*",        usernameField},
            {"Email*",           emailField},
            {"Phone",            phoneField},
            {"Course*",          courseField},
            {"Branch*",          branchField},
            {"Section",          sectionField},
            {"CGPA",             cgpaField},
            {"Password*",        passField},
            {"Confirm Password*",confirmPassField},
        };

        for (int i = 0; i < rows.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            form.add(UIHelper.formLabel((String) rows[i][0]), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            form.add((Component) rows[i][1], gbc);
        }

        JScrollPane scrollForm = new JScrollPane(form);
        scrollForm.setBorder(null);
        root.add(scrollForm, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(UIHelper.BG_LIGHT);
        btnPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(UIHelper.FONT_BODY);
        JButton registerBtn = UIHelper.primaryButton("Register");

        btnPanel.add(cancelBtn);
        btnPanel.add(registerBtn);
        root.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(root);

        registerBtn.addActionListener(e -> handleRegister());
        cancelBtn.addActionListener(e -> { parent.setVisible(true); dispose(); });
    }

    private void handleRegister() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email    = emailField.getText().trim();
        String phone    = phoneField.getText().trim();
        String course   = courseField.getText().trim();
        String branch   = branchField.getText().trim();
        String section  = sectionField.getText().trim();
        String cgpaStr  = cgpaField.getText().trim();
        String pass     = new String(passField.getPassword());
        String confirm  = new String(confirmPassField.getPassword());

        // Validation
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() ||
            course.isEmpty() || branch.isEmpty() || pass.isEmpty()) {
            UIHelper.error(this, "Please fill all required (*) fields.");
            return;
        }
        if (!pass.equals(confirm)) {
            UIHelper.error(this, "Passwords do not match.");
            return;
        }
        if (pass.length() < 6) {
            UIHelper.error(this, "Password must be at least 6 characters.");
            return;
        }
        if (!email.contains("@")) {
            UIHelper.error(this, "Invalid email address.");
            return;
        }

        double cgpa = 0;
        if (!cgpaStr.isEmpty()) {
            try {
                cgpa = Double.parseDouble(cgpaStr);
                if (cgpa < 0 || cgpa > 4) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                UIHelper.error(this, "CGPA must be a number between 0 and 4.");
                return;
            }
        }

        if (studentDAO.usernameExists(username)) {
            UIHelper.error(this, "Username already taken. Choose another.");
            return;
        }
        if (studentDAO.emailExists(email)) {
            UIHelper.error(this, "Email already registered. Please login.");
            return;
        }

        Student s = new Student();
        s.setFullName(fullName);
        s.setUsername(username);
        s.setEmail(email);
        s.setPassword(pass);
        s.setPhone(phone);
        s.setCourse(course);
        s.setBranch(branch);
        s.setSection(section);
        s.setCgpa(cgpa);

        int id = studentDAO.register(s);
        if (id > 0) {
            UIHelper.success(this, "Registration successful! You can now login.");
            parent.setVisible(true);
            dispose();
        } else {
            UIHelper.error(this, "Registration failed. Please try again.");
        }
    }
}
