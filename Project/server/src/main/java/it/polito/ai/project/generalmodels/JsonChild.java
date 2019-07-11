package it.polito.ai.project.generalmodels;

import lombok.Data;
import org.springframework.boot.jackson.JsonComponent;

import javax.validation.constraints.Size;

@JsonComponent
@Data
public class JsonChild {
    @Size(min = 2, max = 30)
    public String name;
    @Size(min = 2, max = 30)
    public String surname;
    @Size(min = 16, max = 16)
    public String CF;

    private String parentId;

    public JsonChild() {
    }

    public JsonChild(@Size(min = 2, max = 30) String name, @Size(min = 2, max = 30) String surname, @Size(min = 16, max = 16) String CF, String parentId) {
        this.name = name;
        this.surname = surname;
        this.CF = CF;
        this.parentId = parentId;
    }
}

