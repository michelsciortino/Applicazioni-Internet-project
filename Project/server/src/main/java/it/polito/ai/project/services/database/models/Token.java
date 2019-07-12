package it.polito.ai.project.services.database.models;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Document(collection = "token")
@Data
public class Token {

    @Id
    private String id;
    @Indexed(unique = true)
    private long tokenid;
    @Indexed(unique = true)
    private String token;

    private Date createdDate;

    private Date expirationDate;

    private ScopeToken scope;

    private String username;

    public Token(String username, ScopeToken scope) {
        this.username = username;
        createdDate = new Date();
        expirationDate = new Date(System.currentTimeMillis() + 3600 * 1000); //1h validity
        token = UUID.randomUUID().toString();
        this.scope = scope;
    }

}
