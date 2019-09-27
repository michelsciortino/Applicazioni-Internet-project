package it.polito.ai.project.controllers;

import it.polito.ai.project.exceptions.BadRequestException;
import it.polito.ai.project.exceptions.InternalServerErrorException;
import it.polito.ai.project.exceptions.ResourceNotFoundException;
import it.polito.ai.project.exceptions.UnauthorizedRequestException;
import it.polito.ai.project.generalmodels.ClientUserNotification;
import it.polito.ai.project.services.database.DatabaseService;
import it.polito.ai.project.services.database.models.DirectionType;
import it.polito.ai.project.services.database.models.UserCredentials;
import it.polito.ai.project.websocket.WebSocketConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Date;

import static org.springframework.http.ResponseEntity.ok;

@Controller
public class NotificationController {

    @Autowired
    SimpMessageSendingOperations messagingTemplate;
    @Autowired
    DatabaseService db;

    @MessageMapping("/notify")
    public void notify(@AuthenticationPrincipal Authentication performer, @Payload ClientUserNotification notification) throws Exception {
        UserCredentials credentials = (UserCredentials) performer.getPrincipal();
        try{
            db.insertNotification(credentials.getUsername(),notification,notification.getTargetUsername());
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
       this.messagingTemplate.convertAndSendToUser(notification.getTargetUsername(), "/queue/notifications", notification);
    }

    @MessageMapping("/notifyAll/{line_name}/{date}/{direction}")
    @SendTo("/topic/notifications/{line_name}/{date}/{direction}")
    public ClientUserNotification notifyAll(@AuthenticationPrincipal UserCredentials performer, @DestinationVariable String line_name, @DestinationVariable Date date, @DestinationVariable DirectionType direction, @Payload ClientUserNotification notification, String targetUsername) throws Exception {
        try {
            db.insertNotification(performer.getUsername(), notification, targetUsername);
            return notification;
        } catch (InternalServerErrorException ie) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ie.getMessage());
        } catch (ResourceNotFoundException re) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, re.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

}
