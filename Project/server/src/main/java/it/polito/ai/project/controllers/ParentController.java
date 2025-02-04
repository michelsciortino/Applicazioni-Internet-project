package it.polito.ai.project.controllers;
import com.mongodb.lang.Nullable;
import it.polito.ai.project.exceptions.BadRequestException;
import it.polito.ai.project.exceptions.InternalServerErrorException;
import it.polito.ai.project.exceptions.ResourceNotFoundException;
import it.polito.ai.project.generalmodels.ClientUserCredentials;
import it.polito.ai.project.requestEntities.ReserveChildrenRequest;
import it.polito.ai.project.services.database.DatabaseService;
import it.polito.ai.project.services.database.models.DirectionType;
import it.polito.ai.project.services.database.models.UserCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Date;

import static org.springframework.http.ResponseEntity.ok;

@CrossOrigin
@RestController
@RequestMapping("/parent")
public class ParentController {

    @Autowired
    private DatabaseService db;
    @RequestMapping(value="/reserveChildren", method = RequestMethod.POST)
    public ResponseEntity reserveChildren(@AuthenticationPrincipal UserCredentials performerUserCredentials, @RequestBody ReserveChildrenRequest reserveChildrenRequest)
    {
        try
        {
            db.reserveChildren(performerUserCredentials.getUsername(), reserveChildrenRequest.getClientRace(), reserveChildrenRequest.getChildren());
            return ok(HttpStatus.OK);
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

    @RequestMapping(value="/removeChildrenFromRace", method = RequestMethod.POST)
    public ResponseEntity removeChildrenFromRace(@AuthenticationPrincipal UserCredentials performerUserCredentials, @RequestBody ReserveChildrenRequest reserveChildrenRequest)
    {
        try
        {
            db.removeChildrenFromRace(performerUserCredentials.getUsername(), reserveChildrenRequest.getClientRace(), reserveChildrenRequest.getChildren());
            return ok(HttpStatus.OK);
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

    @RequestMapping(value="/races", method = RequestMethod.GET)
    public ResponseEntity getParentRaces(@AuthenticationPrincipal UserCredentials performerUserCredential,
                                         @Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  @RequestParam Date fromDate,
                                         @Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  @RequestParam Date toDate,
                                         @Nullable @RequestParam boolean reserved,
                                         @Nullable @RequestParam @Valid DirectionType direction,
                                         @Nullable @RequestParam @Valid String lineName)
    {
        try
        {
            if(reserved)
                return ok(db.getParentReservedRacesBetweenDate(performerUserCredential.getUsername(), fromDate, toDate, direction, lineName));
            else
                return ok(db.getParentRacesBetweenDate(performerUserCredential.getUsername(), fromDate, toDate, direction, lineName));
        }
        catch(ResourceNotFoundException re)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        catch(InternalServerErrorException ie)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

