package it.polito.ai.labs.lab2.services;

import it.polito.ai.labs.lab2.models.mongo.Line;
import it.polito.ai.labs.lab2.models.rest.LineReservations;
import it.polito.ai.labs.lab2.models.rest.Reservation;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
public interface DatabaseServiceInterface {
    public Collection<String> getLinesNames();

    public Line getLine(String lineName);

    public LineReservations getLineReservations(String lineName, LocalDateTime dateTime);

    public String addReservation(Reservation reservation, String lineName, LocalDateTime dateTime);

    public boolean updateReservation(Reservation reservation, String lineName, LocalDateTime dateTime, String reservationId);

    public boolean deleteReservation(String lineName, LocalDateTime dateTime, String reservationId);

    public Reservation getReservation(String lineName, LocalDateTime dateTime, String reservationId);

}
