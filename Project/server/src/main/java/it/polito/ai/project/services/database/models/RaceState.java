package it.polito.ai.project.services.database.models;

public enum RaceState {
    SCHEDULED,
    VALIDATED,
    STARTED,
    ENDED;

    public final static String _SCHEDULED = "SCHEDULED";
    public final static String _VALIDATED = "VALIDATED";
    public final static String _STARTED = "STARTED";
    public final static String _ENDED = "ENDED";
}
