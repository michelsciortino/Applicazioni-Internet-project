package it.polito.ai.project.services.database;

import it.polito.ai.project.exceptions.InternalServerErrorException;
import it.polito.ai.project.generalmodels.ClientChild;
import it.polito.ai.project.generalmodels.ClientLine;
import it.polito.ai.project.generalmodels.ClientUserCredentials;
import it.polito.ai.project.generalmodels.JsonLine;

import java.util.Collection;
import java.util.List;

public interface DatabaseServiceInterface {
    //---------------------------------------------###UserCredentials###----------------------------------------------//

    ClientUserCredentials insertCredentials(String username, String password, List<String> role);

    ClientUserCredentials getCredentials(String id);

    void modifyUserPassword(ClientUserCredentials credential, String password);

    void updateCredentials(ClientUserCredentials credential);

    void deleteCredentials(ClientUserCredentials credential);

    //---------------------------------------------------###Line###---------------------------------------------------//

    void insertLine(JsonLine line) throws InternalServerErrorException;

    Collection<String> getLinesNames();

    Collection<ClientLine> getLines();

    ClientLine addChildToLine(String UserID, ClientChild child, String lineName, List<String> roles);

    //---------------------------------------------------###Race###---------------------------------------------------//



}
