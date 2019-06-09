package it.polito.ai.lab5.controllers.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest implements Serializable {
    @NotNull
    private String username;
    @NotNull
    private String password;
    @NotNull
    private String passwordCheck;
}
