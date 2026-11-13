package com.reservation.ui;

import com.reservation.dao.UserDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.sql.SQLException;

/** Login screen. Access is denied (with a dialog) for invalid credentials. */
public class LoginFrame extends JFrame {

    private final JTextField usernameField = UiKit.textField(18);
    private final JPasswordField passwordField = new JPasswordField(18);
    private final JButton signInButton = UiKit.primaryButton("Sign In");

    public LoginFrame() {
        super("RailConnect — Online Reservation System");

        passwordField.setFont(UiKit.INPUT_FONT);
        passwordField.setBackground(UiKit.INPUT_BG);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, new Color(0xC9, 0xD0, 0xDE)),
                new EmptyBorder(6, 8, 6, 8)));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiKit.BG);
        root.add(UiKit.banner("RailConnect", "Online Reservation System — Sign in to continue"), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(28, 24, 28, 24));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UiKit.CARD);
        card.setBorder(UiKit.cardBorder());
        card.setPreferredSize(new Dimension(420, 330));

        JLabel heading = new JLabel("Welcome back 👋");
        heading.setFont(new Font("Segoe UI Semibold", Font.BOLD, 20));
        heading.setForeground(UiKit.TEXT);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Enter your credentials to access booking and cancellation.");
        sub.setFont(UiKit.LABEL_FONT);
        sub.setForeground(UiKit.MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(heading);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(18));

        card.add(fieldLabel("User Name"));
        card.add(Box.createVerticalStrut(4));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(12));

        card.add(fieldLabel("Password"));
        card.add(Box.createVerticalStrut(4));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(18));

        JPanel buttons = new JPanel(new BorderLayout());
        buttons.setOpaque(false);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        signInButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttons.add(signInButton, BorderLayout.CENTER);
        card.add(buttons);
        card.add(Box.createVerticalStrut(12));

        JLabel hint = new JLabel("<html><font color='#6B748C'>Demo accounts — <b>admin</b> / admin123 &nbsp;·&nbsp; <b>user</b> / user123</font></html>");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(hint);

        center.add(card);
        root.add(center, BorderLayout.CENTER);

        setContentPane(root);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(620, 560);
        setMinimumSize(new Dimension(560, 520));
        setLocationRelativeTo(null);

        signInButton.addActionListener(e -> attemptLogin());
        getRootPane().setDefaultButton(signInButton);
    }

    private static JLabel fieldLabel(String text) {
        JLabel l = UiKit.label(text);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
        return l;
    }

    private void attemptLogin() {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            UiKit.showWarning(this, "Both user name and password are required.", "Missing Credentials");
            return;
        }

        signInButton.setEnabled(false);
        signInButton.setText("Signing in…");
        try {
            String fullName = UserDAO.authenticate(username, password);
            if (fullName == null) {
                // Access denied for invalid credentials
                UiKit.showError(this, "Access denied. Invalid user name or password.", "Login Failed");
                passwordField.setText("");
                passwordField.requestFocusInWindow();
                return;
            }
            MainFrame main = new MainFrame(username, fullName);
            main.setVisible(true);
            dispose();
        } catch (SQLException e) {
            UiKit.showError(this,
                    "Could not reach the database.\n\n" + e.getMessage(),
                    "Database Error");
        } finally {
            signInButton.setEnabled(true);
            signInButton.setText("Sign In");
        }
    }
}
