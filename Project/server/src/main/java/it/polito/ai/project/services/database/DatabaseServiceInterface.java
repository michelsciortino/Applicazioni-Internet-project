package it.polito.ai.project.services.database;

import it.polito.ai.project.exceptions.InternalServerErrorException;
import it.polito.ai.project.generalmodels.*;
import it.polito.ai.project.services.database.models.DirectionType;
import it.polito.ai.project.services.database.models.Line;
import it.polito.ai.project.services.database.models.Token;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface DatabaseServiceInterface {
    //---------------------------------------------###UserCredentials###----------------------------------------------//

    ClientUserCredentials insertCredentials(String username, String password, List<String> role);

    ClientUserCredentials getCredentials(String username);

    void modifyUserPassword(ClientUserCredentials credential, String password);

    void updateCredentials(ClientUserCredentials credential);

    void deleteCredentials(ClientUserCredentials credential);

    //---------------------------------------------------###Token###--------------------------------------------------//

    void insertToken(Token token);

    void deleteToken(Token token);

    //---------------------------------------------------###User###---------------------------------------------------//

    Page<ClientUser> getUsers(int pageNumber);

    ClientUser getUser(String id);

    ClientUser getUserByUsername(String username);

    ClientUser insertUser(ClientUser user);

    void updateUser(ClientUser user);

    //----------------------------------------------###Notification###------------------------------------------------//

    Page<ClientUserNotification> getUserNotificationByUsername(int pageNumber, String username);

    //-------------------------------------------------###Parent###---------------------------------------------------//

    void reserveChildren(String performerUsername, ClientRace clientRace, List<ClientPassenger> passengers);

    //--------------------------------------------------###Admin###---------------------------------------------------//

    void makeLineAdmin(String performerUsername, String targetUsername, String line);

    void removeLineAdmin(String performerUsername, String targetUsername, String line);

    void makeCompanion(String performerUsername, String targetUsername);

    void removeCompanion( String performerUsername, ClientCompanion clientCompanion);

    void selectCompanion(String performerUsername, ClientRace clientRace, List<String> companions);

    void unselectCompanions(String performerUsername, ClientRace clientRace);

    void validCompanions(String performerUsername, ClientRace clientRace);

    //------------------------------------------------###Companion###-------------------------------------------------//

    void stateCompanionAvailability(ClientCompanion clientcompanion, String performerUsername, ClientRace clientRace);

    void removeCompanionAvailability(ClientCompanion clientcompanion, String performerUsername, ClientRace clientRace);

    void confirmChosenState(String performerUsername, ClientRace clientRace);

    void takeChildren(String performerUsername, ClientRace clientRace, List<ClientPassenger> clientPassengers, ClientPediStop takePediStop);

    void deliverChildren(String performerUsername, ClientRace clientRace, List<ClientPassenger> clientPassengers, ClientPediStop deliverPediStop);

    void absentChildren(String performerUsername, ClientRace clientRace, List<ClientPassenger> clientPassengers);


    //---------------------------------------------------###Line###---------------------------------------------------//

    void insertLine(JsonLine line) throws InternalServerErrorException;

    ClientLine updateLine(ClientLine line);

    Collection<String> getLinesNames();

    ClientLine getLinebyName(String line_name);

    Collection<ClientLine> getLines();

    ClientLine addChildToLine(String UserID, ClientChild child, String lineName, List<String> roles);

    //---------------------------------------------------###Race###---------------------------------------------------//

    ClientRace insertRace(ClientRace clientRace, String performerUsername);

    Collection<ClientRace> getRaces();

    ClientRace getRace(ClientRace clientRace);

    Collection<ClientRace> getRacesByLine(String lineName);

    Collection<ClientRace> getRacesByDateAndLine(Date date, String lineName);

    Collection<ClientRace> getRacesByLineAndDateInterval(String lineName, Date fromDate, Date toDate);

    Collection<ClientRace> getRacesByLineAndDirection(String lineName, DirectionType direction);

    Collection<ClientRace> getRacesByLineAndDateAndDirection(String lineName, Date date, DirectionType direction);

    Collection<ClientRace> getRacesByLineAndDirectionAndDateInterval(String lineName, DirectionType direction, Date fromDate, Date toDate);


    void updateRace(ClientRace clientRace, String performerUsername);

    void deleteRace(ClientRace clientRace, String performerUsername);


}
