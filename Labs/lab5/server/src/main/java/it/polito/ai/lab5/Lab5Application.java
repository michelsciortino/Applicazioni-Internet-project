package it.polito.ai.lab5;

import it.polito.ai.lab5.controllers.models.DirectionType;
import it.polito.ai.lab5.controllers.models.Reservation;
import it.polito.ai.lab5.files.LinesDeserializer;
import it.polito.ai.lab5.files.json.Line;
import it.polito.ai.lab5.files.json.PediStop;
import it.polito.ai.lab5.services.database.DatabaseServiceInterface;
import it.polito.ai.lab5.services.database.models.*;
import it.polito.ai.lab5.services.database.repositories.CredentialRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class Lab5Application {
    private static final Logger logger = LoggerFactory.getLogger(Lab5Application.class);

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    public static void main(String[] args) {
        SpringApplication.run(Lab5Application.class, args);
    }


}
