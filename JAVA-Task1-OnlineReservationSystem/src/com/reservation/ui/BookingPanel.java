package com.reservation.ui;

import com.reservation.dao.ReservationDAO;
import com.reservation.dao.TrainDAO;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/** Reservation (booking) form. */
public class BookingPanel extends JPanel {

    private static final String[] STATIONS = {
            "New Delhi", "Howrah", "Mumbai Central", "Mumbai CST", "Chennai Central",
            "Bengaluru City", "Hyderabad", "Pune", "Ahmedabad", "Nagpur",
            "Jaipur", "Lucknow", "Amritsar", "Patna", "Dibrugarh",
            "Bandra Terminus", "Thiruvananthapuram Central"
    };

    private static final String[] CLASSES = {
            "Sleeper (SL)", "AC 3 Tier (3A)", "AC 2 Tier (2A)", "AC First Class (1A)",
            "Chair Car (CC)", "Second Sitting (2S)", "General (GEN)"
    };

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

    private final String bookedBy;

    private final JTextField nameField = UiKit.textField(24);
    private final JTextField trainNoField = UiKit.textField(8);
    private final JTextField trainNameField = UiKit.textField(26);
    private final JComboBox<String> classBox = new JComboBox<>(CLASSES);
    private final JTextField dateField = UiKit.textField(12);
    private final JComboBox<String> fromBox = new JComboBox<>(STATIONS);
    private final JComboBox<String> toBox = new JComboBox<>(STATIONS);
    private final JLabel trainStatus = new JLabel("Enter a train number and press Fetch (or Tab).");
    private final JButton bookButton = UiKit.primaryButton("Book Ticket");
    private final JButton clearButton = UiKit.secondaryButton("Clear Form");
    private final JButton fetchTrainButton = UiKit.secondaryButton("Fetch");

    public BookingPanel(String bookedBy) {
        this.bookedBy = bookedBy;
        setLayout(new BorderLayout());
        setBackground(UiKit.BG);
        setBorder(new EmptyBorder(18, 22, 18, 22));

        trainNameField.setEditable(false);
        trainNameField.setBackground(new Color(0xEF, 0xF1, 0xF7));
        ((javax.swing.text.AbstractDocument) trainNoField.getDocument()).setDocumentFilter(digitsOnly(6));
        fromBox.setEditable(true);
        toBox.setEditable(true);
        fromBox.setFont(UiKit.INPUT_FONT);
        toBox.setFont(UiKit.INPUT_FONT);
        classBox.setFont(UiKit.INPUT_FONT);
        trainStatus.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        trainStatus.setForeground(UiKit.MUTED);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UiKit.CARD);
        card.setBorder(UiKit.cardBorder());

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 8, 6, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;

        // Section header spanning both columns
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
        card.add(UiKit.section("Passenger & Journey Details"), gc);
        gc.gridwidth = 1;

        int row = 1;
        addRow(card, gc, row++, "Passenger Name *", nameField);

        // Train number + fetch button
        gc.gridy = row;
        gc.gridx = 0; gc.weightx = 0;
        card.add(UiKit.label("Train Number *"), gc);
        JPanel trainRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        trainRow.setOpaque(false);
        trainRow.add(trainNoField);
        trainRow.add(fetchTrainButton);
        gc.gridx = 1; gc.weightx = 1;
        card.add(trainRow, gc);

        // Train lookup status line (under the train-number row)
        ++row;
        gc.gridx = 1; gc.weightx = 1; gc.gridy = row;
        gc.insets = new Insets(0, 8, 2, 8);
        card.add(trainStatus, gc);
        gc.insets = new Insets(6, 8, 6, 8);

        ++row;
        gc.gridx = 0; gc.weightx = 0; gc.gridy = row;
        card.add(UiKit.label("Train Name"), gc);
        gc.gridx = 1; gc.weightx = 1;
        card.add(trainNameField, gc);

