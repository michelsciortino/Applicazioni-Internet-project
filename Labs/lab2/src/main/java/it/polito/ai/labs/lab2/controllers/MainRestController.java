package it.polito.ai.labs.lab2.controllers;

import it.polito.ai.labs.lab2.models.json.Line;
import it.polito.ai.labs.lab2.models.rest.LineReservations;
import it.polito.ai.labs.lab2.models.rest.Reservation;
import it.polito.ai.labs.lab2.services.DatabaseServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collection;

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
    public LineReservations getReservation(@PathVariable String line_name,
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @PathVariable LocalDateTime date) {
        try {
            LineReservations reservations = database.getLineReservations(line_name, date);
            if (reservations == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Line not found.");
            return reservations;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/reservations/{line_name}/{date}", method = RequestMethod.POST)
    public String postReservation(@PathVariable String line_name,
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @PathVariable LocalDateTime date,
                                  @RequestBody Reservation reservation) {
        try {
            String result = database.addReservation(null, reservation, line_name, date);
            if (result == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Line not found.");
            return result;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/reservations/{line_name}/{date}/{reservation_id}", method = RequestMethod.PUT)
    public void updateReservation(@PathVariable String line_name,
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @PathVariable LocalDateTime date,
                                  @PathVariable String reservation_id,
                                  @RequestBody Reservation updatedReservation)
     {
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
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @PathVariable LocalDateTime date,
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
                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @PathVariable LocalDateTime date,
                                      @PathVariable String reservation_id) {
        try {
            return database.getReservation(null, line_name, date, reservation_id);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
