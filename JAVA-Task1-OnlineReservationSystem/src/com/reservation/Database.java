package com.reservation;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/** Handles configuration loading, connections and schema bootstrap. */
public final class Database {

    private static Properties props;

    private Database() {}

    public static synchronized Properties config() {
        if (props == null) {
            props = new Properties();
            Path path = Path.of("db.properties");
            if (Files.notExists(path)) {
                throw new IllegalStateException(
                        "db.properties was not found in " + Path.of("").toAbsolutePath()
                                + ". Create it (see README.md) and try again.");
            }
            try (InputStream in = Files.newInputStream(path)) {
                props.load(in);
            } catch (IOException e) {
                throw new IllegalStateException("Could not read db.properties: " + e.getMessage(), e);
            }
        }
        return props;
    }

    public static Connection getConnection() throws SQLException {
        Properties p = config();
        String url = p.getProperty("db.url",
                "jdbc:mysql://localhost:3306/train_reservation?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=UTF-8");
        return DriverManager.getConnection(url,
                p.getProperty("db.user", "root"),
                p.getProperty("db.password", ""));
    }

    /** Creates tables (if missing) and seeds demo users/trains. Safe to call on every startup. */
    public static void ensureSchema() throws SQLException {
        String[] ddl = {
                "CREATE TABLE IF NOT EXISTS users ("
                        + " id INT AUTO_INCREMENT PRIMARY KEY,"
                        + " username VARCHAR(50) NOT NULL UNIQUE,"
                        + " password_hash CHAR(64) NOT NULL,"
                        + " full_name VARCHAR(100) NOT NULL,"
                        + " created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                        + ") ENGINE=InnoDB",

                "CREATE TABLE IF NOT EXISTS trains ("
                        + " train_number VARCHAR(10) PRIMARY KEY,"
                        + " train_name VARCHAR(120) NOT NULL,"
                        + " from_station VARCHAR(100),"
                        + " to_station VARCHAR(100)"
                        + ") ENGINE=InnoDB",

                "CREATE TABLE IF NOT EXISTS reservations ("
                        + " id INT AUTO_INCREMENT PRIMARY KEY,"
                        + " pnr VARCHAR(10) NOT NULL UNIQUE,"
                        + " passenger_name VARCHAR(100) NOT NULL,"
                        + " train_number VARCHAR(10) NOT NULL,"
                        + " train_name VARCHAR(120) NOT NULL,"
                        + " class_type VARCHAR(40) NOT NULL,"
                        + " journey_date DATE NOT NULL,"
                        + " source_station VARCHAR(100) NOT NULL,"
                        + " destination_station VARCHAR(100) NOT NULL,"
                        + " booked_by VARCHAR(50) NOT NULL,"
                        + " booking_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                        + ") ENGINE=InnoDB",

                "INSERT IGNORE INTO users (username, password_hash, full_name) VALUES "
                        + "('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Administrator'),"
                        + "('user',  'e606e38b0d8c19b24cf0ee3808183162ea7cd63ff7912dbb22b5e803286b4446', 'Demo User')",

                "INSERT IGNORE INTO trains (train_number, train_name, from_station, to_station) VALUES "
                        + "('12301', 'Howrah Rajdhani Express',    'New Delhi', 'Howrah'),"
                        + "('12951', 'Mumbai Rajdhani Express',    'New Delhi', 'Mumbai Central'),"
                        + "('12621', 'Tamil Nadu Express',         'New Delhi', 'Chennai Central'),"
                        + "('12627', 'Karnataka Express',          'New Delhi', 'Bengaluru City'),"
                        + "('12723', 'Telangana Express',          'Hyderabad', 'New Delhi'),"
                        + "('12903', 'Golden Temple Mail',         'Mumbai CST', 'Amritsar'),"
                        + "('12009', 'Shatabdi Express',           'New Delhi', 'Bandra Terminus'),"
                        + "('12423', 'Dibrugarh Rajdhani Express', 'New Delhi', 'Dibrugarh'),"
                        + "('12133', 'Punjab Mail',                'Mumbai CST', 'New Delhi'),"
                        + "('12625', 'Kerala Express',             'New Delhi', 'Thiruvananthapuram Central')"
        };
        try (Connection c = getConnection(); Statement st = c.createStatement()) {
            for (String sql : ddl) {
                st.execute(sql);
            }
        }
    }
}
