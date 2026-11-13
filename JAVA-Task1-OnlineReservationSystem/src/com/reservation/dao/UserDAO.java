package com.reservation.dao;

import com.reservation.Database;
import com.reservation.Passwords;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    /**
     * Validates credentials against the users table.
     *
     * @return the user's full name when credentials are valid, otherwise null
     */
    public static String authenticate(String username, String password) throws SQLException {
        String sql = "SELECT password_hash, full_name FROM users WHERE username = ?";
        String hash = Passwords.sha256(password == null ? "" : password);
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username == null ? "" : username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return rs.getString(1).equalsIgnoreCase(hash) ? rs.getString(2) : null;
            }
        }
    }
}
