package it.polito.ai.labs.lab2.services;

import it.polito.ai.labs.lab2.models.json.Line;
import it.polito.ai.labs.lab2.models.rest.LineReservations;
import it.polito.ai.labs.lab2.models.rest.Reservation;

import java.net.UnknownServiceException;
import java.time.LocalDateTime;
import java.util.Collection;

public interface DatabaseServiceInterface {
    public boolean InsertLine(Line line) throws UnknownServiceException;

    public Collection<String> getLinesNames();

    public Line getLine(String lineName) throws UnknownServiceException;

    public LineReservations getLineReservations(String lineName, LocalDateTime dateTime) throws UnknownServiceException;

    public String addReservation(String UserID, Reservation reservation, String lineName, LocalDateTime dateTime) throws UnknownServiceException;

    public boolean updateReservation(String UserID, Reservation reservation, String lineName, LocalDateTime dateTime, String reservationId) throws UnknownServiceException;

    public boolean deleteReservation(String UserID, String lineName, LocalDateTime dateTime, String reservationId) throws UnknownServiceException;

    public Reservation getReservation(String UserID, String lineName, LocalDateTime dateTime, String reservationId) throws UnknownServiceException;

}
