package it.polito.ai.labs.lab2;

import it.polito.ai.labs.lab2.files.LinesDeserializer;
import it.polito.ai.labs.lab2.models.json.Line;
import it.polito.ai.labs.lab2.repositories.LineRepository;
import it.polito.ai.labs.lab2.services.DatabaseServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class Lab2Application implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(Lab2Application.class);
    @Autowired
    private ApplicationConfig config;

    @Autowired
    LinesDeserializer deserializer;
    @Autowired
    DatabaseServiceInterface database;

    @Autowired
    private LineRepository repository;

    public static void main(String[] args) {
        SpringApplication.run(Lab2Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        for (String filename : config.getFiles()) {
            InsertLineFromFile(filename);
        }
    }

    public void InsertLineFromFile(String filename) throws IOException {
        File file = new ClassPathResource("JsonFiles").getFile();
        Line line = deserializer.readLinesFromJsonFile(file.toPath());
        database.insertLine(line);
    }

    @Configuration
    @EnableConfigurationProperties
    public class ApplicationConfig {
        private String[] files;

        public String[] getFiles() {
            return files;
        }

        public void setFiles(String[] files) {
            this.files = files;
        }
    }

}
