package com.reservation.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

/** Main application window shown after a successful login: tabs for Booking and Cancellation. */
public class MainFrame extends JFrame {

    public MainFrame(String username, String fullName) {
        super("RailConnect — Online Reservation System");

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UiKit.BG);

        // ----- Header with user info and logout -----
        JPanel banner = UiKit.banner("RailConnect", "Online Reservation System");
        JLabel user = new JLabel("Logged in as:  " + fullName + "  (" + username + ")  ");
        user.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        user.setForeground(new Color(0xC9, 0xD4, 0xF0));

        JButton logout = new JButton("Logout");
        logout.setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
        logout.setForeground(Color.WHITE);
        logout.setBackground(UiKit.NAVY_DARK);
        logout.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, new Color(0x3A, 0x4E, 0x7E)),
                new EmptyBorder(6, 16, 6, 16)));
        logout.setFocusPainted(false);
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logout.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Log out of RailConnect?", "Logout",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 18));
        right.setOpaque(false);
        right.add(user);
        right.add(logout);
        banner.add(right, BorderLayout.EAST);
        root.add(banner, BorderLayout.NORTH);

        // ----- Tabs -----
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI Semibold", Font.BOLD, 13));
        tabs.setBackground(UiKit.BG);
        tabs.addTab("  Book Ticket  ", new BookingPanel(username));
        tabs.addTab("  Cancel Ticket (PNR)  ", new CancellationPanel());
        tabs.setBorder(new EmptyBorder(6, 10, 8, 10));
        root.add(tabs, BorderLayout.CENTER);

        // ----- Status bar -----
        JLabel status = new JLabel("  Connected to MySQL 8 · train_reservation @ localhost:3306");
        status.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        status.setForeground(UiKit.MUTED);
        status.setBorder(new EmptyBorder(4, 6, 6, 6));
        root.add(status, BorderLayout.SOUTH);

        setContentPane(root);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(860, 640);
        setMinimumSize(new Dimension(760, 580));
        setLocationRelativeTo(null);
    }
}
