package it.polito.ai.labs.lab1.ViewModels;

import lombok.Builder;
import lombok.Data;

import java.util.Objects;

@Data
@Builder
public class User {
    String name;
    String surname;
    String mail;
    String pass_hash;
    String salt;
}