        ++row;
        gc.gridx = 0; gc.weightx = 0; gc.gridy = row;
        card.add(UiKit.label("Class Type *"), gc);
        gc.gridx = 1; gc.weightx = 1;
        card.add(classBox, gc);

        ++row;
        gc.gridx = 0; gc.weightx = 0; gc.gridy = row;
        card.add(UiKit.label("Date of Journey *"), gc);
        JPanel dateRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        dateRow.setOpaque(false);
        dateField.setColumns(12);
        dateRow.add(dateField);
        JLabel dateHint = new JLabel("YYYY-MM-DD (e.g. 2026-11-20)");
        dateHint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dateHint.setForeground(UiKit.MUTED);
        dateRow.add(dateHint);
        gc.gridx = 1; gc.weightx = 1;
        card.add(dateRow, gc);

        ++row;
        gc.gridx = 0; gc.weightx = 0; gc.gridy = row;
        card.add(UiKit.label("Source Station *"), gc);
        gc.gridx = 1; gc.weightx = 1;
        card.add(fromBox, gc);

        ++row;
        gc.gridx = 0; gc.weightx = 0; gc.gridy = row;
        card.add(UiKit.label("Destination Station *"), gc);
        gc.gridx = 1; gc.weightx = 1;
        card.add(toBox, gc);

        // Buttons row
        ++row;
        gc.gridy = row; gc.gridx = 0; gc.gridwidth = 2;
        gc.insets = new Insets(16, 8, 4, 8);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        buttons.add(clearButton);
        buttons.add(bookButton);
        card.add(buttons, gc);
        gc.gridwidth = 1;
        gc.insets = new Insets(6, 8, 6, 8);

        add(card, BorderLayout.CENTER);

