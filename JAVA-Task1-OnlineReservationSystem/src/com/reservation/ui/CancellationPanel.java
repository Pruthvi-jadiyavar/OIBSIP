package com.reservation.ui;

import com.reservation.dao.ReservationDAO;
import com.reservation.model.Reservation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

/** Cancellation form: fetch a booking by PNR, then confirm cancellation. */
public class CancellationPanel extends JPanel {

    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private static final DateTimeFormatter DISPLAY_TIME = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

    private final JTextField pnrField = UiKit.textField(14);
    private final JButton fetchButton = UiKit.secondaryButton("Fetch");
    private final JButton cancelButton = UiKit.dangerButton("Confirm Cancellation");
    private final JButton clearButton = UiKit.secondaryButton("Clear");
    private final JLabel fetchStatus = new JLabel(" ");

    private final JTextField valName = readOnlyField();
    private final JTextField valPnr = readOnlyField();
    private final JTextField valTrain = readOnlyField();
    private final JTextField valTrainName = readOnlyField();
    private final JTextField valClass = readOnlyField();
    private final JTextField valDate = readOnlyField();
    private final JTextField valFrom = readOnlyField();
    private final JTextField valTo = readOnlyField();
    private final JTextField valBookedBy = readOnlyField();
    private final JTextField valBookedOn = readOnlyField();

    private Reservation current;

    public CancellationPanel() {
        setLayout(new BorderLayout());
        setBackground(UiKit.BG);
        setBorder(new EmptyBorder(18, 22, 18, 22));

        ((javax.swing.text.AbstractDocument) pnrField.getDocument()).setDocumentFilter(digitsOnly(10));
        fetchStatus.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        fetchStatus.setForeground(UiKit.MUTED);
        cancelButton.setEnabled(false);

        // ---------- PNR lookup card ----------
        JPanel searchCard = new JPanel(new GridBagLayout());
        searchCard.setBackground(UiKit.CARD);
        searchCard.setBorder(UiKit.cardBorder());

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 8, 4, 8);
        gc.anchor = GridBagConstraints.WEST;

        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
        searchCard.add(UiKit.section("Find Booking by PNR"), gc);

        gc.gridwidth = 1; gc.gridy = 1; gc.gridx = 0;
        searchCard.add(UiKit.label("PNR Number *"), gc);
        JPanel pnrRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnrRow.setOpaque(false);
        pnrRow.add(pnrField);
        pnrRow.add(fetchButton);
        gc.gridx = 1; gc.weightx = 1;
        searchCard.add(pnrRow, gc);

        gc.gridy = 2; gc.gridx = 1; gc.insets = new Insets(0, 8, 4, 8);
        searchCard.add(fetchStatus, gc);

        // ---------- Details card ----------
        JPanel detailsCard = new JPanel(new GridBagLayout());
        detailsCard.setBackground(UiKit.CARD);
        detailsCard.setBorder(UiKit.cardBorder());

        GridBagConstraints dc = new GridBagConstraints();
        dc.insets = new Insets(5, 8, 5, 8);
        dc.anchor = GridBagConstraints.WEST;
        dc.fill = GridBagConstraints.HORIZONTAL;

        dc.gridx = 0; dc.gridy = 0; dc.gridwidth = 2;
        detailsCard.add(UiKit.section("Booking Details"), dc);
        dc.gridwidth = 1;

        int row = 1;
        addDetailRow(detailsCard, dc, row++, "PNR Number", valPnr);
        addDetailRow(detailsCard, dc, row++, "Passenger Name", valName);
        addDetailRow(detailsCard, dc, row++, "Train Number", valTrain);
        addDetailRow(detailsCard, dc, row++, "Train Name", valTrainName);
        addDetailRow(detailsCard, dc, row++, "Class Type", valClass);
        addDetailRow(detailsCard, dc, row++, "Date of Journey", valDate);
        addDetailRow(detailsCard, dc, row++, "From", valFrom);
        addDetailRow(detailsCard, dc, row++, "To", valTo);
        addDetailRow(detailsCard, dc, row++, "Booked By", valBookedBy);
        addDetailRow(detailsCard, dc, row, "Booked On", valBookedOn);

