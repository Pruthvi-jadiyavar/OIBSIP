package com.reservation.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

/** Shared colors, fonts and small builders used across the screens. */
public final class UiKit {

    public static final Color NAVY        = new Color(0x14, 0x26, 0x4C);
    public static final Color NAVY_DARK   = new Color(0x0E, 0x1B, 0x38);
    public static final Color ACCENT      = new Color(0xF2, 0xA6, 0x3B); // saffron
    public static final Color ACCENT_DARK = new Color(0xD9, 0x8B, 0x1F);
    public static final Color BG          = new Color(0xF2, 0xF4, 0xFA);
    public static final Color CARD        = Color.WHITE;
    public static final Color TEXT        = new Color(0x1F, 0x2A, 0x44);
    public static final Color MUTED       = new Color(0x6B, 0x74, 0x8C);
    public static final Color DANGER      = new Color(0xC03A2B);
    public static final Color DANGER_DARK = new Color(0xA32E21);
    public static final Color INPUT_BG    = new Color(0xFB, 0xFC, 0xFE);

    public static final Font TITLE_FONT = new Font("Segoe UI Semibold", Font.BOLD, 22);
    public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font SECTION_FONT = new Font("Segoe UI Semibold", Font.BOLD, 13);
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font BUTTON_FONT = new Font("Segoe UI Semibold", Font.BOLD, 13);

    private UiKit() {}

    /** Top banner used on the login and main screens. */
    public static JPanel banner(String title, String subtitle) {
        JPanel banner = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, NAVY_DARK, getWidth(), 0, NAVY));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(ACCENT);
                g2.fillRect(0, getHeight() - 4, getWidth(), 4);
                g2.dispose();
            }
        };
        banner.setLayout(new BorderLayout());
        banner.setBorder(new EmptyBorder(16, 22, 18, 22));

        JLabel t = new JLabel(title);
        t.setFont(TITLE_FONT);
        t.setForeground(Color.WHITE);
        JLabel s = new JLabel(subtitle);
        s.setFont(SUBTITLE_FONT);
        s.setForeground(new Color(0xC9, 0xD4, 0xF0));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(t);
        left.add(s);

        JLabel logo = new JLabel("🚆");
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 38));

        banner.add(left, BorderLayout.WEST);
        banner.add(logo, BorderLayout.EAST);
        return banner;
    }

    public static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, new Color(0xDD, 0xE2, 0xEC)),
                new EmptyBorder(18, 22, 18, 22));
    }

    public static JLabel section(String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(new Font("Segoe UI Semibold", Font.BOLD, 11));
        l.setForeground(MUTED);
        l.setBorder(new EmptyBorder(0, 2, 6, 0));
        return l;
    }

    public static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(LABEL_FONT);
        l.setForeground(TEXT);
        return l;
    }

    public static JTextField textField(int columns) {
        JTextField f = new JTextField(columns);
        f.setFont(INPUT_FONT);
        f.setBackground(INPUT_BG);
        f.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, new Color(0xC9, 0xD0, 0xDE)),
                new EmptyBorder(6, 8, 6, 8)));
        return f;
    }

    /** Solid accent (primary) button. */
    public static JButton primaryButton(String text) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isEnabled()) { super.paintComponent(g); return; }
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(getModel().isRollover() ? ACCENT_DARK : ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(BUTTON_FONT);
        b.setForeground(new Color(0x2B, 0x1E, 0x05));
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(9, 18, 9, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** Outlined neutral button. */
    public static JButton secondaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(BUTTON_FONT);
        b.setForeground(TEXT);
        b.setBackground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, new Color(0xC9, 0xD0, 0xDE)),
                new EmptyBorder(8, 16, 8, 16)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** Solid red danger button. */
    public static JButton dangerButton(String text) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isEnabled()) { super.paintComponent(g); return; }
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(getModel().isRollover() ? DANGER_DARK : DANGER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(BUTTON_FONT);
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(9, 18, 9, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showError(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfo(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showWarning(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
    }
}
