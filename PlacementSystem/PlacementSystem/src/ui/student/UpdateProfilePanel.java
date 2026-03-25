package ui.student;

import dao.StudentDAO;
import models.Student;
import ui.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class UpdateProfilePanel extends JPanel {

    private final Student    student;
    private final StudentDAO studentDAO = new StudentDAO();

    private JTextField fullNameField, phoneField, courseField;
    private JTextField branchField, sectionField, cgpaField;
    private JLabel     resumeLabel;
    private String     resumePath;

    public UpdateProfilePanel(Student student) {
        this.student    = student;
        this.resumePath = student.getResumePath();
        setLayout(new BorderLayout(10, 10));
        setBackground(UIHelper.BG_LIGHT);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        buildUI();
    }

    private void buildUI() {
        JLabel title = UIHelper.headerLabel("My Profile");
        add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 20, 0));
        content.setOpaque(false);

        // ── Left: Personal details ───────────────────────────────────
        JPanel leftCard = UIHelper.cardPanel();
        leftCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel sectionTitle = new JLabel("Personal & Academic Details");
        sectionTitle.setFont(UIHelper.FONT_SUBTITLE);
        sectionTitle.setForeground(UIHelper.PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        leftCard.add(sectionTitle, gbc);

        gbc.gridwidth = 1;
        Object[][] rows = {
            {"Full Name",  fullNameField = UIHelper.textField()},
            {"Phone",      phoneField    = UIHelper.textField()},
            {"Course",     courseField   = UIHelper.textField()},
            {"Branch",     branchField   = UIHelper.textField()},
            {"Section",    sectionField  = UIHelper.textField()},
            {"CGPA",       cgpaField     = UIHelper.textField()},
        };
        for (int i = 0; i < rows.length; i++) {
            gbc.gridx = 0; gbc.gridy = i + 1; gbc.weightx = 0;
            leftCard.add(UIHelper.formLabel((String) rows[i][0]), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            leftCard.add((Component) rows[i][1], gbc);
        }

        // Read-only fields
        gbc.gridx = 0; gbc.gridy = rows.length + 1; gbc.weightx = 0;
        leftCard.add(UIHelper.formLabel("Username"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JLabel usernameLabel = new JLabel(student.getUsername());
        usernameLabel.setFont(UIHelper.FONT_BODY);
        usernameLabel.setForeground(UIHelper.TEXT_SECONDARY);
        leftCard.add(usernameLabel, gbc);

        gbc.gridx = 0; gbc.gridy = rows.length + 2;
        leftCard.add(UIHelper.formLabel("Email"), gbc);
        gbc.gridx = 1;
        JLabel emailLabel = new JLabel(student.getEmail());
        emailLabel.setFont(UIHelper.FONT_BODY);
        emailLabel.setForeground(UIHelper.TEXT_SECONDARY);
        leftCard.add(emailLabel, gbc);

        content.add(leftCard);

        // ── Right: Documents + Password ──────────────────────────────
        JPanel rightPanel = new JPanel(new BorderLayout(0, 15));
        rightPanel.setOpaque(false);

        // Documents card
        JPanel docCard = UIHelper.cardPanel();
        docCard.setLayout(new BoxLayout(docCard, BoxLayout.Y_AXIS));
        JLabel docTitle = new JLabel("Documents");
        docTitle.setFont(UIHelper.FONT_SUBTITLE);
        docTitle.setForeground(UIHelper.PRIMARY);
        docCard.add(docTitle);
        docCard.add(Box.createVerticalStrut(10));
        resumeLabel = new JLabel("Resume: " + (resumePath != null ? resumePath : "Not uploaded"));
        resumeLabel.setFont(UIHelper.FONT_BODY);
        resumeLabel.setForeground(UIHelper.TEXT_SECONDARY);
        docCard.add(resumeLabel);
        docCard.add(Box.createVerticalStrut(10));
        JButton uploadBtn = UIHelper.primaryButton("Upload Resume (PDF)");
        uploadBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        docCard.add(uploadBtn);
        rightPanel.add(docCard, BorderLayout.NORTH);

        // Password change card
        JPanel passCard = UIHelper.cardPanel();
        passCard.setLayout(new GridBagLayout());
        GridBagConstraints pgbc = new GridBagConstraints();
        pgbc.insets = new Insets(6, 8, 6, 8);
        pgbc.fill = GridBagConstraints.HORIZONTAL;
        pgbc.anchor = GridBagConstraints.WEST;

        JLabel passTitle = new JLabel("Change Password");
        passTitle.setFont(UIHelper.FONT_SUBTITLE);
        passTitle.setForeground(UIHelper.PRIMARY);
        pgbc.gridx = 0; pgbc.gridy = 0; pgbc.gridwidth = 2;
        passCard.add(passTitle, pgbc);
        pgbc.gridwidth = 1;

        JPasswordField newPassField     = UIHelper.passwordField();
        JPasswordField confirmPassField = UIHelper.passwordField();

        pgbc.gridx = 0; pgbc.gridy = 1; pgbc.weightx = 0;
        passCard.add(UIHelper.formLabel("New Password"), pgbc);
        pgbc.gridx = 1; pgbc.weightx = 1;
        passCard.add(newPassField, pgbc);

        pgbc.gridx = 0; pgbc.gridy = 2; pgbc.weightx = 0;
        passCard.add(UIHelper.formLabel("Confirm Password"), pgbc);
        pgbc.gridx = 1; pgbc.weightx = 1;
        passCard.add(confirmPassField, pgbc);

        pgbc.gridx = 0; pgbc.gridy = 3; pgbc.gridwidth = 2;
        pgbc.insets = new Insets(12, 8, 6, 8);
        JButton changePassBtn = UIHelper.accentButton("Change Password");
        passCard.add(changePassBtn, pgbc);
        rightPanel.add(passCard, BorderLayout.CENTER);

        content.add(rightPanel);
        add(content, BorderLayout.CENTER);

        // ── Save button ──────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        JButton saveBtn = UIHelper.primaryButton("Save Profile");
        btnPanel.add(saveBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // Pre-fill fields
        prefillFields();

        // ── Listeners ────────────────────────────────────────────────
        uploadBtn.addActionListener(e -> chooseResume());
        saveBtn.addActionListener(e -> saveProfile());
        changePassBtn.addActionListener(e ->
                changePassword(new String(newPassField.getPassword()),
                               new String(confirmPassField.getPassword()),
                               newPassField, confirmPassField));
    }

    private void prefillFields() {
        fullNameField.setText(student.getFullName() != null ? student.getFullName() : "");
        phoneField.setText(student.getPhone()       != null ? student.getPhone()    : "");
        courseField.setText(student.getCourse()     != null ? student.getCourse()   : "");
        branchField.setText(student.getBranch()     != null ? student.getBranch()   : "");
        sectionField.setText(student.getSection()   != null ? student.getSection()  : "");
        cgpaField.setText(student.getCgpa() > 0 ? String.valueOf(student.getCgpa()) : "");
    }

    private void chooseResume() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            resumePath = f.getAbsolutePath();
            resumeLabel.setText("Resume: " + f.getName());
        }
    }

    private void saveProfile() {
        String fullName = fullNameField.getText().trim();
        String phone    = phoneField.getText().trim();
        String course   = courseField.getText().trim();
        String branch   = branchField.getText().trim();
        String section  = sectionField.getText().trim();
        String cgpaStr  = cgpaField.getText().trim();

        if (fullName.isEmpty() || course.isEmpty() || branch.isEmpty()) {
            UIHelper.error(this, "Full name, course, and branch are required.");
            return;
        }
        double cgpa = 0;
        if (!cgpaStr.isEmpty()) {
            try {
                cgpa = Double.parseDouble(cgpaStr);
            } catch (NumberFormatException e) {
                UIHelper.error(this, "CGPA must be a valid number."); return;
            }
        }

        student.setFullName(fullName);
        student.setPhone(phone);
        student.setCourse(course);
        student.setBranch(branch);
        student.setSection(section);
        student.setCgpa(cgpa);
        student.setResumePath(resumePath);

        if (studentDAO.updateProfile(student)) {
            UIHelper.success(this, "Profile updated successfully!");
        } else {
            UIHelper.error(this, "Failed to update profile.");
        }
    }

    private void changePassword(String newPass, String confirm,
                                JPasswordField f1, JPasswordField f2) {
        if (newPass.isEmpty()) { UIHelper.error(this, "Password cannot be empty."); return; }
        if (!newPass.equals(confirm)) { UIHelper.error(this, "Passwords do not match."); return; }
        if (newPass.length() < 6) { UIHelper.error(this, "Minimum 6 characters."); return; }

        if (studentDAO.resetPassword(student.getEmail(), newPass)) {
            student.setPassword(newPass);
            UIHelper.success(this, "Password changed successfully.");
            f1.setText(""); f2.setText("");
        } else {
            UIHelper.error(this, "Failed to change password.");
        }
    }
}
