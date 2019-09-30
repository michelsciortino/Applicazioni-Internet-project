package it.polito.ai.project.requestEntities;

import it.polito.ai.project.generalmodels.ClientPassenger;
import it.polito.ai.project.generalmodels.ClientPediStop;
import it.polito.ai.project.generalmodels.ClientRace;
import it.polito.ai.project.services.database.models.DirectionType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data

public class TakeDeliverChildrenRequest {
    private String lineName;
    private DirectionType direction;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date date;
    private List<ClientPassenger> children;
    //starting or arrival stop;
    private String stopName;

    public TakeDeliverChildrenRequest(String lineName, DirectionType direction, Date date, List<ClientPassenger> children, String stopName) {
        this.lineName = lineName;
        this.direction = direction;
        this.date = date;
        this.children = children;
        this.stopName = stopName;
    }
}
