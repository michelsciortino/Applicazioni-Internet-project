package it.polito.ai.lab5.files.json;

import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
public class ChildJson {
    public String name;
    public String surname;
    public String CF;

    public ChildJson() {
    }

    public ChildJson(String name, String surname, String CF) {
        this.name = name;
        this.surname = surname;
        this.CF = CF;
    }
}
