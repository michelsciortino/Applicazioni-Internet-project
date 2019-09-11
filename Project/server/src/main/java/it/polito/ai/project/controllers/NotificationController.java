package it.polito.ai.project.controllers;

import it.polito.ai.project.exceptions.BadRequestException;
import it.polito.ai.project.exceptions.InternalServerErrorException;
import it.polito.ai.project.exceptions.ResourceNotFoundException;
import it.polito.ai.project.generalmodels.ClientUserNotification;
import it.polito.ai.project.services.database.DatabaseService;
import it.polito.ai.project.services.database.models.DirectionType;
import it.polito.ai.project.services.database.models.UserCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@Controller
public class NotificationController {

    @Autowired
    SimpMessageSendingOperations messagingTemplate;
    @Autowired
    DatabaseService db;

    @MessageMapping("/notify")

    public void notify(@AuthenticationPrincipal UserCredentials performer, @Payload ClientUserNotification notification, String targetUsername) throws Exception {
        try{
            db.insertNotification(performer.getUsername(),notification,targetUsername);
        }
        catch(InternalServerErrorException ie)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ie.getMessage());
        }
        catch(ResourceNotFoundException re)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, re.getMessage());
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
       this.messagingTemplate.convertAndSendToUser(targetUsername, "queue/notifications", notification);

    }

    @MessageMapping("/notifyAll/{line_name}/{date}/{direction}")
    @SendTo("/topic/notifications/{line_name}/{date}/{direction}")
    public ClientUserNotification notifyAll(@AuthenticationPrincipal UserCredentials performer, @DestinationVariable String line_name, @DestinationVariable Date date, @DestinationVariable DirectionType direction, @Payload ClientUserNotification notification, String targetUsername) throws Exception {
        try{
            db.insertNotification(performer.getUsername(),notification,targetUsername);
            return notification;
        }
        catch(InternalServerErrorException ie)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ie.getMessage());
        }
        catch(ResourceNotFoundException re)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, re.getMessage());
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

}
