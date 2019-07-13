package it.polito.ai.project;

import it.polito.ai.project.services.email.EmailSenderService;
import it.polito.ai.project.services.email.models.Mail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private static Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final EmailSenderService emailSenderService;

    public DataInitializer(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Spring Mail - Sending Email with JavaMailSender");

        Mail mail = new Mail();
        mail.setFrom("brucolini19@gmail.com");
        mail.setTo("andrix.m.94@hotmail.it");
        mail.setSubject("Sending Email with JavaMailSender");
        mail.setContent("Test send a email using Spring Framework.");

        emailSenderService.sendSimpleMail(mail);
    }
}

