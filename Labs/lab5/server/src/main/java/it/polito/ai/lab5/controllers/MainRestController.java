package it.polito.ai.lab5.controllers;

import it.polito.ai.lab5.controllers.models.LineReservations;
import it.polito.ai.lab5.controllers.models.Reservation;
import it.polito.ai.lab5.files.json.Line;
import it.polito.ai.lab5.services.database.DatabaseServiceInterface;
import it.polito.ai.lab5.services.database.models.Credential;
import it.polito.ai.lab5.services.database.models.ReservationMongo;
import it.polito.ai.lab5.services.database.models.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.http.ResponseEntity.ok;

@CrossOrigin
@RestController
public class MainRestController {
    private DatabaseServiceInterface database;

    @Autowired
    public MainRestController(DatabaseServiceInterface database) {
        this.database = database;
    }

    @RequestMapping(value = "/lines", method = RequestMethod.GET)
    public Collection<String> getLines() {
        try {
            return database.getLinesNames();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/lines/{line_name}", method = RequestMethod.GET)
    public Line getLine(@PathVariable String line_name) {
        try {
            Line line = database.getLine(line_name);
            if (line == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Line not found.");
            return line;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/reservations/{line_name}/{date}", method = RequestMethod.GET)
    public ResponseEntity getReservation(@PathVariable String line_name,
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @PathVariable LocalDate date) {
        try {
            LineReservations reservations = database.getLineReservations(line_name, date);
            if (reservations == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Line not found.");
            return ok(reservations);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/reservations/{line_name}/{date}", method = RequestMethod.POST)
    public ResponseEntity postReservation(@PathVariable String line_name,
                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @PathVariable LocalDate date,
                                          @RequestBody Reservation reservation) {
        try {
            Reservation reservationAdd = database.addReservation(null, reservation, line_name, date);
            if (reservationAdd == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Line not found.");
            return ok(reservationAdd);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/reservations/{line_name}/{date}/{reservation_id}", method = RequestMethod.PUT)
    public void updateReservation(@PathVariable String line_name,
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @PathVariable LocalDate date,
                                  @PathVariable String reservation_id,
                                  @RequestBody Reservation updatedReservation) {
        boolean result;
        try {
            result = database.updateReservation(null, updatedReservation, line_name, date, reservation_id);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (!result)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/reservations/{line_name}/{date}/{reservation_id}", method = RequestMethod.DELETE)
    public void deleteReservation(@PathVariable String line_name,
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @PathVariable LocalDate date,
                                  @PathVariable String reservation_id) {
        boolean result;
        try {
            result = database.deleteReservation(null, line_name, date, reservation_id);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (!result)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/reservations/{line_name}/{date}/{reservation_id}", method = RequestMethod.GET)
    public Reservation getReservation(@PathVariable String line_name,
                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @PathVariable LocalDate date,
                                      @PathVariable String reservation_id, @AuthenticationPrincipal Credential credential) {
        try {
            if (credential.getRoles().contains(Roles.prefix + Roles.SYSTEM_ADMIN) ||
                    credential.getRoles().contains(Roles.prefix + Roles.ADMIN)) {
                return database.getReservation(null, line_name, date, reservation_id);
            } else if (credential.getRoles().contains(Roles.prefix + Roles.USER)) {
                ReservationMongo reservation = database.getReservationMongo(null, line_name, date, reservation_id);
                if (credential.getId().equals(reservation.getUserID()))
                    return database.getReservation(null, line_name, date, reservation_id);
                else
                    return null;
            }
            return database.getReservation(null, line_name, date, reservation_id);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
