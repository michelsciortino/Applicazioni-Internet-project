package it.polito.ai.project.controllers;


import it.polito.ai.project.exceptions.BadRequestException;
import it.polito.ai.project.exceptions.InternalServerErrorException;
import it.polito.ai.project.exceptions.ResourceNotFoundException;
import it.polito.ai.project.exceptions.UnauthorizedRequestException;
import it.polito.ai.project.generalmodels.ClientUserCredentials;
import it.polito.ai.project.services.database.DatabaseService;
import it.polito.ai.project.services.database.models.Roles;
import it.polito.ai.project.services.database.models.UserCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.ResponseEntity.ok;

@CrossOrigin
@RestController
@RequestMapping("/credentials")
public class CredentialsController {

    @Autowired
    private DatabaseService db;
    @RequestMapping(value="", method = RequestMethod.POST)
    public ResponseEntity postUserCredentials(@RequestBody ClientUserCredentials clientUserCredentials)
    {
        try
        {
            return ok("");//db.insertCredentials(clientUserCredentials.getUsername(), clientUserCredentials.getPassword(), clientUserCredentials.getRoles(),false));
        }
        catch(ResourceNotFoundException re)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        catch(InternalServerErrorException ie)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(BadRequestException be)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value="/{username}", method = RequestMethod.GET)
    public ResponseEntity getUserCredentials(@PathVariable(value="username") String username)
    {
        try
        {
            return ok( db.getCredentials(username));
        }
        catch(ResourceNotFoundException re)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        catch(InternalServerErrorException ie)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(BadRequestException be)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value="/{username}", method = RequestMethod.PUT)
    public ResponseEntity updateCredentials(@AuthenticationPrincipal ClientUserCredentials clientUserCredentials, @PathVariable(value="username") String username, @RequestBody ClientUserCredentials newUserCredentials)
    {
        try
        {
            if(!clientUserCredentials.getRoles().contains(Roles.ADMIN) && !!clientUserCredentials.getRoles().contains(Roles.SYSTEM_ADMIN)) {
                if (!clientUserCredentials.getUsername().equals(newUserCredentials.getUsername()))
                    throw new UnauthorizedRequestException();
            }
            db.updateCredentials(clientUserCredentials);
            return ok( HttpStatus.OK);
        }
        catch(ResourceNotFoundException re)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        catch(InternalServerErrorException ie)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(BadRequestException be)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value="/modifyPassword", method = RequestMethod.PUT)
    public ResponseEntity modifyPassword(@AuthenticationPrincipal ClientUserCredentials clientUserCredentials, @RequestBody String password)
    {
        try
        {

            db.modifyUserPassword(clientUserCredentials, password);
            return ok( HttpStatus.OK);
        }
        catch(ResourceNotFoundException re)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        catch(InternalServerErrorException ie)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(BadRequestException be)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value="/{username}", method = RequestMethod.DELETE)
    public ResponseEntity deleteCredentials(@AuthenticationPrincipal ClientUserCredentials clientUserCredentials, @PathVariable(value="username") String username)
    {
        try
        {
            if(!clientUserCredentials.getRoles().contains(Roles.ADMIN) && !clientUserCredentials.getRoles().contains(Roles.SYSTEM_ADMIN)) {
                if (!clientUserCredentials.getUsername().equals(username))
                    throw new UnauthorizedRequestException();
            }
            db.deleteCredentials(db.getCredentials(username));
            return ok( HttpStatus.OK);
        }
        catch(ResourceNotFoundException re)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        catch(InternalServerErrorException ie)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(BadRequestException be)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
