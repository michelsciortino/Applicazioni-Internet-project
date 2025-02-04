package it.polito.ai.project.controllers;

import com.mongodb.lang.Nullable;
import it.polito.ai.project.exceptions.BadRequestException;
import it.polito.ai.project.exceptions.InternalServerErrorException;
import it.polito.ai.project.exceptions.ResourceNotFoundException;
import it.polito.ai.project.exceptions.UnauthorizedRequestException;
import it.polito.ai.project.generalmodels.ClientRace;
import it.polito.ai.project.generalmodels.CompanionRequest;
import it.polito.ai.project.requestEntities.*;
import it.polito.ai.project.services.database.DatabaseService;
import it.polito.ai.project.services.database.models.DirectionType;
import it.polito.ai.project.services.database.models.RaceState;
import it.polito.ai.project.services.database.models.UserCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

import static org.springframework.http.ResponseEntity.ok;

@CrossOrigin
@RestController
@RequestMapping("/companion")
public class CompanionController {
    @Autowired
    private DatabaseService db;

    //ATTENZIONE! Dato che questo è il controller per il companion, solo lui è autorizzato e la gestione delle credenziali va fatta a monte
    @RequestMapping(value="/races", method = RequestMethod.GET)
    public ResponseEntity getCompanionRaces(@AuthenticationPrincipal UserCredentials performerUserCredential, @Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  @RequestParam Date date)
    {
        try
        {
           return ok(db.getCompanionRacesFromDate(performerUserCredential.getUsername(), date));
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
    @RequestMapping(value = "/makeCompanion", method = RequestMethod.POST)
    public ResponseEntity makeCompanion(@AuthenticationPrincipal UserCredentials performerUserCredentials, @RequestBody MakeOrRemoveCompanionRequest companionRequest) {
        try {
            db.makeCompanion(performerUserCredentials.getUsername(), companionRequest.getTargetName());
            return ok(HttpStatus.OK);
        } catch (ResourceNotFoundException re) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, re.getMessage());
        } catch (InternalServerErrorException ie) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ie.getMessage());
        } catch (BadRequestException be) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, be.getMessage());
        } catch (UnauthorizedRequestException ue) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ue.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/removeCompanion", method = RequestMethod.POST)
    public ResponseEntity removeCompanion(@AuthenticationPrincipal UserCredentials performerUserCredentials, @RequestBody MakeOrRemoveCompanionRequest companionRequest) {
        try {
            db.removeCompanion(performerUserCredentials.getUsername(), companionRequest.getTargetName());
            return ok(HttpStatus.OK);
        } catch (ResourceNotFoundException re) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (InternalServerErrorException ie) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (BadRequestException be) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } catch (UnauthorizedRequestException ue) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/takeChildren", method = RequestMethod.POST)
    public ResponseEntity takeChildren(@AuthenticationPrincipal UserCredentials performerUserCredentials, @RequestBody TakeDeliverChildrenRequest request) {
        try {
            db.takeChildren(performerUserCredentials.getUsername(), request.getLineName(), request.getDate(),request.getDirection(), request.getChildren(), request.getStopName());
            return ok(HttpStatus.OK);
        } catch (ResourceNotFoundException re) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, re.getMessage());
        } catch (InternalServerErrorException ie) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ie.getMessage());
        } catch (BadRequestException be) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, be.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/deliverChildren", method = RequestMethod.POST)
    public ResponseEntity deliverChildren(@AuthenticationPrincipal UserCredentials performerUserCredentials, @RequestBody TakeDeliverChildrenRequest request) {
        try {
            db.deliverChildren(performerUserCredentials.getUsername(), request.getLineName(), request.getDate(),request.getDirection(), request.getChildren(), request.getStopName());
            return ok(HttpStatus.OK);
        } catch (ResourceNotFoundException re) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, re.getMessage());
        } catch (InternalServerErrorException ie) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ie.getMessage());
        } catch (BadRequestException be) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, be.getMessage());
        } catch (UnauthorizedRequestException ue) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ue.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/absentChildren", method = RequestMethod.POST)
    public ResponseEntity absentChildren(@AuthenticationPrincipal UserCredentials performerUserCredentials, @RequestBody AbsentChildrenRequest request) {
        try {
            db.absentChildren(performerUserCredentials.getUsername(), request.getLineName(), request.getDate(),request.getDirection(), request.getChildren());
            return ok(HttpStatus.OK);
        } catch (ResourceNotFoundException re) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, re.getMessage());
        } catch (InternalServerErrorException ie) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ie.getMessage());
        } catch (BadRequestException be) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, be.getMessage());
        } catch (UnauthorizedRequestException ue) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ue.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/companionRequests", method = RequestMethod.GET)
    public ResponseEntity getCompanionRequest(@AuthenticationPrincipal UserCredentials performerUserCredentials, @Nullable @RequestParam RaceState state) {
        try {
            return ok(db.getCompanionRequestsByCompanion(performerUserCredentials.getUsername(), state));
        } catch (ResourceNotFoundException re) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, re.getMessage());
        } catch (InternalServerErrorException ie) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ie.getMessage());
        } catch (BadRequestException be) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, be.getMessage());
        } catch (UnauthorizedRequestException ue) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ue.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /*@RequestMapping(value = "/stateAvailability", method = RequestMethod.POST)
    public ResponseEntity stateAvailability(@AuthenticationPrincipal UserCredentials performerUserCredentials, @RequestBody StateOrRemoveCompanionAvailabilityRequest stateRequest) {
        try {
            db.stateCompanionAvailability(stateRequest.getTargetCompanion(), performerUserCredentials.getUsername(), stateRequest.getClientRace());
            return ok(HttpStatus.OK);
        } catch (ResourceNotFoundException re) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, re.getMessage());
        } catch (InternalServerErrorException ie) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ie.getMessage());
        } catch (BadRequestException be) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, be.getMessage());
        } catch (UnauthorizedRequestException ue) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ue.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/removeAvailability", method = RequestMethod.POST)
    public ResponseEntity removeAvailability(@AuthenticationPrincipal UserCredentials performerUserCredentials, @RequestBody StateOrRemoveCompanionAvailabilityRequest stateRequest) {
        try {
            db.removeCompanionAvailability(stateRequest.getTargetCompanion(), performerUserCredentials.getUsername(), stateRequest.getClientRace());
            return ok(HttpStatus.OK);
        } catch (ResourceNotFoundException re) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, re.getMessage());
        } catch (InternalServerErrorException ie) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ie.getMessage());
        } catch (BadRequestException be) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, be.getMessage());
        } catch (UnauthorizedRequestException ue) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ue.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/confirmChosen", method = RequestMethod.POST)
    public ResponseEntity confirmChosen(@AuthenticationPrincipal UserCredentials performerUserCredentials, @RequestBody ClientRace clientRace) {
        try {
            db.confirmChosenState(performerUserCredentials.getUsername(), clientRace);
            return ok(HttpStatus.OK);
        } catch (ResourceNotFoundException re) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, re.getMessage());
        } catch (InternalServerErrorException ie) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ie.getMessage());
        } catch (BadRequestException be) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, be.getMessage());
        } catch (UnauthorizedRequestException ue) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ue.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }*/

    @RequestMapping(value = "/giveAvailability", method = RequestMethod.POST)
    public ResponseEntity stateAvailability(@AuthenticationPrincipal UserCredentials performerUserCredentials, @RequestBody CompanionRequest companionRequest) {
        try {
            db.giveCompanionAvailability(performerUserCredentials.getUsername(), companionRequest);
            return ok(HttpStatus.OK);
        } catch (ResourceNotFoundException re) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, re.getMessage());
        } catch (InternalServerErrorException ie) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ie.getMessage());
        } catch (BadRequestException be) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, be.getMessage());
        } catch (UnauthorizedRequestException ue) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ue.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/removeAvailability", method = RequestMethod.POST)
    public ResponseEntity removeAvailability(@AuthenticationPrincipal UserCredentials performerUserCredentials, @RequestBody CompanionRequest companionRequest) {
        try {
            db.removeCompanionAvailability(performerUserCredentials.getUsername(), companionRequest);
            return ok(HttpStatus.OK);
        } catch (ResourceNotFoundException re) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, re.getMessage());
        } catch (InternalServerErrorException ie) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ie.getMessage());
        } catch (BadRequestException be) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, be.getMessage());
        } catch (UnauthorizedRequestException ue) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ue.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/updateAvailability", method = RequestMethod.POST)
    public ResponseEntity updateAvailability(@AuthenticationPrincipal UserCredentials performerUserCredentials, @RequestBody CompanionRequest companionRequest) {
        try {
            db.updateCompanionAvailability(performerUserCredentials.getUsername(), companionRequest);
            return ok(HttpStatus.OK);
        } catch (ResourceNotFoundException re) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, re.getMessage());
        } catch (InternalServerErrorException ie) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ie.getMessage());
        } catch (BadRequestException be) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, be.getMessage());
        } catch (UnauthorizedRequestException ue) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ue.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/confirmAvailability", method = RequestMethod.POST)
    public ResponseEntity confirmChosen(@AuthenticationPrincipal UserCredentials performerUserCredentials, @RequestBody CompanionRequest companionRequest) {
        try {
            db.confirmChosenState(performerUserCredentials.getUsername(), companionRequest);
            return ok(HttpStatus.OK);
        } catch (ResourceNotFoundException re) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, re.getMessage());
        } catch (InternalServerErrorException ie) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ie.getMessage());
        } catch (BadRequestException be) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, be.getMessage());
        } catch (UnauthorizedRequestException ue) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ue.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/startRace/{lineName}/{date}/{direction}", method = RequestMethod.POST)
    public ResponseEntity startRace(@AuthenticationPrincipal UserCredentials performerUserCredentials,
                                    @PathVariable(value = "lineName") String lineName,
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @PathVariable(value = "date") Date date,
                                    @PathVariable(value = "direction") DirectionType direction) {
        try {
            db.startRace(performerUserCredentials.getUsername(), lineName, date, direction);
            return ok(HttpStatus.OK);
        } catch (ResourceNotFoundException re) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (InternalServerErrorException ie) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (BadRequestException be) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } catch (UnauthorizedRequestException ue) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/stopReached/{lineName}/{date}/{direction}/{stopName}", method = RequestMethod.POST)
    public ResponseEntity stopReached(@AuthenticationPrincipal UserCredentials performerUserCredentials,
                                      @PathVariable(value = "lineName") String lineName,
                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @PathVariable(value = "date") Date date,
                                      @PathVariable(value = "direction") DirectionType direction,
                                      @PathVariable(value = "stopName") String stopName) {
        try {
            db.stopReached(performerUserCredentials.getUsername(), lineName,date,direction,stopName);
            return ok(HttpStatus.OK);
        } catch (ResourceNotFoundException re) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (InternalServerErrorException ie) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (BadRequestException be) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } catch (UnauthorizedRequestException ue) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping(value = "/stopLeft/{lineName}/{date}/{direction}/{stopName}", method = RequestMethod.POST)
    public ResponseEntity stopLeft(@AuthenticationPrincipal UserCredentials performerUserCredentials,
                                   @PathVariable(value = "lineName") String lineName,
                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @PathVariable(value = "date") Date date,
                                   @PathVariable(value = "direction") DirectionType direction,
                                   @PathVariable(value = "stopName") String stopName) {
        try {
            db.stopLeft(performerUserCredentials.getUsername(),lineName,date,direction,stopName);
            return ok(HttpStatus.OK);
        } catch (ResourceNotFoundException re) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (InternalServerErrorException ie) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (BadRequestException be) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } catch (UnauthorizedRequestException ue) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/endRace/{lineName}/{date}/{direction}", method = RequestMethod.POST)
    public ResponseEntity endRace(@AuthenticationPrincipal UserCredentials performerUserCredentials,
                                  @PathVariable(value = "lineName") String lineName,
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @PathVariable(value = "date") Date date,
                                  @PathVariable(value = "direction") DirectionType direction) {
        try {
            db.endRace(performerUserCredentials.getUsername(), lineName, date,direction);
            return ok(HttpStatus.OK);
        } catch (ResourceNotFoundException re) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (InternalServerErrorException ie) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (BadRequestException be) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } catch (UnauthorizedRequestException ue) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
