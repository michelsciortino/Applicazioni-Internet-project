package it.polito.ai.project.generalmodels;

import it.polito.ai.project.services.database.models.CompanionState;
import it.polito.ai.project.services.database.models.User;
import lombok.Data;

@Data
public class ClientCompanion {

        private ClientUser userDetails;
        private ClientPediStop initialStop;
        private ClientPediStop finalStop;
        private CompanionState state;

}
