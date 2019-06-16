package it.polito.ai.lab5.services.database.models;

import lombok.Data;

@Data
public class LineSubscribedChild{
    private Child child;
    private String parentId;
}
