package it.polito.ai.labs.lab1.ViewModels;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
public class LoginVM {
    @Email
    @Size(min=5, max=255)
    String mail;
    @Size(min=5, max=32)
    String pass;
}
