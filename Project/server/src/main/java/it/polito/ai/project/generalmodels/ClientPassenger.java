package it.polito.ai.project.generalmodels;

import it.polito.ai.project.services.database.models.Child;
import lombok.Data;

@Data
public class ClientPassenger {
        private Child childDetails;
        private boolean isReserved;
        private boolean isPresent;

}
