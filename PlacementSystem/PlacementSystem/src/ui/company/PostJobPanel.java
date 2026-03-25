package ui.company;

import dao.JobDAO;
import java.awt.*;
import java.sql.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import models.Company;
import models.Job;
import ui.UIHelper;

public class PostJobPanel extends JPanel {

    private final Company company;
    private final JobDAO  jobDAO = new JobDAO();

    private JTextField  titleField, salaryField, locationField, deadlineField;
    private JTextArea   descArea, eligArea, criteriaArea;
    private JComboBox<String> typeBox;

    public PostJobPanel(Company company) {
        this.company = company;
        setLayout(new BorderLayout(10, 10));
        setBackground(UIHelper.BG_LIGHT);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        buildUI();
    }

    private void buildUI() {
        add(UIHelper.headerLabel("Post a New Job / Placement Offer"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIHelper.CARD_BG);
        form.setBorder(new EmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(6, 8, 6, 8);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.anchor  = GridBagConstraints.WEST;

        titleField    = UIHelper.textField();
        salaryField   = UIHelper.textField();
        locationField = UIHelper.textField();
        deadlineField = UIHelper.textField();
        deadlineField.setToolTipText("Format: YYYY-MM-DD");
        typeBox       = new JComboBox<>(new String[]{"Job", "Placement"});
        typeBox.setFont(UIHelper.FONT_BODY);
        descArea     = new JTextArea(4, 30);
        eligArea     = new JTextArea(3, 30);
        criteriaArea = new JTextArea(3, 30);

        Object[][] rows = {
            {"Job Title*",           titleField},
            {"Job Type*",            typeBox},
            {"Salary / Stipend",     salaryField},
            {"Location",             locationField},
            {"Application Deadline*",""},  // placeholder row header only
            {"Description*",         UIHelper.scrollableTextArea(descArea)},
            {"Eligibility",          UIHelper.scrollableTextArea(eligArea)},
            {"Criteria",             UIHelper.scrollableTextArea(criteriaArea)},
        };

        // Manual layout to handle deadline field specially
        int r = 0;
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0;
        form.add(UIHelper.formLabel("Job Title*"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(titleField, gbc);

        r++;
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0;
        form.add(UIHelper.formLabel("Job Type*"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(typeBox, gbc);

        r++;
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0;
        form.add(UIHelper.formLabel("Salary / Stipend"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(salaryField, gbc);

        r++;
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0;
        form.add(UIHelper.formLabel("Location"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(locationField, gbc);

        r++;
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0;
        form.add(UIHelper.formLabel("Deadline (YYYY-MM-DD)*"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(deadlineField, gbc);

        r++;
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0;
        form.add(UIHelper.formLabel("Description*"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(UIHelper.scrollableTextArea(descArea), gbc);

        r++;
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0;
        form.add(UIHelper.formLabel("Eligibility"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(UIHelper.scrollableTextArea(eligArea), gbc);

        r++;
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0;
        form.add(UIHelper.formLabel("Criteria"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(UIHelper.scrollableTextArea(criteriaArea), gbc);

        JScrollPane scrollForm = new JScrollPane(form);
        scrollForm.setBorder(null);
        add(scrollForm, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        JButton clearBtn = new JButton("Clear Form");
        clearBtn.setFont(UIHelper.FONT_BODY);
        JButton postBtn = UIHelper.primaryButton("Post Job");
        btnPanel.add(clearBtn);
        btnPanel.add(postBtn);
        add(btnPanel, BorderLayout.SOUTH);

        clearBtn.addActionListener(e -> clearForm());
        postBtn.addActionListener(e -> postJob());
    }

    private void clearForm() {
        titleField.setText("");
        salaryField.setText("");
        locationField.setText("");
        deadlineField.setText("");
        typeBox.setSelectedIndex(0);
        descArea.setText("");
        eligArea.setText("");
        criteriaArea.setText("");
    }

    private void postJob() {
        String title    = titleField.getText().trim();
        String salary   = salaryField.getText().trim();
        String location = locationField.getText().trim();
        String deadline = deadlineField.getText().trim();
        String desc     = descArea.getText().trim();
        String elig     = eligArea.getText().trim();
        String criteria = criteriaArea.getText().trim();
        String type     = (String) typeBox.getSelectedItem();

        if (title.isEmpty() || deadline.isEmpty() || desc.isEmpty()) {
            UIHelper.error(this, "Title, deadline, and description are required.");
            return;
        }

        Date sqlDeadline;
        try {
            sqlDeadline = Date.valueOf(deadline);
        } catch (IllegalArgumentException ex) {
            UIHelper.error(this, "Deadline must be in YYYY-MM-DD format.");
            return;
        }

        Job job = new Job();
        job.setCompanyId(company.getId());
        job.setTitle(title);
        job.setSalary(salary);
        job.setLocation(location);
        job.setDeadline(sqlDeadline);
        job.setDescription(desc);
        job.setEligibility(elig);
        job.setCriteria(criteria);
        job.setJobType(type);
        job.setStatus("available");

        int id = jobDAO.postJob(job);
        if (id > 0) {
            UIHelper.success(this, "Job posted successfully (ID: " + id + ").");
            clearForm();
        } else {
            UIHelper.error(this, "Failed to post job. Please try again.");
        }
    }
}
