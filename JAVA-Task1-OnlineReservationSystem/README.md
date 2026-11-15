# RailConnect — Online Reservation System

A GUI-based train reservation system built with **Java Swing**, **JDBC** and **MySQL**.
Users log in, book tickets (auto-generated 10-digit PNR) and cancel bookings by PNR.

## Tech stack

| Layer    | Choice                              |
|----------|-------------------------------------|
| GUI      | Java Swing (Nimbus look & feel)     |
| Database | MySQL 8 (via MySQL Connector/J)     |
| Access   | JDBC, SHA-256 password hashing      |

## Project layout

```
├── run.bat                         # build + launch (Windows)
├── database.sql                    # schema + seed data (optional; app auto-creates too)
├── db.properties                   # MySQL connection settings  (local; do not commit)
├── lib/mysql-connector-j-9.4.0.jar # JDBC driver
├── src/com/reservation/
│   ├── Main.java                   # entry point (+ --smoke-test)
│   ├── Database.java               # config, connections, schema bootstrap
│   ├── Passwords.java              # SHA-256 hashing
│   ├── model/Reservation.java      # reservation record
│   ├── dao/UserDAO.java            # login/authentication
│   ├── dao/TrainDAO.java           # train lookup (auto-populate)
│   ├── dao/ReservationDAO.java     # book / fetch by PNR / cancel by PNR
│   └── ui/                         # LoginFrame, MainFrame, BookingPanel,
│                                   # CancellationPanel, UiKit
└── README.md
```

## Prerequisites

- JDK 17+ (verified with JDK 24)
- MySQL Server 8 running on `localhost:3306` with user `root`

## Setup

1. Make sure MySQL is running (Windows service: `MySQL80`).
2. `db.properties` already contains the local connection settings:
   ```properties
   db.url=jdbc:mysql://localhost:3306/train_reservation?createDatabaseIfNotExist=true&...
   db.user=root
   db.password=yourpassword
   ```
   On first launch the app automatically creates the `train_reservation` database,
   the `users` / `trains` / `reservations` tables and seed data.
   (Equivalent manual setup: `mysql -u root -p < database.sql`)

## Run

Double-click **`run.bat`**, or:

```bat
run.bat
```

It compiles `src/` into `out/` and starts the app.

### Demo accounts

| Username | Password   |
|----------|------------|
| `admin`  | `admin123` |
| `user`   | `user123`  |

Invalid credentials are rejected with an "Access denied" dialog.

### Headless self check

```bat
java -cp "out;lib\mysql-connector-j-9.4.0.jar" com.reservation.Main --smoke-test
```

Verifies: schema creation → admin login → train lookup → book → fetch by PNR → cancel.

## Feature checklist

- [x] **Login form** — username + password; access denied for invalid credentials
- [x] **Reservation form** — passenger name, train number, train name (auto-populated
      on Fetch / Enter / Tab), class type, date of journey, source & destination stations
- [x] **Book Ticket** — saves the reservation and generates a unique 10-digit PNR
      (collision-checked against the database)
- [x] **Confirmation dialog** — shows the full booking details after a successful reservation
- [x] **Cancellation form** — PNR input + **Fetch** button retrieves and displays all booking details
- [x] **Confirm cancellation** — "Are you sure?" dialog; deletes the booking on confirmation
- [x] **Validation** — no empty required fields, `YYYY-MM-DD` date check (strict calendar +
      not in the past), numeric train number (4–6 digits), source ≠ destination, 10-digit PNR

## Notes

- `db.properties` holds the local database password. Keep it out of version control.
- Passwords for the demo accounts are stored as SHA-256 hashes in the `users` table.
- Seeded trains live in the `trains` table; insert your own rows to add more.



<img width="1907" height="911" alt="Screenshot 2026-11-15 132303" src="https://github.com/user-attachments/assets/f10d5878-d0a6-4b08-8835-eb20bf762760" />
<img width="1907" height="905" alt="Screenshot 2026-11-15 132242" src="https://github.com/user-attachments/assets/763e8e17-8fb6-4812-bc5c-ee7aeaeb043a" />
<img width="1907" height="942" alt="Screenshot 2026-11-15 132146" src="https://github.com/user-attachments/assets/bba298a8-e0ef-4d17-9929-437fd1309efa" />

