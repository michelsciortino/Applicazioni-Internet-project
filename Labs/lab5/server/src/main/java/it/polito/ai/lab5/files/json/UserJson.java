package it.polito.ai.lab5.files.json;

import org.springframework.boot.jackson.JsonComponent;

import java.util.List;

@JsonComponent

public class UserJson {
    public String username;
    public String name;
    public String surname;
    public List<ChildJson> children;
    public List<String> lines;

    public UserJson(String username, String name, String surname, List<ChildJson> children, List<String> lines) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.children = children;
        this.lines = lines;
    }

    public UserJson() {
    }
}

