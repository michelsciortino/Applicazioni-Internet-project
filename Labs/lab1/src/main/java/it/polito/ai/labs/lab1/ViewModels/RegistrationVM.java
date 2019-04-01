package it.polito.ai.labs.lab1.ViewModels;

import lombok.Data;
import org.springframework.context.annotation.Bean;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
public class RegistrationVM {
    @Size(min = 2, max = 255)
    String name, surname;
    @Email
    @Size(min = 5, max = 255)
    String mail;
    @Size(min = 5, max = 32)
    String pass1, pass2;
    boolean privacyConsentAccepted;
}
