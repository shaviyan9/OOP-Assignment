package ui.student;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import ui.UIHelper;

/** Tab that displays the system privacy policy. */
public class PrivacyPolicyPanel extends JPanel {

    public PrivacyPolicyPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIHelper.BG_LIGHT);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        buildUI();
    }

    private void buildUI() {
        add(UIHelper.headerLabel("Privacy Policy"), BorderLayout.NORTH);

        JTextArea ta = new JTextArea(POLICY_TEXT);
        ta.setEditable(false);
        ta.setFont(UIHelper.FONT_BODY);
        ta.setForeground(UIHelper.TEXT_PRIMARY);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBackground(UIHelper.CARD_BG);
        ta.setBorder(new EmptyBorder(15, 15, 15, 15));

        JScrollPane sp = new JScrollPane(ta);
        sp.setBorder(BorderFactory.createLineBorder(UIHelper.BORDER_COLOR));
        add(sp, BorderLayout.CENTER);

        JLabel updated = new JLabel("Last updated: January 2025");
        updated.setFont(UIHelper.FONT_SMALL);
        updated.setForeground(UIHelper.TEXT_SECONDARY);
        add(updated, BorderLayout.SOUTH);
    }

    private static final String POLICY_TEXT = "PLACEMENT & JOB RECRUITMENT SYSTEM — PRIVACY POLICY\n" +
            "═══════════════════════════════════════════════════\n\n" +

            "1. INTRODUCTION\n" +
            "This Privacy Policy describes how the Placement and Job Recruitment System\n" +
            "(the \"System\") collects, uses, and protects your personal information.\n\n" +

            "2. INFORMATION WE COLLECT\n" +
            "We collect information you provide when you register, including:\n" +
            "  • Personal details: full name, email address, phone number.\n" +
            "  • Academic details: course, branch, section, and CGPA.\n" +
            "  • Documents: uploaded resumes and supporting files.\n" +
            "  • Application data: job applications, Application Letter, and offer letters.\n\n" +

            "3. HOW WE USE YOUR INFORMATION\n" +
            "Your information is used to:\n" +
            "  • Facilitate job and placement applications.\n" +
            "  • Allow companies to review and evaluate your profile.\n" +
            "  • Allow administrators to manage and track placement activities.\n" +
            "  • Notify you of application status updates.\n\n" +

            "4. DATA SHARING\n" +
            "Your profile and academic information may be shared with:\n" +
            "  • Registered companies to whom you apply.\n" +
            "  • The placement department administrators.\n" +
            "Your data will NOT be sold or disclosed to unrelated third parties.\n\n" +

            "5. DATA SECURITY\n" +
            "We implement reasonable technical safeguards to protect your data.\n" +
            "You are responsible for keeping your login credentials confidential.\n\n" +

            "6. YOUR RIGHTS\n" +
            "You may at any time:\n" +
            "  • View and update your personal and academic profile.\n" +
            "  • Upload or replace your resume.\n" +
            "  • Request account deletion by contacting the administrator.\n\n" +

            "7. COOKIES & TRACKING\n" +
            "This stand-alone desktop application does not use browser cookies.\n" +
            "No analytics or tracking scripts are embedded.\n\n" +

            "8. CHANGES TO THIS POLICY\n" +
            "We reserve the right to update this policy. Significant changes will\n" +
            "be communicated at the next login.\n\n" +

            "9. CONTACT\n" +
            "For privacy-related enquiries, contact the Placement Department Administrator.\n";
}