        fetchTrainButton.addActionListener(e -> lookupTrain());
        trainNoField.addActionListener(e -> lookupTrain());
        trainNoField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                if (!trainNoField.getText().trim().isEmpty() && trainNameField.getText().trim().isEmpty()) {
                    lookupTrain();
                }
            }
        });
        trainNoField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { invalidateTrainName(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { invalidateTrainName(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { invalidateTrainName(); }
        });
        bookButton.addActionListener(e -> bookTicket());
        clearButton.addActionListener(e -> clearForm());
    }

    private static void addRow(JPanel card, GridBagConstraints gc, int row, String label, JComponent field) {
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0;
        card.add(UiKit.label(label), gc);
        gc.gridx = 1; gc.weightx = 1;
        card.add(field, gc);
    }

    /** Numeric-only document filter for the train number field. */
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

    private void invalidateTrainName() {
        trainNameField.setText("");
        trainStatus.setText("Enter a train number and press Fetch (or Tab).");
        trainStatus.setForeground(UiKit.MUTED);
    }

    /** Auto-populate the train name from the train number. */
    private void lookupTrain() {
        String number = trainNoField.getText().trim();
        if (number.isEmpty()) {
            trainStatus.setText("Please enter a train number first.");
            trainStatus.setForeground(UiKit.DANGER);
            return;
        }
        try {
            String name = TrainDAO.findTrainName(number);
            if (name == null) {
                trainNameField.setText("");
                trainStatus.setText("No train found for number " + number + ". Check the trains table.");
                trainStatus.setForeground(UiKit.DANGER);
            } else {
                trainNameField.setText(name);
                trainStatus.setText("✓ Train found: " + name);
                trainStatus.setForeground(new Color(0x1E, 0x7E, 0x34));
            }
        } catch (SQLException e) {
            UiKit.showError(this, "Could not look up train " + number + ":\n" + e.getMessage());
        }
    }

    private boolean lookupTrainForBooking() {
        String number = trainNoField.getText().trim();
        try {
            String name = TrainDAO.findTrainName(number);
            if (name != null) {
                trainNameField.setText(name);
                return true;
            }
        } catch (SQLException e) {
            UiKit.showError(this, "Could not verify train number:\n" + e.getMessage());
            return false;
        }
        return false;
    }

    private void bookTicket() {
        String name = nameField.getText().trim();
        String trainNo = trainNoField.getText().trim();
        String classType = String.valueOf(classBox.getSelectedItem());
        String dateText = dateField.getText().trim();
        String source = String.valueOf(fromBox.getSelectedItem() == null ? "" : fromBox.getSelectedItem()).trim();
        String destination = String.valueOf(toBox.getSelectedItem() == null ? "" : toBox.getSelectedItem()).trim();

        // ---- Basic input validation ----
        if (name.isEmpty()) {
            UiKit.showWarning(this, "Passenger name is required.", "Validation"); nameField.requestFocusInWindow(); return;
        }
        if (trainNo.isEmpty()) {
            UiKit.showWarning(this, "Train number is required.", "Validation"); trainNoField.requestFocusInWindow(); return;
        }
        if (!trainNo.matches("\\d{4,6}")) {
            UiKit.showWarning(this, "Train number must be numeric (4–6 digits).", "Validation"); trainNoField.requestFocusInWindow(); return;
        }
        if (!lookupTrainForBooking()) {
            UiKit.showWarning(this, "Unknown train number " + trainNo + ". Please enter a valid train number.", "Validation");
            trainNoField.requestFocusInWindow(); return;
        }
        if (!dateText.matches("\\d{4}-\\d{2}-\\d{2}")) {
            UiKit.showWarning(this, "Journey date must be in YYYY-MM-DD format.", "Validation"); dateField.requestFocusInWindow(); return;
        }
        LocalDate journeyDate;
        try {
            journeyDate = LocalDate.parse(dateText, DATE_FMT);
        } catch (DateTimeParseException e) {
            UiKit.showWarning(this, "Journey date is not a valid calendar date.", "Validation"); dateField.requestFocusInWindow(); return;
        }
        if (journeyDate.isBefore(LocalDate.now())) {
            UiKit.showWarning(this, "Journey date cannot be in the past.", "Validation"); dateField.requestFocusInWindow(); return;
        }
        if (source.isEmpty() || destination.isEmpty()) {
            UiKit.showWarning(this, "Please select both source and destination stations.", "Validation"); return;
        }
        if (source.equalsIgnoreCase(destination)) {
            UiKit.showWarning(this, "Source and destination stations cannot be the same.", "Validation"); return;
        }

        // ---- Persist + generate PNR ----
        String trainName = trainNameField.getText().trim();
        bookButton.setEnabled(false);
        bookButton.setText("Booking…");
        try {
            String pnr = ReservationDAO.generateUniquePnr();
            Reservation r = new Reservation(pnr, name, trainNo, trainName, classType,
                    journeyDate, source, destination, bookedBy, null);
            ReservationDAO.book(r);

            // Confirmation dialog with booking details
            String message = "🎉 Booking confirmed! Your ticket has been reserved.\n\n" + r.summary();
            JOptionPane.showMessageDialog(this, message,
                    "Booking Confirmed — PNR " + pnr, JOptionPane.INFORMATION_MESSAGE);

            clearForm();
        } catch (SQLException e) {
            UiKit.showError(this, "Could not save the reservation:\n" + e.getMessage());
        } finally {
            bookButton.setEnabled(true);
            bookButton.setText("Book Ticket");
        }
    }

    private void clearForm() {
        nameField.setText("");
        trainNoField.setText("");
        trainNameField.setText("");
        classBox.setSelectedIndex(0);
        dateField.setText("");
        fromBox.setSelectedItem(STATIONS[0]);
        toBox.setSelectedItem(STATIONS[1]);
        trainStatus.setText("Enter a train number and press Fetch (or Tab).");
        trainStatus.setForeground(UiKit.MUTED);
        nameField.requestFocusInWindow();
    }
}
