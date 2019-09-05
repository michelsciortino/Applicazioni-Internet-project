package it.polito.ai.project.services.database;

import it.polito.ai.project.DataInitializer;
import it.polito.ai.project.exceptions.BadRequestException;
import it.polito.ai.project.exceptions.InternalServerErrorException;
import it.polito.ai.project.exceptions.ResourceNotFoundException;
import it.polito.ai.project.generalmodels.ClientUser;
import it.polito.ai.project.generalmodels.ClientUserCredentials;
import it.polito.ai.project.requestEntities.ConfirmRequest;
import it.polito.ai.project.security.JwtTokenProvider;
import it.polito.ai.project.services.database.models.Roles;
import it.polito.ai.project.services.database.models.ScopeToken;
import it.polito.ai.project.services.database.models.Token;
import it.polito.ai.project.services.database.models.UserCredentials;
import it.polito.ai.project.services.database.repositories.TokenRepository;
import it.polito.ai.project.services.database.repositories.UserCredentialsRepository;
import it.polito.ai.project.services.email.EmailConfiguration;
import it.polito.ai.project.services.email.EmailSenderService;
import it.polito.ai.project.services.email.models.Mail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthService implements AuthServiceInterface {

    private static Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private DatabaseServiceInterface database;
    @Autowired
    private UserCredentialsRepository userCredentialsRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private EmailSenderService emailSenderService;

    /**
     * Function to get credential
     *
     * @param username username requested
     * @return UserCredentials: credential requested
     * @throws InternalServerErrorException
     * @throws ResourceNotFoundException
     */
    @Override
    public UserCredentials getCredentials(String username) {
        try {
            return userCredentialsRepository.findByUsername(username).get();
        } catch (NoSuchElementException e1) {
            throw new ResourceNotFoundException();
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }

    /**
     * Function to log user
     *
     * @param mail     user mail
     * @param password user password
     * @return String: jwt token
     * @throws BadRequestException
     * @throws ResourceNotFoundException
     */
    @Override
    public String loginUser(String mail, String password) {
        Optional<UserCredentials> user = userCredentialsRepository.findByUsername(mail);
        if (user.isPresent()) {
            if (!user.get().isEnable())
                throw new BadRequestException("User not enable");
            if (!user.get().isAccountNotExpired())
                throw new BadRequestException("Account is expired");
            if (!user.get().isAccountNotLocked())
                throw new BadRequestException("Account is locked");
            if (!user.get().isCredentialsNotExpired())
                throw new BadRequestException("Credentials are expired");
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(mail, password));
            return jwtTokenProvider.createToken(mail, user.get().getRoles());
        } else {
            throw new ResourceNotFoundException();
        }
    }

    /**
     * Function to confirm mail
     *
     * @param confirmationToken link token
     * @param confirmRequest    body confirm request
     * @return String: mail confirmed
     * @throws BadRequestException
     * @throws ResourceNotFoundException
     */
    @Override
    public String confirmMail(String confirmationToken, ConfirmRequest confirmRequest) {
        Token token = tokenRepository.findByToken(confirmationToken);
        if (token != null && token.getScope().equals(ScopeToken.CONFIRM)) {
            String mail = token.getUsername();
            Optional<UserCredentials> userCredentials = userCredentialsRepository.findByUsername(mail);
            if (userCredentials.isPresent()) {
                if (token.getExpirationDate().after(new Date())) {
                    userCredentials.get().setEnable(true);
                    database.modifyUserPassword(userCredentialsToClientUserCredentials(userCredentials.get()),confirmRequest.getPassword());
                    database.updateCredentials(userCredentialsToClientUserCredentials(userCredentials.get()));
                    ClientUser user = new ClientUser(mail,confirmRequest.getName(),confirmRequest.getSurname(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),new ArrayList<>());
                    database.updateUser(user);
                    database.deleteToken(token);
                    return mail;
                } else
                    throw new BadRequestException("Token expired");
            } else
                throw new ResourceNotFoundException();
        } else
            throw new BadRequestException("Token is invalid or link is broken!");
    }

    /**
     * Function to request reset password
     *
     * @param mail account mail, for reset password
     * @throws InternalServerErrorException
     * @throws ResourceNotFoundException
     */
    @Override
    public void sendRecoveryPassword(String mail) {
        Optional<UserCredentials> userCredentials = userCredentialsRepository.findByUsername(mail);
        if (userCredentials.isPresent()) {
            Token resetToken = new Token(userCredentials.get().getUsername(), ScopeToken.RECOVERY);
            database.insertToken(resetToken);
            try {
                Mail email = new Mail();
                email.setFrom("brucolini19@gmail.com");
                email.setTo(userCredentials.get().getUsername());
                email.setSubject("Reset Password!");
                email.setContent("To change your password please click here: " + EmailConfiguration.BASE_URL + "/auth/recovery/reset/" + resetToken.getToken());
                emailSenderService.sendSimpleMail(email);
                System.out.println(email);
            } catch (Exception e) {
                database.deleteToken(resetToken);
                throw new InternalServerErrorException("Send Mail Error");
            }
        } else
            throw new ResourceNotFoundException("Email does not exist");
    }

    /**
     * Function to request reset password
     *
     * @param resetToken  link token
     * @param newPassword new password for account
     * @throws BadRequestException
     * @throws ResourceNotFoundException
     * @throws InternalServerErrorException
     */
    @Override
    public void resetPassword(String resetToken, String newPassword) {
        Token token = tokenRepository.findByToken(resetToken);
        if (token != null && token.getScope().equals(ScopeToken.RECOVERY)) {
            Optional<UserCredentials> userCredentials = userCredentialsRepository.findByUsername(token.getUsername());
            if (userCredentials.isPresent()) {
                if (token.getExpirationDate().after(new Date())) {
                    try {
                        database.modifyUserPassword(userCredentialsToClientUserCredentials(userCredentials.get()), newPassword);
                    } catch (ResourceNotFoundException re) {
                        throw new ResourceNotFoundException();
                    } catch (InternalServerErrorException e) {
                        throw new InternalServerErrorException();
                    }
                } else
                    throw new BadRequestException("Token expired");
            } else
                throw new ResourceNotFoundException();
        } else
            throw new BadRequestException("Token is invalid or link is broken!");

    }

    /**
     * Function to register user
     *
     * @param mail user mail
     * @throws BadRequestException
     * @throws ResourceNotFoundException
     */
    @Override
    public void registerUser(String mail) {
        Optional<UserCredentials> userCredentials = userCredentialsRepository.findByUsername(mail);
        if (userCredentials.isPresent()) {
            log.debug("Invalid mail... Credential already exist");
            throw new BadRequestException("Invalid mail... Credential already exist");
        } else {
            log.debug("register new credential");
            ClientUserCredentials credentials = database.insertCredentials(mail, "", Arrays.asList(Roles.prefix + Roles.USER),false);
            ClientUser user = new ClientUser(mail, "", "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            database.insertUser(user);
            Token confirmToken = new Token(mail, ScopeToken.CONFIRM);
            tokenRepository.save(confirmToken);
            try {
                Mail email = new Mail();
                email.setFrom("brucolini19@gmail.com");
                email.setTo(mail);
                email.setSubject("Confirm Mail Pedibus!");
                email.setContent("To confirm and complete your account please click here : " + EmailConfiguration.BASE_URL + "/auth/confirm/" + confirmToken.getToken());
                emailSenderService.sendSimpleMail(email);
                log.debug("email sent: " + email);
            } catch (Exception e) {
                database.deleteToken(confirmToken);
                database.deleteCredentials(credentials);
                database.deleteUser(user);
                throw new InternalServerErrorException("Send Mail Error");
            }
        }
    }

    /**
     * Function to convert UserCredentials to ClientUserCredentials
     *
     * @param uc UserCredentials to convert
     * @return ClientUserCredentials converted
     */
    private ClientUserCredentials userCredentialsToClientUserCredentials(UserCredentials uc) {
        return new ClientUserCredentials(uc.getUsername(), uc.getRoles(), uc.isEnable(), uc.isCredentialsNotExpired(), uc.isAccountNotLocked(), uc.isAccountNotExpired());
    }
}
