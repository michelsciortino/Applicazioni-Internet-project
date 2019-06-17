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
public class Lab5Application implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(Lab5Application.class);
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
        SpringApplication.run(Lab5Application.class, args);
    }

    @Autowired
    CredentialRepository credentials;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String[] args) throws Exception {

        if (!credentials.findByUsername("user@mail.com").isPresent())
            this.credentials.save(new Credential(this.passwordEncoder.encode("password"), "user@mail.com", Arrays.asList(Roles.prefix + Roles.USER)));
        if (!credentials.findByUsername("admin@mail.com").isPresent())
            this.credentials.save(new Credential(this.passwordEncoder.encode("password"), "admin@mail.com", Arrays.asList(Roles.prefix + Roles.USER, Roles.prefix + Roles.ADMIN, Roles.prefix + Roles.SYSTEM_ADMIN)));

        for (String arg : args) {
            if (arg.startsWith("--files=")) {
                config.setFileNames(arg);
                for (String filename : config.getFileNames()) {
                    InsertLineFromFile(filename);
                }
            } else if (arg.startsWith("--folder=")) {
                config.setFolderName(arg);
                InsertLineFromFileInFolder(config.getFolderName());
            } else if (arg.startsWith("--test=")) {
                String[] splitted = arg.split("=");
                int repetitions = Integer.parseInt(splitted[1]);

                for (int i = 0; i < repetitions; i++) {
                    Child child1 = new Child();
                    child1.setCF("cf" + new Integer(i).toString());
                    child1.setName("c" + new Integer(i).toString());
                    child1.setSurname("c" + new Integer(i).toString());

                    Child child2 = new Child();
                    child2.setCF("cf" + new Integer(i + 1).toString());
                    child2.setName("c" + new Integer(i + 1).toString());
                    child2.setSurname("c" + new Integer(i + 1).toString());


                    Child zerochild = new Child();
                    zerochild.setCF("Zerocf");
                    zerochild.setName("Zerochild");
                    zerochild.setSurname("Zerochild");

                    List<Child> children1 = new ArrayList<>();
                    children1.add(child1);
                    children1.add(child2);
                    children1.add(zerochild);

                    Credential c1 = new Credential("password", "u" + new Integer(i).toString() + "@mail.com", Arrays.asList(Roles.prefix + Roles.USER));
                    c1 = database.insertCredential(c1.getUsername(), c1.getPassword(), c1.getRoles());
                    LocalDateTime localDate = LocalDateTime.now();
                    PediStop p1 = new PediStop("ps1", (float) 1, (float) 1, localDate.format(DateTimeFormatter.ofPattern("HH:mm")));
                    PediStop p2 = new PediStop("ps2", (float) 2, (float) 2, localDate.plusMinutes(5).format(DateTimeFormatter.ofPattern("HH:mm")));
                    PediStop p3 = new PediStop("ps3", (float) 3, (float) 3, localDate.plusMinutes(10).format(DateTimeFormatter.ofPattern("HH:mm")));
                    PediStop p4 = new PediStop("ps4", (float) 4, (float) 4, localDate.plusMinutes(15).format(DateTimeFormatter.ofPattern("HH:mm")));
                    ArrayList<PediStop> outboundStops = new ArrayList<>();
                    outboundStops.add(p1);
                    outboundStops.add(p2);
                    outboundStops.add(p3);
                    outboundStops.add(p4);
                    ArrayList<PediStop> returnStops = new ArrayList<>();
                    localDate = localDate.plusHours(2);
                    PediStop p5 = new PediStop("ps4", (float) 4, (float) 4, localDate.format(DateTimeFormatter.ofPattern("HH:mm")));
                    PediStop p6 = new PediStop("ps3", (float) 3, (float) 3, localDate.plusMinutes(5).format(DateTimeFormatter.ofPattern("HH:mm")));
                    PediStop p7 = new PediStop("ps1", (float) 1, (float) 1, localDate.plusMinutes(10).format(DateTimeFormatter.ofPattern("HH:mm")));
                    returnStops.add(p5);
                    returnStops.add(p6);
                    returnStops.add(p7);
                    Line l1 = new Line("l" + new Integer(i).toString(), outboundStops, returnStops, new ArrayList<LineSubscribedChild>());
                    database.insertLine(l1);

                    List<String> linelist1 = new ArrayList<>();
                    linelist1.add(l1.getName());


                    User u1 = new User();
                    u1.setCredential(c1);
                    u1.setUsername(c1.getUsername());
                    u1.setName("User" + new Integer(i).toString());
                    u1.setSurname("User" + new Integer(i).toString());
                    u1.setChildren(children1);
                    u1.setLines(linelist1);
                    database.insertUser(u1);

                    Reservation r1 = new Reservation(null, child1.getSurname(), child1.getName(), child1.getCF(), u1.getUsername(), l1.getOutboundStops().get(2).name, DirectionType.OUTWARD, false);


                    database.addSubscriber(u1.getUsername(), child1, l1.getName(), c1.getRoles());

                    database.addSubscriber(u1.getUsername(), zerochild, l1.getName(), c1.getRoles());
                    database.addReservation(u1.getCredential().getRoles(), u1.getUsername(), r1, l1.getName(), LocalDate.now());
                }

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
                foldername += "/";
            if (!foldername.startsWith("/"))
                foldername = "/" + foldername;
        }
    }
}
