package it.polito.ai.lab5;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.ai.lab5.controllers.models.DirectionType;
import it.polito.ai.lab5.controllers.models.Reservation;
import it.polito.ai.lab5.files.LinesDeserializer;
import it.polito.ai.lab5.files.json.*;
import it.polito.ai.lab5.services.database.DatabaseServiceInterface;
import it.polito.ai.lab5.services.database.models.*;
import it.polito.ai.lab5.services.database.repositories.CredentialRepository;
import it.polito.ai.lab5.services.database.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {
    @Autowired
    LinesDeserializer deserializer;
    @Autowired
    DatabaseServiceInterface database;
    @Autowired
    CredentialRepository credentials;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private ApplicationConfig config;

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
            } else if (arg.startsWith("--real=")) {
                String[] splitted = arg.split("=");
                if (splitted[1] != null) {
                    config.setFolderName(arg);
                    insertReal(config.getFolderName());
                }
            }
        }
    }

    private void insertReal(String foldername) throws IOException {
        File folder = new ClassPathResource(foldername).getFile();
        if (folder.exists()) {
            File usersFile = null;
            File reservationsFile = null;
            File[] linesFiles = null;
            for (File f : folder.listFiles()) {
                if (f.getName().equals("lines"))
                    linesFiles = f.listFiles();
                if (f.getName().equals("Reservations.json"))
                    reservationsFile = f;
                if (f.getName().equals("Users.json"))
                    usersFile = f;
            }
            AddCredentialsAndUsers(usersFile);
            AddLines(linesFiles);
            AddReservations(reservationsFile);
        }
    }

    private void AddReservations(File reservationsFile) throws IOException {
        // ADD reservations
        String jsonReservations = null;
        try {
            byte[] jsonBytes = Files.readAllBytes(reservationsFile.toPath());
            jsonReservations = new String(jsonBytes);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        ReservationsJson reservations = new ObjectMapper().readValue(jsonReservations, ReservationsJson.class);

        for (ReservationJson res : reservations.reservations) {
            Reservation r = new Reservation(null, res.childSurname, res.childName, res.childCf, res.username, res.stopName, res.direction, res.present);
            //1. Convert Date -> Instant
            Instant instant = res.data.toInstant();
            //2. Instant + system default time zone + toLocalDate() = LocalDate
            LocalDate localDate = instant.atZone(ZoneOffset.UTC).toLocalDate();
            if (credentials.findByUsername(r.getParentUsername()).isPresent() && database.getReservationByLineNameStopNameDateDirectionChildCf(res.lineName, localDate, res.childCf, res.direction, res.stopName) == null) {
                database.addReservation(credentials.findByUsername(r.getParentUsername()).get().getRoles(), r.getParentUsername(), r, res.lineName, localDate);
            }
        }
    }

    private void AddLines(File[] linesFiles) throws IOException {

        // ADD lines
        for (File f : linesFiles) {
            String jsonLines = null;
            try {
                byte[] jsonBytes = Files.readAllBytes(f.toPath());
                jsonLines = new String(jsonBytes);
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }
            Line line = new ObjectMapper().readValue(jsonLines, Line.class);
            Line l = new Line(line.name, line.outboundStops, line.returnStops, line.subscribedChildren);
            if (database.getLine(l.name) == null) {
                database.insertLine(l);
                for (LineSubscribedChild c : line.subscribedChildren) {
                    Child child = new Child();
                    child.setCF(c.child.CF);
                    child.setName(c.child.name);
                    child.setSurname(c.child.surname);
                    if (credentials.findByUsername(c.parentId).isPresent() && !database.getLine(l.name).subscribedChildren.contains(c))
                        database.addSubscriber(c.parentId, child, l.getName(), credentials.findByUsername(c.parentId).get().getRoles());
                }
            }

        }

    }

    private void AddCredentialsAndUsers(File usersFile) throws IOException {
        // Add Credentials and Users
        String jsonUsers = null;
        try {
            byte[] jsonBytes = Files.readAllBytes(usersFile.toPath());
            jsonUsers = new String(jsonBytes);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        UsersJson users = new ObjectMapper().readValue(jsonUsers, UsersJson.class);

        for (UserJson user : users.users) {
            if (!credentials.findByUsername(user.username).isPresent()) {
                Credential c1 = new Credential("password", user.username, Arrays.asList(Roles.prefix + Roles.USER));
                Credential result = database.insertCredential(c1.getUsername(), c1.getPassword(), c1.getRoles());
                if (database.getUserByUsername(user.username) == null) {
                    User u = new User();
                    u.setCredential(result);
                    u.setUsername(user.username);
                    u.setName(user.name);
                    u.setSurname(user.surname);
                    List<Child> children = new ArrayList<>();
                    for (ChildJson c : user.children) {
                        Child child = new Child();
                        child.setCF(c.CF);
                        child.setName(c.name);
                        child.setSurname(c.surname);
                        children.add(child);
                    }
                    u.setChildren(children);
                    u.setLines(user.lines);
                    database.insertUser(u);
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
}
