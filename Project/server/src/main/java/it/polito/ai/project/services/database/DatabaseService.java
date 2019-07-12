package it.polito.ai.project.services.database;

import it.polito.ai.project.exceptions.BadRequestException;
import it.polito.ai.project.exceptions.InternalServerErrorException;
import it.polito.ai.project.exceptions.ResourceNotFoundException;
import it.polito.ai.project.exceptions.UnauthorizedRequestException;
import it.polito.ai.project.generalmodels.*;
import it.polito.ai.project.services.database.models.*;
import it.polito.ai.project.services.database.repositories.*;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DatabaseService implements DatabaseServiceInterface {
    final
    private PasswordEncoder passwordEncoder;
    private final LineRepository lineRepository;
    private final RaceRepository raceRepository;
    private final UserCredentialsRepository userCredentialsRepository;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Autowired
    public DatabaseService(PasswordEncoder passwordEncoder, LineRepository lineRepository, RaceRepository raceRepository, UserCredentialsRepository userCredentialsRepository, TokenRepository tokenRepository, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.lineRepository = lineRepository;
        this.raceRepository = raceRepository;
        this.userCredentialsRepository = userCredentialsRepository;
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }


    //---------------------------------------------###UserCredentials###----------------------------------------------//

    /**
     * Function to insert new credential
     *
     * @param username username to insert
     * @param password user password
     * @param roles    list of user's roles
     * @return ClientUserCredentials: credential inserted
     * @throws InternalServerErrorException
     * @throws BadRequestException
     */
    @Override
    @Transactional
    public ClientUserCredentials insertCredentials(String username, String password, List<String> roles) {
        try {
            if (!userCredentialsRepository.findByUsername(username).isPresent()) {
                UserCredentials u = userCredentialsRepository.save(new UserCredentials(this.passwordEncoder.encode(password), username, roles));
                return userCredentialsToClientUserCredentials(u);
            }
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        throw new BadRequestException();
    }

    /**
     * Function to get user's credential
     *
     * @param username username requested
     * @return ClientUserCredentials: credential requested
     * @throws InternalServerErrorException
     * @throws ResourceNotFoundException
     */
    @Override
    public ClientUserCredentials getCredentials(String username) {
        try {
            UserCredentials u = userCredentialsRepository.findByUsername(username).get();
            return userCredentialsToClientUserCredentials(u);
        } catch (NoSuchElementException e1) {
            throw new ResourceNotFoundException();
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }

    /**
     * Function to modify user's password
     *
     * @param clientUserCredentials user credential
     * @param password              user new password
     * @throws InternalServerErrorException
     * @throws ResourceNotFoundException
     */
    @Override
    @Transactional
    public void modifyUserPassword(ClientUserCredentials clientUserCredentials, String password) {
        try {
            Optional<UserCredentials> u = userCredentialsRepository.findByUsername(clientUserCredentials.getUsername());
            if (u.isPresent()) {
                u.get().setPassword(this.passwordEncoder.encode(password));
                userCredentialsRepository.save(u.get());
            } else {
                throw new ResourceNotFoundException();
            }
        } catch (ResourceNotFoundException e1) {
            throw new ResourceNotFoundException();
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }

    /**
     * Function to update user's credential
     *
     * @param clientUserCredentials user new credential
     * @throws InternalServerErrorException
     * @throws ResourceNotFoundException
     */
    @Override
    @Transactional
    public void updateCredentials(ClientUserCredentials clientUserCredentials) {
        try {
            Optional<UserCredentials> u = userCredentialsRepository.findByUsername(clientUserCredentials.getUsername());
            if (u.isPresent()) {
                u.get().setRoles(clientUserCredentials.getRoles());
                u.get().setAccountNotExpired(clientUserCredentials.isAccountNotExpired());
                u.get().setAccountNotLocked(clientUserCredentials.isAccountNotLocked());
                u.get().setCredentialsNotExpired(clientUserCredentials.isCredentialsNotExpired());
                u.get().setEnable(clientUserCredentials.isEnable());

                userCredentialsRepository.save(u.get());
            } else {
                throw new ResourceNotFoundException();
            }
        } catch (ResourceNotFoundException e1) {
            throw new ResourceNotFoundException();
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }

    /**
     * Function to delete credential
     *
     * @param clientUserCredentials user delete credential
     * @throws InternalServerErrorException
     * @throws ResourceNotFoundException
     */
    @Override
    @Transactional
    public void deleteCredentials(ClientUserCredentials clientUserCredentials) {
        try {
            Optional<UserCredentials> u = userCredentialsRepository.findByUsername(clientUserCredentials.getUsername());
            if (u.isPresent()) {
                userCredentialsRepository.delete(u.get());
            } else {
                throw new ResourceNotFoundException();
            }
        } catch (ResourceNotFoundException e1) {
            throw new ResourceNotFoundException();
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }

    /**
     * Function to convert UserCredentials to ClientUserCredentials
     *
     * @param uc UserCredentials to convert
     * @return ClientUserCredentials converted
     */
    private ClientUserCredentials userCredentialsToClientUserCredentials(UserCredentials uc) {
        return new ClientUserCredentials(uc.getUsername(), uc.getRoles(), uc.isEnable(), uc.isCredentialsNotExpired(), uc.isAccountNotLocked(), uc.isAccountNotExpired());
    }

    //---------------------------------------------------###Token###--------------------------------------------------//

    /**
     * Function to save selected Token
     *
     * @param token token to save
     * @throws InternalServerErrorException
     */
    @Override
    public void insertToken(Token token) {
        try {
            tokenRepository.save(token);
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }

    /**
     * Function to delete selected Token
     *
     * @param token token to delete
     * @throws InternalServerErrorException
     */
    @Override
    public void deleteToken(Token token) {
        try {
            tokenRepository.delete(token);
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }

    //---------------------------------------------------###User###---------------------------------------------------//

    //TODO checkare se funge

    /**
     * Function to get all Users paged
     *
     * @param pageNumber number of page to retrieve
     * @return Page ClientUser: page requested
     * @throws InternalServerErrorException
     */
    @Override
    public Page<ClientUser> getUsers(int pageNumber) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, 10);

            List<ClientUser> usersList = new ArrayList<>();
            for (User p : userRepository.findAll(pageable))
                usersList.add(userToClientUser(p));

            Page<ClientUser> finalPage = new PageImpl<>(usersList);
            return finalPage;
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }

    /**
     * Function to get User by Id
     *
     * @param id user id
     * @return ClientUser: requested user
     * @throws InternalServerErrorException
     * @throws ResourceNotFoundException
     */
    @Override
    public ClientUser getUser(String id) {
        try {
            Optional<User> p = userRepository.findById(id);
            if (p.isPresent())
                return userToClientUser(p.get());
            else
                throw new ResourceNotFoundException();
        } catch (ResourceNotFoundException e1) {
            throw new ResourceNotFoundException();
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }

    /**
     * Function to get User by Username
     *
     * @param username user username
     * @return ClientUser: requested user
     * @throws InternalServerErrorException
     * @throws ResourceNotFoundException
     */
    @Override
    public ClientUser getUserByUsername(String username) {
        try {
            Optional<User> p = userRepository.findByUsername(username);
            if (p.isPresent())
                return userToClientUser(p.get());
            else
                throw new ResourceNotFoundException();
        } catch (ResourceNotFoundException e1) {
            throw new ResourceNotFoundException();
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }

    /**
     * Function to insert User
     *
     * @param user: user to insert
     * @return ClientUser: inserted user
     * @throws InternalServerErrorException
     * @throws ResourceNotFoundException
     */
    @Override
    @Transactional
    public ClientUser insertUser(ClientUser user) {
        try {
            if (!userRepository.findByUsername(user.getUsername()).isPresent()) {
                userRepository.save(clientUserToUser(user));
                return user;
            }
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        throw new BadRequestException();
    }

    /**
     * Function to update User
     *
     * @param user user to update
     * @throws InternalServerErrorException
     * @throws ResourceNotFoundException
     */
    @Override
    @Transactional
    public void updateUser(ClientUser user) {
        Optional<User> u = userRepository.findByUsername(user.getUsername());
        try {
            if (u.isPresent()) {
                u.get().setName(user.getName());
                u.get().setSurname(user.getSurname());
                u.get().setLines(user.getLines());
                u.get().setChildren(user.getChildren());
                u.get().setContacts(user.getContacts());
                u.get().setNotifications(user.getNotifications());

                userRepository.save(u.get());
                return;
            }
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        throw new ResourceNotFoundException();
    }

    /**
     * Function to convert User to ClientUser
     *
     * @param p user to convert
     * @return ClientUser: converted client user
     */
    private ClientUser userToClientUser(User p) {
        return new ClientUser(p.getUsername(), p.getName(), p.getSurname(), p.getContacts(), p.getChildren(), p.getLines(), p.getNotifications());
    }

    /**
     * Function to convert ClientUser to  User
     *
     * @param p ClientUser to convert
     * @return User: converted user
     */
    private User clientUserToUser(ClientUser p) {
        return new User(p.getUsername(), p.getName(), p.getSurname(), p.getContacts(), p.getChildren(), p.getLines(), p.getNotifications());
    }

    //---------------------------------------------------###Admin###--------------------------------------------------//

    /**
     * Function to make line Admin
     *
     * @param performerUsername user that perform operation
     * @param targetUsername    user to make Admin
     * @param line              line in which to add the admin
     * @throws InternalServerErrorException
     * @throws ResourceNotFoundException
     * @throws BadRequestException
     */
    @Transactional
    @Override
    public void makeLineAdmin(String performerUsername, String targetUsername, String line) {
        Optional<UserCredentials> perfCredentials;
        Optional<User> performer;
        Optional<UserCredentials> targetCredentials;
        Optional<User> target;
        Optional<Line> mongoLine;

        try {
            perfCredentials = userCredentialsRepository.findByUsername(performerUsername);
            performer = userRepository.findByUsername(performerUsername);
            targetCredentials = userCredentialsRepository.findByUsername(targetUsername);
            target = userRepository.findByUsername(targetUsername);
            mongoLine = lineRepository.findLineByName(line);
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        // If one of line, performer, performerCredentials, target or targetCredentials misses: Throw ResourceNotFound
        if (!mongoLine.isPresent() || !perfCredentials.isPresent() || !performer.isPresent() || !target.isPresent() || !targetCredentials.isPresent())
            throw new ResourceNotFoundException();

        List<String> performerLines = performer.get().getLines();
        List<String> performerRoles = perfCredentials.get().getRoles();
        List<String> targetLines = target.get().getLines();
        List<String> targetRoles = targetCredentials.get().getRoles();

        // If performer isn't the System_Admin or an Admin of the selected line: Throw Unahuthorized
        if (!isAdminOfLineOrSysAdmin(performerLines, performerRoles, line))
            throw new UnauthorizedRequestException();

        // If target is the System_Admin or an Admin of the selected line: Throw BadRequest
        if (isAdminOfLineOrSysAdmin(targetLines, targetRoles, line))
            throw new BadRequestException();

        //If target was a normal user before this action: Add Admin in UserCredentials.roles
        if (!isAdmin(targetRoles)) {
            targetCredentials.get().getRoles().add(Roles.prefix + Roles.ADMIN);
        }

        // Adding line to User.lines
        Objects.requireNonNull(target.get().getLines()).add(line);

        // UpdateLine: Add user admin in line
        mongoLine.get().getAdmins().add(targetCredentials.get().getUsername());
        updateLine(mongoLine.get());

        // Save results: update User modified before
        updateCredentials(userCredentialsToClientUserCredentials(targetCredentials.get()));
        updateUser(userToClientUser(target.get()));
    }

    /**
     * Function to remove line Admin
     *
     * @param performerUsername user that perform operation
     * @param targetUsername    user to remove from Admin
     * @param line              line in which to remove the admin
     * @throws InternalServerErrorException
     * @throws ResourceNotFoundException
     * @throws BadRequestException
     */
    @Transactional
    @Override
    public void removeLineAdmin(String performerUsername, String targetUsername, String line) {
        Optional<UserCredentials> perfCredentials;
        Optional<User> performer;
        Optional<UserCredentials> targetCredentials;
        Optional<User> target;
        Optional<Line> mongoLine;

        try {
            perfCredentials = userCredentialsRepository.findByUsername(performerUsername);
            performer = userRepository.findByUsername(performerUsername);
            targetCredentials = userCredentialsRepository.findByUsername(targetUsername);
            target = userRepository.findByUsername(targetUsername);
            mongoLine = lineRepository.findLineByName(line);
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }

        // If one of line, performer, performerCredentials, target or targetCredentials misses: Throw ResourceNotFound
        if (!mongoLine.isPresent() || !perfCredentials.isPresent() || !performer.isPresent() || !target.isPresent() || !targetCredentials.isPresent())
            throw new ResourceNotFoundException();

        List<String> performerLines = performer.get().getLines();
        List<String> performerRoles = perfCredentials.get().getRoles();
        List<String> targetLines = target.get().getLines();
        List<String> targetRoles = targetCredentials.get().getRoles();

        // If performer isn't the System_Admin or an Admin of the selected line: Throw Unahuthorized
        if (!isAdminOfLineOrSysAdmin(performerLines, performerRoles, line))
            throw new UnauthorizedRequestException();

        // If target isn't the System_Admin or an Admin of the selected line: Throw BadRequest
        if (!isAdminOfLine(targetLines, targetRoles, line))
            throw new BadRequestException();

        // UpdateLine: remove user admin from line
        mongoLine.get().getAdmins().remove(targetCredentials.get().getUsername());
        updateLine(mongoLine.get());

        // Removing line from User.lines
        Objects.requireNonNull(target.get().getLines()).remove(line);

        // Since an Admin without lines can exist we choose to don't remove ADMIN role
        updateUser(userToClientUser(target.get()));
    }

    //TODO: DA VEDERE BENE.... NON MI PIACE TANTISSIMO, LA RIGUARDO IO
    //TODO Update Race
    @Transactional
    @Override
    public void selectCompanion(String performerUsername, ClientRace clientRace, List<String> companions) {
        Optional<UserCredentials> performerCredentials;
        Optional<Race> race;
        Optional<Line> line;
        List<UserCredentials> companionsCredentials;
        try {
            performerCredentials = userCredentialsRepository.findByUsername(performerUsername);
            race = raceRepository.findRaceByDataAndLineNameAndDirection(clientRace.getData(), clientRace.getLineName(), clientRace.getDirection().toString());
            companionsCredentials = new ArrayList<>();
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        // If performer or race not found: Throw ResourceNotFound
        if (!performerCredentials.isPresent() || !race.isPresent())
            throw new ResourceNotFoundException();

        try {
            line = lineRepository.findLineByName(race.get().getLineName());
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        if (!line.isPresent())
            throw new ResourceNotFoundException();

        //reperisce le credenziali di tutti i companion e ne verifica il ruolo
        for (String c : companions) {
            Optional<UserCredentials> userTemp;
            try {
                userTemp = userCredentialsRepository.findByUsername(c);
            } catch (Exception e) {
                throw new InternalServerErrorException();
            }
            //TODO: da discutere!! se uno non è companion....
            if (!userTemp.isPresent())
                throw new ResourceNotFoundException();
            companionsCredentials.add(userTemp.get());
            if (!userTemp.get().getRoles().contains(Roles.prefix + Roles.COMPANION))
                throw new BadRequestException();
        }

        //Seleziona i companion richiesti dalla lista di companion della race
        List<Companion> tempCompanions = new ArrayList<>();
        for (Companion c : race.get().getCompanions()) {
            if (companions.contains(c.getUserDetails().getUsername())) {
                tempCompanions.add(c);
            }
        }

        //Seleziona i Pedistop della race in esame
        List<PediStop> tempStops = new ArrayList<>();
        if (race.get().getDirection().toString().equals(DirectionType.OUTWARD))
            tempStops.addAll(line.get().getOutwardStops());
        else
            tempStops.addAll(line.get().getReturnStops());

        // Crea una lista di liste in cui ogni elemento è la lista degli stop coperta da un singolo companion
        List<Pair<String, List<PediStop>>> companionStops = new ArrayList<>();
        List<PediStop> finalstops = new ArrayList<>();
        for (Companion c : tempCompanions) {
            List<PediStop> temp = new ArrayList<>();
            boolean flag = false;
            for (PediStop stop : tempStops) {
                if (c.getInitialStop().getName().equals(stop.getName()))
                    flag = true;
                if (c.getFinalStop().getName().equals(stop.getName())) {
                    //solo in caso di ultimo companion aggiunge il final stop
                    if (tempCompanions.get(tempCompanions.size() - 1).getUserDetails().getName().equals(c.getUserDetails().getName()))
                        temp.add(stop);

                    break;
                }
                if (flag) temp.add(stop);

            }
            finalstops.addAll(temp);
            companionStops.add(new Pair<>(c.getUserDetails().getName(), temp));
        }
        //Verifica che non ci sia sovrapposizione tra gli stop dei companion selezionati
        for (Pair<String, List<PediStop>> compstops : companionStops) {
            for (Pair<String, List<PediStop>> comp : companionStops) {
                if (!compstops.getKey().equals(comp.getKey())) {
                    List<PediStop> tempPedi = comp.getValue();
                    tempPedi.retainAll(compstops.getValue());
                    if (!tempPedi.isEmpty()) {
                        throw new BadRequestException();
                    }
                }
            }
        }

        // se tempstops non è uguale a finalstops allora l'itinerario non è completamente coperto

        if (!tempStops.equals(finalstops))
            throw new BadRequestException();

        //Vengono adesso Marcati i companions;
        for (Pair<String, List<PediStop>> companionObj : companionStops) ;
        //TODO: updateRace
    }

    private boolean isAdminOfLineOrSysAdmin(List<String> lines, List<String> roles, String line) {
        if (isSystemAdmin(roles)) return true;
        return isAdminOfLine(lines, roles, line);
    }

    private boolean isAdminOfLine(List<String> lines, List<String> roles, String line) {
        if (!isAdmin(roles)) return false;
        return lines.contains(line);
    }

    private boolean isAdmin(List<String> roles) {
        return roles.contains(Roles.prefix + Roles.ADMIN);
    }

    private boolean isSystemAdmin(List<String> roles) {
        return roles.contains(Roles.prefix + Roles.SYSTEM_ADMIN);
    }

    //------------------------------------------------###Companion###-------------------------------------------------//
    
    //TODO Update Race
    @Transactional
    @Override
    public void stateCompanionAvailability(ClientCompanion clientCompanion, String performerUsername, ClientRace clientRace) {
        Optional<Race> race;
        Optional<UserCredentials> targetCredentials;
        Optional<UserCredentials> performerCredentials;

        try {
            race = raceRepository.findRaceByDataAndLineNameAndDirection(clientRace.getData(), clientRace.getLineName(), clientRace.getDirection().toString());
            targetCredentials = userCredentialsRepository.findByUsername(clientCompanion.getUserDetails().getUsername());
            performerCredentials = userCredentialsRepository.findByUsername(performerUsername);
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        // If race, targetCredentials or PerformCredentials aren't in db: Throw ResourceNotFound
        if (!race.isPresent() || !targetCredentials.isPresent() || !performerCredentials.isPresent())
            throw new ResourceNotFoundException();
        // If the performer isn't the SysAdmin or the Companion: Throw UnauthorizedRequest
        if (!isCompanion(performerCredentials.get().getRoles()) && !isSystemAdmin(performerCredentials.get().getRoles()))
            throw new UnauthorizedRequestException();
        // If the target isn't a companion: Throw BadRequest
        if (!isCompanion(targetCredentials.get().getRoles()))
            throw new BadRequestException();
        // If race companions already contains companion: Throw BadRequest
        if (isCompanionOfRace(race.get().getCompanions(), targetCredentials.get().getRoles(), clientCompanionToCompanion((clientCompanion))))
            throw new BadRequestException();

        // If the performer is a Companion: check if he is stating availability for himself
        if (isCompanion(performerCredentials.get().getRoles())) {
            if (!performerCredentials.get().getUsername().equals(targetCredentials.get().getUsername()))
                throw new UnauthorizedRequestException();
        }

        race.get().getCompanions().add(clientCompanionToCompanion(clientCompanion));
        //TODO call update race

    }

    @Transactional
    @Override
    public void makeCompanion(String performerUsername, String targetUsername) {
        Optional<UserCredentials> perfCredentials;
        Optional<User> performer;
        Optional<UserCredentials> targetCredentials;
        Optional<User> target;

        try {
            perfCredentials = userCredentialsRepository.findByUsername(performerUsername);
            performer = userRepository.findByUsername(performerUsername);
            targetCredentials = userCredentialsRepository.findByUsername(targetUsername);
            target = userRepository.findByUsername(targetUsername);
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        // If one among line, performer, performerCredentials, target or targetCredentials misses: Throw ResourceNotFound
        if (!perfCredentials.isPresent() || !performer.isPresent() || !target.isPresent() || !targetCredentials.isPresent())
            throw new ResourceNotFoundException();

        List<String> performerRoles = perfCredentials.get().getRoles();
        List<String> targetRoles = targetCredentials.get().getRoles();

        // If performer isn't the SysAdmin or an Admin: Throw Unauthorized
        if (!isAdmin(performerRoles) || !isSystemAdmin(performerRoles))
            throw new UnauthorizedRequestException();
        // If target is already a Companion: Throw BadRequest
        if (isCompanion(targetRoles))
            throw new BadRequestException();

        targetCredentials.get().getRoles().add(Roles.prefix + Roles.COMPANION);

        // Update Credentials: update user to companion
        updateCredentials(userCredentialsToClientUserCredentials(targetCredentials.get()));

        //TODO: accompagnatore abbiamo detto che può fare tutte le linee vero? se no va messa la lista delle linee
    }

    @Transactional
    @Override
    public void removeCompanion(ClientCompanion clientCompanion, String performerUsername) {

        List<Race> races;
        Optional<UserCredentials> targetCredentials;
        Optional<User> target;
        Optional<UserCredentials> perfCredentials;
        Optional<User> performer;
        try {
            perfCredentials = userCredentialsRepository.findByUsername(performerUsername);
            performer = userRepository.findByUsername(performerUsername);
            targetCredentials = userCredentialsRepository.findByUsername(clientCompanion.getUserDetails().getUsername());
            target = userRepository.findByUsername(clientCompanion.getUserDetails().getUsername());
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        // If one among line, performer, performerCredentials, target or targetCredentials misses: Throw ResourceNotFound
        if (!perfCredentials.isPresent() || !performer.isPresent() || !target.isPresent() || !targetCredentials.isPresent())
            throw new ResourceNotFoundException();

        String pattern = "yyyy-MM-dd'T'HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String stringDate = simpleDateFormat.format(new Date());
        try {
            Date date = simpleDateFormat.parse(stringDate);
            races = raceRepository.findAllByCompanions(clientCompanionToCompanion(clientCompanion), date);
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }

        List<String> performerRoles = perfCredentials.get().getRoles();
        List<String> targetRoles = targetCredentials.get().getRoles();

        // If target isn't a Companion: Throw BadRequest
        if (!isCompanion(targetRoles))
            throw new BadRequestException();
        // If performer isn't the SysAdmin or an Admin: Throw Unauthorized
        if (!isAdmin(performerRoles) || !isSystemAdmin(performerRoles))
            throw new UnauthorizedRequestException();

        if (!races.isEmpty()) {
            for (Race r : races) {
                r.getCompanions().remove(clientCompanionToCompanion(clientCompanion));
                //TODO: update future race
            }
        }

        targetCredentials.get().getRoles().remove(Roles.prefix + Roles.COMPANION);

        // Update Credentials: update user to remove companion
        updateCredentials(userCredentialsToClientUserCredentials(targetCredentials.get()));

        //TODO: se non ci sono vincoli delle linee ok
    }

    private boolean isCompanionOfRace(List<Companion> companions, List<String> roles, Companion companion) {
        if (!isCompanion(roles)) return false;
        return companions.contains(companion);
    }

    private boolean isCompanion(List<String> roles) {
        return roles.contains(Roles.prefix + Roles.COMPANION);
    }

    private Companion clientCompanionToCompanion(ClientCompanion clientCompanion) {
        return new Companion(clientUserToUser(clientCompanion.getUserDetails()), clientPediStopToPediStop(clientCompanion.getInitialStop()), clientPediStopToPediStop(clientCompanion.getFinalStop()), clientCompanion.getState());
    }

    //---------------------------------------------------###Line###---------------------------------------------------//

    /**
     * Function to insert new Line From Json Deserializer on startup
     *
     * @param line: line to insert
     * @throws InternalServerErrorException
     */
    @Override
    @Transactional
    public void insertLine(JsonLine line) {
        try {
            ArrayList<PediStop> outStops = new ArrayList<>();
            for (JsonPediStop p : line.getOutwardStops())
                outStops.add(new PediStop(p.getName(), p.getLongitude(), p.getLatitude(), p.getDelayInMillis()));
            ArrayList<PediStop> retStops = new ArrayList<>();
            for (JsonPediStop p : line.getReturnStops())
                retStops.add(new PediStop(p.getName(), p.getLongitude(), p.getLatitude(), p.getDelayInMillis()));

            for (String a : line.getAdmins()) {
                Optional<UserCredentials> res = userCredentialsRepository.findByUsername(a);
                if (!res.isPresent()) {
                    subscribeAdminAndSendMail(a);
                } else if (!res.get().getRoles().contains(Roles.prefix + Roles.ADMIN)) {
                    res.get().getRoles().add(Roles.prefix + Roles.ADMIN);
                    userCredentialsRepository.save(res.get());
                }
            }
            Line l = new Line(line.getName(), outStops, retStops, line.getAdmins());
            lineRepository.save(l);
        } catch (Exception e) {
            //TO-DO check unique index Exception
            throw new InternalServerErrorException(e);
        }
    }

    /**
     * Function to update Admins  in Line
     *
     * @param line: line to update
     * @throws InternalServerErrorException
     * @throws ResourceNotFoundException
     */
    @Override
    @Transactional
    public void updateLine(Line line) {
        Optional<Line> temp;
        try {
            temp = lineRepository.findLineByName(line.getName());
        } catch (Exception e) {
            //TO-DO check unique index Exception
            throw new InternalServerErrorException(e);
        }
        if (!temp.isPresent())
            throw new ResourceNotFoundException();

        try {
            temp.get().setAdmins(line.getAdmins());
            lineRepository.save(temp.get());
        } catch (Exception e) {
            //TO-DO check unique index Exception
            throw new InternalServerErrorException(e);
        }
    }

    private boolean subscribeAdminAndSendMail(String username) {
        //TODO: implementa insert user (admin)
        return true;
    }

    /**
     * Function to get all line names in Db as string collection
     *
     * @return Collection of line names
     * @throws InternalServerErrorException
     */
    @Override
    public Collection<String> getLinesNames() {
        try {
            List<Line> list = lineRepository.findAllName();
            List<String> names = new ArrayList<>();
            for (Line lineMongo : list)
                names.add(lineMongo.getName());
            return names;
        } catch (Exception e) {
            throw new InternalServerErrorException(e);
        }
    }

    /**
     * Function to get all lines in DB as ClientLine collection
     *
     * @return Collection of lines
     * @throws InternalServerErrorException
     */
    @Override
    public Collection<ClientLine> getLines() {
        try {
            List<Line> list = lineRepository.findAll();
            List<ClientLine> lines = new ArrayList<>();
            for (Line lineMongo : list)
                lines.add(LineToClientLine(lineMongo));
            return lines;
        } catch (Exception e) {
            throw new InternalServerErrorException(e);
        }
    }

    /**
     * Function to insert new child to a line
     *
     * @param UserID   Id of admin performing the action
     * @param child    Child to insert
     * @param lineName Name of the selected Line
     * @param roles    List of performing action user roles
     * @return List of line names
     * @throws InternalServerErrorException
     * @throws UnauthorizedRequestException
     * @throws BadRequestException
     */
    @Override
    @Transactional
    public ClientLine addChildToLine(String UserID, ClientChild child, String lineName, List<String> roles) {
        try {
            Optional<Line> l = lineRepository.findLineByName(lineName);
            if (!l.isPresent())
                throw new ResourceNotFoundException();

            if (!roles.contains(Roles.prefix + Roles.SYSTEM_ADMIN)) {
                if (!l.get().getAdmins().contains(UserID))
                    throw new UnauthorizedRequestException("Line Admins only can perform this operation");
            }
            Child c = new Child(child.getName(), child.getSurname(), child.getCF(), child.getParentId());

            if (!l.get().getSubscribedChildren().contains(c)) {

                l.get().getSubscribedChildren().add(c);
                lineRepository.save(l.get());
                return LineToClientLine(l.get());
            } else {
                throw new BadRequestException();
            }
        } catch (Exception e) {
            throw new InternalServerErrorException(e);
        }
    }

    //TODO Move to Utils

    /**
     * Function to convert MongoDB Line into a ClientLine
     *
     * @param lineMongo: line to convert
     * @return line converted
     */
    private ClientLine LineToClientLine(Line lineMongo) {
        ArrayList<ClientPediStop> out = new ArrayList<>();
        for (PediStop p : lineMongo.getOutwardStops())
            out.add(new ClientPediStop(p.getName(), p.getLatitude(), p.getLongitude(), p.getDelayInMillis()));

        ArrayList<ClientPediStop> ret = new ArrayList<>();
        for (PediStop p : lineMongo.getReturnStops())
            ret.add(new ClientPediStop(p.getName(), p.getLatitude(), p.getLongitude(), p.getDelayInMillis()));

        List<ClientChild> childList = new ArrayList<>();
        for (Child c : lineMongo.getSubscribedChildren()) {
            ClientChild cc = new ClientChild(c.getName(), c.getSurname(), c.getCF(), c.getParentId());
            childList.add(cc);
        }
        return new ClientLine(lineMongo.getName(), out, ret, childList, lineMongo.getAdmins());
    }

    private PediStop clientPediStopToPediStop(ClientPediStop clientPediStop) {
        return new PediStop(clientPediStop.getName(), clientPediStop.getLongitude(), clientPediStop.getLatitude(), clientPediStop.getDelayInMillis());
    }

    //---------------------------------------------------###Race###---------------------------------------------------//

}
