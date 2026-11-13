package com.reservation.dao;

import com.reservation.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TrainDAO {

    /** Returns the train name for a number, or null when the train is unknown. */
    public static String findTrainName(String trainNumber) throws SQLException {
        String sql = "SELECT train_name FROM trains WHERE train_number = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, trainNumber == null ? "" : trainNumber.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }
}
