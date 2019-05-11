package it.polito.ai.labs.lab3.services.database.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

@Document(collection = "confirmationToken")
@Data
public class ConfirmationToken {

    @Id
    private String id;

    private long tokenid;

    private String confirmationToken;

    private Date createdDate;

    private Date expirationDate;

    @DBRef
    private User user;

    public ConfirmationToken(User user) {
        this.user = user;
        createdDate = new Date();
        expirationDate = new Date(System.currentTimeMillis() + 3600 * 1000); //1h validity
        confirmationToken = UUID.randomUUID().toString();
    }

}