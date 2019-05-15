package it.polito.ai.labs.lab3.controllers;

import it.polito.ai.labs.lab3.services.database.DatabaseServiceInterface;
import it.polito.ai.labs.lab3.services.database.models.Roles;
import it.polito.ai.labs.lab3.services.database.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownServiceException;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/users")
public class UsersController {

    private DatabaseServiceInterface database;

    @Autowired
    public UsersController(DatabaseServiceInterface database) {
        this.database = database;
    }

    @RequestMapping(value = "", method = {RequestMethod.GET})
    public ResponseEntity getUsers(@RequestParam int page) {
        try {
            Page<User> credentials = database.getUsers(page);
            return new ResponseEntity<>("users: " + credentials.getContent(), HttpStatus.OK);
        } catch (UnknownServiceException e) {
            return new ResponseEntity<>("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{userID}", method = {RequestMethod.PUT})
    public ResponseEntity putUser(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("line") String line, @RequestParam("operation") String operation, @PathVariable("userID") String userID, @RequestBody @Validated User user) {
        try {
            List<String> roles = userDetails.getAuthorities()
                    .stream()
                    .map(a -> ((GrantedAuthority) a).getAuthority())
                    .collect(toList());
            if (operation.equals("admin")) {

                if (roles.contains(Roles.prefix + Roles.SYSTEM_ADMIN)) {
                    if (database.makeAdminFromSystemAdmin(user, userID))
                        return new ResponseEntity<>("User update", HttpStatus.OK);
                    else
                        return new ResponseEntity<>("Error while updating", HttpStatus.BAD_REQUEST);
                } else if (roles.contains(Roles.prefix + Roles.ADMIN)) {
                    if (database.makeAdminFromAdmin(user, userDetails, userID, line))
                        return new ResponseEntity<>("User update", HttpStatus.OK);
                    else
                        return new ResponseEntity<>("Error while updating", HttpStatus.BAD_REQUEST);
                } else
                    return new ResponseEntity<>("Not permitted for this line", HttpStatus.BAD_REQUEST);
            } else if (operation.equals("remove")) {
                if (roles.contains(Roles.prefix + Roles.SYSTEM_ADMIN)) {
                    if (database.removeAdminFromSystemAdmin(user, userID))
                        return new ResponseEntity<>("User update", HttpStatus.OK);
                    else
                        return new ResponseEntity<>("Error while updating", HttpStatus.BAD_REQUEST);
                } else if (roles.contains(Roles.prefix + Roles.ADMIN)) {
                    if (database.removeAdminFromAdmin(user, userDetails, userID, line))
                        return new ResponseEntity<>("User update", HttpStatus.OK);
                    else
                        return new ResponseEntity<>("Error while updating", HttpStatus.BAD_REQUEST);
                } else
                    return new ResponseEntity<>("Not permitted for this line", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Not permitted for this line", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
