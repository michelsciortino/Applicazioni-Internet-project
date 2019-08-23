package it.polito.ai.project.controllers;

import com.mongodb.lang.Nullable;
import it.polito.ai.project.exceptions.InternalServerErrorException;
import it.polito.ai.project.exceptions.ResourceNotFoundException;
import it.polito.ai.project.exceptions.UnauthorizedRequestException;
import it.polito.ai.project.generalmodels.ClientLine;
import it.polito.ai.project.generalmodels.ClientRace;
import it.polito.ai.project.generalmodels.ClientUserCredentials;
import it.polito.ai.project.services.database.DatabaseService;
import it.polito.ai.project.services.database.models.DirectionType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;

import static org.springframework.http.ResponseEntity.ok;

@CrossOrigin
@RestController
@RequestMapping("/lines")
public class LinesController
{

    private DatabaseService db;



    @RequestMapping(value="", method = RequestMethod.GET)
    public ResponseEntity getLines()
    {
        try
        {
            return ok(db.getLines());
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value="/[line_name]", method = RequestMethod.GET)
    public ResponseEntity getLine(@PathVariable String line_name)
    {
        try
        {
            return ok(db.getLinebyName(line_name));
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value="/[line_name]/races", method = RequestMethod.GET)
    public ResponseEntity getLineRaces(@PathVariable String line_name, @Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  @RequestParam Date fromDate, @Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date toDate, @Nullable @RequestParam @Valid DirectionType direction)
    {
        try
        {
            if(fromDate == null)
            {
                if(toDate != null)
                   throw new  ResponseStatusException(HttpStatus.BAD_REQUEST);

                if(direction == null)
                   return ok(db.getRacesByLine(line_name));
                else
                   return ok(db.getRacesByLineAndDirection(line_name, direction));

            }
            else
                {
                    if(toDate == null)
                    {
                        if(direction == null)
                            return ok(db.getRacesByDateAndLine(fromDate, line_name));
                        else
                            return ok(db.getRacesByLineAndDateAndDirection(line_name, fromDate, direction));
                    }
                    else
                    {
                        if(direction == null)
                            return ok(db.getRacesByLineAndDateInterval(line_name, fromDate, toDate));
                        else
                            return ok(db.getRacesByLineAndDirectionAndDateInterval(line_name, direction, fromDate, toDate));

                    }


                }
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

    //TODO abilitare solo per l'admin o il sysadmin
    @RequestMapping(value="/[line_name]", method = RequestMethod.PUT)
    public ResponseEntity putLine(@PathVariable String line_name, @RequestBody ClientLine line)
    {
        try
        {
            return ok(db.updateLine(line));
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

    //TODO può essere chiamata da admin o sysadmin. In caso di admin viene verificato che sia l'admin della linea specificata
    @RequestMapping(value="/[line_name]/races", method = RequestMethod.POST)
    public ResponseEntity postLineRace(@PathVariable String line_name, @RequestBody ClientRace clientRace, @AuthenticationPrincipal ClientUserCredentials clientUserCredentials)
    {
        try
        {
         return ok(db.insertRace(clientRace,clientUserCredentials.getUsername()));
        }
        catch(ResourceNotFoundException re)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        catch(InternalServerErrorException ie)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(UnauthorizedRequestException ue)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value="/[line_name]/races/[date]/[direction]", method = RequestMethod.DELETE)
    public ResponseEntity deleteLineRace(@AuthenticationPrincipal ClientUserCredentials clientUserCredentials, @PathVariable String line_name,  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @PathVariable Date date, @PathVariable DirectionType direction)
    {
        try
        {
            ClientRace clientRace = new ClientRace(line_name,direction,date, new ArrayList<>(), new ArrayList<>());
            db.deleteRace(clientRace, clientUserCredentials.getUsername());
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
        catch(UnauthorizedRequestException ue)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}