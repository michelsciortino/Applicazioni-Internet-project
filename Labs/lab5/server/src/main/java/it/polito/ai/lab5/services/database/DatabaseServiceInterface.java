package it.polito.ai.lab5.services.database;


import it.polito.ai.lab5.controllers.models.LineReservations;
import it.polito.ai.lab5.controllers.models.Reservation;
import it.polito.ai.lab5.files.json.Line;
import it.polito.ai.lab5.services.database.models.*;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;

import java.net.UnknownServiceException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface DatabaseServiceInterface {
    boolean insertLine(Line line) throws UnknownServiceException;

    Collection<String> getLinesNames() throws UnknownServiceException;

    Collection<Line> getLines() throws UnknownServiceException;

    Line getLine(String lineName) throws UnknownServiceException;

    LineReservations getLineReservations(String lineName, LocalDate date) throws UnknownServiceException;

    //LineReservations getLineReservationsNames(String lineName, LocalDate date) throws UnknownServiceException;

    Line addSubscriber(String UserID, Child child, String lineName, List<String> roles) throws UnknownServiceException;

    Reservation addReservation(List<String> roles, String UserID, Reservation reservation, String lineName, LocalDate date) throws UnknownServiceException;

    Reservation updateReservation(String UserID, Reservation reservation, String lineName, LocalDate date, String reservationId) throws UnknownServiceException;

    boolean deleteReservation(String UserID, String lineName, LocalDate date, String reservationId) throws UnknownServiceException;

    Reservation getReservation(String UserID, String lineName, LocalDate date, String reservationId) throws UnknownServiceException;

    ReservationMongo getReservationMongo(String UserID, String lineName, LocalDate date, String reservationId) throws UnknownServiceException;

    Credential insertCredential(String Username, String Password, List<String> role) throws UnknownServiceException;

    Credential getCredential(String id) throws UnknownServiceException;

    boolean modifyUserPassword(Credential credential, String password) throws UnknownServiceException;

    boolean updateCredential(Credential credential) throws UnknownServiceException;

    boolean deleteCredential(Credential credential) throws UnknownServiceException;

    boolean insertToken(Token token) throws UnknownServiceException;

    boolean deleteToken(Token token) throws UnknownServiceException;

    Page<User> getUsers(int pageNumber) throws UnknownServiceException;

    User getUser(String id) throws UnknownServiceException;

    User getUserByUsername(String username) throws UnknownServiceException;

    User insertUser(User user) throws UnknownServiceException;

    boolean makeAdminFromAdmin(User user, UserDetails userDetails, String userID, String line) throws UnknownServiceException;

    boolean makeAdminFromSystemAdmin(User user, String userID) throws UnknownServiceException;

    boolean removeAdminFromAdmin(User user, UserDetails userDetails, String userID, String line) throws UnknownServiceException;

    boolean removeAdminFromSystemAdmin(User user, String userID) throws UnknownServiceException;
}
