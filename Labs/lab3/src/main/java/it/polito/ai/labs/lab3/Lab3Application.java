package it.polito.ai.labs.lab3;

import it.polito.ai.labs.lab3.files.LinesDeserializer;
import it.polito.ai.labs.lab3.files.json.Line;
import it.polito.ai.labs.lab3.services.database.models.User;
import it.polito.ai.labs.lab3.services.database.repositories.LineRepository;
import it.polito.ai.labs.lab3.services.database.DatabaseServiceInterface;
import it.polito.ai.labs.lab3.services.database.repositories.UserRepository;
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
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class Lab3Application implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(Lab3Application.class);
    @Autowired
    private ApplicationConfig config;

    @Autowired
    LinesDeserializer deserializer;
    @Autowired
    DatabaseServiceInterface database;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    public static void main(String[] args) {
        SpringApplication.run(it.polito.ai.labs.lab3.Lab3Application.class, args);
    }

    @Autowired
    UserRepository users;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String[] args) throws Exception {


        if(!users.findByUsername("user").isPresent())
            this.users.save(new User( this.passwordEncoder.encode("password"), "user", Arrays.asList("ROLE_USER") ));
        if(!users.findByUsername("admin").isPresent())
            this.users.save(new User( this.passwordEncoder.encode("password"), "admin", Arrays.asList("ROLE_USER", "ROLE_ADMIN") ));

        for (String arg : args) {
            if (arg.startsWith("--files=")) {
                config.setFileNames(arg);
                for (String filename : config.getFileNames()) {
                    InsertLineFromFile(filename);
                }
            }
            else if (arg.startsWith("--folder=")) {
                config.setFolderName(arg);
                InsertLineFromFileInFolder(config.getFolderName());
            }

        }

    }

    public void InsertLineFromFileInFolder(String foldername) throws IOException {
        File folder = new ClassPathResource(foldername).getFile();
        for (File f : folder.listFiles()) {
            InsertLineFromFile(foldername + f.getName());
        }
    }

    public void InsertLineFromFile(String filename) throws IOException {

        File file = new ClassPathResource(filename).getFile();
        Line line;
        if (file.exists()) {
            line = deserializer.readLinesFromJsonFile(file.toPath());
            database.insertLine(line);
        } else throw new IOException("One of files doesn't exist");
    }

    @Configuration
    @EnableConfigurationProperties
    public class ApplicationConfig {
        public List<String> filenames;
        public String foldername;

        public String getFolderName() {
            return foldername;
        }

        public List<String> getFileNames() {
            return filenames;
        }

        public void setFileNames(String arg) {
            filenames = new ArrayList<>();
            String[] splitted = arg.split("=");
            if (splitted.length < 2)
                return;
            String[] fileList = splitted[1].split(",");
            for (int i = 0, size = fileList.length; i < size; i++) {
                filenames.add(fileList[i]);
            }
        }

        public void setFolderName(String arg) {
            String[] splitted = arg.split("=");
            if (splitted.length < 2)
                return;
            foldername = splitted[1];
            if (!foldername.endsWith("/"))
                foldername+="/";
            if (!foldername.startsWith("/"))
                foldername = "/" + foldername;
        }
    }
}
