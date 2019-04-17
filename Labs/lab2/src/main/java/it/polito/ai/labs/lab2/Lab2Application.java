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

import javax.validation.constraints.Null;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    public void run(String[] args) throws Exception {
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
