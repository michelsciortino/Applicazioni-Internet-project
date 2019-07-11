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

    public Child(@Size(min = 2, max = 30) String name, @Size(min = 2, max = 30) String surname, @Size(min = 16, max = 16) String CF, String parentId) {
        this.name = name;
        this.surname = surname;
        this.CF = CF;
        this.parentId = parentId;
    }
}
