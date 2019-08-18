package it.polito.ai.project.controllers;

import it.polito.ai.project.exceptions.BadRequestException;
import it.polito.ai.project.exceptions.InternalServerErrorException;
import it.polito.ai.project.exceptions.ResourceNotFoundException;
import it.polito.ai.project.generalmodels.ClientUser;
import it.polito.ai.project.generalmodels.ClientUserCredentials;
import it.polito.ai.project.requestEntities.ReserveChildrenRequest;
import it.polito.ai.project.services.database.DatabaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.ResponseEntity.ok;

@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserController {

    private DatabaseService db;
    @RequestMapping(value="", method = RequestMethod.POST)
    public ResponseEntity postUsers(@RequestBody ClientUser clientUser)
    {
        try
        {
            return ok( db.insertUser(clientUser));
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

    @RequestMapping(value="", method = RequestMethod.GET)
    public ResponseEntity getUsers(@RequestParam int pageNumber)
    {
        try
        {
           return ok( db.getUsers(pageNumber));
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
    @RequestMapping(value="[username]", method = RequestMethod.POST)
    public ResponseEntity getUser(@PathVariable String username)
    {
        try
        {
            return ok( db.getUserByUsername(username));
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
