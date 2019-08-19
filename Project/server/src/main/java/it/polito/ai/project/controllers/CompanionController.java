package it.polito.ai.project.controllers;

import it.polito.ai.project.exceptions.BadRequestException;
import it.polito.ai.project.exceptions.InternalServerErrorException;
import it.polito.ai.project.exceptions.ResourceNotFoundException;
import it.polito.ai.project.exceptions.UnauthorizedRequestException;
import it.polito.ai.project.generalmodels.ClientCompanion;
import it.polito.ai.project.generalmodels.ClientRace;
import it.polito.ai.project.generalmodels.ClientUserCredentials;
import it.polito.ai.project.requestEntities.AbsentChildrenRequest;
import it.polito.ai.project.requestEntities.ReserveChildrenRequest;
import it.polito.ai.project.requestEntities.StateOrRemoveCompanionAvailabilityRequest;
import it.polito.ai.project.requestEntities.TakeorDeliverChildrenRequest;
import it.polito.ai.project.services.database.DatabaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.ResponseEntity.ok;

@CrossOrigin
@RestController
@RequestMapping("/companion")
public class CompanionController {

    private DatabaseService db;

    //ATTENZIONE! Dato che questo è il controller per il companion, solo lui è autorizzato e la gestione delle credenziali va fatta a monte

    @RequestMapping(value="/makeCompanion", method = RequestMethod.PUT)
    public ResponseEntity makeCompanion(@AuthenticationPrincipal ClientUserCredentials performerUserCredentials, @RequestBody String targetUsername)
    {
        try
        {
            db.makeCompanion(performerUserCredentials.getUsername(), targetUsername );
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
        catch(UnauthorizedRequestException ue)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value="/removeCompanion", method = RequestMethod.PUT)
    public ResponseEntity removeCompanion(@AuthenticationPrincipal ClientUserCredentials performerUserCredentials, @RequestBody ClientCompanion clientCompanion)
    {
        try
        {
            db.removeCompanion(performerUserCredentials.getUsername(), clientCompanion );
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
        catch(UnauthorizedRequestException ue)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value="/takeChildren", method = RequestMethod.PUT)
    public ResponseEntity takeChildren(@AuthenticationPrincipal ClientUserCredentials performerUserCredentials, @RequestBody TakeorDeliverChildrenRequest takeChildrenRequest)
    {
        try
        {
            db.takeChildren(performerUserCredentials.getUsername(), takeChildrenRequest.getClientRace(), takeChildrenRequest.getChildren(), takeChildrenRequest.getPedistop());
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

    @RequestMapping(value="/deliverChildren", method = RequestMethod.PUT)
    public ResponseEntity deliverChildren(@AuthenticationPrincipal ClientUserCredentials performerUserCredentials, @RequestBody TakeorDeliverChildrenRequest deliverChildrenRequest)
    {
        try
        {
            db.deliverChildren(performerUserCredentials.getUsername(), deliverChildrenRequest.getClientRace(), deliverChildrenRequest.getChildren(), deliverChildrenRequest.getPedistop());
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

    @RequestMapping(value="/absentChildren", method = RequestMethod.PUT)
    public ResponseEntity absentChildren(@AuthenticationPrincipal ClientUserCredentials performerUserCredentials, @RequestBody AbsentChildrenRequest absentChildrenRequest)
    {
        try
        {
            db.absentChildren(performerUserCredentials.getUsername(), absentChildrenRequest.getClientRace(), absentChildrenRequest.getChildren());
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

    @RequestMapping(value="/stateAvailability", method = RequestMethod.PUT)
    public ResponseEntity stateAvailability(@AuthenticationPrincipal ClientUserCredentials performerUserCredentials, @RequestBody StateOrRemoveCompanionAvailabilityRequest stateRequest)
    {
        try
        {
            db.stateCompanionAvailability(stateRequest.getTargetCompanion(), performerUserCredentials.getUsername(), stateRequest.getClientRace());
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
        catch(UnauthorizedRequestException ue)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value="/removeAvailability", method = RequestMethod.PUT)
    public ResponseEntity removeAvailability(@AuthenticationPrincipal ClientUserCredentials performerUserCredentials, @RequestBody StateOrRemoveCompanionAvailabilityRequest stateRequest)
    {
        try
        {
            db.removeCompanionAvailability(stateRequest.getTargetCompanion(), performerUserCredentials.getUsername(), stateRequest.getClientRace());
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
        catch(UnauthorizedRequestException ue)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value="/confirmChosen", method = RequestMethod.PUT)
    public ResponseEntity confirmChosen(@AuthenticationPrincipal ClientUserCredentials performerUserCredentials, @RequestBody ClientRace clientRace)
    {
        try
        {
            db.confirmChosenState(performerUserCredentials.getUsername(), clientRace);
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
