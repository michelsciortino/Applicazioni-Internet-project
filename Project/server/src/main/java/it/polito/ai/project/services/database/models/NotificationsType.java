package it.polito.ai.project.services.database.models;

public enum NotificationsType {
    //From Companion to 1 Parent, child has been taken
    CHILD_TAKEN,
    //From Companion to 1 Parent, child has been delivered
    CHILD_ABSENT,
    //From Companion to 1 Parent, child is absent
    CHILD_DELIVERED,
    //From Companion to 1 Admin(iterative), companion available for race
    COMPANION_STATEAVAILABILITY,
    //From Companion to 1 Admin(iterative), companion removed his availability for race
    COMPANION_REMOVEAVAILABILITY,
    //From Admin to 1 Companion, companion chosen for race
    COMPANION_CHOSEN,
    //From Companion to 1 Admin(iterative), companion confirmed chosen statement
    COMPANION_CONFIRMCHOSEN,
    //From Companion to 1 Admin(iterative), companion has not confirmed chosen statement
    COMPANION_REMOVECHOSEN,
    //From Admin to everyone related to this race, race and companions are validated
    ROUND_CONFIRM,
    //From Admin to Companion(iterative), Admin need to change Companions round due to a Companion refuse
    ROUND_DELETED,
    //From Companion to everyone related to this race, race is starting
    RACE_STARTED,
    //From Companion to everyone related to this race, race ended
    RACE_ENDED,
    //From Companion to everyone related to this race, stop x reached
    STOP_REACHED
}
