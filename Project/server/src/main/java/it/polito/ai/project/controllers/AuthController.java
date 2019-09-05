package it.polito.ai.project.controllers;

import it.polito.ai.project.exceptions.BadRequestException;
import it.polito.ai.project.exceptions.ResourceNotFoundException;
import it.polito.ai.project.generalmodels.ClientUser;
import it.polito.ai.project.responseEntities.LoginResponse;
import it.polito.ai.project.requestEntities.*;
import it.polito.ai.project.services.database.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity login(@RequestBody @Validated LoginRequest loginRequest) {
        try {
            Map<Object, Object> model = new HashMap<>();
            String mail = loginRequest.getMail();
            ClientUser user = authService.getUserByUsername(mail);
            String token = authService.loginUser(mail, loginRequest.getPassword());
//            model.put("mail", mail);
//            model.put("token", token);
//            model.put("userInfo", user);
            return ok(new LoginResponse(mail,token,user.getName(),user.getSurname(),user.getRoles()));
        } catch (ResourceNotFoundException re) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid credentials, user not found");
        } catch (BadRequestException | BadCredentialsException be) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, be.getMessage());
        } catch (Exception ie) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/confirm/{token}", method = {RequestMethod.POST})
    public ResponseEntity confirmUserAccount(@PathVariable(value="token") String confirmationToken, @RequestBody @Validated ConfirmRequest confirmRequest) {
        try {
            Map<Object, Object> model = new HashMap<>();
            String mail=authService.confirmMail(confirmationToken, confirmRequest);
            model.put("mail", mail);
            return ok(model);
        } catch (ResourceNotFoundException re) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (BadRequestException be) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, be.getMessage());
        } catch (Exception ie) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/recovery", method = {RequestMethod.POST})
    public ResponseEntity recoveryPassword(@RequestBody @Validated RecoveryRequest recoveryRequest) {
        try {
            Map<Object, Object> model = new HashMap<>();
            authService.sendRecoveryPassword(recoveryRequest.getMail());
            model.put("username", recoveryRequest.getMail());
            model.put("message", "Mail sent to indicated address.");
            return ok(model);
        } catch (ResourceNotFoundException re) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, re.getMessage());
        } catch (BadRequestException be) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } catch (Exception ie) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/recovery/reset/{token}", method = {RequestMethod.POST})
    public ResponseEntity resetPassword(@PathVariable(value="token") String resetToken, @RequestBody @Validated ResetPasswordRequest resetPasswordRequest) {
        try {
            Map<Object, Object> model = new HashMap<>();
            authService.resetPassword(resetToken, resetPasswordRequest.getNewPassword());
            model.put("result", "Password Change correctly");
            return ok(model);
        } catch (ResourceNotFoundException re) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, re.getMessage());
        } catch (BadRequestException be) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, be.getMessage());
        } catch (Exception ie) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity login(@RequestBody @Validated RegisterRequest registerRequest) {
        try {
            Map<Object, Object> model = new HashMap<>();
            authService.registerUser(registerRequest.getMail());
            model.put("username", registerRequest.getMail());
            return ok(model);
        } catch (ResourceNotFoundException re) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (BadRequestException | BadCredentialsException be) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, be.getMessage());
        } catch (Exception ie) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ie.getMessage());
        }
    }
}
