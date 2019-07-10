package it.polito.ai.project.services.database.models;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class Child {
    @Size(min = 2, max = 30)
    public String name;
    @Size(min = 2, max = 30)
    public String surname;
    @Size(min = 16, max = 16)
    public String CF;

    private String parentId;
}
