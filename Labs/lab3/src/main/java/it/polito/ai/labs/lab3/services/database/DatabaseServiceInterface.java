package it.polito.ai.labs.lab3.services.database;


import it.polito.ai.labs.lab3.files.json.Line;
import it.polito.ai.labs.lab3.controllers.models.LineReservations;
import it.polito.ai.labs.lab3.controllers.models.Reservation;
import it.polito.ai.labs.lab3.services.database.models.Token;
import it.polito.ai.labs.lab3.services.database.models.Credential;

import java.net.UnknownServiceException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface DatabaseServiceInterface {
    public boolean insertLine(Line line) throws UnknownServiceException;

    public Collection<String> getLinesNames() throws UnknownServiceException;

    public Line getLine(String lineName) throws UnknownServiceException;

    public LineReservations getLineReservations(String lineName, LocalDateTime dateTime) throws UnknownServiceException;

    public String addReservation(String UserID, Reservation reservation, String lineName, LocalDateTime dateTime) throws UnknownServiceException;

    public boolean updateReservation(String UserID, Reservation reservation, String lineName, LocalDateTime dateTime, String reservationId) throws UnknownServiceException;

    public boolean deleteReservation(String UserID, String lineName, LocalDateTime dateTime, String reservationId) throws UnknownServiceException;

    public Reservation getReservation(String UserID, String lineName, LocalDateTime dateTime, String reservationId) throws UnknownServiceException;

    public Credential insertUser(String Username, String Password, List<String> role) throws UnknownServiceException;

    public boolean modifyUserPassword(Credential credential, String password) throws  UnknownServiceException;

    public boolean updateUser(Credential credential) throws  UnknownServiceException;

    public boolean insertToken(Token token) throws UnknownServiceException;

    public List<Credential> getUsers() throws UnknownServiceException;
}
