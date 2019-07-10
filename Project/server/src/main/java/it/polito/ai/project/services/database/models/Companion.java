package it.polito.ai.project.services.database.models;

import lombok.Data;

@Data
public class Companion {
    private User userDetails;
    private PediStop initialStop;
    private PediStop finalStop;
    private CompanionState state;
}
