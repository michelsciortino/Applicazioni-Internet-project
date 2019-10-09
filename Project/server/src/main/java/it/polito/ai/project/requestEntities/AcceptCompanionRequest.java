package it.polito.ai.project.requestEntities;

import it.polito.ai.project.services.database.models.DirectionType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class AcceptCompanionRequest {

    private String lineName;
    private DirectionType direction;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date date;
    private String companion;
}
