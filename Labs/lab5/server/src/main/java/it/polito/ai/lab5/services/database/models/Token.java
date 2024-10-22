package it.polito.ai.lab5.services.database.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

@Document(collection = "token")
@Data
public class Token {

    @Id
    private String id;

    private long tokenid;

    private String confirmationToken;

    private Date createdDate;

    private Date expirationDate;

    private String Scope;

    @DBRef
    private Credential credential;

    public Token(Credential credential) {
        this.credential = credential;
        createdDate = new Date();
        expirationDate = new Date(System.currentTimeMillis() + 3600 * 1000); //1h validity
        confirmationToken = UUID.randomUUID().toString();
    }

}