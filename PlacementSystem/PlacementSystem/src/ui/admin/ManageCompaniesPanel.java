package ui.admin;

import dao.CompanyDAO;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import models.Company;
import ui.UIHelper;

public class ManageCompaniesPanel extends JPanel {

    private final CompanyDAO companyDAO = new CompanyDAO();

    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;

    public ManageCompaniesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIHelper.BG_LIGHT);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        buildUI();
        loadCompanies();
    }

    private void buildUI() {
        // ── Header ───────────────────────────────────────────────────
        JPanel top = new JPanel(new BorderLayout(10, 8));
        top.setOpaque(false);
        top.add(UIHelper.headerLabel("Manage Companies"), BorderLayout.NORTH);

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchBar.setOpaque(false);
        searchBar.add(UIHelper.formLabel("Search:"));
        searchField = UIHelper.textField();
        searchField.setPreferredSize(new Dimension(200, 30));
        searchBar.add(searchField);
        JButton searchBtn = UIHelper.primaryButton("Search");
        JButton refreshBtn = UIHelper.accentButton("Refresh");
        JButton addBtn = UIHelper.primaryButton("Add Company");
        searchBar.add(searchBtn);
        searchBar.add(refreshBtn);
        searchBar.add(Box.createHorizontalStrut(20));
        searchBar.add(addBtn);
        top.add(searchBar, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        // ── Table ────────────────────────────────────────────────────
        String[] cols = { "ID", "Company Name", "Industry", "HR Contact", "Email", "Status" };
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(model);
        UIHelper.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(130);
        table.getColumnModel().getColumn(3).setPreferredWidth(130);
        table.getColumnModel().getColumn(4).setPreferredWidth(180);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(UIHelper.BORDER_COLOR));
        add(sp, BorderLayout.CENTER);

        // ── Action buttons ───────────────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        btnPanel.setOpaque(false);
        JButton viewBtn = UIHelper.primaryButton("View Details");
        JButton editBtn = UIHelper.accentButton("Edit");
        JButton deleteBtn = UIHelper.dangerButton("Delete");
        btnPanel.add(viewBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // ── Listeners ────────────────────────────────────────────────
        searchBtn.addActionListener(e -> searchCompanies());
        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            loadCompanies();
        });
        addBtn.addActionListener(e -> showAddDialog());
        viewBtn.addActionListener(e -> viewDetails());
        editBtn.addActionListener(e -> editCompany());
        deleteBtn.addActionListener(e -> deleteCompany());
    }

    // ── Data loading ─────────────────────────────────────────────────

    private void loadCompanies() {
        model.setRowCount(0);
        for (Company c : companyDAO.getAll())
            addRow(c);
        if (model.getRowCount() == 0)
            model.addRow(new Object[] { "", "No companies found", "", "", "", "" });
    }

    private void searchCompanies() {
        model.setRowCount(0);
        String q = searchField.getText().trim().toLowerCase();
        for (Company c : companyDAO.getAll()) {
            if (q.isEmpty() ||
                    c.getCompanyName().toLowerCase().contains(q) ||
                    (c.getIndustry() != null && c.getIndustry().toLowerCase().contains(q))) {
                addRow(c);
            }
        }
        if (model.getRowCount() == 0)
            model.addRow(new Object[] { "", "No results", "", "", "", "" });
    }

    private void addRow(Company c) {
        model.addRow(new Object[] {
                c.getId(), c.getCompanyName(),
                c.getIndustry() != null ? c.getIndustry() : "",
                c.getHrContact() != null ? c.getHrContact() : "",
                c.getEmail(), c.getStatus()
        });
    }

    // ── Actions ──────────────────────────────────────────────────────

    private Company getSelectedCompany() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UIHelper.error(this, "Please select a company.");
            return null;
        }
        Object idObj = model.getValueAt(row, 0);
        if (idObj == null || idObj.toString().isEmpty())
            return null;
        return companyDAO.getById(Integer.parseInt(idObj.toString()));
    }

    private void viewDetails() {
        Company c = getSelectedCompany();
        if (c == null)
            return;
        String info = "Company Name : " + c.getCompanyName() + "\n" +
                "Industry     : " + c.getIndustry() + "\n" +
                "Email        : " + c.getEmail() + "\n" +
                "HR Contact   : " + c.getHrContact() + "\n" +
                "Website      : " + c.getWebsite() + "\n" +
                "Status       : " + c.getStatus() + "\n\n" +
                "Description:\n" + (c.getDescription() != null ? c.getDescription() : "N/A");
        JTextArea ta = new JTextArea(info);
        ta.setEditable(false);
        ta.setFont(UIHelper.FONT_BODY);
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(480, 300));
        JOptionPane.showMessageDialog(this, sp, "Company Details – " + c.getCompanyName(),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void editCompany() {
        Company c = getSelectedCompany();
        if (c == null)
            return;
        showCompanyForm(c, false);
    }

    private void deleteCompany() {
        Company c = getSelectedCompany();
        if (c == null)
            return;
        if (UIHelper.confirm(this, "Delete company '" + c.getCompanyName()
                + "'?\nAll associated jobs and applications will also be removed.")) {
            if (companyDAO.delete(c.getId())) {
                UIHelper.success(this, "Company deleted.");
                loadCompanies();
            } else {
                UIHelper.error(this, "Failed to delete company.");
            }
        }
    }

    private void showAddDialog() {
        showCompanyForm(new Company(), true);
    }

    private void showCompanyForm(Company c, boolean isNew) {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                isNew ? "Add New Company" : "Edit Company", true);
        dlg.setSize(500, 520);
        UIHelper.centre(dlg);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIHelper.CARD_BG);
        form.setBorder(new EmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JTextField nameField = UIHelper.textField();
        nameField.setText(c.getCompanyName() != null ? c.getCompanyName() : "");
        JTextField emailField = UIHelper.textField();
        emailField.setText(c.getEmail() != null ? c.getEmail() : "");
        JPasswordField passField = UIHelper.passwordField();
        JTextField industryField = UIHelper.textField();
        industryField.setText(c.getIndustry() != null ? c.getIndustry() : "");
        JTextField websiteField = UIHelper.textField();
        websiteField.setText(c.getWebsite() != null ? c.getWebsite() : "");
        JTextField hrField = UIHelper.textField();
        hrField.setText(c.getHrContact() != null ? c.getHrContact() : "");
        JTextArea descArea = new JTextArea(4, 25);
        descArea.setText(c.getDescription() != null ? c.getDescription() : "");
        JComboBox<String> statusBox = new JComboBox<>(new String[] { "active", "inactive" });
        if (c.getStatus() != null)
            statusBox.setSelectedItem(c.getStatus());

        Object[][] rows;
        if (isNew) {
            rows = new Object[][] {
                    { "Company Name*", nameField },
                    { "Email*", emailField },
                    { "Password*", passField },
                    { "Industry", industryField },
                    { "Website", websiteField },
                    { "HR Contact", hrField },
                    { "Description", UIHelper.scrollableTextArea(descArea) },
            };
        } else {
            rows = new Object[][] {
                    { "Company Name*", nameField },
                    { "Email*", emailField },
                    { "Industry", industryField },
                    { "Website", websiteField },
                    { "HR Contact", hrField },
                    { "Status", statusBox },
                    { "Description", UIHelper.scrollableTextArea(descArea) },
            };
        }

        for (int i = 0; i < rows.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0;
            form.add(UIHelper.formLabel((String) rows[i][0]), gbc);
            gbc.gridx = 1;
            gbc.weightx = 1;
            form.add((Component) rows[i][1], gbc);
        }

        gbc.gridx = 0;
        gbc.gridy = rows.length;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 5, 5, 5);
        JButton saveBtn = UIHelper.primaryButton(isNew ? "Add Company" : "Save Changes");
        form.add(saveBtn, gbc);

        dlg.add(new JScrollPane(form));

        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            if (name.isEmpty() || email.isEmpty()) {
                UIHelper.error(dlg, "Name and email are required.");
                return;
            }
            if (isNew && new String(passField.getPassword()).isEmpty()) {
                UIHelper.error(dlg, "Password is required for new company.");
                return;
            }

            c.setCompanyName(name);
            c.setEmail(email);
            c.setIndustry(industryField.getText().trim());
            c.setWebsite(websiteField.getText().trim());
            c.setHrContact(hrField.getText().trim());
            c.setDescription(descArea.getText().trim());

            boolean ok;
            if (isNew) {
                c.setPassword(new String(passField.getPassword()));
                ok = companyDAO.add(c) > 0;
            } else {
                c.setStatus((String) statusBox.getSelectedItem());
                ok = companyDAO.update(c);
            }

            if (ok) {
                UIHelper.success(dlg, isNew ? "Company added!" : "Company updated!");
                loadCompanies();
                dlg.dispose();
            } else {
                UIHelper.error(dlg, "Operation failed. Email may already exist.");
            }
        });
        dlg.setVisible(true);
    }
}
