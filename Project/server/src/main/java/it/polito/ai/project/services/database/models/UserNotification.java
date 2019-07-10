package it.polito.ai.project.services.database.models;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class UserNotification {
    @Size(min = 2, max = 140)
    private String message;
    private Boolean isRead;
}
