package it.polito.ai.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.ai.project.exceptions.BadRequestException;
import it.polito.ai.project.exceptions.ResourceNotFoundException;
import it.polito.ai.project.generalmodels.*;
import it.polito.ai.project.services.database.DatabaseService;
import it.polito.ai.project.services.database.models.RaceState;
import it.polito.ai.project.services.database.models.Roles;
import it.polito.ai.project.services.email.EmailSenderService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public void run(String[] args) throws Exception {

        try {
            ClientUserCredentials user = db.getCredentials("user@mail.com");
        } catch (ResourceNotFoundException e) {
            db.insertCredentials("user@mail.com", "password", Arrays.asList(Roles.USER), true);
            ClientUser u = new ClientUser("user@mail.com", "user", "user", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            db.insertUser(u);
        }
        try {
            ClientUserCredentials user1 = db.getCredentials("admin@mail.com");
        } catch (ResourceNotFoundException e) {
            db.insertCredentials("admin@mail.com", "password", Arrays.asList(Roles.USER, Roles.ADMIN, Roles.SYSTEM_ADMIN), true);
            ClientUser u = new ClientUser("admin@mail.com", "admin", "admin", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            db.insertUser(u);
        }

        for (String arg : args) {
            if (arg.startsWith("--real=")) {
                String[] splitted = arg.split("=");
                if (splitted[1] != null) {
                    insertReal(splitted[1]);
                }
            }
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

    private void insertReal(String filesTable) throws IOException, JSONException {
        URL resource = this.getClass().getClassLoader().getResource(filesTable);
        if (resource != null) {
            InputStream inputStream = resource.openStream();
            if (inputStream != null) {
                byte[] bytes = StreamUtils.copyToByteArray(inputStream);
                String json = new String(bytes);
                JSONObject jsonObject = new JSONObject(json);

                AddCredentialsAndUsers(jsonObject.getString("users"));

                JSONArray lines = jsonObject.getJSONArray("lines");
                for (int i = 0; i < lines.length(); i++) {
                    AddLines(lines.getString(i));
                }

                JSONArray reservations = jsonObject.getJSONArray("reservations");
                for (int i = 0; i < reservations.length(); i++) {
                    AddRaces(reservations.getString(i));
                }


            } else {
                throw new IOException("Error in fileTable data stream");
            }
        } else {
            throw new IOException("Wrong FilesTable location");
        }
    }

    private void AddRaces(String racesFile) throws IOException {
        // ADD reservations
        URL resource = this.getClass().getClassLoader().getResource(racesFile);
        if (resource != null) {
            InputStream reservationsStream = resource.openStream();
            if (reservationsStream != null) {
                String jsonReservations;
                try {
                    byte[] jsonBytes = StreamUtils.copyToByteArray(reservationsStream);
                    jsonReservations = new String(jsonBytes);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw e;
                }
                JsonRaces races = new ObjectMapper().readValue(jsonReservations, JsonRaces.class);

                for (JsonRace race : races.races) {
                    if (db.getRacesByLineAndDateAndDirection(race.getLineName(), race.getDate(), race.getDirection()).isEmpty()) {
                        ClientRace clientRace = new ClientRace();
                        clientRace.setLineName(race.getLineName());
                        clientRace.setDirection(race.getDirection());
                        clientRace.setDate(race.getDate());
                        clientRace.setRaceState(RaceState.NULL);
                        for (JsonCompanion companion : race.getCompanions()) {
                            ClientPediStop initialStop = new ClientPediStop(companion.getInitialStop().getName(), companion.getInitialStop().getLongitude(), companion.getInitialStop().getLatitude(), companion.getInitialStop().getDelayInMillis());
                            ClientPediStop finalStop = new ClientPediStop(companion.getFinalStop().getName(), companion.getFinalStop().getLongitude(), companion.getFinalStop().getLatitude(), companion.getFinalStop().getDelayInMillis());
                            List<ClientChild> children = new ArrayList<>();
                            if (companion.getUserDetails().getChildren() != null) {
                                for (JsonChild c : companion.getUserDetails().getChildren()) {
                                    ClientChild child = new ClientChild(c.getName(), c.getSurname(), c.getCF(), c.getParentId());
                                    children.add(child);
                                }
                            }
                            ClientUser clientUser = new ClientUser(companion.getUserDetails().getUsername(), companion.getUserDetails().getName(), companion.getUserDetails().getSurname(), companion.getUserDetails().getContacts(), children, companion.getUserDetails().getLines(), new ArrayList<>());
                            ClientCompanion clientCompanion = new ClientCompanion(clientUser, initialStop, finalStop, companion.getState());
                            clientRace.getCompanions().add(clientCompanion);
                        }
                        for (JsonPassenger passenger : race.getPassengers()) {
                            ClientPassenger clientPassenger = new ClientPassenger();
                            clientPassenger.setChildDetails(new ClientChild(passenger.getChildDetails().getName(), passenger.getChildDetails().getSurname(), passenger.getChildDetails().getCF(), passenger.getChildDetails().getParentId()));
                            clientPassenger.setReserved(passenger.isReserved());
                            clientPassenger.setState(passenger.getState());
                            ClientPediStop stopReserved = new ClientPediStop(passenger.getStopReserved().getName(), passenger.getStopReserved().getLongitude(), passenger.getStopReserved().getLatitude(), passenger.getStopReserved().getDelayInMillis());
                            clientPassenger.setStopReserved(stopReserved);
                            clientPassenger.setStopTaken(new ClientPediStop());
                            clientPassenger.setStopDelivered(new ClientPediStop());
                            clientRace.getPassengers().add(clientPassenger);
                        }
                        db.insertRace(clientRace, db.getLinebyName(race.getLineName()).getAdmins().get(0));
                    }
                }
            } else {
                throw new IOException("Error in reservations data stream");
            }
        } else {
            throw new IOException("Wrong reservations' file allocation.");
        }
    }

    private void AddLines(String fileName) throws IOException {
        //Add lines
        URL resource = this.getClass().getClassLoader().getResource(fileName);
        if (resource != null) {
            InputStream inputStream = resource.openStream();
            if (inputStream != null) {
                String jsonLines;
                try {
                    byte[] jsonBytes = StreamUtils.copyToByteArray(inputStream);
                    jsonLines = new String(jsonBytes);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw e;
                }
                JsonLine line = new ObjectMapper().readValue(jsonLines, JsonLine.class);

                try {
                    db.insertLine(line);
                    for (JsonChild c : line.getSubscribedChildren()) {
                        ClientChild child = new ClientChild(c.getName(), c.getSurname(), c.getCF(), c.getParentId());
                        if (db.getCredentials(child.getParentId()) != null)
                            db.addChildToLine(line.getAdmins().get(0), child, line.getName(), db.getCredentials(line.getAdmins().get(0)).getRoles());
                    }
                    for(String admin : line.getAdmins()){
                        if(!db.getCredentials(admin).getRoles().contains("ROLE_ADMIN"))
                            db.makeLineAdmin("admin@mail.com",admin,line.getName());
                    }
                } catch (BadRequestException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                throw new IOException("Error in lines' data stream");
            }
        } else {
            throw new IOException("One of lines' file location is wrong.");
        }
    }


    private void AddCredentialsAndUsers(String usersFile) throws IOException {
        // Add Credentials and Users
        URL resource = this.getClass().getClassLoader().getResource(usersFile);
        if (resource != null) {
            InputStream usersStream = resource.openStream();
            if (usersStream != null) {
                String jsonUsers;
                try {
                    byte[] jsonBytes = StreamUtils.copyToByteArray(usersStream);
                    jsonUsers = new String(jsonBytes);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw e;
                }
                JsonUsers users = new ObjectMapper().readValue(jsonUsers, JsonUsers.class);

                for (JsonUser user : users.users) {
                    try {
                        if ((user.getChildren() != null ? user.getChildren().size() : 0) == 0) {
                            List<String> roles = new ArrayList<>();
                            roles.add(Roles.USER);
                            roles.add(Roles.COMPANION);
                            db.insertCredentials(user.username, "password", roles, Boolean.TRUE);
                        } else
                            db.insertCredentials(user.username, "password", Arrays.asList(Roles.USER), Boolean.TRUE);

                        ClientUser u = new ClientUser();
                        u.setMail(user.getUsername());
                        u.setName(user.getName());
                        u.setSurname(user.getSurname());
                        u.setContacts(user.getContacts());
                        List<ClientChild> children = new ArrayList<>();
                        if (user.getChildren() != null) {
                            for (JsonChild c : user.getChildren()) {
                                ClientChild child = new ClientChild(c.getName(), c.getSurname(), c.getCF(), c.getParentId());
                                children.add(child);
                            }
                        }
                        u.setChildren(children);
                        u.setLines(user.getLines());
                        db.insertUser(u);
                    } catch (BadRequestException e) {
                        System.out.println("User already exists");
                    }
                }
            } else {
                throw new IOException("Error in users' data stream.");
            }
        } else throw new IOException("Wrong location for users' json file.");
    }

}
