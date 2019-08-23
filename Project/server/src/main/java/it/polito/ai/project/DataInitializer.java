package it.polito.ai.project;

import it.polito.ai.project.exceptions.ResourceNotFoundException;
import it.polito.ai.project.generalmodels.ClientUserCredentials;
import it.polito.ai.project.services.database.DatabaseService;
import it.polito.ai.project.services.database.models.Roles;
import it.polito.ai.project.services.email.EmailSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {
    private static Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final DatabaseService db;
    private final PasswordEncoder passwordEncoder;
    private final EmailSenderService emailSenderService;

    @Autowired
    public DataInitializer(EmailSenderService emailSenderService, DatabaseService db, PasswordEncoder passwordEncoder) {
        this.emailSenderService = emailSenderService;
        this.db = db;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        try{
            ClientUserCredentials user=db.getCredentials("user@mail.com");
        }catch (ResourceNotFoundException e){
            db.insertCredentials("user@mail.com","password",Arrays.asList(Roles.prefix + Roles.USER),true);
        }
        try{
            ClientUserCredentials user1=db.getCredentials("admin@mail.com");
        }catch (ResourceNotFoundException e){
            db.insertCredentials("admin@mail.com","password",Arrays.asList(Roles.prefix + Roles.USER, Roles.prefix + Roles.ADMIN, Roles.prefix + Roles.SYSTEM_ADMIN),true);
        }

//        log.info("Spring Mail - Sending Email with JavaMailSender");

//        Mail mail = new Mail();
//        mail.setFrom("brucolini19@gmail.com");
//        mail.setTo("andrix.m.94@hotmail.it");
//        mail.setSubject("Sending Email with JavaMailSender");
//        mail.setContent("Test send a email using Spring Framework.");
//
//        emailSenderService.sendSimpleMail(mail);
    }
}

