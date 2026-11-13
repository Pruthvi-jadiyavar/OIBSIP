package com.reservation.dao;

import com.reservation.Database;
import com.reservation.model.Reservation;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservationDAO {

    private static final SecureRandom RNG = new SecureRandom();

    /** Returns a 10-digit PNR that does not yet exist in the database. */
    public static String generateUniquePnr() throws SQLException {
        for (int attempt = 0; attempt < 25; attempt++) {
            String pnr = Long.toString(1_000_000_000L + RNG.nextLong(9_000_000_000L));
            try (Connection c = Database.getConnection();
                 PreparedStatement ps = c.prepareStatement("SELECT 1 FROM reservations WHERE pnr = ?")) {
                ps.setString(1, pnr);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return pnr;
                    }
                }
            }
        }
        throw new SQLException("Could not generate a unique PNR after 25 attempts. Please try again.");
    }

    /** Inserts a reservation. The caller must supply a unique PNR. */
    public static void book(Reservation r) throws SQLException {
        String sql = "INSERT INTO reservations"
                + " (pnr, passenger_name, train_number, train_name, class_type,"
                + "  journey_date, source_station, destination_station, booked_by)"
                + " VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, r.pnr);
            ps.setString(2, r.passengerName);
            ps.setString(3, r.trainNumber);
            ps.setString(4, r.trainName);
            ps.setString(5, r.classType);
            ps.setDate(6, java.sql.Date.valueOf(r.journeyDate));
            ps.setString(7, r.sourceStation);
            ps.setString(8, r.destinationStation);
            ps.setString(9, r.bookedBy);
            ps.executeUpdate();
        }
    }

    /** Looks up a reservation by its 10-digit PNR; returns null when not found. */
    public static Reservation findByPnr(String pnr) throws SQLException {
        String sql = "SELECT pnr, passenger_name, train_number, train_name, class_type,"
                + "       journey_date, source_station, destination_station, booked_by, booking_time"
                + "  FROM reservations WHERE pnr = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, pnr.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                java.sql.Date d = rs.getDate("journey_date");
                Timestamp t = rs.getTimestamp("booking_time");
                return new Reservation(
                        rs.getString("pnr"),
                        rs.getString("passenger_name"),
                        rs.getString("train_number"),
                        rs.getString("train_name"),
                        rs.getString("class_type"),
                        d != null ? d.toLocalDate() : LocalDate.now(),
                        rs.getString("source_station"),
                        rs.getString("destination_station"),
                        rs.getString("booked_by"),
                        t != null ? t.toLocalDateTime() : null);
            }
        }
    }

    /** Deletes a reservation by PNR. Returns true when a row was actually removed. */
    public static boolean cancelByPnr(String pnr) throws SQLException {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM reservations WHERE pnr = ?")) {
            ps.setString(1, pnr.trim());
            return ps.executeUpdate() > 0;
        }
    }
}
