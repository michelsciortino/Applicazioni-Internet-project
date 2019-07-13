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

        public ClientCompanion(ClientUser userDetails, ClientPediStop initialStop, ClientPediStop finalStop, CompanionState state) {
                this.userDetails = userDetails;
                this.initialStop = initialStop;
                this.finalStop = finalStop;
                this.state = state;
        }
}
