package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Centralised look-and-feel constants and helper factories so every frame
 * shares the same palette without repeating literals.
 */
public class UIHelper {

    // ---- Palette ----
    public static final Color PRIMARY        = new Color(30, 90, 180);
    public static final Color PRIMARY_DARK   = new Color(20, 60, 140);
    public static final Color ACCENT         = new Color(0, 168, 120);
    public static final Color DANGER         = new Color(200, 50, 50);
    public static final Color BG_LIGHT       = new Color(245, 247, 252);
    public static final Color CARD_BG        = Color.WHITE;
    public static final Color TEXT_PRIMARY   = new Color(30, 30, 60);
    public static final Color TEXT_SECONDARY = new Color(100, 110, 130);
    public static final Color BORDER_COLOR   = new Color(210, 215, 225);
    public static final Color STATUS_PENDING  = new Color(230, 170, 0);
    public static final Color STATUS_ACCEPTED = new Color(0, 160, 90);
    public static final Color STATUS_REJECTED = new Color(200, 50, 50);

    // ---- Fonts ----
    public static final Font FONT_TITLE    = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY     = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL    = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BOLD     = new Font("Segoe UI", Font.BOLD, 13);

    private UIHelper() {}

    /** Apply Nimbus look-and-feel (falls back to system). */
    public static void applyLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    UIManager.put("control",         BG_LIGHT);
                    UIManager.put("info",            BG_LIGHT);
                    UIManager.put("nimbusBase",      PRIMARY);
                    UIManager.put("nimbusBlueGrey",  new Color(130, 145, 175));
                    return;
                }
            }
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
    }

    /** Styled primary button. */
    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setOpaque(true);
        return btn;
    }

    /** Styled danger button. */
    public static JButton dangerButton(String text) {
        JButton btn = primaryButton(text);
        btn.setBackground(DANGER);
        return btn;
    }

    /** Styled accent button. */
    public static JButton accentButton(String text) {
        JButton btn = primaryButton(text);
        btn.setBackground(ACCENT);
        return btn;
    }

    /** Styled text field. */
    public static JTextField textField() {
        JTextField tf = new JTextField();
        tf.setFont(FONT_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(5, 8, 5, 8)));
        return tf;
    }

    /** Styled password field. */
    public static JPasswordField passwordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(FONT_BODY);
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(5, 8, 5, 8)));
        return pf;
    }

    /** Styled text area inside a scroll pane. */
    public static JScrollPane scrollableTextArea(JTextArea ta) {
        ta.setFont(FONT_BODY);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBorder(new EmptyBorder(5, 8, 5, 8));
        JScrollPane sp = new JScrollPane(ta);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        return sp;
    }

    /** Section header label. */
    public static JLabel headerLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_TITLE);
        lbl.setForeground(PRIMARY);
        return lbl;
    }

    /** Form label. */
    public static JLabel formLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_BOLD);
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    /** Card panel (white rounded-look panel). */
    public static JPanel cardPanel() {
        JPanel p = new JPanel();
        p.setBackground(CARD_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(15, 15, 15, 15)));
        return p;
    }

    /** JTable styled header. */
    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(28);
        table.setGridColor(BORDER_COLOR);
        table.getTableHeader().setFont(FONT_BOLD);
        table.getTableHeader().setBackground(PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(200, 220, 255));
        table.setFillsViewportHeight(true);
    }

    /** Show error dialog. */
    public static void error(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Show success dialog. */
    public static void success(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Show confirmation dialog. */
    public static boolean confirm(Component parent, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    /** Centre a window on screen. */
    public static void centre(Window w) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        w.setLocation((screen.width - w.getWidth()) / 2,
                      (screen.height - w.getHeight()) / 2);
    }
}
