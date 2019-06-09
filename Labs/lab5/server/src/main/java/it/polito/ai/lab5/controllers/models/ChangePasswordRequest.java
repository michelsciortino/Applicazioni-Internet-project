package it.polito.ai.lab5.controllers.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    @NotNull
    private String username;
    @NotNull
    private String OldPassword;
    @NotNull
    private String password;
    @NotNull
    private String passwordCheck;
}
