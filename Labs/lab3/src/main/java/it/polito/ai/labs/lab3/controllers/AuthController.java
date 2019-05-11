package it.polito.ai.labs.lab3.controllers;

import it.polito.ai.labs.lab3.controllers.models.AuthenticationRequest;
import it.polito.ai.labs.lab3.controllers.models.RegistrationRequest;
import it.polito.ai.labs.lab3.security.JwtTokenProvider;
import it.polito.ai.labs.lab3.services.database.DatabaseServiceInterface;
import it.polito.ai.labs.lab3.services.database.models.User;
import it.polito.ai.labs.lab3.services.database.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.net.UnknownServiceException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    UserRepository users;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthenticationRequest data) {
        try {
            String username = data.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword()));
            String token = jwtTokenProvider.createToken(username, this.users.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username " + username + "not found")).getRoles());
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
    public ResponseEntity register(@RequestBody @Valid RegistrationRequest data) {
        try {
            String username = data.getUsername();

            Optional<User> user= this.users.findByUsername(username);
            if (user.isPresent()) {
                System.out.println("User already register");
                    return new ResponseEntity<>("Invalid username... User already exist", HttpStatus.BAD_REQUEST);
            } else {
                if(!data.getPassword().equals(data.getPasswordCheck()))
                    return new ResponseEntity<>("password is not equals", HttpStatus.BAD_REQUEST);

                System.out.println("register new user");
                database.insertUser(username,data.getPassword());

                Map<Object, Object> model = new HashMap<>();
                model.put("username", username);
                return ok(model);
            }
        } catch (UnknownServiceException e) {
            return new ResponseEntity<>("Internal error create user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}