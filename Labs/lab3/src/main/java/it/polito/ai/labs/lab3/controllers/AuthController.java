package it.polito.ai.labs.lab3.controllers;

import it.polito.ai.labs.lab3.controllers.models.AuthenticationRequest;
import it.polito.ai.labs.lab3.controllers.models.ChangePasswordRequest;
import it.polito.ai.labs.lab3.controllers.models.RegistrationRequest;
import it.polito.ai.labs.lab3.security.JwtTokenProvider;
import it.polito.ai.labs.lab3.services.database.DatabaseServiceInterface;
import it.polito.ai.labs.lab3.services.database.models.Token;
import it.polito.ai.labs.lab3.services.database.models.Credential;
import it.polito.ai.labs.lab3.services.database.models.Roles;
import it.polito.ai.labs.lab3.services.database.models.ScopeToken;
import it.polito.ai.labs.lab3.services.database.repositories.TokenRepository;
import it.polito.ai.labs.lab3.services.database.repositories.CredentialRepository;
import it.polito.ai.labs.lab3.services.email.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownServiceException;
import java.util.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private DatabaseServiceInterface database;

    @Autowired
    public AuthController(DatabaseServiceInterface database) {
        this.database = database;
    }

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    CredentialRepository credentialRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthenticationRequest data) {
        try {
            String username = data.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword()));
            String token = jwtTokenProvider.createToken(username, this.credentialRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username " + username + "not found")).getRoles());
            Map<Object, Object> model = new HashMap<>();
            model.put("username", username);
            model.put("token", token);
            return ok(model);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password supplied");
        }
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String showRegisterPage(ModelMap model) {
        return "register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity register(@RequestBody @Validated RegistrationRequest data) {
        try {
            String username = data.getUsername();

            Optional<Credential> userExist = this.credentialRepository.findByUsername(username);
            if (userExist.isPresent()) {
                System.out.println("Credential already register");
                return new ResponseEntity<>("Invalid username... Credential already exist", HttpStatus.BAD_REQUEST);
            } else {
                if (!data.getPassword().equals(data.getPasswordCheck()))
                    return new ResponseEntity<>("password is not equals", HttpStatus.BAD_REQUEST);

                System.out.println("register new credential");
                Credential credential = database.insertUser(username, data.getPassword(), Arrays.asList(Roles.USER,Roles.ADMIN));

                Token token = new Token(credential);
                token.setScope(ScopeToken.CONFIRM);
                tokenRepository.save(token);

                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo(credential.getUsername());
                mailMessage.setSubject("Complete Registration!");
                mailMessage.setFrom("chand312902@gmail.com");
                mailMessage.setText("To confirm your account, please click here : "
                        + "http://localhost:8080/confirm?token=" + token.getConfirmationToken());

                //link print in console not send with email for test
                System.out.println(mailMessage);
                //emailSenderService.sendEmail(mailMessage);

                Map<Object, Object> model = new HashMap<>();
                model.put("username", username);
                return ok(model);
            }
        } catch (UnknownServiceException e) {
            return new ResponseEntity<>("Internal error create credentials", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/confirm", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity confirmUserAccount(@RequestParam("token") String confirmationToken) {
        Token token = tokenRepository.findByConfirmationToken(confirmationToken);

        try {
            if (token != null) {
                Optional<Credential> user = credentialRepository.findById(token.getCredential().getId());
                if (user.isPresent() && token.getExpirationDate().after(new Date())) {
                    user.get().setEnable(true);
                    database.updateUser(user.get());
                    Map<Object, Object> model = new HashMap<>();
                    model.put("username", user.get().getUsername());
                    model.put("verification", "accountVerified");
                    return ok(model);
                } else
                    return new ResponseEntity<>("Credential not found or token expired", HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>("The link is invalid or broken!", HttpStatus.BAD_REQUEST);
            }
        } catch (UnknownServiceException e) {
            return new ResponseEntity<>("Internal error create credentials", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/recover", method = RequestMethod.POST)
    public ResponseEntity register(@RequestParam String email) {
        try {
            Optional<Credential> userExist = this.credentialRepository.findByUsername(email);
            if (userExist.isPresent()) {

                Token changeToken = new Token(userExist.get());
                changeToken.setScope(ScopeToken.RECOVERY);
                database.insertToken(changeToken);

                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo(userExist.get().getUsername());
                mailMessage.setSubject("Change Password!");
                mailMessage.setFrom("chand312902@gmail.com");
                mailMessage.setText("To change your password, please click here : "
                        + "http://localhost:8080/recover?token=" + changeToken.getConfirmationToken());

                //link print in console not send with email for test
                System.out.println(mailMessage);
                //emailSenderService.sendEmail(mailMessage);

                Map<Object, Object> model = new HashMap<>();
                model.put("username", email);
                return ok(model);
            } else {
                return new ResponseEntity<>("Email does not exist", HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/recover/{randomUUID}", method = {RequestMethod.GET})
    public ResponseEntity getModifyPassword(@PathVariable("randomUUID") String randomUUID) {
        return new ResponseEntity<>("Return HTML per modificare password. randomUUID: " + randomUUID, HttpStatus.OK);
    }

    @RequestMapping(value = "/recover/{randomUUID}", method = {RequestMethod.POST})
    public ResponseEntity modifyPassword(@PathVariable("randomUUID") String randomUUID, @RequestBody @Validated ChangePasswordRequest data) {

        Token token = tokenRepository.findByConfirmationToken(randomUUID);

        if (token != null && token.getScope().equals(ScopeToken.RECOVERY)) {
            Optional<Credential> user = credentialRepository.findById(token.getCredential().getId());
            if (user.isPresent() && token.getExpirationDate().after(new Date())) {
                if (user.get().getUsername().equals(token.getCredential().getUsername())) {
                    //non serve controllare la password vecchia perchè si dovrebbe essere gia autenticati con il token
                    if (data.getPassword().equals(data.getPasswordCheck())) {
                        try {
                            database.modifyUserPassword(user.get(), data.getPassword());
                        } catch (UnknownServiceException e) {
                            return new ResponseEntity<>("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    } else
                        return new ResponseEntity<>("password is not equals", HttpStatus.BAD_REQUEST);
                } else {
                    return new ResponseEntity<>("Token credentials is not equal of credentials pass", HttpStatus.BAD_REQUEST);
                }

                Map<Object, Object> model = new HashMap<>();
                model.put("username", user.get().getUsername());
                model.put("result", "Password Change correctly");
                return ok(model);
            } else
                return new ResponseEntity<>("Credential not found or token expired", HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>("The link is invalid or broken!", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/users", method = {RequestMethod.GET})
    public ResponseEntity getUsers() {
        try {
            List<Credential> credentials = database.getUsers();
            return new ResponseEntity<>("credentials: "+ credentials, HttpStatus.OK);
        } catch (UnknownServiceException e) {
            return new ResponseEntity<>("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}