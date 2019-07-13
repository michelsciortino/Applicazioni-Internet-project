package it.polito.ai.project.services.database;

import it.polito.ai.project.exceptions.InternalServerErrorException;
import it.polito.ai.project.generalmodels.*;
import it.polito.ai.project.services.database.models.Line;
import it.polito.ai.project.services.database.models.Token;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface DatabaseServiceInterface {
    //---------------------------------------------###UserCredentials###----------------------------------------------//

    ClientUserCredentials insertCredentials(String username, String password, List<String> role);

    ClientUserCredentials getCredentials(String id);

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
    //-------------------------------------------------###Parent###---------------------------------------------------//

    void reserveChildren(ClientRace client, List<ClientPassenger> p);
    //--------------------------------------------------###Admin###---------------------------------------------------//

    void makeLineAdmin(String performerUsername, String targetUsername, String line);

    void removeLineAdmin(String performerUsername, String targetUsername, String line);

    //------------------------------------------------###Companion###-------------------------------------------------//

    void selectCompanion(String performerUsername, ClientRace clientRace, List<String> companions);

    void unselectCompanions(String performerUsername, ClientRace clientRace);


    void confirmCompanion(String performerUsername, ClientRace clientRace, List<String> companions);

    void stateCompanionAvailability(ClientCompanion clientcompanion, String performerUsername, ClientRace clientRace);

    void removeCompanionAvailability(ClientCompanion clientcompanion, String performerUsername, ClientRace clientRace);

    void takeChildren(ClientRace client, List<ClientPassenger> p);

    void makeCompanion(String performerUsername, String targetUsername);

    void removeCompanion(ClientCompanion clientCompanion, String performerUsername);

    //---------------------------------------------------###Line###---------------------------------------------------//

    void insertLine(JsonLine line) throws InternalServerErrorException;

    void updateLine(Line line);

    Collection<String> getLinesNames();

    Collection<ClientLine> getLines();

    ClientLine addChildToLine(String UserID, ClientChild child, String lineName, List<String> roles);

    //---------------------------------------------------###Race###---------------------------------------------------//

    ClientRace insertRace(ClientRace clientRace, String performerUsername);

    Collection<ClientRace> getRaces();

    ClientRace getRace(ClientRace clientRace);

    Collection<ClientRace> getRacesByDateAndLine(Date date, String lineName);

    void updateRace(ClientRace clientRace, String performerUsername);

    void deleteRace(ClientRace clientRace);


}
