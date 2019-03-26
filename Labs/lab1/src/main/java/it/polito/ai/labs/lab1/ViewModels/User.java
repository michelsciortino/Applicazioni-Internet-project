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

    @Override
    public int hashCode(){
        return Objects.hash(name,surname,mail,pass_hash,salt);
    }
}
