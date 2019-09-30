package it.polito.ai.project.services.database;

import ch.qos.logback.core.net.server.Client;
import it.polito.ai.project.exceptions.InternalServerErrorException;
import it.polito.ai.project.generalmodels.*;
import it.polito.ai.project.services.database.models.DirectionType;
import it.polito.ai.project.services.database.models.RaceState;
import it.polito.ai.project.services.database.models.Token;
import it.polito.ai.project.services.database.models.UserCredentials;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface DatabaseServiceInterface {
    //---------------------------------------------###UserCredentials###----------------------------------------------//

    ClientUserCredentials insertCredentials(String username, String password, List<String> role, Boolean isEnable);

    ClientUserCredentials getCredentials(String username);

    void modifyUserPassword(ClientUserCredentials credential, String password);

    void updateCredentials(ClientUserCredentials credential);

    void deleteCredentials(ClientUserCredentials credential);

    //---------------------------------------------------###Token###--------------------------------------------------//

    void insertToken(Token token);

    void deleteToken(Token token);

    //---------------------------------------------------###User###---------------------------------------------------//

    Page<ClientUser> getUsers(int pageSize, int pageNumber, String sortBy, String filterBy, String filter);

    ClientUser getUser(String id);

    ClientUser getUserByUsername(String username);

    ClientUser insertUser(ClientUser user);

    void updateUser(ClientUser user);

    ClientUser controlledUpdateUser(ClientUser user);

    void deleteUser(ClientUser user);

    //----------------------------------------------###Notification###------------------------------------------------//

    void insertNotification(String performerUsername, ClientUserNotification clientUserNotification, String targetUsername);



    //-------------------------------------------------###Parent###---------------------------------------------------//


    Page<ClientUserNotification> getUserNotifications(int pageNumber, int pageSize, String username);

    List<Page<ClientUserNotification>> getUserBroadcastNotifications(int pageNumber, int pageSize, String username);

    void reserveChildren(String performerUsername, ClientRace clientRace, List<ClientPassenger> passengers);

    //--------------------------------------------------###Admin###---------------------------------------------------//

    void removeChildrenFromRace(String performerUsername, ClientRace clientRace, List<ClientPassenger> clientPassengers);

    List<ClientRace> getParentRacesFromDate(String performerUsername, Date date);

    void makeLineAdmin(String performerUsername, String targetUsername, String line);

    void removeLineAdmin(String performerUsername, String targetUsername, String line);

    void makeCompanion(String performerUsername, String targetUsername);

    void removeCompanion(String performerUsername, String targetUsername);

    void selectCompanions(String performerUsername, ClientRace clientRace, List<String> companions);

    void acceptCompanion(String performerUsername, ClientRace clientRace, String companion);

    void unselectCompanions(String performerUsername, ClientRace clientRace);

    void unAcceptCompanion(String performerUsername, ClientRace clientRace, String companion);

    void rejectCompanion(String performerUsername, ClientRace clientRace, String companion);

    void validCompanions(String performerUsername, ClientRace clientRace);

    void validRace(String performerUsername, ClientRace clientRace);

    //------------------------------------------------###Companion###-------------------------------------------------//

    //void stateCompanionAvailability(ClientCompanion clientcompanion, String performerUsername, ClientRace clientRace);

    //void removeCompanionAvailability(ClientCompanion clientcompanion, String performerUsername, ClientRace clientRace);

    //void confirmChosenState(String performerUsername, ClientRace clientRace);

    void giveCompanionAvailability(String performerUsername, CompanionRequest companionRequest);

    void removeCompanionAvailability(String performerUsername, CompanionRequest companionRequest);

    void confirmChosenState(String performerUsername, CompanionRequest companionRequest);

    void updateCompanionAvailability(String performerUsername, CompanionRequest companionRequest);

    void takeChildren(String performerUsername, String lineName,Date date, DirectionType direction, List<ClientPassenger> clientPassengers, String stopName);

    void deliverChildren(String performerUsername, String lineName,Date date, DirectionType direction, List<ClientPassenger> clientPassengers, String stopName);

    void absentChildren(String performerUsername, String lineName,Date date, DirectionType direction, List<ClientPassenger> clientPassengers);

    void startRace(String performerUsername,String lineName,Date date, DirectionType direction);

    void stopReached(String performerUsername, String lineName,Date date, DirectionType direction, String stopName);

    @Transactional
    void stopLeft(String performerUsername, String lineName,Date date, DirectionType direction, String stopName);

    List<ClientRace> getCompanionRacesFromDate(String performerUsername, Date date);

    void endRace(String performerUsername, ClientRace clientRace);

    //---------------------------------------------------###Line###---------------------------------------------------//

    void insertLine(JsonLine line) throws InternalServerErrorException;

    ClientLine updateLine(ClientLine line);

    Collection<String> getLinesNames();

    ClientLine getLinebyName(String line_name);

    Collection<ClientLine> getLines();

    ClientLine addChildToLine(String UserID, ClientChild child, String lineName, List<String> roles);

    //---------------------------------------------------###Race###---------------------------------------------------//

    @Transactional
    ClientLine removeChildFromLine(String UserID, ClientChild child, String lineName, List<String> roles);

    ClientRace insertRace(ClientRace clientRace, String performerUsername);

    @Transactional
    ClientRace insertRaceInitializer(ClientRace clientRace, String performerUsername);

    Collection<ClientRace> getRaces();

    ClientRace getRace(UserCredentials performer, ClientRace clientRace);

    Collection<ClientRace> getRacesByLine(UserCredentials performer, String lineName);

    Collection<ClientRace> getRacesByDateAndLine(UserCredentials performer, Date date, String lineName);

    Collection<ClientRace> getRacesByLineAndDateInterval(UserCredentials performer, String lineName, Date fromDate, Date toDate);

    Collection<ClientRace> getRacesByLineAndDirection(UserCredentials performer, String lineName, DirectionType direction);

    Collection<ClientRace> getRacesByLineAndDateAndDirection(UserCredentials performer, String lineName, Date date, DirectionType direction);

    Collection<ClientRace> getRacesByLineAndDirectionAndDateInterval(UserCredentials performer, String lineName, DirectionType direction, Date fromDate, Date toDate);


    Collection<CompanionRequest> getCompanionRequestsByCompanion(String username, RaceState state);

    Collection<CompanionRequest> getCompanionRequestsByAdmin(String username, RaceState state);


    void updateRace(ClientRace clientRace, String performerUsername);

    void deleteRace(ClientRace clientRace, String performerUsername);
}
