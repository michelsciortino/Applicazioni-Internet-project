package it.polito.ai.project.requestEntities;

import it.polito.ai.project.generalmodels.ClientPassenger;
import it.polito.ai.project.generalmodels.ClientRace;
import it.polito.ai.project.services.database.models.DirectionType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;


@Data
public class AbsentChildrenRequest {

    private String lineName;
    private DirectionType direction;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date date;
    private List<ClientPassenger> children;

    public AbsentChildrenRequest(String lineName, DirectionType direction, Date date, List<ClientPassenger> children) {
        this.lineName = lineName;
        this.direction = direction;
        this.date = date;
        this.children = children;
    }
}
