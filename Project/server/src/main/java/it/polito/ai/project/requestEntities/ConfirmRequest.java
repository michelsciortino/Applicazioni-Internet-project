package it.polito.ai.project.requestEntities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmRequest {
    @NotNull
    private String password;
    @NotNull
    private String name;
    @NotNull
    private String surname;
}
