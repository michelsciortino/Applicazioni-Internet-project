package it.polito.ai.project.generalmodels;

import it.polito.ai.project.services.database.models.Child;
import lombok.Data;

@Data
public class ClientPassenger {
        private Child childDetails;
        private boolean isReserved;
        private boolean isPresent;

        public ClientPassenger(Child childDetails, boolean isReserved, boolean isPresent) {
                this.childDetails = childDetails;
                this.isReserved = isReserved;
                this.isPresent = isPresent;
        }
}
