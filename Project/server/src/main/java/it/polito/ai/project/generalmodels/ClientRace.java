package it.polito.ai.project.generalmodels;
import it.polito.ai.project.services.database.models.DirectionType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class ClientRace {

    private String lineName;
    private DirectionType direction;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date data;

    private List<ClientPassenger> passengers;

    private List<ClientCompanion> companions;
}