package it.polito.ai.project.generalmodels;

import org.springframework.boot.jackson.JsonComponent;

import java.util.ArrayList;
import java.util.List;

@JsonComponent
public class JsonUsers {
    public List<JsonUser> users = new ArrayList<>();

    public JsonUsers(List<JsonUser> users) {
        this.users = users;
    }

    public JsonUsers() {
    }
}
