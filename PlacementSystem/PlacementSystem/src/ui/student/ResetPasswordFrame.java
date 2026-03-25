package ui.student;

import dao.StudentDAO;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import ui.UIHelper;

public class ResetPasswordFrame extends JFrame {

    private final JFrame     parent;
    private final StudentDAO studentDAO = new StudentDAO();

    private JTextField     emailField;
    private JPasswordField newPassField, confirmPassField;

    public ResetPasswordFrame(JFrame parent) {
        super("Reset Password");
        this.parent = parent;
        setSize(400, 400);
        setResizable(true);
        UIHelper.centre(this);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIHelper.BG_LIGHT);

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(UIHelper.PRIMARY_DARK);
        header.setBorder(new EmptyBorder(12, 20, 12, 20));
        JLabel title = new JLabel("Reset Password");
        title.setFont(UIHelper.FONT_SUBTITLE);
        title.setForeground(Color.WHITE);
        header.add(title);
        root.add(header, BorderLayout.NORTH);

        // Form
        JPanel form = UIHelper.cardPanel();
        form.setLayout(new GridBagLayout());
        form.setBorder(new EmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1; gbc.gridx = 0;

        JLabel note = new JLabel("<html>Enter your registered email and a new password.</html>");
        note.setFont(UIHelper.FONT_SMALL);
        note.setForeground(UIHelper.TEXT_SECONDARY);
        gbc.gridy = 0; form.add(note, gbc);

        gbc.gridy = 1; form.add(UIHelper.formLabel("Registered Email"), gbc);
        gbc.gridy = 2; emailField = UIHelper.textField(); form.add(emailField, gbc);

        gbc.gridy = 3; form.add(UIHelper.formLabel("New Password"), gbc);
        gbc.gridy = 4; newPassField = UIHelper.passwordField(); form.add(newPassField, gbc);

        gbc.gridy = 5; form.add(UIHelper.formLabel("Confirm New Password"), gbc);
        gbc.gridy = 6; confirmPassField = UIHelper.passwordField(); form.add(confirmPassField, gbc);

        gbc.gridy = 7; gbc.insets = new Insets(14, 0, 0, 0);
        JButton resetBtn = UIHelper.primaryButton("Reset Password");
        form.add(resetBtn, gbc);

        root.add(form, BorderLayout.CENTER);
        setContentPane(root);

        resetBtn.addActionListener(e -> handleReset());
    }

    private void handleReset() {
        String email   = emailField.getText().trim();
        String newPass = new String(newPassField.getPassword());
        String confirm = new String(confirmPassField.getPassword());

        if (email.isEmpty() || newPass.isEmpty()) {
            UIHelper.error(this, "Please fill all fields."); return;
        }
        if (!newPass.equals(confirm)) {
            UIHelper.error(this, "Passwords do not match."); return;
        }
        if (newPass.length() < 6) {
            UIHelper.error(this, "Password must be at least 6 characters."); return;
        }

        if (studentDAO.resetPassword(email, newPass)) {
            UIHelper.success(this, "Password reset successfully! You can now login.");
            dispose();
        } else {
            UIHelper.error(this, "Email not found. Please check and try again.");
        }
    }
}
