package it.polito.ai.project.services.database.models;

import jdk.nashorn.internal.ir.annotations.Reference;
import lombok.Data;

@Data
public class Companion {
    @Reference
    private User userDetails;

    private PediStop initialStop;
    private PediStop finalStop;
    private CompanionState state;

    public Companion(User userDetails, PediStop initialStop, PediStop finalStop, CompanionState state) {
        this.userDetails = userDetails;
        this.initialStop = initialStop;
        this.finalStop = finalStop;
        this.state = state;
    }

    public Companion() {
    }
}
