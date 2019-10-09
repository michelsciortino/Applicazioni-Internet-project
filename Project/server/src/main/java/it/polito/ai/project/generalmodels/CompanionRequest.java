package it.polito.ai.project.generalmodels;

import it.polito.ai.project.services.database.models.CompanionState;
import it.polito.ai.project.services.database.models.DirectionType;
import it.polito.ai.project.services.database.models.RaceState;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class CompanionRequest {

    private String username;
    private String lineName;
    private DirectionType direction;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date date;
    private ClientPediStop initialStop;
    private ClientPediStop finalStop;
    private CompanionState state;

    public CompanionRequest(String username, String lineName, DirectionType direction, Date date, ClientPediStop initialStop, ClientPediStop finalStop, CompanionState state) {
        this.username = username;
        this.lineName = lineName;
        this.direction = direction;
        this.date = date;
        this.initialStop = initialStop;
        this.finalStop = finalStop;
        this.state = state;
    }

}