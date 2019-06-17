package it.polito.ai.lab5.services.database.models;

import lombok.Data;

@Data
public class LineSubscribedChild {
    public Child child;
    public String parentId;
}
