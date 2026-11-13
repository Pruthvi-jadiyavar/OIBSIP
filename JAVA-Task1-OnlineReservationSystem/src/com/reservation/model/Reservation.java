package com.reservation.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Immutable view of a reservation row. */
public class Reservation {
    public final String pnr;
    public final String passengerName;
    public final String trainNumber;
    public final String trainName;
    public final String classType;
    public final LocalDate journeyDate;
    public final String sourceStation;
    public final String destinationStation;
    public final String bookedBy;
    public final LocalDateTime bookingTime; // may be null when not yet read back

    public Reservation(String pnr, String passengerName, String trainNumber, String trainName,
                       String classType, LocalDate journeyDate,
                       String sourceStation, String destinationStation,
                       String bookedBy, LocalDateTime bookingTime) {
        this.pnr = pnr;
        this.passengerName = passengerName;
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.classType = classType;
        this.journeyDate = journeyDate;
        this.sourceStation = sourceStation;
        this.destinationStation = destinationStation;
        this.bookedBy = bookedBy;
        this.bookingTime = bookingTime;
    }

    /** Multi-line summary shown in confirmation / fetch dialogs. */
    public String summary() {
        StringBuilder sb = new StringBuilder();
        sb.append("PNR Number     : ").append(pnr).append('\n');
        sb.append("Passenger Name : ").append(passengerName).append('\n');
        sb.append("Train Number   : ").append(trainNumber).append('\n');
        sb.append("Train Name     : ").append(trainName).append('\n');
        sb.append("Class Type     : ").append(classType).append('\n');
        sb.append("Date of Journey: ").append(journeyDate).append('\n');
        sb.append("From           : ").append(sourceStation).append('\n');
        sb.append("To             : ").append(destinationStation).append('\n');
        sb.append("Booked By      : ").append(bookedBy).append('\n');
        if (bookingTime != null) {
            sb.append("Booked On      : ").append(bookingTime).append('\n');
        }
        return sb.toString();
    }
}