        // ---------- South: actions ----------
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionRow.setOpaque(false);
        actionRow.setBorder(new EmptyBorder(12, 0, 0, 0));
        actionRow.add(clearButton);
        actionRow.add(cancelButton);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        searchCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, searchCard.getPreferredSize().height));
        detailsCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, detailsCard.getPreferredSize().height));
        content.add(searchCard);
        content.add(Box.createVerticalStrut(14));
        content.add(detailsCard);

        add(content, BorderLayout.CENTER);
        add(actionRow, BorderLayout.SOUTH);

        fetchButton.addActionListener(e -> fetchBooking());
        pnrField.addActionListener(e -> fetchBooking());
        cancelButton.addActionListener(e -> confirmCancellation());
        clearButton.addActionListener(e -> clearAll());
    }

    private static JTextField readOnlyField() {
        JTextField f = UiKit.textField(24);
        f.setEditable(false);
        f.setBackground(new Color(0xEF, 0xF1, 0xF7));
        f.setForeground(UiKit.TEXT);
        return f;
    }

    private static void addDetailRow(JPanel card, GridBagConstraints gc, int row, String label, JComponent field) {
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0;
        card.add(UiKit.label(label), gc);
        gc.gridx = 1; gc.weightx = 1;
        card.add(field, gc);
    }

    private static DocumentFilter digitsOnly(int maxLength) {
        return new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string != null && string.matches("\\d*") && fb.getDocument().getLength() + string.length() <= maxLength) {
                    fb.insertString(offset, string, attr);
                }
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text != null && text.matches("\\d*") && fb.getDocument().getLength() - length + text.length() <= maxLength) {
                    fb.replace(offset, length, text, attrs);
                }
            }
        };
    }

    /** PNR + Fetch button → retrieve and display the full booking details. */
    private void fetchBooking() {
        String pnr = pnrField.getText().trim();
        if (pnr.isEmpty()) {
            UiKit.showWarning(this, "Please enter a PNR number.", "Validation"); return;
        }
        if (!pnr.matches("\\d{10}")) {
            UiKit.showWarning(this, "PNR must be exactly 10 digits.", "Validation"); return;
        }
        fetchButton.setEnabled(false);
        fetchButton.setText("Fetching…");
        try {
            Reservation r = ReservationDAO.findByPnr(pnr);
            if (r == null) {
                clearDetails();
                current = null;
                cancelButton.setEnabled(false);
                fetchStatus.setText("No reservation found for PNR " + pnr + ".");
                fetchStatus.setForeground(UiKit.DANGER);
                UiKit.showWarning(this, "No reservation found for PNR " + pnr + ".", "Not Found");
                return;
            }
            current = r;
            valPnr.setText(r.pnr);
            valName.setText(r.passengerName);
            valTrain.setText(r.trainNumber);
            valTrainName.setText(r.trainName);
            valClass.setText(r.classType);
            valDate.setText(r.journeyDate.format(DISPLAY_DATE));
            valFrom.setText(r.sourceStation);
            valTo.setText(r.destinationStation);
            valBookedBy.setText(r.bookedBy);
            valBookedOn.setText(r.bookingTime != null ? r.bookingTime.format(DISPLAY_TIME) : "—");
            cancelButton.setEnabled(true);
            fetchStatus.setText("✓ Booking found. Review the details, then confirm cancellation.");
            fetchStatus.setForeground(new Color(0x1E, 0x7E, 0x34));
        } catch (SQLException e) {
            UiKit.showError(this, "Could not fetch the booking:\n" + e.getMessage());
        } finally {
            fetchButton.setEnabled(true);
            fetchButton.setText("Fetch");
        }
    }

    /** Confirm cancellation with an "Are you sure?" dialog, then delete on confirmation. */
    private void confirmCancellation() {
        if (current == null) {
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel the booking with PNR " + current.pnr + "?\n"
                        + "Passenger: " + current.passengerName + "  |  Train: " + current.trainNumber
                        + " — " + current.trainName + "\n\nThis action cannot be undone.",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        cancelButton.setEnabled(false);
        try {
            boolean deleted = ReservationDAO.cancelByPnr(current.pnr);
            if (deleted) {
                UiKit.showInfo(this,
                        "Reservation with PNR " + current.pnr + " has been cancelled.\n"
                                + "The booking has been removed from the database.",
                        "Cancellation Confirmed");
                clearAll();
            } else {
                UiKit.showWarning(this,
                        "The booking for PNR " + current.pnr + " was not found in the database. "
                                + "It may have already been cancelled.",
                        "Not Found");
                clearDetails();
                current = null;
                cancelButton.setEnabled(false);
            }
        } catch (SQLException e) {
            cancelButton.setEnabled(true);
            UiKit.showError(this, "Could not cancel the reservation:\n" + e.getMessage());
        }
    }

    private void clearDetails() {
        valPnr.setText(""); valName.setText(""); valTrain.setText(""); valTrainName.setText("");
        valClass.setText(""); valDate.setText(""); valFrom.setText(""); valTo.setText("");
        valBookedBy.setText(""); valBookedOn.setText("");
    }

    private void clearAll() {
        pnrField.setText("");
        clearDetails();
        current = null;
        cancelButton.setEnabled(false);
        fetchStatus.setText(" ");
        pnrField.requestFocusInWindow();
    }
}
