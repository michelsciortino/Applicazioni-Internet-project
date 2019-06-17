package it.polito.ai.lab5.files.json;

import org.springframework.boot.jackson.JsonComponent;

import java.util.ArrayList;
import java.util.List;

@JsonComponent
public class UsersJson {
    public List<UserJson> users = new ArrayList<>();

    public UsersJson(List<UserJson> users) {
        this.users = users;
    }

    public UsersJson() {
    }
}
