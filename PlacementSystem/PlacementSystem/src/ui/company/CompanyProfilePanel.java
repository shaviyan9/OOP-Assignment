package ui.company;

import dao.CompanyDAO;
import models.Company;
import ui.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CompanyProfilePanel extends JPanel {

    private final Company    company;
    private final CompanyDAO companyDAO = new CompanyDAO();

    private JTextField  nameField, industryField, websiteField, hrField;
    private JTextArea   descArea;

    public CompanyProfilePanel(Company company) {
        this.company = company;
        setLayout(new BorderLayout(10, 10));
        setBackground(UIHelper.BG_LIGHT);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        buildUI();
    }

    private void buildUI() {
        add(UIHelper.headerLabel("Company Profile"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIHelper.CARD_BG);
        form.setBorder(new EmptyBorder(25, 35, 25, 35));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 8, 7, 8);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        nameField     = UIHelper.textField(); nameField.setText(company.getCompanyName());
        industryField = UIHelper.textField(); industryField.setText(company.getIndustry()  != null ? company.getIndustry()  : "");
        websiteField  = UIHelper.textField(); websiteField.setText(company.getWebsite()    != null ? company.getWebsite()   : "");
        hrField       = UIHelper.textField(); hrField.setText(company.getHrContact()       != null ? company.getHrContact() : "");
        descArea      = new JTextArea(5, 30); descArea.setText(company.getDescription()    != null ? company.getDescription() : "");

        Object[][] rows = {
            {"Company Name*",  nameField},
            {"Industry",       industryField},
            {"Website",        websiteField},
            {"HR Contact",     hrField},
            {"Description",    UIHelper.scrollableTextArea(descArea)},
        };

        for (int i = 0; i < rows.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            form.add(UIHelper.formLabel((String) rows[i][0]), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            form.add((Component) rows[i][1], gbc);
        }

        // Read-only email row
        gbc.gridx = 0; gbc.gridy = rows.length; gbc.weightx = 0;
        form.add(UIHelper.formLabel("Email (read-only)"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JLabel emailLabel = new JLabel(company.getEmail());
        emailLabel.setFont(UIHelper.FONT_BODY);
        emailLabel.setForeground(UIHelper.TEXT_SECONDARY);
        form.add(emailLabel, gbc);

        add(new JScrollPane(form), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        JButton saveBtn = UIHelper.primaryButton("Save Changes");
        btnPanel.add(saveBtn);
        add(btnPanel, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> saveProfile());
    }

    private void saveProfile() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) { UIHelper.error(this, "Company name is required."); return; }

        company.setCompanyName(name);
        company.setIndustry(industryField.getText().trim());
        company.setWebsite(websiteField.getText().trim());
        company.setHrContact(hrField.getText().trim());
        company.setDescription(descArea.getText().trim());

        if (companyDAO.updateDetails(company)) {
            UIHelper.success(this, "Company profile updated successfully.");
        } else {
            UIHelper.error(this, "Failed to update profile.");
        }
    }
}
