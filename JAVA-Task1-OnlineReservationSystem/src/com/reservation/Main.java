package com.reservation;

import com.reservation.dao.ReservationDAO;
import com.reservation.dao.TrainDAO;
import com.reservation.dao.UserDAO;
import com.reservation.model.Reservation;
import com.reservation.ui.LoginFrame;

import javax.swing.*;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Entry point.
 *
 * <p>Normal run: opens the login screen.
 * <p>Headless check: {@code java com.reservation.Main --smoke-test} verifies DB connectivity,
 * user authentication, a book → fetch → cancel round trip, then exits.
 */
public class Main {

    public static void main(String[] args) {
        // Use a native-ish, pleasant look and feel when available
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) { /* fall back to default */ }

        if (args.length > 0 && args[0].equals("--smoke-test")) {
            runSmokeTest();
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                Database.ensureSchema();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Could not initialise the database.\n\n" + e.getMessage()
                                + "\n\nCheck that MySQL is running and db.properties is correct.",
                        "Startup Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
                return;
            }
            new LoginFrame().setVisible(true);
        });
    }

    private static void runSmokeTest() {
        System.out.println("== RailConnect smoke test ==");
        try {
            Database.ensureSchema();
            System.out.println("[1/6] Schema ready (train_reservation).");

            String fullName = UserDAO.authenticate("admin", "admin123");
            if (fullName == null) throw new IllegalStateException("admin login failed");
            System.out.println("[2/6] admin login OK (" + fullName + ").");

            String trainName = TrainDAO.findTrainName("12301");
            if (trainName == null) throw new IllegalStateException("train 12301 not found");
            System.out.println("[3/6] Train lookup OK: 12301 → " + trainName);

            String pnr = ReservationDAO.generateUniquePnr();
            Reservation r = new Reservation(pnr, "Smoke Test", "12301", trainName,
                    "Sleeper (SL)", LocalDate.now().plusDays(7),
                    "New Delhi", "Howrah", "smoketest", null);
            ReservationDAO.book(r);
            System.out.println("[4/6] Booked test reservation. PNR = " + pnr);

            Reservation fetched = ReservationDAO.findByPnr(pnr);
            if (fetched == null || !fetched.pnr.equals(pnr)) {
                throw new IllegalStateException("fetch by PNR failed");
            }
            System.out.println("[5/6] Fetch by PNR OK: " + fetched.passengerName + " → " + fetched.trainName);

            boolean cancelled = ReservationDAO.cancelByPnr(pnr);
            if (!cancelled) throw new IllegalStateException("cancel returned false");
            if (ReservationDAO.findByPnr(pnr) != null) throw new IllegalStateException("row still present after cancel");
            System.out.println("[6/6] Cancellation OK. Test reservation removed.");

            System.out.println("SMOKE TEST PASSED");
        } catch (SQLException e) {
            System.out.println("SMOKE TEST FAILED (database): " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.out.println("SMOKE TEST FAILED: " + e);
            System.exit(1);
        }
    }
}
