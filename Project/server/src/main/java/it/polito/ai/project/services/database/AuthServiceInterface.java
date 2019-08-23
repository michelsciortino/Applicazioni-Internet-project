package it.polito.ai.project.services.database;

import it.polito.ai.project.requestEntities.ConfirmRequest;
import it.polito.ai.project.services.database.models.UserCredentials;

public interface AuthServiceInterface {

    UserCredentials getCredentials(String username);

    String loginUser(String mail, String password);

    String confirmMail(String confirmationToken, ConfirmRequest confirmRequest);

    void sendRecoveryPassword(String mail);

    void resetPassword(String resetToken,String newPassword);

    void registerUser(String mail);
}
