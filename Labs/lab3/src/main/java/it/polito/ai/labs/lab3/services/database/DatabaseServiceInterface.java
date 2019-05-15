package it.polito.ai.labs.lab3.services.database;


import it.polito.ai.labs.lab3.files.json.Line;
import it.polito.ai.labs.lab3.controllers.models.LineReservations;
import it.polito.ai.labs.lab3.controllers.models.Reservation;
import it.polito.ai.labs.lab3.services.database.models.Token;
import it.polito.ai.labs.lab3.services.database.models.Credential;
import it.polito.ai.labs.lab3.services.database.models.User;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;

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

    public Credential insertCredential(String Username, String Password, List<String> role) throws UnknownServiceException;

    public boolean modifyUserPassword(Credential credential, String password) throws UnknownServiceException;

    public boolean updateCredential(Credential credential) throws UnknownServiceException;

    public boolean deleteCredential(Credential credential) throws UnknownServiceException;

    public boolean insertToken(Token token) throws UnknownServiceException;

    public boolean deleteToken(Token token) throws UnknownServiceException;

    public Page<User> getUsers(int pageNumber) throws UnknownServiceException;

    public User getUser(String id) throws UnknownServiceException;

    public User getUserByUsername(String username) throws UnknownServiceException;

    public User insertUser(User user) throws UnknownServiceException;

    public boolean adminmakeAdmin(User user, UserDetails userDetails, String userID, String line) throws UnknownServiceException;

    public boolean superadminmakeAdmin(User user, String userID) throws UnknownServiceException;
}
