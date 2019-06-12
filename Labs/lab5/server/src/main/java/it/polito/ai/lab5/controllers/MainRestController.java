package it.polito.ai.lab5.controllers;

import it.polito.ai.lab5.controllers.models.LineReservations;
import it.polito.ai.lab5.controllers.models.Reservation;
import it.polito.ai.lab5.files.json.Line;
import it.polito.ai.lab5.services.database.DatabaseServiceInterface;
import it.polito.ai.lab5.services.database.models.Child;
import it.polito.ai.lab5.services.database.models.Credential;
import it.polito.ai.lab5.services.database.models.ReservationMongo;
import it.polito.ai.lab5.services.database.models.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sun.rmi.runtime.Log;

import javax.validation.Valid;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;
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
    public ResponseEntity getReservations(@PathVariable String line_name,
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
                                          @RequestBody @Valid Reservation reservation, @AuthenticationPrincipal Credential credential) {
        try {
            Reservation reservationAdd = database.addReservation( credential.getRoles(), credential.getUsername(), reservation, line_name, date);
            if (reservationAdd == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Line not found.");
            return ok(reservationAdd);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/reservations/{line_name}/{date}/{reservation_id}", method = RequestMethod.PUT)
    public ResponseEntity updateReservation(@PathVariable String line_name,
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @PathVariable LocalDate date,
                                  @PathVariable String reservation_id,
                                  @RequestBody Reservation updatedReservation,  @AuthenticationPrincipal Credential credential) {
        try {
            Reservation result = database.updateReservation( credential.getUsername(), updatedReservation, line_name, date, reservation_id);
            return ok(result);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/reservations/{line_name}/{date}/{reservation_id}", method = RequestMethod.DELETE)
    public void deleteReservation(@PathVariable String line_name,
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @PathVariable LocalDate date,
                                  @PathVariable String reservation_id,  @AuthenticationPrincipal Credential credential) {
        boolean result;
        try {
            result = database.deleteReservation(credential.getUsername(), line_name, date, reservation_id);
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
            //TO-DO insert parent and roles  and move contro in db service;

            if (credential.getRoles().contains(Roles.prefix + Roles.SYSTEM_ADMIN) ||
                    credential.getRoles().contains(Roles.prefix + Roles.ADMIN)) {
                return database.getReservation(credential.getUsername(), line_name, date, reservation_id);
            } else if (credential.getRoles().contains(Roles.prefix + Roles.USER)) {
                ReservationMongo reservation = database.getReservationMongo(credential.getUsername(), line_name, date, reservation_id);
                if (credential.getId().equals(reservation.getUserID()))
                    return database.getReservation(credential.getUsername(), line_name, date, reservation_id);
                else
                    return null;
            }
            return database.getReservation(credential.getUsername(), line_name, date, reservation_id);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/lines/{line_name}/subscribers", method = RequestMethod.POST)
    public ResponseEntity postSubscriber(@PathVariable String line_name,
                                          @RequestBody Child child, @AuthenticationPrincipal Credential credential) {
        try {
            String userid = credential.getUsername();
            List<String> roles = credential.getRoles();

            Line line = database.addSubscriber(userid, child, line_name, roles);
            if (line == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Line not found.");
            return ok(line);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value = "/lines/{line_name}/subscribers", method = RequestMethod.GET)
    public ResponseEntity getSubscriber(@PathVariable String line_name) {
        try {
            Line line = database.getLine(line_name);
            if (line == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Line not found.");
            return ok(line.getSubscribedChildren());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
