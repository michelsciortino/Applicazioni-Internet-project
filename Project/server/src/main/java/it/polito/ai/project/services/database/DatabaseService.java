package it.polito.ai.project.services.database;

import it.polito.ai.project.exceptions.BadRequestException;
import it.polito.ai.project.exceptions.InternalServerErrorException;
import it.polito.ai.project.exceptions.ResourceNotFoundException;
import it.polito.ai.project.exceptions.UnauthorizedRequestException;
import it.polito.ai.project.generalmodels.*;
import it.polito.ai.project.services.database.models.*;
import it.polito.ai.project.services.database.repositories.*;
import it.polito.ai.project.services.email.EmailConfiguration;
import it.polito.ai.project.services.email.EmailSenderService;
import it.polito.ai.project.services.email.models.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class DatabaseService implements DatabaseServiceInterface {
    private final PasswordEncoder passwordEncoder;
    private final LineRepository lineRepository;
    private final RaceRepository raceRepository;
    private final UserCredentialsRepository userCredentialsRepository;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final EmailSenderService emailSenderService;
    private final SimpMessageSendingOperations messagingTemplate;

    @Autowired
    public DatabaseService(PasswordEncoder passwordEncoder, LineRepository lineRepository, RaceRepository raceRepository, UserCredentialsRepository userCredentialsRepository, TokenRepository tokenRepository, UserRepository userRepository, UserNotificationRepository userNotificationRepository, EmailSenderService emailSenderService, SimpMessageSendingOperations messagingTemplate) {
        this.passwordEncoder = passwordEncoder;
        this.lineRepository = lineRepository;
        this.raceRepository = raceRepository;
        this.userCredentialsRepository = userCredentialsRepository;
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.userNotificationRepository = userNotificationRepository;
        this.emailSenderService = emailSenderService;
        this.messagingTemplate = messagingTemplate;
    }


    //---------------------------------------------###UserCredentials###----------------------------------------------//

    private static Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private static Date midnightTime(Date date) {
        return new Date(date.getTime() + 24 * 60 * 60 * 1000);
    }

    private static String dateToISO(Date date){
        SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return format.format(date);
    }

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
    public ClientUserCredentials insertCredentials(String username, String password, List<String> roles, Boolean isEnable) {
        try {
            if (!userCredentialsRepository.findByUsername(username).isPresent()) {
                UserCredentials user = userCredentialsRepository.save(new UserCredentials(username, this.passwordEncoder.encode(password), clientRolesToRoles(roles), isEnable));
                return userCredentialsToClientUserCredentials(user);
            }
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        throw new BadRequestException("Credentials already inserted");
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
                u.get().setRoles(clientRolesToRoles(clientUserCredentials.getRoles()));
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

    //---------------------------------------------------###Token###--------------------------------------------------//

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
        return new ClientUserCredentials(uc.getUsername(), rolesToClientRoles(uc.getRoles()), uc.isEnable(), uc.isCredentialsNotExpired(), uc.isAccountNotLocked(), uc.isAccountNotExpired());
    }

    //---------------------------------------------------###User###---------------------------------------------------//

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

    /**
     * Function to get all Users paged
     *
     * @param pageNumber number of page to retrieve
     * @return Page ClientUser: page requested
     * @throws InternalServerErrorException
     */
    @Override
    public Page<ClientUser> getUsers(int pageSize, int pageNumber, String sortBy, String filterBy, String filter) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(SortType.get(sortBy)));
            List<ClientUser> usersList = new ArrayList<>();

            if (filterBy == null && filter == null) {
                Page<User> users = userRepository.findAll(pageable);
                long totalElements = users.getTotalElements();
                for (User p : users) {
                    Optional<UserCredentials> credentials = userCredentialsRepository.findByUsername(p.getUsername());
                    usersList.add(userToClientUser(p, credentials.get().getRoles()));
                }
                return new PageImpl<>(usersList, pageable, totalElements);
            }
            if (filterBy == null || filter == null)
                throw new BadRequestException();

            if (filterBy.equals("MAIL")) {
                Page<User> users = userRepository.findAllByUsernameContainsIgnoreCase(filter, pageable);
                long totalElements = users.getTotalElements();
                for (User p : users) {
                    Optional<UserCredentials> credentials = userCredentialsRepository.findByUsername(p.getUsername());
                    usersList.add(userToClientUser(p, credentials.get().getRoles()));
                }
                return new PageImpl<>(usersList, pageable, totalElements);
            }
            if (filterBy.equals("NAME")) {
                Page<User> users = userRepository.findAllByNameContainsIgnoreCase(filter, pageable);
                long totalElements = users.getTotalElements();
                for (User p : users) {
                    Optional<UserCredentials> credentials = userCredentialsRepository.findByUsername(p.getUsername());
                    usersList.add(userToClientUser(p, credentials.get().getRoles()));
                }
                return new PageImpl<>(usersList, pageable, totalElements);
            }
            if (filterBy.equals("SURNAME")) {
                Page<User> users = userRepository.findAllBySurnameContainsIgnoreCase(filter, pageable);
                long totalElements = users.getTotalElements();
                for (User p : users) {
                    Optional<UserCredentials> credentials = userCredentialsRepository.findByUsername(p.getUsername());
                    usersList.add(userToClientUser(p, credentials.get().getRoles()));
                }
                return new PageImpl<>(usersList, pageable, totalElements);
            }
            if (filterBy.equals("ROLE")) {
                Pageable pageableC = PageRequest.of(pageNumber, pageSize);
                List<String> roles = new ArrayList<>();
                roles.add(filter.toUpperCase());
                Page<UserCredentials> usersC = userCredentialsRepository.findAllByRolesContains(clientRolesToRoles(roles).get(0), pageableC);
                long totalElements = usersC.getTotalElements();
                for (UserCredentials p : usersC) {
                    Optional<User> user = userRepository.findByUsername(p.getUsername());
                    usersList.add(userToClientUser(user.get(), p.getRoles()));
                }
                return new PageImpl<>(usersList, pageable, totalElements);
            } else {
                throw new BadRequestException();
            }
        } catch (BadRequestException be) {
            throw new BadRequestException(be);
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
            if (p.isPresent()) {
                Optional<UserCredentials> credentials = userCredentialsRepository.findByUsername(p.get().getUsername());
                return userToClientUser(p.get(), credentials.get().getRoles());
            } else
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
            if (p.isPresent()) {
                Optional<UserCredentials> credentials = userCredentialsRepository.findByUsername(p.get().getUsername());
                return userToClientUser(p.get(), credentials.get().getRoles());
            } else
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
            if (!userRepository.findByUsername(user.getMail()).isPresent()) {
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
        Optional<User> u = userRepository.findByUsername(user.getMail());
        try {
            if (u.isPresent()) {
                u.get().setName(user.getName());
                u.get().setSurname(user.getSurname());
                u.get().setLines(user.getLines());
                List<Child> children = new ArrayList<>();
                if (user.getChildren() != null) {
                    for (ClientChild cc : user.getChildren())
                        children.add(clientChildToChild(cc));
                }
                u.get().setChildren(children);
                u.get().setContacts(user.getContacts());

                userRepository.save(u.get());
                return;
            }
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        throw new ResourceNotFoundException();
    }

    /**
     * Function to update User unsensitive fields
     *
     * @param user user to update
     * @throws InternalServerErrorException
     * @throws ResourceNotFoundException
     */
    @Override
    @Transactional
    public ClientUser controlledUpdateUser(ClientUser user) {
        Optional<User> u = userRepository.findByUsername(user.getMail());
        try {
            if (u.isPresent()) {
                u.get().setName(user.getName());
                u.get().setSurname(user.getSurname());
                List<Child> children = new ArrayList<>();
                if (user.getChildren() != null) {
                    for (ClientChild cc : user.getChildren())
                        children.add(clientChildToChild(cc));
                }
                u.get().setChildren(children);
                u.get().setContacts(user.getContacts());
                ClientUser cu = userToClientUser(u.get(), clientRolesToRoles(user.getRoles()));
                userRepository.save(u.get());
                return cu;
            }
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        throw new ResourceNotFoundException();
    }

    /**
     * Function to delete user
     *
     * @param user user delete credential
     * @throws InternalServerErrorException
     * @throws ResourceNotFoundException
     */
    @Override
    @Transactional
    public void deleteUser(ClientUser user) {
        try {
            Optional<User> u = userRepository.findByUsername(user.getMail());
            if (u.isPresent()) {
                userRepository.delete(u.get());
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
     * Function to convert User to ClientUser
     *
     * @param p     user to convert
     * @param roles
     * @return ClientUser: converted client user
     */
    private ClientUser userToClientUser(User p, List<String> roles) {
        assert p.getChildren() != null;
        return new ClientUser(p.getUsername(), p.getName(), p.getSurname(), p.getContacts(), childListToClientChildList(p.getChildren()), p.getLines(), rolesToClientRoles(roles));
    }

    /**
     * Function to convert Roles to ClientRoles
     *
     * @param roles list of roles
     * @return list<ClientRoles>: converted client user
     */
    private List<String> rolesToClientRoles(List<String> roles) {
        return roles.stream().map((role) -> ClientRoles.valueOf((role.substring(Roles.prefix.length()))).toString()).collect(Collectors.toList());
    }

    /**
     * Function to convert ClientRoles to Roles
     *
     * @param roles list of roles
     * @return list<ClientRoles>: converted client user
     */
    private List<String> clientRolesToRoles(List<String> roles) {
        return roles.stream().map((role) -> {
            role = Roles.prefix.toString() + role;
            return role;
        }).collect(Collectors.toList());
    }

    /**
     * Function to convert ClientUser to  User
     *
     * @param p ClientUser to convert
     * @return User: converted user
     */
    private User clientUserToUser(ClientUser p) {
        assert p.getChildren() != null;
        return new User(p.getMail(), p.getName(), p.getSurname(), p.getContacts(), clientChildListToChildList(p.getChildren()), p.getLines());
    }

    /**
     * Function to convert list of Child to list of ClientChild
     *
     * @param children List of children to convert
     * @return List ClientChild: converted list
     */
    private List<ClientChild> childListToClientChildList(List<Child> children) {
        List<ClientChild> clientChildren = new ArrayList<>();
        for (Child c : children)
            clientChildren.add(childToClientChild(c));
        return clientChildren;
    }

    /**
     * Function to convert list of ClientChild to list of Child
     *
     * @param clientChildren List of children to convert
     * @return List Child: converted list
     */
    private List<Child> clientChildListToChildList(List<ClientChild> clientChildren) {
        List<Child> children = new ArrayList<>();
        for (ClientChild cc : clientChildren)
            children.add(clientChildToChild(cc));
        return children;
    }

    //----------------------------------------------###Notification###------------------------------------------------//
    /*@Transactional
    @Override
    public void insertNotification(String performerUsername, ClientUserNotification clientUserNotification, String targetUsername) {
        Optional<User> performer;
        Optional<UserCredentials> performerCredentials;
        List<Race> races;
        Optional<User> target;
        try {
            performer = userRepository.findByUsername(performerUsername);
            performerCredentials = userCredentialsRepository.findByUsername(performerUsername);
            target = userRepository.findByUsername(targetUsername);
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        //Check if performer is present
        if (!performer.isPresent() || !performerCredentials.isPresent())
            throw new ResourceNotFoundException("Performer User not found");
        //If notification isn't broadcast and the target is not in db: Throw Resource not Found
        if (!clientUserNotification.getBroadcast()) {
            if (!target.isPresent())
                throw new ResourceNotFoundException("Target User not found");
        }
        //If notification is broadcast
        else {
            //if performer isn't admin or system admin or companion: Throw BadRequest
            if (!isAdmin(performerCredentials.get().getRoles()) && !isSystemAdmin(performerCredentials.get().getRoles()) && !isCompanion(performerCredentials.get().getRoles()))
                throw new BadRequestException("Only Admin and Companions can send broadcast notifications");
            try {
                races = raceRepository.findAllByLineNameAndDirectionAndDateBetween(clientUserNotification.getBroadcastRace().getLine().getName(), clientUserNotification.getBroadcastRace().getDirection(), clientUserNotification.getBroadcastRace().getDate(), null);
            } catch (Exception e) {
                throw new InternalServerErrorException();
            }
            if (races.isEmpty())
                throw new ResourceNotFoundException("BroadcastRace not found");
            if (races.size() > 1)
                throw new InternalServerErrorException("Too many Race found");
            for (Race r : races) {
                if (!isAdminOfLine(performer.get().getLines(), performerCredentials.get().getRoles(), r.getLineName()) && !isCompanionOfRace(r.getCompanions(), performerCredentials.get().getRoles(), new Companion(performer.get(), null, null, CompanionState.AVAILABLE))) {
                    //if performer isn't a Lineadmin or or RaceCompanion: Throw BadRequest
                    throw new BadRequestException("Performer isn't a Companion of Race, a Line Admin, or SysAdmin");
                }
                clientUserNotification.setBroadcastRace(raceToClientRace(r, null));
            }
        }
        try {
            userNotificationRepository.save(clientUserNotificationToUserNotification(clientUserNotification));
            this.messagingTemplate.convertAndSendToUser(clientUserNotification.getTargetUsername(), "/queue/notifications", clientUserNotification);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }*/

    public void readNotification(String performerUsername, String notificationId) {
        Optional<User> performer;
        Optional<UserNotification> userNotification;

        try {
            performer = userRepository.findByUsername(performerUsername);
            userNotification = userNotificationRepository.findById(notificationId);
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        //Check if performer is present
        if (!performer.isPresent() || !userNotification.isPresent())
            throw new ResourceNotFoundException("Performer User or Notification not found");

        if (userNotification.get().getIsRead())
            return;

        userNotification.get().setIsRead(true);
        userNotificationRepository.save(userNotification.get());
    }

    @Override
    public Page<ClientUserNotification> getUserNotifications(int pageNumber, int pageSize, String performerUsername) {
        try {
            Optional<User> performer;
            Optional<UserCredentials> performerCredentials;

            try {
                performer = userRepository.findByUsername(performerUsername);
                performerCredentials = userCredentialsRepository.findByUsername(performerUsername);

            } catch (Exception e) {
                throw new InternalServerErrorException();
            }
            //Check if performer is present
            if (!performer.isPresent() || !performerCredentials.isPresent())
                throw new ResourceNotFoundException("Performer User not found");
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            List<ClientUserNotification> usersNotificationList = new ArrayList<>();
            Page<UserNotification> userNotifications = userNotificationRepository.findAllByTargetUsername(performerUsername, pageable);
            long totalElements = userNotifications.getTotalElements();

            for (UserNotification un : userNotifications)
                usersNotificationList.add(userNotificationToClientUserNotification(un));

            return new PageImpl<>(usersNotificationList, pageable, totalElements);
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }
//
//    @Override
//    public List<Page<ClientUserNotification>> getUserBroadcastNotifications(int pageNumber, int pageSize, String performerUsername) {
//        try {
//            Optional<User> performer;
//            Optional<UserCredentials> performerCredentials;
//
//            try {
//                performer = userRepository.findByUsername(performerUsername);
//                performerCredentials = userCredentialsRepository.findByUsername(performerUsername);
//
//            } catch (Exception e) {
//                throw new InternalServerErrorException();
//            }
//            //Check if performer is present
//            if (!performer.isPresent() || !performerCredentials.isPresent())
//                throw new ResourceNotFoundException("Performer User not found");
//
//            Pageable adminPageable = null;
//            List<ClientUserNotification> adminNotificationList = new ArrayList<>();
//            Page<UserNotification> adminNotifications;
//            long totalElements1 = 0;
//            if (isAdmin(performerCredentials.get().getRoles())) {
//
//                if (pageNumber == 1) {
//                    int oldpagesize = pageSize;
//                    pageSize = pageSize / 2;
//                    adminPageable = PageRequest.of(pageNumber, pageSize, Sort.by(SortType.get("date")));
//                    if (pageSize * 2 != oldpagesize) pageSize = pageSize + 1;
//                } else {
//                    int oldpagenumber = pageNumber;
//                    pageNumber = pageNumber / 2;
//                    adminPageable = PageRequest.of(pageNumber, pageSize, Sort.by(SortType.get("date")));
//                    if (pageNumber * 2 != oldpagenumber) pageNumber = pageNumber + 1;
//                }
//
//              //  adminNotifications = userNotificationRepository.find(performer.get().getLines(), adminPageable);
//                //totalElements1 = adminNotifications.getTotalElements();
//
////                for (UserNotification un : adminNotifications)
////                    adminNotificationList.add(userNotificationToClientUserNotification(un));
//            }
//
//            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(SortType.get("date")));
//            List<ClientUserNotification> usersNotificationList = new ArrayList<>();
//
//            Page<UserNotification> userNotifications = userNotificationRepository.findAllByBroadcastIsTrueAndBroadcastRace_CompanionsContainsAndBroadcastRace_PassengersContains(performerUsername, performerUsername, pageable);
//
//            long totalElements = userNotifications.getTotalElements();
//
//            for (UserNotification un : userNotifications)
//                usersNotificationList.add(userNotificationToClientUserNotification(un));
//
//            List<Page<ClientUserNotification>> returnlist = new ArrayList<>();
//            returnlist.add(new PageImpl<>(usersNotificationList, pageable, totalElements));
//            if (!adminNotificationList.isEmpty() && adminPageable != null)
//                returnlist.add(new PageImpl<>(adminNotificationList, adminPageable, totalElements));
//            return returnlist;
//        } catch (Exception e) {
//            throw new InternalServerErrorException();
//        }
//    }

    private ClientUserNotification userNotificationToClientUserNotification(UserNotification n) {
        return new ClientUserNotification(
                n.getId(),
                n.getPerformerUsername(),
                n.getTargetUsername(),
                n.getType(),
                n.getDate(),
                raceToClientRace(n.getReferredRace(), null),
                n.getMessage(),
                n.getIsRead()
        );
    }

    private UserNotification clientUserNotificationToUserNotification(ClientUserNotification n) {
        UserNotification notification = new UserNotification();
        notification.setPerformerUsername(n.getPerformerUsername());
        notification.setIsRead(false);
        notification.setReferredRace(n.getReferredRace() == null ? null : clientRaceToRace(n.getReferredRace()));
        notification.setMessage(n.getMessage());
        notification.setTargetUsername(n.getTargetUsername());
        notification.setDate(n.getDate());
        notification.setType(n.getType());
        return notification;
    }

    //-------------------------------------------------###Parent###---------------------------------------------------//
    //TODO controllare
    @Transactional
    @Override
    public void reserveChildren(String performerUsername, ClientRace clientRace, List<ClientPassenger> clientPassengers) {
        Optional<Race> race;
        Optional<UserCredentials> performer;
        // Take information from DB
        try {
            race = raceRepository.findRaceByDateAndLineNameAndDirection(clientRace.getDate(), clientRace.getLine().getName(), clientRace.getDirection());
            performer = userCredentialsRepository.findByUsername(performerUsername);
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        // If race or performer is not present: Throw ResourceNotFound
        if (!race.isPresent() || !performer.isPresent())
            throw new ResourceNotFoundException();

        // If performer user is not parent for children: Throw BadRequest
        for (ClientPassenger c : clientPassengers) {
            if (!c.getChildDetails().getParentId().equals(performerUsername) || !performer.get().getRoles().contains(Roles.prefix + Roles.USER))
                throw new BadRequestException();
        }
        int count = 0;
        for (Passenger p : race.get().getPassengers()) {
            for (ClientPassenger cp : clientPassengers) {
                // For each Passenger in list passed check if he is in race
                if (p.getChildDetails().getCF().equals(cp.getChildDetails().getCf())) {
                    {
                        count++;
                        race.get().getPassengers().get(race.get().getPassengers().indexOf(p)).setState(PassengerState.NULL);
                        race.get().getPassengers().get(race.get().getPassengers().indexOf(p)).setStopReserved(clientPediStopToPediStop(cp.getStopReserved()));
                        race.get().getPassengers().get(race.get().getPassengers().indexOf(p)).setReserved(true);
                    }
                }
            }
        }
        // Check if all passenger in list passed are taken else Throw BadRequest
        if (count != clientPassengers.size())
            throw new BadRequestException();
        // Update the race
        updateRace(raceToClientRace(race.get(), null), performerUsername);
    }

    @Transactional
    @Override
    public void removeChildrenFromRace(String performerUsername, ClientRace clientRace, List<ClientPassenger> clientPassengers) {
        Optional<Race> race;
        Optional<UserCredentials> performer;
        // Take information from DB
        try {
            race = raceRepository.findRaceByDateAndLineNameAndDirection(clientRace.getDate(), clientRace.getLine().getName(), clientRace.getDirection());
            performer = userCredentialsRepository.findByUsername(performerUsername);
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        // If race or performer is not present: Throw ResourceNotFound
        if (!race.isPresent() || !performer.isPresent())
            throw new ResourceNotFoundException();

        // If performer user is not parent for children: Throw BadRequest
        for (ClientPassenger c : clientPassengers) {
            if (!c.getChildDetails().getParentId().equals(performerUsername) || !performer.get().getRoles().contains(Roles.prefix + Roles.USER))
                throw new BadRequestException();
        }
        int count = 0;
        for (Passenger p : race.get().getPassengers()) {
            for (ClientPassenger cp : clientPassengers) {
                // For each Passenger in list passed check if he is in race
                if (p.getChildDetails().getCF().equals(cp.getChildDetails().getCf())) {
                    {
                        count++;
                        race.get().getPassengers().get(race.get().getPassengers().indexOf(p)).setReserved(false);
                        race.get().getPassengers().get(race.get().getPassengers().indexOf(p)).setStopReserved(null);
                    }
                }
            }
        }
        // Check if all passenger in list passed are removed else Throw BadRequest
        if (count != clientPassengers.size())
            throw new BadRequestException();
        // Update the race
        updateRace(raceToClientRace(race.get(), null), performerUsername);
    }

    // Get parent races
    @Override
    public List<ClientRace> getParentRacesBetweenDate(String performerUsername, Date fromDate, Date toDate, DirectionType direction, String lineName) {
        List<ClientRace> parentRaces;
        List<Race> races;

        try {
            if (direction == null) {
                if (fromDate == null || toDate == null)
                    races = raceRepository.findAllByLineName(lineName);
                else
                    races = raceRepository.findAllByLineNameAndDateBetween(lineName, removeTime(fromDate), midnightTime(toDate));
            } else {
                if (fromDate == null || toDate == null)
                    races = raceRepository.findAllByLineNameAndDirection(lineName, direction);
                else
                    races = raceRepository.findAllByLineNameAndDirectionAndDateBetween(lineName, direction, removeTime(fromDate), midnightTime(toDate));
            }
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }

        parentRaces = races.stream()
                .filter(race -> race.getPassengers().stream().anyMatch(passenger -> passenger.getChildDetails().getParentId().equals(performerUsername))
                ).map(race -> raceToClientRace(race, null)
                ).collect(Collectors.toList());

        return parentRaces;
    }

    // Get parent reserved races
    @Override
    public List<ClientRace> getParentReservedRacesBetweenDate(String performerUsername, Date fromDate, Date toDate, DirectionType direction, String lineName) {
        List<ClientRace> parentRaces;
        List<Race> races;

        try {
            if(lineName==null){
                if(direction==null){
                    if (fromDate == null || toDate == null)
                        races = raceRepository.findAll();
                    else
                        races = raceRepository.findAllByAndDateBetween(removeTime(fromDate), midnightTime(toDate));
                }
                else{
                    if (fromDate == null || toDate == null)
                        races = raceRepository.findAll();
                    else
                        races = raceRepository.findAllByAndDateBetween(removeTime(fromDate), midnightTime(toDate));
                }
            }else{
                if(direction==null){
                    if (fromDate == null || toDate == null)
                        races = raceRepository.findAllByLineName(lineName);
                    else
                        races = raceRepository.findAllByLineNameAndDateBetween(lineName,removeTime(fromDate), midnightTime(toDate));
                }
                else{
                    if (fromDate == null || toDate == null)
                        races = raceRepository.findAllByLineNameAndDirection(lineName, direction);
                    else
                        races = raceRepository.findAllByLineNameAndDirectionAndDateBetween(lineName, direction,removeTime(fromDate), midnightTime(toDate));
                }
            }
        }  catch (Exception e) {
            throw new InternalServerErrorException();
        }

        parentRaces = races.stream()
                .filter(race -> race.getPassengers().stream().anyMatch(passenger -> passenger.getChildDetails().getParentId().equals(performerUsername)
                        && passenger.isReserved())
                )
                .map(race -> raceToClientRace(race, null)
                ).collect(Collectors.toList());

        return parentRaces;
    }

    //---------------------------------------------------###Admin###--------------------------------------------------//

    @Override
    public List<ClientRace> getParentRacesFromDate(String performerUsername, Date date) {
        List<ClientRace> parentRaces;
        List<Race> races;


        try {

            races = raceRepository.findAllByAndDateBetween(removeTime(date), midnightTime(date));

        } catch (Exception e) {
            throw new InternalServerErrorException();
        }

        parentRaces = races.stream()
                .filter(
                        race -> race.getPassengers().stream()
                                .filter(passenger -> passenger.getChildDetails().getParentId().equals(performerUsername))
                                .count() > 0
                ).map(
                        race -> {
                            return raceToClientRace(race, null);
                        }
                ).collect(Collectors.toList());
        return parentRaces;
    }

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
            throw new ResourceNotFoundException("Line or Target doesn't exist");

        List<String> performerLines = Objects.requireNonNull(performer.get()).getLines();
        List<String> performerRoles = perfCredentials.get().getRoles();
        List<String> targetLines = Objects.requireNonNull(target.get()).getLines();
        List<String> targetRoles = targetCredentials.get().getRoles();

        // If performer isn't the System_Admin or an Admin of the selected line: Throw Unahuthorized
        if (!isAdminOfLineOrSysAdmin(performerLines, performerRoles, line))
            throw new UnauthorizedRequestException("Performer must be either SystemAdmin or Line Admin");

        // If target is the System_Admin or an Admin of the selected line: Throw BadRequest
        if (isAdminOfLineOrSysAdmin(targetLines, targetRoles, line))
            throw new BadRequestException("Target User is already Line Admin of selected Line or is System Admin");

        //If target was a normal user before this action: Add Admin in UserCredentials.roles
        if (!isAdmin(targetRoles)) {
            targetCredentials.get().getRoles().add(Roles.prefix + Roles.ADMIN);
        }

        // Adding line to User.lines
        if (targetLines == null) {
            target.get().setLines(new ArrayList<>());
            target.get().getLines().add(line);
        } else {
            if (!targetLines.contains(line))
                Objects.requireNonNull(target.get().getLines()).add(line);
        }
        // UpdateLine: Add user admin in line
        mongoLine.get().getAdmins().add(targetCredentials.get().getUsername());
        updateLine(lineToClientLine(mongoLine.get()));

        // Save results: update User modified before
        updateCredentials(userCredentialsToClientUserCredentials(targetCredentials.get()));
        updateUser(userToClientUser(target.get(), targetCredentials.get().getRoles()));
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
        Optional<User> target;
        Optional<Line> mongoLine;
        Optional<UserCredentials> perfCredentials;
        Optional<User> performer;
        Optional<UserCredentials> targetCredentials;

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
            throw new ResourceNotFoundException("Target or Line doesn't exist");


        // If performer isn't the System_Admin or an Admin of the selected line: Throw Unahuthorized
        if (!isAdminOfLineOrSysAdmin(performer.get().getLines(), perfCredentials.get().getRoles(), line))
            throw new UnauthorizedRequestException("Performer must be either SystemAdmin or Line Admin");

        // If target isn't the  Admin of the selected line: Throw BadRequest
        if (!isAdminOfLine(target.get().getLines(), targetCredentials.get().getRoles(), line))
            throw new BadRequestException("Target must be Line Admin");

        // UpdateLine: remove user admin from line
        mongoLine.get().getAdmins().remove(targetCredentials.get().getUsername());
        updateLine(lineToClientLine(mongoLine.get()));

        // Removing line from User.lines
        Objects.requireNonNull(target.get().getLines()).remove(line);

        // Since an Admin without lines can exist we choose to don't remove ADMIN role
        updateUser(userToClientUser(target.get(), targetCredentials.get().getRoles()));
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

        // If performer isn't the SysAdmin or an Admin or a Companion: Throw Unauthorized
        if (!isAdmin(performerRoles) && !isSystemAdmin(performerRoles) && !isCompanion(performerRoles))
            throw new UnauthorizedRequestException();
        // If target is already a Companion: Throw BadRequest
        if (isCompanion(targetRoles))
            throw new BadRequestException();

        targetCredentials.get().getRoles().add(Roles.prefix + Roles.COMPANION);

        // Update Credentials: update user to companion
        updateCredentials(userCredentialsToClientUserCredentials(targetCredentials.get()));

    }

    @Transactional
    @Override
    public void removeCompanion(String performerUsername, String targetUsername) {

        List<Race> races;
        Optional<UserCredentials> targetCredentials;
        Optional<User> target;
        Optional<UserCredentials> perfCredentials;
        Optional<User> performer;

        try {
            perfCredentials = userCredentialsRepository.findByUsername(performerUsername);
            performer = userRepository.findByUsername(performerUsername);
            targetCredentials = userCredentialsRepository.findByUsername(targetUsername);
            target = userRepository.findByUsername(targetUsername);
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        // If one among  performer, performerCredentials, target or targetCredentials misses: Throw ResourceNotFound
        if (!perfCredentials.isPresent() || !performer.isPresent() || !target.isPresent() || !targetCredentials.isPresent())
            throw new ResourceNotFoundException();


        String pattern = "yyyy-MM-dd'T'HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String stringDate = simpleDateFormat.format(new Date());

        try {
            Date date = simpleDateFormat.parse(stringDate);
            races = raceRepository.findAllByCompanionsAndDateGreaterThan(targetUsername, date);
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }

        List<String> performerRoles = perfCredentials.get().getRoles();
        List<String> targetRoles = targetCredentials.get().getRoles();

        // If target isn't a Companion: Throw BadRequest
        if (!isCompanion(targetRoles))
            throw new BadRequestException();
        // If performer isn't the SysAdmin or an Admin: Throw Unauthorized
        if (!isAdmin(performerRoles) && !isSystemAdmin(performerRoles) && !isCompanion(performerRoles))
            throw new UnauthorizedRequestException();

        if (!races.isEmpty()) {
            for (Race r : races) {
                Optional<Line> line;
                // take line related with race
                try {
                    line = lineRepository.findLineByName(r.getLineName());
                } catch (Exception e) {
                    throw new InternalServerErrorException();
                }
                if (!line.isPresent())
                    throw new InternalServerErrorException();
                CompanionState targetCompanionState = null;
                Companion targetcompanion = null;
                boolean lockedrace = false;
                if (!r.getRaceState().equals(RaceState.SCHEDULED))
                    lockedrace = true;

                for (Companion c : r.getCompanions()) {
                    //verify if race is locked
                    if (c.getState() == CompanionState.VALIDATED)
                        lockedrace = true;
                    //put target companion in a local variable
                    if (c.getUserDetails().getUsername().equals(targetUsername)) {
                        targetCompanionState = c.getState();
                        targetcompanion = c;
                    }
                }
                if (lockedrace)
                    continue;
                if (targetCompanionState == null || targetcompanion == null)
                    throw new InternalServerErrorException();

                //if companion is available, he's suddenly removed
                if (targetCompanionState.equals(CompanionState.AVAILABLE))
                    r.getCompanions().remove(targetcompanion);
                    //if companion is chosen, all other chosen companions become available and a notification must be sent to admin
                else if (targetCompanionState.equals(CompanionState.CHOSEN)) {
                    //Remove chosen companion
                    for (Companion c : r.getCompanions()) {
                        if (c.getState().equals(CompanionState.CHOSEN) || c.getState().equals(CompanionState.CONFIRMED)) {
                            r.getCompanions().get(r.getCompanions().indexOf(c)).setState(CompanionState.AVAILABLE);
                        }
                    }
                    /*Format content and send email to each admin
                    String content = targetUsername + ": Removed form Race: " + r.getLineName() + "/" + r.getDate().toString() + "/" + r.getDirection();
                    for (String address : line.get().getAdmins())
                        emailSenderService.sendSimpleMail(new Mail(performerUsername, address, "Companion Removed", content));
                     */
                } else if (targetCompanionState.equals(CompanionState.CONFIRMED)) {
                    //Remove chosen companion
                    for (Companion c : r.getCompanions()) {
                        String content = "You have been removed form Race: " + r.getLineName() + "/" + r.getDate().toString() + "/" + r.getDirection();
                        if (c.getState().equals(CompanionState.CONFIRMED) || c.getState().equals(CompanionState.CHOSEN)) {
                            r.getCompanions().get(r.getCompanions().indexOf(c)).setState(CompanionState.AVAILABLE);
                            //emailSenderService.sendSimpleMail(new Mail(performerUsername, c.getUserDetails().getUsername(), "Rounds Changed!", content));
                        }
                    }
                    /*Format content and send email to each admin
                    String content = targetUsername + ": Removed form Race: " + r.getLineName() + "/" + r.getDate().toString() + "/" + r.getDirection();
                    for (String address : line.get().getAdmins())
                        emailSenderService.sendSimpleMail(new Mail(performerUsername, address, "Companion Removed", content));
                    */
                }

                updateRace(raceToClientRace(r, null), performerUsername);

            }
        }

        targetCredentials.get().getRoles().remove(Roles.prefix + Roles.COMPANION);

        // Update Credentials: update user to remove companion
        updateCredentials(userCredentialsToClientUserCredentials(targetCredentials.get()));

    }

    /**
     * Function to select companion in race, this function change companion state for a race in CHOSEN
     *
     * @param performerUsername user that perform operation
     * @param clientRace        race in witch select companion
     * @param companions        list of companion chosen
     * @throws InternalServerErrorException
     * @throws ResourceNotFoundException
     * @throws BadRequestException
     * @throws UnauthorizedRequestException
     */
    @Transactional
    @Override
    public void selectCompanions(String performerUsername, ClientRace clientRace, List<String> companions) {
        Optional<UserCredentials> performerCredentials;
        Optional<User> performer;
        Optional<Race> race;
        Optional<Line> line;

        try {
            performerCredentials = userCredentialsRepository.findByUsername(performerUsername);
            race = raceRepository.findRaceByDateAndLineNameAndDirection(clientRace.getDate(), clientRace.getLine().getName(), clientRace.getDirection());
            performer = userRepository.findByUsername(performerUsername);
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        // If performer or race not found: Throw ResourceNotFound
        if (!performerCredentials.isPresent() || !race.isPresent() || !performer.isPresent())
            throw new ResourceNotFoundException();

        // if performer isn't SystemAdmin or LineAdmin: Throw UnauthorizedRequestException
        if (!isAdminOfLineOrSysAdmin(performer.get().getLines(), performerCredentials.get().getRoles(), clientRace.getLine().getName()))
            throw new UnauthorizedRequestException("Performer must be a LineAdmin or SysAdmin ");

        //If race is started or endend is not modifiable: Throw Bad Request
        if (!race.get().getRaceState().equals(RaceState.SCHEDULED))
            throw new BadRequestException("Race already Started or ended");

        try {
            line = lineRepository.findLineByName(race.get().getLineName());
        } catch (Exception e) {
            throw new InternalServerErrorException("Error during repository lookup");
        }
        if (!line.isPresent())
            throw new ResourceNotFoundException();

        // Take credentials for each companion and check if contains roles COMPANION
        for (String c : companions) {
            Optional<UserCredentials> userTemp;
            try {
                userTemp = userCredentialsRepository.findByUsername(c);
            } catch (Exception e) {
                throw new InternalServerErrorException("Error during repository lookup");
            }
            if (!userTemp.isPresent())
                throw new ResourceNotFoundException();
            if (!userTemp.get().getRoles().contains(Roles.prefix + Roles.COMPANION))
                throw new BadRequestException("Target isn't a companion");
        }

        // Select required companion from race.Companions
        List<Companion> selectedCompanions = new ArrayList<>();
        for (Companion c : race.get().getCompanions()) {
            // If a companion with the status VALIDATED is present the race is locked
            if (c.getState().equals(CompanionState.VALIDATED))
                throw new BadRequestException();
            if (companions.contains(c.getUserDetails().getUsername())) {
                // If the state of the selected companion isn't Available: Throw BadRequestException
                if (!c.getState().equals(CompanionState.AVAILABLE))
                    throw new BadRequestException();
                selectedCompanions.add(c);
            }
        }

        // If dimension differs client has requested at least one not in race companion
        if (selectedCompanions.size() != companions.size())
            throw new BadRequestException();

        // Create Map with PediStop type key and boolean value which marks a stop as covered if true
        Map<PediStop, Boolean> coverageStopsMap = new LinkedHashMap<>();
        if (race.get().getDirection().toString().equals(DirectionType.OUTWARD.toString())) {
            for (PediStop p : line.get().getOutwardStops())
                coverageStopsMap.put(p, false);
        } else
            for (PediStop p : line.get().getReturnStops())
                coverageStopsMap.put(p, false);
        for (Companion c : selectedCompanions) {

            boolean flag = false;
            for (Map.Entry<PediStop, Boolean> element : coverageStopsMap.entrySet()) {
                if (element.getKey().getName().equals(c.getInitialStop().getName()))
                    flag = true;
                if (flag)
                    coverageStopsMap.put(element.getKey(), true);
                if (element.getKey().getName().equals(c.getFinalStop().getName()))
                    break;
            }
        }

        // If map contains a false the coverage is incomplete
        if (coverageStopsMap.containsValue(false))
            throw new BadRequestException();


        // Set CompanionState to CHOSEN
        for (Companion selectedC : selectedCompanions) {
            if (!isCompanionStillAvailable(selectedC, line.get(), race.get()))
                throw new BadRequestException();
            for (Companion c : race.get().getCompanions())
                if (selectedC.getUserDetails().getName().equals(c.getUserDetails().getName()))
                    race.get().getCompanions().get(race.get().getCompanions().indexOf(c)).setState(CompanionState.CHOSEN);
        }
        updateRace(raceToClientRace(race.get(), null), performerUsername);
//        for (Companion selectedC : selectedCompanions) {
//            for (Companion c : race.get().getCompanions())
//                if (selectedC.getUserDetails().getName().equals(c.getUserDetails().getName())) {
//                    Date d = new Date();
//                    UserNotification notification = new UserNotification();
//                    notification.setPerformerUsername(performerUsername);
//                    notification.setTargetUsername(c.getUserDetails().getUsername());
//                    notification.setType(NotificationsType.COMPANION_CHOSEN);
//                    notification.setDate(d);
//                    notification.setBroadcast(false);
//                    notification.setBroadcastRace(new Race(race.get().getLineName(), race.get().getDirection(), race.get().getDate(), race.get().getCurrentStop(), race.get().getRaceState(), race.get().getPassengers(), race.get().getReachedStops(), race.get().getCompanions()));
//                    notification.setParameters(null);
//                    notification.setMessage(performerUsername + " has chosen you to accompanying the race on " + race.get().getDate().toString() + ", of line " + race.get().getLineName() + " with direction " + race.get().getDirection().toString() + ", from stop " + c.getInitialStop().getName() + " to stop " + c.getFinalStop().getName());
//                    notification.setIsRead(false);
//                    userNotificationRepository.save(notification);
//                    Optional<UserNotification> un = userNotificationRepository.findByPerformerUsernameAndTargetUsernameAndEqDate(performerUsername, c.getUserDetails().getUsername(), d);
//                    un.ifPresent(userNotification -> messagingTemplate.convertAndSendToUser(userNotification.getTargetUsername(), "/queue/notifications", userNotificationToClientUserNotification(userNotification)));
//                }
//        }
    }

    /**
     * Function to accept a companion in race, this function change companion state for a race in CHOSEN
     * NOTE: Not check coverage, coverage check is made when you valid race
     *
     * @param performerUsername user that perform operation
     * @param clientRace        race in witch select companion
     * @param companion         companion
     * @throws InternalServerErrorException
     * @throws ResourceNotFoundException
     * @throws BadRequestException
     * @throws UnauthorizedRequestException
     */
    @Transactional
    @Override
    public void acceptCompanion(String performerUsername, ClientRace clientRace, String companion) {
        Optional<UserCredentials> performerCredentials;
        Optional<User> performer;
        Optional<Race> race;
        Optional<Line> line;
        Optional<UserCredentials> companionCredential;

        try {
            performerCredentials = userCredentialsRepository.findByUsername(performerUsername);
            race = raceRepository.findRaceByDateAndLineNameAndDirection(clientRace.getDate(), clientRace.getLine().getName(), clientRace.getDirection());
            performer = userRepository.findByUsername(performerUsername);
            companionCredential = userCredentialsRepository.findByUsername(companion);
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        // If performer or race not found: Throw ResourceNotFound
        if (!performerCredentials.isPresent() || !race.isPresent() || !performer.isPresent() || !companionCredential.isPresent())
            throw new ResourceNotFoundException();

        // if performer isn't SystemAdmin or LineAdmin: Throw UnauthorizedRequestException
        if (!isAdminOfLineOrSysAdmin(performer.get().getLines(), performerCredentials.get().getRoles(), clientRace.getLine().getName()))
            throw new UnauthorizedRequestException("Performer must be a LineAdmin or SysAdmin ");

        //If race is started or endend is not modifiable: Throw Bad Request
        if (!race.get().getRaceState().equals(RaceState.SCHEDULED))
            throw new BadRequestException("Race already Started or ended");

        // Check companion if contains roles COMPANION
        if (!companionCredential.get().getRoles().contains(Roles.prefix + Roles.COMPANION))
            throw new BadRequestException("Target isn't a companion");

        try {
            line = lineRepository.findLineByName(race.get().getLineName());
        } catch (Exception e) {
            throw new InternalServerErrorException("Error during repository lookup");
        }
        if (!line.isPresent())
            throw new ResourceNotFoundException();

        Companion selectedCompanion = new Companion();
        for (Companion c : race.get().getCompanions()) {
            // If a companion with the status VALIDATED is present the race is locked
            if (c.getState().equals(CompanionState.VALIDATED))
                throw new BadRequestException();
            if (c.getUserDetails().getUsername().equals(companion)) {
                selectedCompanion = c;
                // If the state of the selected companion isn't Available: Throw BadRequestException
                if (!c.getState().equals(CompanionState.AVAILABLE))
                    throw new BadRequestException();
            }
        }

        // Set CompanionState to CHOSEN
        if (!isCompanionStillAvailable(selectedCompanion, line.get(), race.get()))
            throw new BadRequestException();

        for (Companion c1 : race.get().getCompanions())
            if (selectedCompanion.getUserDetails().getName().equals(c1.getUserDetails().getName()))
                race.get().getCompanions().get(race.get().getCompanions().indexOf(c1)).setState(CompanionState.CHOSEN);
        updateRace(raceToClientRace(race.get(), null), performerUsername);

        //Sending notification to companion
        Date date = new Date();
        UserNotification notification = new UserNotification();
        notification.setPerformerUsername(performerUsername);
        notification.setTargetUsername(companion);
        notification.setType(NotificationsType.COMPANION_CHOSEN);
        notification.setDate(date);
        notification.setReferredRace(
                new Race(
                        race.get().getLineName(),
                        race.get().getDirection(),
                        race.get().getDate(),
                        race.get().getCurrentStop(),
                        race.get().getRaceState(),
                        race.get().getPassengers(),
                        race.get().getReachedStops(),
                        race.get().getCompanions()
                )
        );
        notification.setMessage(
                "Your request for race of line " + race.get().getLineName()
                        + " in " + race.get().getDate().toString()
                        + " with direction " + race.get().getDirection().toString()
                        + " has been accepted."
        );
        notification.setIsRead(false);
        try {
            userNotificationRepository.save(notification);
            Optional<List<UserNotification>> uNotifications = userNotificationRepository.findAllByPerformerUsernameAndTargetUsernameAndEqDate(performerUsername,companion, date);
            if (uNotifications.isPresent()) {
                Optional<UserNotification> uNotification = uNotifications.get()
                        .stream()
                        .filter(n -> {
                            Race r = n.getReferredRace();
                            return n.getTargetUsername().equals(companion) && r.getLineName().equals(race.get().getLineName()) && r.getDirection().equals(race.get().getDirection()) && r.getDate().equals(race.get().getDate());
                        })
                        .findFirst();
                uNotification.ifPresent(userNotification -> this.messagingTemplate.convertAndSendToUser(userNotification.getTargetUsername(), "/queue/notifications", userNotificationToClientUserNotification(userNotification)));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InternalServerErrorException();
        }
    }

    /**
     * Function to un-accept request after having accepted it
     *
     * @param performerUsername user that perform operation
     * @param clientRace        race in witch select companion
     * @param companion         companion
     * @throws InternalServerErrorException
     * @throws ResourceNotFoundException
     * @throws BadRequestException
     * @throws UnauthorizedRequestException
     */
    @Override
    @Transactional
    public void unAcceptCompanion(String performerUsername, ClientRace clientRace, String companion) {
        Optional<UserCredentials> performerCredentials;
        Optional<Race> race;
        Optional<Line> line;
        Optional<User> performer;
        try {
            race = raceRepository.findRaceByDateAndLineNameAndDirection(clientRace.getDate(), clientRace.getLine().getName(), clientRace.getDirection());
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        // If race not found: Throw ResourceNotFound
        if (!race.isPresent())
            throw new ResourceNotFoundException();
        try {
            performerCredentials = userCredentialsRepository.findByUsername(performerUsername);
            line = lineRepository.findLineByName(race.get().getLineName());
            performer = userRepository.findByUsername(performerUsername);
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        // If performer, line not found: Throw ResourceNotFound
        if (!performerCredentials.isPresent() || !line.isPresent() || !performer.isPresent())
            throw new ResourceNotFoundException();

        // If performer isn't an Admin or LineAdmin: Throw UnauthorizedRequestException
        if (!isAdminOfLineOrSysAdmin(performer.get().getLines(), performerCredentials.get().getRoles(), clientRace.getLine().getName()))
            throw new UnauthorizedRequestException();

        //If race is started or endend is not modifiable: Throw Bad Request
        if (!race.get().getRaceState().equals(RaceState.SCHEDULED))
            throw new UnauthorizedRequestException("Race already Started or ended");

        for (Companion c : race.get().getCompanions()) {
            // If a companion with the status VALIDATED is present the race is locked
            if (c.getState().equals(CompanionState.VALIDATED))
                throw new BadRequestException("Companions already validated for this race");
            // Unselect all Chosen Companion
            if (c.getUserDetails().getUsername().equals(companion) && c.getState().equals(CompanionState.CHOSEN))
                race.get().getCompanions().get(race.get().getCompanions().indexOf(c)).setState(CompanionState.AVAILABLE);
        }
        updateRace(raceToClientRace(race.get(), null), performerUsername);

        //Sending notification to companion
        Date date = new Date();
        UserNotification notification = new UserNotification();
        notification.setPerformerUsername(performerUsername);
        notification.setTargetUsername(companion);
        notification.setType(NotificationsType.COMPANION_REMOVECHOSEN);
        notification.setDate(date);
        notification.setReferredRace(
                new Race(
                        race.get().getLineName(),
                        race.get().getDirection(),
                        race.get().getDate(),
                        race.get().getCurrentStop(),
                        race.get().getRaceState(),
                        race.get().getPassengers(),
                        race.get().getReachedStops(),
                        race.get().getCompanions()
                )
        );
        notification.setMessage(
                "Your request for race of line " + race.get().getLineName()
                        + " in " + race.get().getDate().toString()
                        + " with direction " + race.get().getDirection().toString()
                        + " has been rejected."
        );
        notification.setIsRead(false);
        try {
            userNotificationRepository.save(notification);
            Optional<List<UserNotification>> uNotifications = userNotificationRepository.findAllByPerformerUsernameAndTargetUsernameAndEqDate(performerUsername,companion, date);
            if (uNotifications.isPresent()) {
                Optional<UserNotification> uNotification = uNotifications.get()
                        .stream()
                        .filter(n -> {
                            Race r = n.getReferredRace();
                            return n.getTargetUsername().equals(companion) && r.getLineName().equals(race.get().getLineName()) && r.getDirection().equals(race.get().getDirection()) && r.getDate().equals(race.get().getDate());
                        })
                        .findFirst();
                uNotification.ifPresent(userNotification -> this.messagingTemplate.convertAndSendToUser(userNotification.getTargetUsername(), "/queue/notifications", userNotificationToClientUserNotification(userNotification)));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InternalServerErrorException();
        }
    }

    /**
     * Function to reject companion
     *
     * @param performerUsername user that perform operation
     * @param clientRace        race in witch remove companion
     * @param companion         companion
     * @throws InternalServerErrorException
     * @throws ResourceNotFoundException
     * @throws BadRequestException
     * @throws UnauthorizedRequestException
     */
    @Override
    @Transactional
    public void rejectCompanion(String performerUsername, ClientRace clientRace, String companion) {
        Optional<UserCredentials> performerCredentials;
        Optional<Race> race;
        Optional<Line> line;
        Optional<User> performer;
        try {
            race = raceRepository.findRaceByDateAndLineNameAndDirection(clientRace.getDate(), clientRace.getLine().getName(), clientRace.getDirection());
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        // If race not found: Throw ResourceNotFound
        if (!race.isPresent())
            throw new ResourceNotFoundException();
        try {
            performerCredentials = userCredentialsRepository.findByUsername(performerUsername);
            line = lineRepository.findLineByName(race.get().getLineName());
            performer = userRepository.findByUsername(performerUsername);
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        // If performer, line not found: Throw ResourceNotFound
        if (!performerCredentials.isPresent() || !line.isPresent() || !performer.isPresent())
            throw new ResourceNotFoundException();

        // If performer isn't an Admin or LineAdmin: Throw UnauthorizedRequestException
        if (!isAdminOfLineOrSysAdmin(performer.get().getLines(), performerCredentials.get().getRoles(), clientRace.getLine().getName()))
            throw new UnauthorizedRequestException();

        //If race is started or endend is not modifiable: Throw Bad Request
        if (!race.get().getRaceState().equals(RaceState.SCHEDULED))
            throw new UnauthorizedRequestException("Race already Started or ended");

        Companion com = new Companion();
        for (Companion c : race.get().getCompanions()) {
            // If a companion with the status VALIDATED is present the race is locked
            if (c.getState().equals(CompanionState.VALIDATED))
                throw new BadRequestException("Companions already validated for this race");
            // Unselect all Chosen Companion
            if (c.getUserDetails().getUsername().equals(companion) && !c.getState().equals(CompanionState.VALIDATED))
                com = c;
        }
        race.get().getCompanions().remove(com);

        updateRace(raceToClientRace(race.get(), null), performerUsername);

        //Sending notification to companion
        Date date = new Date();
        UserNotification notification = new UserNotification();
        notification.setPerformerUsername(performerUsername);
        notification.setTargetUsername(companion);
        notification.setType(NotificationsType.COMPANION_REMOVECHOSEN);
        notification.setDate(date);
        notification.setReferredRace(
                new Race(
                        race.get().getLineName(),
                        race.get().getDirection(),
                        race.get().getDate(),
                        race.get().getCurrentStop(),
                        race.get().getRaceState(),
                        race.get().getPassengers(),
                        race.get().getReachedStops(),
                        race.get().getCompanions()
                )
        );
        notification.setMessage(
                "Your request for race of line " + race.get().getLineName()
                        + " in " + race.get().getDate().toString()
                        + " with direction " + race.get().getDirection().toString()
                        + " has been rejected."
        );
        notification.setIsRead(false);
        try {
            userNotificationRepository.save(notification);
            Optional<List<UserNotification>> uNotifications = userNotificationRepository.findAllByPerformerUsernameAndTargetUsernameAndEqDate(performerUsername,companion, date);
            if (uNotifications.isPresent()) {
                Optional<UserNotification> uNotification = uNotifications.get()
                        .stream()
                        .filter(n -> {
                            Race r = n.getReferredRace();
                            return n.getTargetUsername().equals(companion) && r.getLineName().equals(race.get().getLineName()) && r.getDirection().equals(race.get().getDirection()) && r.getDate().equals(race.get().getDate());
                        })
                        .findFirst();
                uNotification.ifPresent(userNotification -> this.messagingTemplate.convertAndSendToUser(userNotification.getTargetUsername(), "/queue/notifications", userNotificationToClientUserNotification(userNotification)));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InternalServerErrorException();
        }
    }

    // unused
    @Transactional
    @Override
    public void validateCompanions(String performerUsername, ClientRace clientRace) {
        Optional<UserCredentials> performerCredentials;
        Optional<Race> race;
        Optional<User> performer;
        try {
            race = raceRepository.findRaceByDateAndLineNameAndDirection(clientRace.getDate(), clientRace.getLine().getName(), clientRace.getDirection());
            performerCredentials = userCredentialsRepository.findByUsername(performerUsername);
            performer = userRepository.findByUsername(performerUsername);
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        // If performer, race not found: Throw ResourceNotFound
        if (!performerCredentials.isPresent() || !performer.isPresent() || !race.isPresent())
            throw new ResourceNotFoundException();

        // If performer isn't an Admin or LineAdmin: Throw UnauthorizedRequestException
        if (!isAdminOfLineOrSysAdmin(performer.get().getLines(), performerCredentials.get().getRoles(), clientRace.getLine().getName()))
            throw new UnauthorizedRequestException();

        //If race is started or endend is not modifiable: Throw Bad Request
        if (!race.get().getRaceState().equals(RaceState.SCHEDULED))
            throw new BadRequestException("Race already Started or ended");

        for (Companion c : race.get().getCompanions()) {
            if (c.getState().equals(CompanionState.VALIDATED))
                throw new BadRequestException("Race locked");
            //if a companion is in chosen state, at least one of the chosen companion has not confirmed
            if (c.getState().equals(CompanionState.CHOSEN))
                throw new BadRequestException("One or more companion/s has/have not confirmed yet ");
            // Valid Companion: set state to VALIDATED
            if (c.getState().equals(CompanionState.CONFIRMED))
                race.get().getCompanions().get(race.get().getCompanions().indexOf(c)).setState(CompanionState.VALIDATED);
        }
        updateRace(raceToClientRace(race.get(), null), performerUsername);
    }

    //Valid race. check if all stop are coverage by confirmed companion
    @Transactional
    @Override
    public void validateRace(String performerUsername, ClientRace clientRace) {
        Optional<UserCredentials> performerCredentials;
        Optional<Race> race;
        Optional<User> performer;
        Optional<Line> line;
        try {
            race = raceRepository.findRaceByDateAndLineNameAndDirection(clientRace.getDate(), clientRace.getLine().getName(), clientRace.getDirection());
            performerCredentials = userCredentialsRepository.findByUsername(performerUsername);
            performer = userRepository.findByUsername(performerUsername);
            line = lineRepository.findLineByName(clientRace.getLine().getName());
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        // If performer, race not found: Throw ResourceNotFound
        if (!performerCredentials.isPresent() || !performer.isPresent() || !race.isPresent())
            throw new ResourceNotFoundException();

        // If performer isn't an Admin or LineAdmin: Throw UnauthorizedRequestException
        if (!isAdminOfLineOrSysAdmin(performer.get().getLines(), performerCredentials.get().getRoles(), clientRace.getLine().getName()))
            throw new UnauthorizedRequestException();

        //If race is started or endend is not modifiable: Throw Bad Request
        if (!race.get().getRaceState().equals(RaceState.SCHEDULED))
            throw new BadRequestException("Race already Started or ended");

        // Check if nothing is VALIDATED
        for (Companion c : race.get().getCompanions()) {
            // If a companion with the status VALIDATED is present the race is locked
            if (c.getState().equals(CompanionState.VALIDATED))
                throw new BadRequestException();
        }

        // Create Map with PediStop type key and boolean value which marks a stop as covered if true
        Map<PediStop, Boolean> coverageStopsMap = new LinkedHashMap<>();
        if (race.get().getDirection().toString().equals(DirectionType.OUTWARD.toString())) {
            for (PediStop p : line.get().getOutwardStops())
                coverageStopsMap.put(p, false);
        } else
            for (PediStop p : line.get().getReturnStops())
                coverageStopsMap.put(p, false);
        for (Companion c : race.get().getCompanions()) {
            if (c.getState().equals(CompanionState.CONFIRMED)) {
                boolean flag = false;
                for (Map.Entry<PediStop, Boolean> element : coverageStopsMap.entrySet()) {
                    if (element.getKey().getName().equals(c.getInitialStop().getName()))
                        flag = true;
                    if (flag)
                        coverageStopsMap.put(element.getKey(), true);
                    if (element.getKey().getName().equals(c.getFinalStop().getName()))
                        break;
                }
            }
        }

        // If map contains a false the coverage is incomplete
        if (coverageStopsMap.containsValue(false))
            throw new BadRequestException();

        for (Companion c : race.get().getCompanions()) {
            if (c.getState().equals(CompanionState.VALIDATED))
                throw new BadRequestException("Race locked");
            // Valid Companion: set state to VALIDATED
            if (c.getState().equals(CompanionState.CONFIRMED))
                race.get().getCompanions().get(race.get().getCompanions().indexOf(c)).setState(CompanionState.VALIDATED);
        }
        race.get().setRaceState(RaceState.VALIDATED);
        updateRace(raceToClientRace(race.get(), null), performerUsername);

        Date date = new Date();
        Collection<String> parents = race.get().getPassengers()
                .stream()
                .filter(passenger -> passenger.isReserved())
                .map(passenger -> passenger.getChildDetails().getParentId())
                .distinct()
                .collect(Collectors.toList());
        Collection<String> companions = race.get().getCompanions()
                .stream()
                .filter(companion -> companion.getState().equals(CompanionState.VALIDATED))
                .map(companion -> companion.getUserDetails().getUsername())
                .distinct()
                .collect(Collectors.toList());

        Collection<String> destinations = new ArrayList<>();
        destinations.addAll(parents);
        destinations.addAll(companions);

        destinations.forEach(user -> {
            UserNotification notification = new UserNotification();
            notification.setPerformerUsername(performerUsername);
            notification.setTargetUsername(user);
            notification.setType(NotificationsType.RACE_CONFIRMED);
            notification.setDate(date);
            notification.setReferredRace(
                    new Race(
                            race.get().getLineName(),
                            race.get().getDirection(),
                            race.get().getDate(),
                            race.get().getCurrentStop(),
                            race.get().getRaceState(),
                            race.get().getPassengers(),
                            race.get().getReachedStops(),
                            race.get().getCompanions()
                    )
            );
            notification.setIsRead(false);
            notification.setMessage(
                    "The race in " + race.get().getDate().toString() +
                            ", in Line " + race.get().getLineName() +
                            ", of direction " + race.get().getDirection().toString() +
                            " has been confirmed.");
            notification.setId(null);
            try {
                userNotificationRepository.save(notification);
                Optional<List<UserNotification>> uNotifications = userNotificationRepository.findAllByPerformerUsernameAndTargetUsernameAndEqDate(performerUsername, user, date);
                if (uNotifications.isPresent()) {
                    Optional<UserNotification> uNotification = uNotifications.get()
                            .stream()
                            .filter(n -> {
                                Race r = n.getReferredRace();
                                return n.getTargetUsername().equals(user) && r.getLineName().equals(race.get().getLineName()) && r.getDirection().equals(race.get().getDirection()) && r.getDate().equals(race.get().getDate());
                            })
                            .findFirst();
                    uNotification.ifPresent(userNotification -> this.messagingTemplate.convertAndSendToUser(user, "/queue/notifications", userNotificationToClientUserNotification(userNotification)));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new InternalServerErrorException();
            }
        });
    }

    /**
     * Function to update race
     *
     * @param clientRace:        race to update
     * @param performerUsername: performer username
     * @throws BadRequestException
     * @throws InternalServerErrorException
     * @throws ResourceNotFoundException
     * @throws UnauthorizedRequestException
     */
    @Override
    @Transactional
    public void updateRace(ClientRace clientRace, String performerUsername) {
        Optional<UserCredentials> performerCredentials;
        Optional<Line> targetLine;
        Optional<Race> targetRace;
        Optional<User> performer;
        try {
            performerCredentials = userCredentialsRepository.findByUsername(performerUsername);
            performer = userRepository.findByUsername(performerUsername);
            targetLine = lineRepository.findLineByName(clientRace.getLine().getName());
            targetRace = raceRepository.findRaceByDateAndLineNameAndDirection(clientRace.getDate(), clientRace.getLine().getName(), clientRace.getDirection());
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        if (!targetLine.isPresent() || !performer.isPresent() || !performerCredentials.isPresent() || !targetRace.isPresent())
            throw new ResourceNotFoundException();

        Race race = clientRaceToRace(clientRace);
        try {
            raceRepository.delete(targetRace.get());
            raceRepository.save(race);
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }

    /**
     * Function to delete race
     *
     * @param clientRace:        race to delete
     * @param performerUsername: performer username
     * @throws BadRequestException
     * @throws InternalServerErrorException
     * @throws ResourceNotFoundException
     * @throws UnauthorizedRequestException
     */
    @Transactional
    @Override
    public void deleteRace(ClientRace clientRace, String performerUsername) {
        Optional<UserCredentials> performerCredentials;
        Optional<Line> targetLine;
        Optional<Race> race;
        Optional<User> performer;

        try {
            performerCredentials = userCredentialsRepository.findByUsername(performerUsername);
            performer = userRepository.findByUsername(performerUsername);
            targetLine = lineRepository.findLineByName(clientRace.getLine().getName());
            race = raceRepository.findRaceByDateAndLineNameAndDirection(clientRace.getDate(), clientRace.getLine().getName(), clientRace.getDirection());
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        if (!targetLine.isPresent() || !performer.isPresent() || !performerCredentials.isPresent() || !race.isPresent())
            throw new ResourceNotFoundException();

        if (!isAdminOfLineOrSysAdmin(performer.get().getLines(), performerCredentials.get().getRoles(), targetLine.get().getName()))
            throw new UnauthorizedRequestException();
        if (!race.get().getRaceState().equals(RaceState.SCHEDULED))
            throw new BadRequestException("Cannot remove started or ended races");
        try {
            raceRepository.delete(race.get());
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        Date date = new Date();
        Collection<String> parents = race.get().getPassengers()
                .stream()
                .filter(passenger -> passenger.isReserved())
                .map(passenger -> passenger.getChildDetails().getParentId())
                .distinct()
                .collect(Collectors.toList());
        Collection<String> companions = race.get().getCompanions()
                .stream()
                .map(companion -> companion.getUserDetails().getUsername())
                .distinct()
                .collect(Collectors.toList());

        Collection<String> destinations = new ArrayList<>();
        destinations.addAll(parents);
        destinations.addAll(companions);

        destinations.forEach(user -> {
            UserNotification notification = new UserNotification();
            notification.setPerformerUsername(performerUsername);
            notification.setTargetUsername(user);
            notification.setType(NotificationsType.RACE_DELETED);
            notification.setDate(date);
            notification.setReferredRace(
                    new Race(
                            race.get().getLineName(),
                            race.get().getDirection(),
                            race.get().getDate(),
                            race.get().getCurrentStop(),
                            race.get().getRaceState(),
                            race.get().getPassengers(),
                            race.get().getReachedStops(),
                            race.get().getCompanions()
                    )
            );
            notification.setIsRead(false);
            notification.setMessage(
                    "The race in " + race.get().getDate().toString() +
                            ", in Line " + race.get().getLineName() +
                            ", of direction " + race.get().getDirection().toString() +
                            " has been deleted.");
            notification.setId(null);
            try {
                userNotificationRepository.save(notification);
                Optional<List<UserNotification>> uNotifications = userNotificationRepository.findAllByPerformerUsernameAndTargetUsernameAndEqDate(performerUsername,user, date);
                if (uNotifications.isPresent()) {
                    Optional<UserNotification> uNotification = uNotifications.get()
                            .stream()
                            .filter(n -> {
                                Race r = n.getReferredRace();
                                return n.getTargetUsername().equals(user) && r.getLineName().equals(race.get().getLineName()) && r.getDirection().equals(race.get().getDirection()) && r.getDate().equals(race.get().getDate());
                            })
                            .findFirst();
                    uNotification.ifPresent(userNotification -> this.messagingTemplate.convertAndSendToUser(user, "/queue/notifications", userNotificationToClientUserNotification(userNotification)));
                }
            } catch (Exception ex) {
                throw new InternalServerErrorException();
            }
        });
    }

    private boolean isCompanionStillAvailable(Companion companion, Line line, Race race) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = formatter.parse(formatter.format(race.getDate()));
        } catch (ParseException e) {
            throw new InternalServerErrorException();
        }
        List<Race> otherRaces = raceRepository.findAllByCompanionsAndEqDate(companion.getUserDetails().getUsername(), date);
        for (Race r : otherRaces) {
            if (!r.getCompanions().contains(companion))
                throw new InternalServerErrorException();
            if (!r.getCompanions().get(r.getCompanions().indexOf(companion)).getState().equals(CompanionState.AVAILABLE))
                return false;
        }
        return true;
    }

    /**
     * Function to check if user is SysAdmin or is Admin of current line
     *
     * @param lines list of managed lines
     * @param roles list of roles
     * @param line  line to check
     * @return boolean: true if manage current line  or is SysAdmin, otherwise return false
     */
    private boolean isAdminOfLineOrSysAdmin(List<String> lines, List<String> roles, String line) {
        if (isSystemAdmin(roles)) return true;
        return isAdminOfLine(lines, roles, line);
    }

    /**
     * Function to check if user is Admin of current line
     *
     * @param lines list of managed lines
     * @param roles list of roles
     * @param line  line to check
     * @return boolean: true if manage current line, otherwise return false and if is not Admin
     */
    private boolean isAdminOfLine(List<String> lines, List<String> roles, String line) {
        if (!isAdmin(roles)) return false;
        return lines.contains(line);
    }

    /**
     * Function to check if user is Admin
     *
     * @param roles list of roles
     * @return boolean: true if user is Admin, otherwise return false
     */
    private boolean isAdmin(List<String> roles) {
        return roles.contains(Roles.prefix + Roles.ADMIN);
    }

    //------------------------------------------------###Companion###-------------------------------------------------//

    /**
     * Function to check if user is SysAdmin
     *
     * @param roles list of roles
     * @return boolean: true if user is SysAdmin, otherwise return false
     */
    private boolean isSystemAdmin(List<String> roles) {
        return roles.contains(Roles.prefix + Roles.SYSTEM_ADMIN);
    }

    @Transactional
    @Override
    public void giveCompanionAvailability(String performerUsername, CompanionRequest companionRequest) {
        Optional<Race> race;
        Optional<UserCredentials> targetCredentials;
        Optional<User> targetUser;
        Optional<UserCredentials> performerCredentials;
        Optional<Line> line;
        try {
            race = raceRepository.findRaceByDateAndLineNameAndDirection(companionRequest.getDate(), companionRequest.getLineName(), companionRequest.getDirection());
            line = lineRepository.findLineByName(companionRequest.getLineName());
            targetCredentials = userCredentialsRepository.findByUsername(performerUsername);
            targetUser = userRepository.findByUsername(performerUsername);
            performerCredentials = userCredentialsRepository.findByUsername(performerUsername);
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        // If race, line, targetCredentials or PerformCredentials aren't in db: Throw ResourceNotFound
        if (!race.isPresent() || !targetCredentials.isPresent() || !targetUser.isPresent() || !performerCredentials.isPresent() || !line.isPresent())
            throw new ResourceNotFoundException();
        // If the performer isn't the SysAdmin or the Companion: Throw UnauthorizedRequest
        if (!isCompanion(performerCredentials.get().getRoles()) && !isSystemAdmin(performerCredentials.get().getRoles()))
            throw new UnauthorizedRequestException();
        // If the target isn't a companion: Throw BadRequest
        if (!isCompanion(targetCredentials.get().getRoles()))
            throw new BadRequestException("Target isn't a Companion");
        // If race companions already contains companion: Throw BadRequest
        if (isCompanionOfRace(race.get().getCompanions(), targetCredentials.get().getRoles(), performerUsername))
            throw new BadRequestException("Companion already in race");
        // If the performer is a Companion: check if he is stating availability for himself
        if (isCompanion(performerCredentials.get().getRoles())) {
            if (!performerCredentials.get().getUsername().equals(targetCredentials.get().getUsername()))
                throw new UnauthorizedRequestException();
        }

        // If stops is null: Throw Bad Request
        if (companionRequest.getFinalStop() == null || companionRequest.getInitialStop() == null)
            throw new BadRequestException();

        //If race is started or endend is not modifiable: Throw Bad Request
        if (!race.get().getRaceState().equals(RaceState.SCHEDULED))
            throw new BadRequestException("Race already Started or ended");
        // Can't be available for locked race
        for (Companion c : race.get().getCompanions()) {
            if (c.getState().equals(CompanionState.VALIDATED))
                throw new BadRequestException();
        }

        boolean initialStopFlag = false;
        boolean finalStopFlag = false;
        if (race.get().getDirection().equals(DirectionType.OUTWARD)) {
            for (PediStop stop : line.get().getOutwardStops()) {
                if (stop.getName().equals(companionRequest.getFinalStop().getName()))
                    finalStopFlag = true;
                if (stop.getName().equals(companionRequest.getInitialStop().getName())) {
                    if (finalStopFlag)
                        throw new BadRequestException("FinalStop preceding InitialStop");
                    initialStopFlag = true;

                }
            }
        } else {
            for (PediStop stop : line.get().getReturnStops()) {
                if (stop.getName().equals(companionRequest.getFinalStop().getName()))
                    finalStopFlag = true;
                if (stop.getName().equals(companionRequest.getInitialStop().getName())) {
                    if (finalStopFlag)
                        throw new BadRequestException("FinalStop preceding InitialStop");
                    initialStopFlag = true;
                }
            }
        }
        if (!finalStopFlag || !initialStopFlag)
            throw new BadRequestException("Stop not Found");

        ClientCompanion c = new ClientCompanion(userToClientUser(targetUser.get(), targetCredentials.get().getRoles()), companionRequest.getInitialStop(), companionRequest.getFinalStop(), CompanionState.AVAILABLE);
        race.get().getCompanions().add(clientCompanionToCompanion(c));
        ClientRace finalRace = raceToClientRace(race.get(), null);

        updateRace(finalRace, performerUsername);
    }

    @Transactional
    @Override
    public void removeCompanionAvailability(String performerUsername, CompanionRequest companionRequest) {
        Optional<Race> race;
        Optional<Line> line;
        Optional<UserCredentials> targetCredentials;
        Optional<UserCredentials> performerCredentials;

        try {
            race = raceRepository.findRaceByDateAndLineNameAndDirection(companionRequest.getDate(), companionRequest.getLineName(), companionRequest.getDirection());
            line = lineRepository.findLineByName(companionRequest.getLineName());
            targetCredentials = userCredentialsRepository.findByUsername(performerUsername);
            performerCredentials = userCredentialsRepository.findByUsername(performerUsername);
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        // If race, targetCredentials or PerformCredentials aren't in db: Throw ResourceNotFound
        if (!line.isPresent() || !race.isPresent() || !targetCredentials.isPresent() || !performerCredentials.isPresent())
            throw new ResourceNotFoundException();
        // If the performer isn't the SysAdmin or the Companion: Throw UnauthorizedRequest
        if (!isCompanion(performerCredentials.get().getRoles()) && !isSystemAdmin(performerCredentials.get().getRoles()))
            throw new UnauthorizedRequestException();
        // If the target isn't a companion: Throw BadRequest
        if (!isCompanion(targetCredentials.get().getRoles()))
            throw new UnauthorizedRequestException();
        // If race companions doesn't contain companion: Throw BadRequest
        if (!isCompanionOfRace(race.get().getCompanions(), targetCredentials.get().getRoles(), performerUsername))
            throw new UnauthorizedRequestException();

        // If the performer is a Companion: check if he is removing availability for himself
        if (isCompanion(performerCredentials.get().getRoles())) {
            if (!performerCredentials.get().getUsername().equals(targetCredentials.get().getUsername()))
                throw new UnauthorizedRequestException();
        }
        //If race is started or endend is not modifiable: Throw Bad Request
        if (!race.get().getRaceState().equals(RaceState.SCHEDULED))
            throw new BadRequestException("Race already Started or ended");
        Companion targetCompanion = null;
        for (Companion c : race.get().getCompanions()) {
            // Can't remove availability for locked race
            if (c.getState().equals(CompanionState.VALIDATED))
                throw new BadRequestException();
            // Can't remove avialability if the target companion isn't in state AVAILABLE or CHOSEN
            if (c.getUserDetails().getUsername().equals(performerUsername)) {
                if (!c.getState().equals(CompanionState.AVAILABLE) && !c.getState().equals(CompanionState.CHOSEN))
                    throw new BadRequestException();
                targetCompanion = c;
            }
        }
        if (targetCompanion == null)
            throw new InternalServerErrorException("Unexpected Error");
        race.get().getCompanions().remove(targetCompanion);
        ClientRace finalRace = raceToClientRace(race.get(), null);

        updateRace(finalRace, performerUsername);
    }

    /**
     * Function to confirm rounds in Race, this function change companion state for a race in CONFIRMED
     *
     * @param performerUsername user that perform operation
     * @param companionRequest  companionRequest to confirm availability. admin chosen
     * @throws InternalServerErrorException
     * @throws ResourceNotFoundException
     * @throws BadRequestException
     * @throws UnauthorizedRequestException
     */
    @Override
    @Transactional
    public void confirmChosenState(String performerUsername, CompanionRequest companionRequest) {
        Optional<UserCredentials> performerCredentials;
        Optional<Race> race;
        Optional<User> performer;
        Optional<Line> line;
        try {
            performerCredentials = userCredentialsRepository.findByUsername(performerUsername);
            performer = userRepository.findByUsername(performerUsername);
            line = lineRepository.findLineByName(companionRequest.getLineName());
            race = raceRepository.findRaceByDateAndLineNameAndDirection(companionRequest.getDate(), companionRequest.getLineName(), companionRequest.getDirection());
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        // If performer, race not found: Throw ResourceNotFound
        if (!performerCredentials.isPresent() || !race.isPresent() || !performer.isPresent() || !line.isPresent())
            throw new ResourceNotFoundException();
        //If race is started or endend is not modifiable: Throw Bad Request
        if (!race.get().getRaceState().equals(RaceState.SCHEDULED))
            throw new BadRequestException("Race already Started or ended");
        Companion perfCompanion = null;
        for (Companion c : race.get().getCompanions()) {
            if (c.getUserDetails().getUsername().equals(performerUsername))
                perfCompanion = c;
            if (c.getState().equals(CompanionState.VALIDATED))
                throw new BadRequestException("Race locked");
        }
        if (perfCompanion == null)
            throw new UnauthorizedRequestException();

        // Set state CONFIRMED
        for (Companion c : race.get().getCompanions()) {
            if (c.getUserDetails().getUsername().equals(performerUsername))
                // If state is not CHOSEN: Throw BadRequest
                if (c.getState().equals(CompanionState.CHOSEN))
                    race.get().getCompanions().get(race.get().getCompanions().indexOf(c)).setState(CompanionState.CONFIRMED);
                else
                    throw new BadRequestException("Companion not in CHOSEN state");
        }
        updateRace(raceToClientRace(race.get(), null), performerUsername);
    }


    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


    //+++++++++++++++++++++++++++++++Companion Controller Methods+++++++++++++++++++++++++++++++

    @Transactional
    @Override
    public void updateCompanionAvailability(String performerUsername, CompanionRequest companionRequest) {
        Optional<Race> race;
        Optional<UserCredentials> targetCredentials;
        Optional<User> targetUser;
        Optional<UserCredentials> performerCredentials;
        Optional<Line> line;
        try {
            race = raceRepository.findRaceByDateAndLineNameAndDirection(companionRequest.getDate(), companionRequest.getLineName(), companionRequest.getDirection());
            line = lineRepository.findLineByName(companionRequest.getLineName());
            targetCredentials = userCredentialsRepository.findByUsername(companionRequest.getUsername());
            targetUser = userRepository.findByUsername(companionRequest.getUsername());
            performerCredentials = userCredentialsRepository.findByUsername(performerUsername);
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        // If race, line, targetCredentials or PerformCredentials aren't in db: Throw ResourceNotFound
        if (!race.isPresent() || !targetCredentials.isPresent() || !targetUser.isPresent() || !performerCredentials.isPresent() || !line.isPresent())
            throw new ResourceNotFoundException();
        // If the performer isn't the SysAdmin or the Companion: Throw UnauthorizedRequest
        if (!isCompanion(performerCredentials.get().getRoles()) && !isSystemAdmin(performerCredentials.get().getRoles()))
            throw new UnauthorizedRequestException();
        // If the target isn't a companion: Throw BadRequest
        if (!isCompanion(targetCredentials.get().getRoles()))
            throw new BadRequestException("Target isn't a Companion");
        // If race companions already contains companion: Throw BadRequest
        if (isCompanionOfRace(race.get().getCompanions(), targetCredentials.get().getRoles(), companionRequest.getUsername()))
            throw new BadRequestException("Companion already in race");
        // If the performer is a Companion: check if he is stating availability for himself
        if (isCompanion(performerCredentials.get().getRoles())) {
            if (!performerCredentials.get().getUsername().equals(targetCredentials.get().getUsername()))
                throw new UnauthorizedRequestException();
        }

        // If stops is null: Throw Bad Request
        if (companionRequest.getFinalStop() == null || companionRequest.getInitialStop() == null)
            throw new BadRequestException();

        //If race is started or endend is not modifiable: Throw Bad Request
        if (!race.get().getRaceState().equals(RaceState.SCHEDULED))
            throw new BadRequestException("Race already Started or ended");
        // Can't be available for locked race
        for (Companion c : race.get().getCompanions()) {
            if (c.getState().equals(CompanionState.VALIDATED))
                throw new BadRequestException();
        }

        boolean initialStopFlag = false;
        boolean finalStopFlag = false;
        if (race.get().getDirection().equals(DirectionType.OUTWARD)) {
            for (PediStop stop : line.get().getOutwardStops()) {
                if (stop.getName().equals(companionRequest.getFinalStop().getName()))
                    finalStopFlag = true;
                if (stop.getName().equals(companionRequest.getInitialStop().getName())) {
                    if (finalStopFlag)
                        throw new BadRequestException("FinalStop preceding InitialStop");
                    initialStopFlag = true;
                }
            }
        } else {
            for (PediStop stop : line.get().getReturnStops()) {
                if (stop.getName().equals(companionRequest.getFinalStop().getName()))
                    finalStopFlag = true;
                if (stop.getName().equals(companionRequest.getInitialStop().getName())) {
                    if (finalStopFlag)
                        throw new BadRequestException("FinalStop preceding InitialStop");
                    initialStopFlag = true;
                }
            }
        }
        if (!finalStopFlag || !initialStopFlag)
            throw new BadRequestException("Stop not Found");

        ClientCompanion c = new ClientCompanion(userToClientUser(targetUser.get(), targetCredentials.get().getRoles()), companionRequest.getInitialStop(), companionRequest.getFinalStop(), CompanionState.AVAILABLE);
        race.get().getCompanions().add(clientCompanionToCompanion(c));
        ClientRace finalRace = raceToClientRace(race.get(), null);

        updateRace(finalRace, performerUsername);
    }

    @Transactional
    @Override
    public void takeChildren(String performerUsername, String lineName, Date date, DirectionType direction, List<ClientPassenger> clientPassengers, String stopName) {
        Optional<Race> race;
        Optional<UserCredentials> performer;
        Optional<Line> line;
        Optional<PediStop> stop;
        List<Passenger> newTakenPassengers = new ArrayList<>();
        // Take information from DB
        try {
            line = lineRepository.findLineByName(lineName);
            race = raceRepository.findRaceByDateAndLineNameAndDirection(date, lineName, direction);
            performer = userCredentialsRepository.findByUsername(performerUsername);
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        // If race or performer is not present: Throw ResourceNotFound
        if (!line.isPresent() || !race.isPresent() || !performer.isPresent())
            throw new ResourceNotFoundException();

        //Getting stop reference
        if (direction == DirectionType.OUTWARD)
            stop = line.get().getOutwardStops().stream().filter(s -> s.getName().equals(stopName)).findFirst();
        else
            stop = line.get().getReturnStops().stream().filter(s -> s.getName().equals(stopName)).findFirst();

        // If the stop is not present: Throw ResourceNotFound
        if (!stop.isPresent())
            throw new ResourceNotFoundException();

        //If race isn't in started state is not modifiable: Throw Bad Request
        if (!race.get().getRaceState().equals(RaceState.STARTED))
            throw new BadRequestException("Race not Started yet");

        // If performer user is not one of selected companion for this race or the stopName isn't in his route: Throw BadRequest
        boolean companionfound = false;
        for (Companion c : race.get().getCompanions()) {
            if (c.getUserDetails().getUsername().equals(performerUsername)) {
                companionfound = true;
                if (!isSelectedCompanionOfRace(race.get().getCompanions(), performer.get().getRoles(), c))
                    throw new BadRequestException("Performer isn't one of the validated Companion");
                if (!isStopInCompanionRoute(race.get(), line.get(), c, stopName))
                    throw new BadRequestException("Performer isn't the Companion in charge for this stopName");
            }
        }
        if (!companionfound)
            throw new BadRequestException("Performer is not a companion of the race");


        // Check if all passengers in request body are registered in the line
        Set<String> registeredPassengersInLine = race.get().getPassengers().stream().map(p -> p.getChildDetails().getCF()).collect(Collectors.toSet());
        int count = (int) clientPassengers.stream().filter(p -> registeredPassengersInLine.contains(p.getChildDetails().getCf())).count();
        if (count != clientPassengers.size())
            throw new BadRequestException("One or more specified children are not registered in this line.");

        for (Passenger p : race.get().getPassengers()) {
            for (ClientPassenger cp : clientPassengers) {
                if (p.getChildDetails().getCF().equals(cp.getChildDetails().getCf())) {
                    {
                        if (p.getStopTaken() != null && p.getStopTaken().getName() != null && !p.getStopTaken().getName().equals(stopName))
                            throw new BadRequestException(p.getChildDetails().getName() + " " + p.getChildDetails().getSurname() + " has already been taken in another stop.");
                        if (p.getState().equals(PassengerState.DELIVERED))
                            throw new BadRequestException(p.getChildDetails().getName() + " " + p.getChildDetails().getSurname() + " has already been delivered.");
                        if (!p.getState().equals(PassengerState.TAKEN)) {
                            p.setState(PassengerState.TAKEN);
                            p.setStopTaken(stop.get());
                            newTakenPassengers.add(p);
                        }
                    }
                }
            }
        }

        // Update the race
        updateRace(raceToClientRace(race.get(), null), performerUsername);

        //Send
        newTakenPassengers
                .stream()
                .forEach(p -> {
                    Date d = new Date();
                    UserNotification notification = new UserNotification();
                    notification.setPerformerUsername(performerUsername);
                    notification.setTargetUsername(p.getChildDetails().getParentId());
                    notification.setType(NotificationsType.CHILD_TAKEN);
                    notification.setDate(d);
                    notification.setReferredRace(
                            new Race(
                                    race.get().getLineName(),
                                    race.get().getDirection(),
                                    race.get().getDate(),
                                    race.get().getCurrentStop(),
                                    race.get().getRaceState(),
                                    race.get().getPassengers(),
                                    race.get().getReachedStops(),
                                    race.get().getCompanions()
                            )
                    );
                    notification.setMessage(p.getChildDetails().getName() + " has been taken by the Pedibus in line " + race.get().getLineName() + " at stop " + p.getStopTaken().getName());
                    notification.setIsRead(false);
                    try {
                        userNotificationRepository.save(notification);

                        Optional<List<UserNotification>> uNotifications = userNotificationRepository.findAllByPerformerUsernameAndTargetUsernameAndEqDate(performerUsername, p.getChildDetails().getParentId(), d);
                        if (uNotifications.isPresent()) {
                            Optional<UserNotification> uNotification = uNotifications.get()
                                    .stream()
                                    .filter(n -> {
                                        Race r = n.getReferredRace();
                                        return n.getTargetUsername().equals(p.getChildDetails().getParentId()) && r.getLineName().equals(race.get().getLineName()) && r.getDirection().equals(race.get().getDirection()) && r.getDate().equals(race.get().getDate());
                                    })
                                    .findFirst();
                            uNotification.ifPresent(userNotification -> this.messagingTemplate.convertAndSendToUser(userNotification.getTargetUsername(), "/queue/notifications", userNotificationToClientUserNotification(userNotification)));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        throw new InternalServerErrorException();
                    }
                });
    }

    @Transactional
    @Override
    public void deliverChildren(String performerUsername, String lineName, Date date, DirectionType direction, List<ClientPassenger> clientPassengers, String stopName) {
        Optional<Race> race;
        Optional<Line> line;
        Optional<UserCredentials> performer;
        Optional<PediStop> stop;
        List<Passenger> newDeliveredPassengers = new ArrayList<>();
        // Take information from DB
        try {
            line = lineRepository.findLineByName(lineName);
            race = raceRepository.findRaceByDateAndLineNameAndDirection(date, lineName, direction);
            performer = userCredentialsRepository.findByUsername(performerUsername);
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        // If race or performer is not present: Throw ResourceNotFound
        if (!race.isPresent() || !performer.isPresent())
            throw new ResourceNotFoundException();

        //Getting stop reference
        if (direction == DirectionType.OUTWARD)
            stop = line.get().getOutwardStops().stream().filter(s -> s.getName().equals(stopName)).findFirst();
        else
            stop = line.get().getReturnStops().stream().filter(s -> s.getName().equals(stopName)).findFirst();

        // If the stop is not present: Throw ResourceNotFound
        if (!stop.isPresent())
            throw new ResourceNotFoundException();

        //If race isn't in started state is not modifiable: Throw Bad Request
        if (!race.get().getRaceState().equals(RaceState.STARTED))
            throw new BadRequestException("Race not Started yet");

        // If performer user is not companion for this race or the stop isn't in his route: Throw BadRequest
        for (Companion c : race.get().getCompanions()) {
            if (c.getUserDetails().getName().equals(performerUsername)) {
                if (!isSelectedCompanionOfRace(race.get().getCompanions(), performer.get().getRoles(), c))
                    throw new BadRequestException("Performer isn't one of the validated Companion");
                if (!isStopInCompanionRoute(race.get(), line.get(), c, stopName)) ;
                throw new BadRequestException("Performer isn't the Companion in charge for this stop");
            }
        }

        // Check if all passengers in request body are registered in the line
        Set<String> registeredPassengersInLine = race.get().getPassengers().stream().map(p -> p.getChildDetails().getCF()).collect(Collectors.toSet());
        int count = (int) clientPassengers.stream().filter(p -> registeredPassengersInLine.contains(p.getChildDetails().getCf())).count();
        if (count != clientPassengers.size())
            throw new BadRequestException("One or more specified children are not registered in this line.");


        for (Passenger p : race.get().getPassengers()) {
            for (ClientPassenger cp : clientPassengers) {
                // For each Passenger in list passed check if is in race
                if (p.getChildDetails().getCF().equals(cp.getChildDetails().getCf())) {
                    {
                        if (p.getStopDelivered() != null && p.getStopDelivered().getName() != null && !p.getStopDelivered().getName().equals(stopName))
                            throw new BadRequestException(p.getChildDetails().getName() + " " + p.getChildDetails().getSurname() + " has already been delivered.");
                        if (!p.getState().equals(PassengerState.TAKEN) && !p.getState().equals(PassengerState.DELIVERED))
                            throw new BadRequestException(p.getChildDetails().getName() + " " + p.getChildDetails().getSurname() + " has not been taken.");

                        if (!p.getState().equals(PassengerState.DELIVERED)) {
                            p.setState(PassengerState.DELIVERED);
                            p.setStopDelivered(stop.get());
                            newDeliveredPassengers.add(p);
                        }
                    }
                }
            }
        }

        // Update the race
        updateRace(raceToClientRace(race.get(), null), performerUsername);

        //Send Notification
        newDeliveredPassengers.stream()
                .forEach(p -> {
                    Date d = new Date();
                    UserNotification notification = new UserNotification();
                    notification.setPerformerUsername(performerUsername);
                    notification.setTargetUsername(p.getChildDetails().getParentId());
                    notification.setType(NotificationsType.CHILD_DELIVERED);
                    notification.setDate(d);
                    notification.setReferredRace(
                            new Race(
                                    race.get().getLineName(),
                                    race.get().getDirection(),
                                    race.get().getDate(),
                                    race.get().getCurrentStop(),
                                    race.get().getRaceState(),
                                    race.get().getPassengers(),
                                    race.get().getReachedStops(),
                                    race.get().getCompanions()
                            )
                    );
                    notification.setMessage(p.getChildDetails().getName() + " has been delivered by the Pedibus in line " + race.get().getLineName() + " at stop " + p.getStopDelivered().getName());
                    notification.setIsRead(false);
                    try {
                        userNotificationRepository.save(notification);
                        Optional<List<UserNotification>> uNotifications = userNotificationRepository.findAllByPerformerUsernameAndTargetUsernameAndEqDate(performerUsername, p.getChildDetails().getParentId(), d);
                        if (uNotifications.isPresent()) {
                            Optional<UserNotification> uNotification = uNotifications.get()
                                    .stream()
                                    .filter(n -> {
                                        Race r = n.getReferredRace();
                                        return n.getTargetUsername().equals(p.getChildDetails().getParentId()) && r.getLineName().equals(race.get().getLineName()) && r.getDirection().equals(race.get().getDirection()) && r.getDate().equals(race.get().getDate());
                                    })
                                    .findFirst();
                            uNotification.ifPresent(userNotification -> this.messagingTemplate.convertAndSendToUser(p.getChildDetails().getParentId(), "/queue/notifications", userNotificationToClientUserNotification(userNotification)));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        throw new InternalServerErrorException();
                    }
                });
    }

    @Transactional
    @Override
    public void absentChildren(String performerUsername, String lineName, Date date, DirectionType direction, List<ClientPassenger> clientPassengers) {
        Optional<Race> race;
        Optional<UserCredentials> performer;
        List<Passenger> newAbsentPassengers = new ArrayList<>();
        // Take information from DB
        try {
            race = raceRepository.findRaceByDateAndLineNameAndDirection(date, lineName, direction);
            performer = userCredentialsRepository.findByUsername(performerUsername);
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        // If race or performer is not present: Throw ResourceNotFound
        if (!race.isPresent() || !performer.isPresent())
            throw new ResourceNotFoundException();
        //If race isn't in started state is not modifiable: Throw Bad Request
        if (!race.get().getRaceState().equals(RaceState.STARTED))
            throw new BadRequestException("Race not Started yet");
        // If performer user is not select companion for this race : Throw BadRequest
        for (Companion c : race.get().getCompanions()) {
            if (c.getUserDetails().getName().equals(performerUsername))
                if (!isSelectedCompanionOfRace(race.get().getCompanions(), performer.get().getRoles(), c))
                    throw new BadRequestException();
        }


        // Check if all passengers in request body are registered in the line
        Set<String> registeredPassengersInLine = race.get().getPassengers().stream().map(p -> p.getChildDetails().getCF()).collect(Collectors.toSet());
        int count = (int) clientPassengers.stream().filter(p -> registeredPassengersInLine.contains(p.getChildDetails().getCf())).count();
        if (count != clientPassengers.size())
            throw new BadRequestException("One or more specified children are not registered in this line.");

        for (Passenger p : race.get().getPassengers()) {
            for (ClientPassenger cp : clientPassengers) {
                if (p.getChildDetails().getCF().equals(cp.getChildDetails().getCf())) {
                    {
                        // If state is not NULL : Throw BadRequest
                        if (p.getState().equals(PassengerState.TAKEN)) {
                            if (p.getStopTaken() != null)
                                p.setStopTaken(null);
                        }

                        if (p.getState().equals(PassengerState.DELIVERED))
                            throw new BadRequestException(p.getChildDetails().getName() + " " + p.getChildDetails().getSurname() + " has already been delivered.");

                        if (!p.getState().equals(PassengerState.ABSENT)) {
                            p.setState(PassengerState.ABSENT);
                            newAbsentPassengers.add(p);
                        }
                    }
                }
            }
        }

        // Update the race
        updateRace(raceToClientRace(race.get(), null), performerUsername);

        //Send Notification
        newAbsentPassengers.stream().forEach(p -> {
            Date d = new Date();
            UserNotification notification = new UserNotification();
            notification.setPerformerUsername(performerUsername);
            notification.setTargetUsername(p.getChildDetails().getParentId());
            notification.setType(NotificationsType.CHILD_ABSENT);
            notification.setDate(d);
            notification.setReferredRace(
                    new Race(
                            race.get().getLineName(),
                            race.get().getDirection(),
                            race.get().getDate(),
                            race.get().getCurrentStop(),
                            race.get().getRaceState(),
                            race.get().getPassengers(),
                            race.get().getReachedStops(),
                            race.get().getCompanions()
                    )
            );
            notification.setMessage(p.getChildDetails().getName() + " is absent in line " + race.get().getLineName() + " at stop " + p.getStopReserved().getName());
            notification.setIsRead(false);
            try {
                userNotificationRepository.save(notification);

                Optional<List<UserNotification>> uNotifications = userNotificationRepository.findAllByPerformerUsernameAndTargetUsernameAndEqDate(performerUsername, p.getChildDetails().getParentId(), d);

                if (uNotifications.isPresent()) {
                    Optional<UserNotification> uNotification = uNotifications.get()
                            .stream()
                            .filter(n -> {
                                Race r = n.getReferredRace();
                                return n.getTargetUsername().equals(p.getChildDetails().getParentId()) && r.getLineName().equals(race.get().getLineName()) && r.getDirection().equals(race.get().getDirection()) && r.getDate().equals(race.get().getDate());
                            })
                            .findFirst();
                    uNotification.ifPresent(userNotification -> this.messagingTemplate.convertAndSendToUser(userNotification.getTargetUsername(), "/queue/notifications", userNotificationToClientUserNotification(userNotification)));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new InternalServerErrorException();
            }
        });
    }

    @Transactional
    @Override
    public void startRace(String performerUsername, String lineName, Date date, DirectionType direction) {
        Optional<Race> race;
        Optional<Line> line;
        Optional<UserCredentials> performer;

        // Take information from DB
        try {
            line = lineRepository.findLineByName(lineName);
            race = raceRepository.findRaceByDateAndLineNameAndDirection(date, lineName, direction);
            performer = userCredentialsRepository.findByUsername(performerUsername);
        } catch (Exception e) {
            throw new InternalServerErrorException("Error in repository lookup");
        }
//        Date today = Calendar.getInstance().getTime();
//        if (!today.before(date))
//            throw new BadRequestException("Race cannot be started before its start time");
        // If race or performer is not present: Throw ResourceNotFound
        if (!line.isPresent() || !race.isPresent() || !performer.isPresent())
            throw new ResourceNotFoundException();
        //If race isn't in null state cannot be started: Throw Bad Request
        if (!race.get().getRaceState().equals(RaceState.VALIDATED))
            throw new BadRequestException("Race already started");
        // If performer user is not select companion for this race : Throw BadRequest
        for (Companion c : race.get().getCompanions()) {
            if (c.getUserDetails().getName().equals(performerUsername)) {
                if (!isSelectedCompanionOfRace(race.get().getCompanions(), performer.get().getRoles(), c))
                    throw new BadRequestException("Performer isn't a Validated Companion");

                if (race.get().getDirection().equals(DirectionType.OUTWARD)) {
                    if (!c.getInitialStop().getName().equals(line.get().getOutwardStops().get(0)))
                        throw new BadRequestException("Performer isn't a Companion selected for the first stop");
                } else {
                    if (!c.getInitialStop().getName().equals(line.get().getReturnStops().get(0)))
                        throw new BadRequestException("Performer isn't a Companion selected for the first stop");
                }
            }
        }
        if(race.get().getRaceState().equals(RaceState.STARTED) || race.get().getRaceState().equals(RaceState.ENDED))
            return;

        race.get().setRaceState(RaceState.STARTED);
        updateRace(raceToClientRace(race.get(), null), performerUsername);

        Date d = new Date();
        UserNotification notification = new UserNotification();
        notification.setPerformerUsername(performerUsername);
        notification.setTargetUsername(null);
        notification.setType(NotificationsType.RACE_STARTED);
        notification.setDate(d);
        notification.setReferredRace(
                new Race(
                        race.get().getLineName(),
                        race.get().getDirection(),
                        race.get().getDate(),
                        race.get().getCurrentStop(),
                        race.get().getRaceState(),
                        race.get().getPassengers(),
                        race.get().getReachedStops(),
                        race.get().getCompanions()
                )
        );
        notification.setIsRead(false);
        notification.setMessage(
                "The race in " + race.get().getDate().toString() +
                        ", in Line " + race.get().getLineName() +
                        ", of direction " + race.get().getDirection().toString() +
                        " has started.");
        notification.setId(null);
        this.messagingTemplate.convertAndSend("/topic/notifications/" + race.get().getLineName() + "/" + dateToISO(race.get().getDate()) + "/" + race.get().getDirection(), userNotificationToClientUserNotification(notification));
    }

    @Transactional
    @Override
    public void stopReached(String performerUsername, String lineName, Date date, DirectionType direction, String stopName) {
        Optional<Race> race;
        Optional<Line> line;
        Optional<UserCredentials> performer;
        Optional<PediStop> stop;

        // Take information from DB
        try {
            line = lineRepository.findLineByName(lineName);
            race = raceRepository.findRaceByDateAndLineNameAndDirection(date, lineName, direction);
            performer = userCredentialsRepository.findByUsername(performerUsername);
        } catch (Exception e) {
            throw new InternalServerErrorException("Error in repository lookup");
        }

//        Date today = Calendar.getInstance().getTime();
//        if (!today.before(date))
//            throw new BadRequestException("Stop cannot be reached before race start time");
        // If race or performer is not present: Throw ResourceNotFound
        if (!line.isPresent() || !race.isPresent() || !performer.isPresent())
            throw new ResourceNotFoundException();

        //Getting stop reference
        if (direction == DirectionType.OUTWARD)
            stop = line.get().getOutwardStops().stream().filter(s -> s.getName().equals(stopName)).findFirst();
        else
            stop = line.get().getReturnStops().stream().filter(s -> s.getName().equals(stopName)).findFirst();

        // If the stop is not present: Throw ResourceNotFound
        if (!stop.isPresent())
            throw new ResourceNotFoundException();

        //If race isn't in started state stop cannot be reached: Throw Bad Request
        if (!race.get().getRaceState().equals(RaceState.STARTED))
            throw new BadRequestException("Race must be started");
        if (!line.get().getOutwardStops().contains(stop.get()) && !line.get().getReturnStops().contains(stop.get()))
            throw new BadRequestException("Stop not in Race");
        // If performer user is not select companion for this race : Throw BadRequest
        for (Companion c : race.get().getCompanions()) {
            if (c.getUserDetails().getName().equals(performerUsername)) {
                if (!isSelectedCompanionOfRace(race.get().getCompanions(), performer.get().getRoles(), c))
                    throw new BadRequestException("Performer isn't a Validated Companion");

                if (race.get().getDirection().equals(DirectionType.OUTWARD)) {
                    boolean initFlag = false;
                    boolean stopFlag = false;
                    boolean found = false;
                    for (PediStop p : line.get().getOutwardStops()) {
                        if (stopFlag) break;

                        if (c.getInitialStop().getName().equals(p.getName())) initFlag = true;

                        if (!initFlag) continue;

                        if (c.getFinalStop().getName().equals(p.getName())) stopFlag = true;
                        if (p.getName().equals(stopName)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        throw new BadRequestException("Performer isn't a Companion selected for this stop");
                    }
                } else {
                    boolean initFlag = false;
                    boolean stopFlag = false;
                    boolean found = false;
                    for (PediStop p : line.get().getReturnStops()) {
                        if (stopFlag) break;

                        if (c.getInitialStop().getName().equals(p.getName())) initFlag = true;

                        if (!initFlag) continue;

                        if (c.getFinalStop().getName().equals(p.getName())) stopFlag = true;
                        if (p.getName().equals(stopName)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        throw new BadRequestException("Performer isn't a Companion selected for this stop");
                    }
                }
            }
        }

        if (race.get().getReachedStops().stream().filter(s -> s.getStopName().equals(stopName)).findAny().orElse(null) == null) {
            race.get().setCurrentStop(stop.get());
            race.get().getReachedStops().add(new ReachedStop(stopName, new Date().getTime() - race.get().getDate().getTime(), -1));
            raceRepository.save(race.get());
        } else //the stop has already been reached
            return;

        Date d = new Date();
        UserNotification notification = new UserNotification();
        notification.setPerformerUsername(performerUsername);
        notification.setTargetUsername(null);
        notification.setType(NotificationsType.STOP_REACHED);
        notification.setDate(d);
        notification.setReferredRace(
                new Race(
                        race.get().getLineName(),
                        race.get().getDirection(),
                        race.get().getDate(),
                        race.get().getCurrentStop(),
                        race.get().getRaceState(),
                        race.get().getPassengers(),
                        race.get().getReachedStops(),
                        race.get().getCompanions()
                )
        );
        notification.setIsRead(false);
        notification.setMessage(null);
        notification.setId(null);
        this.messagingTemplate.convertAndSend("/topic/notifications/" + race.get().getLineName() + "/" + dateToISO(race.get().getDate()) + "/" + race.get().getDirection(), userNotificationToClientUserNotification(notification));
    }

    @Transactional
    @Override
    public void stopLeft(String performerUsername, String lineName, Date date, DirectionType direction, String stopName) {
        Optional<Race> race;
        Optional<Line> line;
        Optional<UserCredentials> performer;
        Optional<PediStop> stop;

        // Take information from DB
        try {
            line = lineRepository.findLineByName(lineName);
            race = raceRepository.findRaceByDateAndLineNameAndDirection(date, lineName, direction);
            performer = userCredentialsRepository.findByUsername(performerUsername);
        } catch (Exception e) {
            throw new InternalServerErrorException("Error in repository lookup");
        }

//        Date today = Calendar.getInstance().getTime();
//        if (!today.before(date))
//            throw new BadRequestException("Stop cannot be reached before race start time");

        // If race or performer is not present: Throw ResourceNotFound
        if (!line.isPresent() || !race.isPresent() || !performer.isPresent())
            throw new ResourceNotFoundException();

        //Getting stop reference
        if (direction == DirectionType.OUTWARD)
            stop = line.get().getOutwardStops().stream().filter(s -> s.getName().equals(stopName)).findFirst();
        else
            stop = line.get().getReturnStops().stream().filter(s -> s.getName().equals(stopName)).findFirst();

        // If the stop is not present: Throw ResourceNotFound
        if (!stop.isPresent())
            throw new ResourceNotFoundException();

        //If race isn't in started state stop cannot be reached: Throw Bad Request
        if (!race.get().getRaceState().equals(RaceState.STARTED))
            throw new BadRequestException("Race must be started");

        if (!line.get().getOutwardStops().contains(stop.get()) && !line.get().getReturnStops().contains(stop.get()))
            throw new BadRequestException("Stop not in Race");

        // If performer user is not select companion for this race : Throw BadRequest
        for (Companion c : race.get().getCompanions()) {
            if (c.getUserDetails().getName().equals(performerUsername)) {
                if (!isSelectedCompanionOfRace(race.get().getCompanions(), performer.get().getRoles(), c))
                    throw new BadRequestException("Performer isn't a Validated Companion");

                if (race.get().getDirection().equals(DirectionType.OUTWARD)) {
                    boolean initFlag = false;
                    boolean stopFlag = false;
                    boolean found = false;
                    for (PediStop p : line.get().getOutwardStops()) {
                        if (stopFlag) break;

                        if (c.getInitialStop().getName().equals(p.getName())) initFlag = true;

                        if (!initFlag) continue;

                        if (c.getFinalStop().getName().equals(p.getName())) stopFlag = true;
                        if (p.getName().equals(stopName)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        throw new BadRequestException("Performer isn't a Companion selected for this stop");
                    }
                } else {
                    boolean initFlag = false;
                    boolean stopFlag = false;
                    boolean found = false;
                    for (PediStop p : line.get().getReturnStops()) {
                        if (stopFlag) break;

                        if (c.getInitialStop().getName().equals(p.getName())) initFlag = true;

                        if (!initFlag) continue;

                        if (c.getFinalStop().getName().equals(p.getName())) stopFlag = true;
                        if (p.getName().equals(stopName)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        throw new BadRequestException("Performer isn't a Companion selected for this stop");
                    }
                }
            }
        }

        ReachedStop reachedStop = race.get().getReachedStops().stream()
                .filter(s -> s.getStopName().equals(stopName))
                .findFirst().orElse(null);

        if (reachedStop != null)
            reachedStop.setDepartureDelay(new Date().getTime() - race.get().getDate().getTime());

        else
            throw new BadRequestException("The stop is not yet reached");

        raceRepository.save(race.get());

        Date d = new Date();
        UserNotification notification = new UserNotification();
        notification.setPerformerUsername(performerUsername);
        notification.setTargetUsername(null);
        notification.setType(NotificationsType.STOP_LEFT);
        notification.setDate(d);
        notification.setReferredRace(
                new Race(
                        race.get().getLineName(),
                        race.get().getDirection(),
                        race.get().getDate(),
                        race.get().getCurrentStop(),
                        race.get().getRaceState(),
                        race.get().getPassengers(),
                        race.get().getReachedStops(),
                        race.get().getCompanions()
                )
        );
        notification.setIsRead(false);
        notification.setMessage(null);
        notification.setId(null);
        this.messagingTemplate.convertAndSend("/topic/notifications/" + race.get().getLineName() + "/" + dateToISO(race.get().getDate()) + "/" + race.get().getDirection(), userNotificationToClientUserNotification(notification));
    }

    @Override
    public List<ClientRace> getCompanionRacesFromDate(String performerUsername, Date date) {
        List<ClientRace> companionRaces;
        List<Race> races;
        Optional<User> performer;
        Optional<UserCredentials> performerCredentials;

        try {
            performer = userRepository.findByUsername(performerUsername);
            races = raceRepository.findAllByAndDateBetween(removeTime(date), midnightTime(date));
            performerCredentials = userCredentialsRepository.findByUsername(performerUsername);
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        if (!performer.isPresent() || !performerCredentials.isPresent())
            throw new ResourceNotFoundException();
        if (!isCompanion(performerCredentials.get().getRoles()))
            throw new BadRequestException();

        companionRaces = races.stream()
                .filter(
                        race -> race.getCompanions().stream()
                                .filter(companion -> companion.getUserDetails().getUsername().equals(performerUsername))
                                .filter(companion -> companion.getState().equals(CompanionState.VALIDATED))
                                .count() > 0
                ).map(
                        race -> {
                            Optional<Line> line = lineRepository.findLineByName(race.getLineName());
                            if (!line.isPresent())
                                throw new BadRequestException();
                            Companion me = race.getCompanions().stream().filter(companion -> companion.getUserDetails().getUsername().equals(performerUsername)).findFirst().orElse(null);
                            List<ClientReachedStop> clientReachedStops = new ArrayList<>();
                            ClientPediStop currentStop = null;
                            if (race.getReachedStops() != null)
                                for (ReachedStop reachedStop : race.getReachedStops())
                                    clientReachedStops.add(new ClientReachedStop(reachedStop.getStopName(), reachedStop.getArrivalDelay(), reachedStop.getDepartureDelay()));
                            if (race.getCurrentStop() != null)
                                currentStop = pediStopToClientPediStop(race.getCurrentStop());
                            if (me != null) {
                                return new ClientRace(lineToClientLine(line.get()),
                                        race.getDirection(),
                                        race.getDate(),
                                        currentStop,
                                        race.getRaceState(),
                                        passengersToClientPassengers(race.getPassengers()),
                                        clientReachedStops,
                                        companionsToClientCompanions(race.getCompanions()),
                                        companionToClientCompanion(me, performerCredentials.get().getRoles()));
                            }
                            return null;
                        }
                ).collect(Collectors.toList());
        return companionRaces;
    }

    @Transactional
    @Override
    public void endRace(String performerUsername, String lineName, Date date, DirectionType direction) {
        Optional<Race> race;
        Optional<Line> line;
        Optional<UserCredentials> performer;
        // Take information from DB
        try {
            race = raceRepository.findRaceByDateAndLineNameAndDirection(date, lineName, direction);
            line = lineRepository.findLineByName(lineName);
            performer = userCredentialsRepository.findByUsername(performerUsername);
        } catch (Exception e) {
            throw new InternalServerErrorException("Error in repository lookup");
        }
        // If race or performer is not present: Throw ResourceNotFound
        if (!race.isPresent() || !performer.isPresent())
            throw new ResourceNotFoundException();
        //If race isn't in started state cannot be ended: Throw Bad Request
        if (!race.get().getRaceState().equals(RaceState.STARTED))
            throw new BadRequestException("Race not started yet or already ended");
        // If performer user is not select companion for this race : Throw BadRequest
        for (Companion c : race.get().getCompanions()) {
            if (c.getUserDetails().getName().equals(performerUsername)) {
                if (!isSelectedCompanionOfRace(race.get().getCompanions(), performer.get().getRoles(), c))
                    throw new BadRequestException("Performer isn't a Validated Companion");
                if (race.get().getDirection().equals(DirectionType.OUTWARD)) {
                    if (!c.getFinalStop().getName().equals(line.get().getOutwardStops().get(line.get().getOutwardStops().size())))
                        throw new BadRequestException("Performer isn't a Companion selected for the last stop");
                } else {
                    if (!c.getFinalStop().getName().equals(line.get().getOutwardStops().get(line.get().getOutwardStops().size())))
                        throw new BadRequestException("Performer isn't a Companion selected for the last stop");
                }
            }
        }

        if(race.get().getRaceState().equals(RaceState.ENDED))
            return;

        race.get().setRaceState(RaceState.ENDED);
        updateRace(raceToClientRace(race.get(), null), performerUsername);

        Date d = new Date();
        UserNotification notification = new UserNotification();
        notification.setPerformerUsername(performerUsername);
        notification.setTargetUsername(null);
        notification.setType(NotificationsType.RACE_ENDED);
        notification.setDate(d);
        notification.setReferredRace(
                new Race(
                        race.get().getLineName(),
                        race.get().getDirection(),
                        race.get().getDate(),
                        race.get().getCurrentStop(),
                        race.get().getRaceState(),
                        race.get().getPassengers(),
                        race.get().getReachedStops(),
                        race.get().getCompanions()
                )
        );
        notification.setIsRead(false);
        notification.setMessage(
                "The race in " + race.get().getDate().toString() +
                        ", in Line " + race.get().getLineName() +
                        ", of direction " + race.get().getDirection().toString() +
                        " has ended.");
        notification.setId(null);
        this.messagingTemplate.convertAndSend("/topic/notifications/" + race.get().getLineName() + "/" + dateToISO(race.get().getDate()) + "/" + race.get().getDirection(), userNotificationToClientUserNotification(notification));
    }

    private boolean isCompanionOfRace(List<Companion> companions, List<String> roles, Companion companion) {
        if (!isCompanion(roles)) return false;
        for (Companion c : companions) {
            if (c.getUserDetails().getUsername().equals(companion.getUserDetails().getUsername()))
                return true;
        }
        return false;
    }

    private boolean isCompanionOfRace(List<Companion> companions, List<String> roles, String username) {
        if (!isCompanion(roles)) return false;
        for (Companion c : companions) {
            if (c.getUserDetails().getUsername().equals(username))
                return true;
        }
        return false;
    }

    private boolean isSelectedCompanionOfRace(List<Companion> companions, List<String> roles, Companion companion) {
        if (!isCompanionOfRace(companions, roles, companion)) return false;
        for (Companion c : companions) {
            if (c.getUserDetails().getUsername().equals(companion.getUserDetails().getUsername())) {
                if (c.getState().equals(CompanionState.VALIDATED)) return true;
                else break;
            }
        }
        return false;
    }

    private boolean isStopInCompanionRoute(Race race, Line line, Companion c, String stopName) {
        AtomicBoolean initialStopFound = new AtomicBoolean(false);
        if (race.getDirection().equals(DirectionType.OUTWARD)) {
            for (PediStop ps : line.getOutwardStops()) {
                //cycle until initial stop is found
                if (!initialStopFound.get() && ps.getName().equals(c.getInitialStop().getName()))
                    initialStopFound.set(true);
                //if we get this point we are checking stops between Initial and Final for current companion
                if (initialStopFound.get() && ps.getName().equals(stopName))
                    return true;
                if (ps.getName().equals(c.getFinalStop().getName()))
                    return false;
            }
        } else {
            for (PediStop ps : line.getReturnStops()) {
                //cycle until initial stop is found
                if (!initialStopFound.get() && ps.getName().equals(c.getInitialStop().getName()))
                    initialStopFound.set(true);

                //if we get this point we are checking stops between Initial and Final for current companion
                if (initialStopFound.get() && ps.getName().equals(stopName))
                    return true;
                if (ps.getName().equals(c.getFinalStop().getName()))
                    return false;
            }
        }
        return false;
    }

    private boolean isCompanion(List<String> roles) {
        return roles.contains(Roles.prefix + Roles.COMPANION);
    }

    private Companion clientCompanionToCompanion(ClientCompanion clientCompanion) {
        return new Companion(clientUserToUser(clientCompanion.getUserDetails()), clientPediStopToPediStop(clientCompanion.getInitialStop()), clientPediStopToPediStop(clientCompanion.getFinalStop()), clientCompanion.getState());
    }

    private ClientCompanion companionToClientCompanion(Companion companion, List<String> roles) {
        return new ClientCompanion(userToClientUser(companion.getUserDetails(), roles), pediStopToClientPediStop(companion.getInitialStop()), pediStopToClientPediStop(companion.getFinalStop()), companion.getState());
    }

    private List<ClientCompanion> companionsToClientCompanions(List<Companion> companions) {
        List<ClientCompanion> clientCompanions = new ArrayList<>();
        for (Companion companion : companions) {
            Optional<UserCredentials> credentials = userCredentialsRepository.findByUsername(companion.getUserDetails().getUsername());
            clientCompanions.add(companionToClientCompanion(companion, credentials.get().getRoles()));
        }
        return clientCompanions;
    }

    //---------------------------------------------------###Line###---------------------------------------------------//

    private List<Companion> clientCompanionsToCompanions(List<ClientCompanion> clientCompanions) {
        List<Companion> companions = new ArrayList<>();
        for (ClientCompanion companion : clientCompanions) {
            companions.add(clientCompanionToCompanion(companion));
        }
        return companions;
    }

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
            if (lineRepository.findLineByName(line.getName()).isPresent()) {
                throw new BadRequestException();
            }
            ArrayList<PediStop> outStops = new ArrayList<>();
            for (JsonPediStop p : line.getOutwardStops())
                outStops.add(new PediStop(p.getName(), p.getLongitude(), p.getLatitude(), p.getDelayInMillis()));
            ArrayList<PediStop> retStops = new ArrayList<>();
            for (JsonPediStop p : line.getReturnStops())
                retStops.add(new PediStop(p.getName(), p.getLongitude(), p.getLatitude(), p.getDelayInMillis()));

            for (String a : line.getAdmins()) {
                Optional<UserCredentials> res = userCredentialsRepository.findByUsername(a);
                if (!res.isPresent()) {
                    //TODO: da fare.... ma dobbiamo aspettare che confermi la mail prima di fare qualcosa?
                    subscribeAdminAndSendMail(a, Arrays.asList(Roles.prefix + Roles.USER, Roles.prefix + Roles.ADMIN));
                } else if (!res.get().getRoles().contains(Roles.prefix + Roles.ADMIN)) {
                    res.get().getRoles().add(Roles.prefix + Roles.ADMIN);
                    userCredentialsRepository.save(res.get());
                }
            }
            Line l = new Line(line.getName(), outStops, retStops, line.getAdmins());
            lineRepository.save(l);

        } catch (BadRequestException e) {
            //TO-DO check unique index Exception
            throw new BadRequestException();
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
    public ClientLine updateLine(ClientLine line) {
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
            return lineToClientLine(temp.get());
        } catch (Exception e) {
            //TO-DO check unique index Exception
            throw new InternalServerErrorException(e);
        }
    }

    //TODO: non sono convinto di farlo qua!!
    private void subscribeAdminAndSendMail(String username, List<String> roles) {
        //TODO: implementa insert user (admin)
        ClientUserCredentials clientUserCredentials = null;
        Token token = null;
        try {
            clientUserCredentials = insertCredentials(username, "", clientRolesToRoles(roles), false);
            token = new Token(clientUserCredentials.getUsername(), ScopeToken.CONFIRM);
            insertToken(token);
            Mail mail = new Mail();
            mail.setFrom(EmailConfiguration.FROM);
            mail.setTo(clientUserCredentials.getUsername());
            mail.setSubject("Complete Registration for Pedibus!");
            mail.setContent("To confirm your account, please click here :" + EmailConfiguration.BASE_URL + "/confirm/" + token.getToken());

            //link print in console not send with email for test
            System.out.println(mail);
            emailSenderService.sendSimpleMail(mail);
        } catch (Exception e) {
            deleteCredentials(clientUserCredentials);
            tokenRepository.delete(token);
            throw new InternalServerErrorException();
        }
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
                lines.add(lineToClientLine(lineMongo));
            return lines;
        } catch (Exception e) {
            throw new InternalServerErrorException(e);
        }
    }

    /**
     * Function to get single line by name in DB as ClientLine
     *
     * @param line_name selected line name
     * @return ClientLine
     * @throws InternalServerErrorException
     */
    @Override
    public ClientLine getLinebyName(String line_name) {
        try {
            Optional<Line> line = lineRepository.findLineByName(line_name);
            if (!line.isPresent())
                throw new ResourceNotFoundException();
            ClientLine clientLine = lineToClientLine(line.get());
            return clientLine;
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
        Optional<Line> l;
        List<Race> childScheduledRaces = new ArrayList<>();

        try {
            childScheduledRaces = raceRepository.findAllByLineNameAndRaceStateIs(lineName, RaceState.SCHEDULED);
            l = lineRepository.findLineByName(lineName);
        } catch (Exception e) {
            throw new InternalServerErrorException(e);
        }

        if (!l.isPresent())
            throw new ResourceNotFoundException();

        if (!roles.contains(Roles.prefix + Roles.SYSTEM_ADMIN)) {
            if (!l.get().getAdmins().contains(UserID))
                throw new UnauthorizedRequestException("Line Admins only can perform this operation");
        }

        Child c = new Child(child.getName(), child.getSurname(), child.getCf(), child.getParentId(), null);

        if (!l.get().getSubscribedChildren().contains(c)) {
            for (Race r : childScheduledRaces) {
                for (Passenger p : r.getPassengers())
                    if (p.getChildDetails().getCF().equals(child.getCf())) {
                        throw new BadRequestException("Child already registered!");
                    }
                r.getPassengers().add(new Passenger(clientChildToChild(child), null, null, null, false, PassengerState.NULL));
                raceRepository.save(r);
            }
            l.get().getSubscribedChildren().add(c);
            lineRepository.save(l.get());
            return lineToClientLine(l.get());
        } else {
            throw new BadRequestException();
        }
    }

    @Override
    @Transactional
    public ClientLine removeChildFromLine(String UserID, ClientChild child, String lineName, List<String> roles) {
        Optional<Line> l;
        List<Race> childScheduledRaces = new ArrayList<>();
        List<Race> childValidatedRaces = new ArrayList<>();
        try {
            l = lineRepository.findLineByName(lineName);
            childScheduledRaces = raceRepository.findAllByLineNameAndRaceStateIs(lineName, RaceState.SCHEDULED);
            childValidatedRaces = raceRepository.findAllByLineNameAndRaceStateIs(lineName, RaceState.VALIDATED);

        } catch (Exception e) {
            throw new InternalServerErrorException(e);
        }

        if (!l.isPresent())
            throw new ResourceNotFoundException();

        if (!roles.contains(Roles.prefix + Roles.SYSTEM_ADMIN)) {
            if (!l.get().getAdmins().contains(UserID))
                throw new UnauthorizedRequestException("Line Admins only can perform this operation");
        }

        Child c = new Child(child.getName(), child.getSurname(), child.getCf(), child.getParentId(), null);

        if (l.get().getSubscribedChildren().contains(c)) {

            for (Race r : childScheduledRaces)
                for (Passenger p : r.getPassengers())
                    if (p.getChildDetails().getCF().equals(child.getCf())) {
                        r.getPassengers().remove(p);
                        raceRepository.save(r);
                        break;
                    }
            for (Race r : childValidatedRaces)
                for (Passenger p : r.getPassengers())
                    if (p.getChildDetails().getCF().equals(child.getCf())) {
                        r.getPassengers().remove(p);
                        raceRepository.save(r);
                        break;
                    }
            l.get().getSubscribedChildren().remove(c);
            lineRepository.save(l.get());
            return lineToClientLine(l.get());
        } else {
            throw new BadRequestException();
        }
    }

    /**
     * Function to convert MongoDB Line into a ClientLine
     *
     * @param lineMongo: line to convert
     * @return line converted
     */
    private ClientLine lineToClientLine(Line lineMongo) {
        ArrayList<ClientPediStop> out = new ArrayList<>();
        for (PediStop p : lineMongo.getOutwardStops())
            out.add(new ClientPediStop(p.getName(), p.getLongitude(), p.getLatitude(), p.getDelayInMillis()));

        ArrayList<ClientPediStop> ret = new ArrayList<>();
        for (PediStop p : lineMongo.getReturnStops())
            ret.add(new ClientPediStop(p.getName(), p.getLongitude(), p.getLatitude(), p.getDelayInMillis()));

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

    private ClientPediStop pediStopToClientPediStop(PediStop pediStop) {
        return new ClientPediStop(pediStop.getName(), pediStop.getLongitude(), pediStop.getLatitude(), pediStop.getDelayInMillis());
    }

    /**
     * Function to insert new race
     *
     * @param clientRace:        race to insert
     * @param performerUsername: performer username
     * @throws BadRequestException
     * @throws InternalServerErrorException
     * @throws ResourceNotFoundException
     * @throws UnauthorizedRequestException
     */
    @Override
    @Transactional
    public ClientRace insertRace(ClientRace clientRace, String performerUsername) {
        Optional<UserCredentials> performerCredentials;
        Optional<User> performer;
        Optional<Line> targetLine;
        Date today = Calendar.getInstance().getTime();
        //TODO attivare quando finisce la fase di testing
        /*if (today.before(clientRace.getDate()))
            throw new BadRequestException();
        */
        try {
            performerCredentials = userCredentialsRepository.findByUsername(performerUsername);
            targetLine = lineRepository.findLineByName(clientRace.getLine().getName());
            performer = userRepository.findByUsername(performerUsername);
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        if (!targetLine.isPresent() || !performerCredentials.isPresent() || !performer.isPresent())
            throw new ResourceNotFoundException();
        if (!isAdminOfLineOrSysAdmin(performer.get().getLines(), performerCredentials.get().getRoles(), clientRace.getLine().getName()))
            throw new UnauthorizedRequestException();

        if (clientRace.getDate() == null || clientRace.getLine().getName() == null || clientRace.getDirection() == null)
            throw new BadRequestException();

        if (!clientRace.getPassengers().isEmpty())
            clientRace.getPassengers().clear();
        if (!clientRace.getReachedStops().isEmpty())
            clientRace.getReachedStops().clear();
        if (clientRace.getCurrentStop() != null)
            clientRace.setCurrentStop(null);
        clientRace.setRaceState(RaceState.SCHEDULED);
        for (Child c : targetLine.get().getSubscribedChildren()) {
            c.setState(null);
            Passenger p = new Passenger(c, null, null, null, false, PassengerState.NULL);
            clientRace.getPassengers().add(passengerToClientPassenger(p));
        }
        Race race = clientRaceToRace(clientRace);
        try {
            raceRepository.save(race);
        } catch (Exception e) {
            throw new InternalServerErrorException(e);
        }
        return clientRace;
    }

    /**
     * Function to insert new race
     *
     * @param clientRace:        race to insert
     * @param performerUsername: performer username
     * @throws BadRequestException
     * @throws InternalServerErrorException
     * @throws ResourceNotFoundException
     * @throws UnauthorizedRequestException
     */
    @Override
    @Transactional
    public ClientRace insertRaceInitializer(ClientRace clientRace, String performerUsername) {
        Optional<UserCredentials> performerCredentials;
        Optional<User> performer;
        Optional<Line> targetLine;
        Date today = Calendar.getInstance().getTime();
        //TODO attivare quando finisce la fase di testing
        /*if (today.before(clientRace.getDate()))
            throw new BadRequestException();
        */
        try {
            performerCredentials = userCredentialsRepository.findByUsername(performerUsername);
            targetLine = lineRepository.findLineByName(clientRace.getLine().getName());
            performer = userRepository.findByUsername(performerUsername);
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        if (!targetLine.isPresent() || !performerCredentials.isPresent() || !performer.isPresent())
            throw new ResourceNotFoundException();
        if (!isAdminOfLineOrSysAdmin(performer.get().getLines(), performerCredentials.get().getRoles(), clientRace.getLine().getName()))
            throw new UnauthorizedRequestException();

        if (clientRace.getDate() == null || clientRace.getLine().getName() == null || clientRace.getDirection() == null)
            throw new BadRequestException();

        Race race = clientRaceToRace(clientRace);
        try {
            raceRepository.save(race);
        } catch (Exception e) {
            throw new InternalServerErrorException(e);
        }
        return clientRace;
    }

    /**
     * Function to get all races in DB as Race collection
     *
     * @return Collection of races
     * @throws InternalServerErrorException
     */
    @Override
    public Collection<ClientRace> getRaces() {
        try {
            List<Race> races = raceRepository.findAll();
            List<ClientRace> clientRaces = new ArrayList<>();
            for (Race raceMongo : races)
                clientRaces.add(raceToClientRace(raceMongo, null));
            return clientRaces;
        } catch (Exception e) {
            throw new InternalServerErrorException(e);
        }
    }

    /**
     * Function to get race
     *
     * @param clientRace: race to get
     * @return ClientRace: race requested
     * @throws InternalServerErrorException
     * @throws ResourceNotFoundException
     */
    @Override
    public ClientRace getRace(UserCredentials performer, ClientRace clientRace) {
        Optional<Race> race;
        try {
            race = raceRepository.findRaceByDateAndLineNameAndDirection(clientRace.getDate(), clientRace.getLine().getName(), clientRace.getDirection());
        } catch (Exception e) {
            throw new InternalServerErrorException(e);
        }
        if (race.isPresent()) {
            if (isCompanionOfRace(race.get().getCompanions(), performer.getRoles(), performer.getUsername()))
                return raceToClientRace(race.get(), performer);
            return raceToClientRace(race.get(), null);
        } else
            throw new ResourceNotFoundException();
    }

    @Override
    public Collection<ClientRace> getRacesByLine(UserCredentials performer, String lineName) {
        List<Race> races;
        Optional<Line> line;
        Collection<ClientRace> clientRaces = new ArrayList<>();
        try {
            line = lineRepository.findLineByName(lineName);
            races = raceRepository.findAllByLineName(lineName);
        } catch (Exception e) {
            throw new InternalServerErrorException(e);
        }
        if (!line.isPresent())
            throw new ResourceNotFoundException();

        for (Race race : races) {
            if (isCompanionOfRace(race.getCompanions(), performer.getRoles(), performer.getUsername()))
                clientRaces.add(raceToClientRace(race, performer));
            else
                clientRaces.add(raceToClientRace(race, null));
        }
        return clientRaces;
    }

    @Override
    public Collection<ClientRace> getRacesByDateAndLine(UserCredentials performer, Date date, String lineName) {
        List<Race> races;
        Optional<Line> line;
        Collection<ClientRace> clientRaces = new ArrayList<>();
        try {
            line = lineRepository.findLineByName(lineName);
            races = raceRepository.findAllByLineNameAndDate(lineName, date);
        } catch (Exception e) {
            throw new InternalServerErrorException(e);
        }
        if (!line.isPresent())
            throw new ResourceNotFoundException();

        for (Race race : races) {
            if (isCompanionOfRace(race.getCompanions(), performer.getRoles(), performer.getUsername()))
                clientRaces.add(raceToClientRace(race, performer));
            else
                clientRaces.add(raceToClientRace(race, null));
        }
        return clientRaces;
    }

    @Override
    public Collection<ClientRace> getRacesByLineAndDateInterval(UserCredentials performer, String lineName, Date fromDate, Date toDate) {
        List<Race> races;
        Optional<Line> line;
        Collection<ClientRace> clientRaces = new ArrayList<>();
        try {
            line = lineRepository.findLineByName(lineName);
            races = raceRepository.findAllByLineNameAndDateBetween(lineName, removeTime(fromDate), midnightTime(toDate));
        } catch (Exception e) {
            throw new InternalServerErrorException(e);
        }
        if (!line.isPresent())
            throw new ResourceNotFoundException();

        for (Race race : races) {
            if (isCompanionOfRace(race.getCompanions(), performer.getRoles(), performer.getUsername()))
                clientRaces.add(raceToClientRace(race, performer));
            else
                clientRaces.add(raceToClientRace(race, null));
        }
        return clientRaces;
    }

    @Override
    public Collection<ClientRace> getRacesByLineAndDirection(UserCredentials performer, String lineName, DirectionType direction) {
        List<Race> races;
        Optional<Line> line;
        Collection<ClientRace> clientRaces = new ArrayList<>();
        try {
            line = lineRepository.findLineByName(lineName);
            races = raceRepository.findAllByLineNameAndDirection(lineName, direction);
        } catch (Exception e) {
            throw new InternalServerErrorException(e);
        }
        if (!line.isPresent())
            throw new ResourceNotFoundException();

        for (Race race : races) {
            if (isCompanionOfRace(race.getCompanions(), performer.getRoles(), performer.getUsername()))
                clientRaces.add(raceToClientRace(race, performer));
            else
                clientRaces.add(raceToClientRace(race, null));
        }
        return clientRaces;
    }

    @Override
    public Collection<ClientRace> getRacesByLineAndDateAndDirection(UserCredentials performer, String lineName, Date date, DirectionType direction) {
        List<Race> races;
        Optional<Line> line;
        Collection<ClientRace> clientRaces = new ArrayList<>();
        try {
            line = lineRepository.findLineByName(lineName);
            races = raceRepository.findAllByLineNameAndDirectionAndDate(lineName, direction, date);
        } catch (Exception e) {
            throw new InternalServerErrorException(e);
        }
        if (!line.isPresent())
            throw new ResourceNotFoundException();

        for (Race race : races) {
            if (isCompanionOfRace(race.getCompanions(), performer.getRoles(), performer.getUsername()))
                clientRaces.add(raceToClientRace(race, performer));
            else
                clientRaces.add(raceToClientRace(race, null));
        }
        return clientRaces;
    }

    @Override
    public Collection<ClientRace> getRacesByLineAndDirectionAndDateInterval(UserCredentials performer, String lineName, DirectionType direction, Date fromDate, Date toDate) {
        List<Race> races;
        Optional<Line> line;
        Collection<ClientRace> clientRaces = new ArrayList<>();
        try {
            line = lineRepository.findLineByName(lineName);
            races = raceRepository.findAllByLineNameAndDirectionAndDateBetween(lineName, direction, removeTime(fromDate), midnightTime(toDate));
        } catch (Exception e) {
            throw new InternalServerErrorException(e);
        }
        if (!line.isPresent())
            throw new ResourceNotFoundException();

        for (Race race : races) {
            if (isCompanionOfRace(race.getCompanions(), performer.getRoles(), performer.getUsername()))
                clientRaces.add(raceToClientRace(race, performer));
            else
                clientRaces.add(raceToClientRace(race, null));
        }
        return clientRaces;
    }


    private Race clientRaceToRace(ClientRace clientRace) {
        PediStop currentStop = null;
        List<ReachedStop> reachedStops = new ArrayList<>();
        if (clientRace.getCurrentStop() != null)
            currentStop = clientPediStopToPediStop(clientRace.getCurrentStop());
        if (clientRace.getReachedStops() != null)
            for (ClientReachedStop clientReachedStop : clientRace.getReachedStops()) {
                reachedStops.add(new ReachedStop(clientReachedStop.getStopName(), clientReachedStop.getArrivalDelay(), clientReachedStop.getDepartureDelay()));
            }

        return new Race(clientRace.getLine().getName(), clientRace.getDirection(), clientRace.getDate(), currentStop, clientRace.getRaceState(), clientPassengersToPassengers(clientRace.getPassengers()), reachedStops, clientCompanionsToCompanions(clientRace.getCompanions()));
    }

    private ClientRace raceToClientRace(Race race, UserCredentials performer) {
        Optional<Line> line;
        List<ClientReachedStop> clientReachedStops = new ArrayList<>();
        if (race.getReachedStops() != null)
            for (ReachedStop reachedStop : race.getReachedStops()) {
                clientReachedStops.add(new ClientReachedStop(reachedStop.getStopName(), reachedStop.getArrivalDelay(), reachedStop.getDepartureDelay()));
            }
        try {
            line = lineRepository.findLineByName(race.getLineName());
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        if (!line.isPresent())
            throw new InternalServerErrorException();
        ClientCompanion companion = null;
        if (performer != null) {
            Companion dbCompanion = race.getCompanions().stream().filter(comp -> comp.getUserDetails().getUsername().equals(performer.getUsername())).findFirst().orElse(null);
            if (dbCompanion != null) companion = companionToClientCompanion(dbCompanion, performer.getRoles());
        }
        return new ClientRace(lineToClientLine(line.get()),
                race.getDirection(),
                race.getDate(), race.getCurrentStop() == null ? null : pediStopToClientPediStop(race.getCurrentStop()),
                race.getRaceState(),
                passengersToClientPassengers(race.getPassengers()),
                clientReachedStops,
                companionsToClientCompanions(race.getCompanions()), companion);
    }

    private ClientPassenger passengerToClientPassenger(Passenger passenger) {
        ClientPediStop stopReserved;
        ClientPediStop stopTaken;
        ClientPediStop stopDeliverd;
        if (passenger.getStopReserved() == null)
            stopReserved = null;
        else
            stopReserved = pediStopToClientPediStop(passenger.getStopReserved());
        if (passenger.getStopTaken() == null)
            stopTaken = null;
        else
            stopTaken = pediStopToClientPediStop(passenger.getStopTaken());
        if (passenger.getStopDelivered() == null)
            stopDeliverd = null;
        else
            stopDeliverd = pediStopToClientPediStop(passenger.getStopDelivered());

        return new ClientPassenger(childToClientChild(passenger.getChildDetails()), stopReserved, stopTaken, stopDeliverd, passenger.isReserved(), passenger.getState());
    }

    private Passenger clientPassengerToPassenger(ClientPassenger clientPassenger) {
        PediStop stopReserved;
        PediStop stopTaken;
        PediStop stopDeliverd;
        if (clientPassenger.getStopReserved() == null)
            stopReserved = null;
        else
            stopReserved = clientPediStopToPediStop(clientPassenger.getStopReserved());
        if (clientPassenger.getStopTaken() == null)
            stopTaken = null;
        else
            stopTaken = clientPediStopToPediStop(clientPassenger.getStopTaken());
        if (clientPassenger.getStopDelivered() == null)
            stopDeliverd = null;
        else
            stopDeliverd = clientPediStopToPediStop(clientPassenger.getStopDelivered());
        return new Passenger(clientChildToChild(clientPassenger.getChildDetails()), stopReserved, stopTaken, stopDeliverd, clientPassenger.isReserved(), clientPassenger.getState());
    }

    private List<ClientPassenger> passengersToClientPassengers(List<Passenger> passengers) {
        List<ClientPassenger> clientPassengers = new ArrayList<>();
        for (Passenger p : passengers) {
            clientPassengers.add(passengerToClientPassenger(p));
        }
        return clientPassengers;
    }

    private List<Passenger> clientPassengersToPassengers(List<ClientPassenger> clientPassengers) {
        List<Passenger> passengers = new ArrayList<>();
        for (ClientPassenger p : clientPassengers) {
            passengers.add(clientPassengerToPassenger(p));
        }
        return passengers;
    }

    private ClientChild childToClientChild(Child child) {
        return new ClientChild(child.getName(), child.getSurname(), child.getCF(), child.getParentId());
    }

    //----------------------------------------------###CompanionRequest###----------------------------------------------//

    private Child clientChildToChild(ClientChild clientChild) {
        return new Child(clientChild.getName(), clientChild.getSurname(), clientChild.getCf(), clientChild.getParentId(), null);
    }

    @Override
    public Collection<CompanionRequest> getCompanionRequestsByCompanion(String username, RaceState state) {
        List<Race> races = null;
        Collection<CompanionRequest> companionRequests;

        if (state == null) {
            try {
                races = raceRepository.findAllByRaceStateIsNot(RaceState.ENDED);
            } catch (Exception e) {
                throw new InternalServerErrorException(e);
            }
        } else {
            try {
                switch (state) {
                    case SCHEDULED:
                        races = raceRepository.findAllByRaceStateIs(RaceState.SCHEDULED);
                        break;
                    case VALIDATED:
                        races = raceRepository.findAllByRaceStateIs(RaceState.VALIDATED);
                        break;
                    case STARTED:
                        races = raceRepository.findAllByRaceStateIs(RaceState.STARTED);
                        break;
                    case ENDED:
                        races = raceRepository.findAllByRaceStateIs(RaceState.ENDED);
                        break;
                }
            } catch (Exception e) {
                throw new InternalServerErrorException(e);
            }
        }

        if (races == null)
            companionRequests = new ArrayList<>();
        else
            companionRequests = races.stream()
                    .filter(
                            race -> race.getCompanions().stream()
                                    .filter(companion -> companion.getUserDetails().getUsername().equals(username))
                                    .count() > 0
                    ).map(
                            race -> {
                                Companion me = race.getCompanions().stream().filter(companion -> companion.getUserDetails().getUsername().equals(username)).findFirst().orElse(null);
                                if (me != null) {
                                    return new CompanionRequest(username,
                                            race.getLineName(),
                                            race.getDirection(),
                                            race.getDate(),
                                            pediStopToClientPediStop(me.getInitialStop()),
                                            pediStopToClientPediStop(me.getFinalStop()),
                                            me.getState());
                                } else return null;
                            }
                    ).collect(Collectors.toList());

        return companionRequests;
    }

    @Override
    public Collection<CompanionRequest> getCompanionRequestsByAdmin(String username, RaceState state) {
        Optional<UserCredentials> performerCredentials;
        Optional<User> performer;
        List<Line> lines = new ArrayList<>();
        List<Race> races = new ArrayList<>();
        Collection<CompanionRequest> companionRequests = new ArrayList<>();

        try {
            performerCredentials = userCredentialsRepository.findByUsername(username);
            performer = userRepository.findByUsername(username);
            lines = lineRepository.findAll();
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        // If performer misses: Throw ResourceNotFound
        if (!performer.isPresent() || !performerCredentials.isPresent())
            throw new ResourceNotFoundException("user doesn't exist");

        // If target is the System_Admin or an Admin of the selected line: Throw BadRequest
        List<String> performerLines = new ArrayList<>();
        if (isSystemAdmin(performerCredentials.get().getRoles()))
            performerLines.addAll(lines.stream().map(line -> line.getName()).collect(Collectors.toList()));
        else
            performerLines = Objects.requireNonNull(performer.get()).getLines();

        if (state.equals(RaceState.ENDED)) {
            try {
                for (String lineName : performerLines)
                    races = raceRepository.findAllByLineNameAndRaceStateIs(lineName, RaceState.ENDED);
            } catch (Exception e) {
                throw new InternalServerErrorException(e);
            }

            for (Race race : races) {
                for (Companion c : race.getCompanions()) {
                    CompanionRequest cr = new CompanionRequest(c.getUserDetails().getUsername(), race.getLineName(), race.getDirection(), race.getDate(), pediStopToClientPediStop(c.getInitialStop()), pediStopToClientPediStop(c.getFinalStop()), c.getState());
                    companionRequests.add(cr);
                }
            }
            return companionRequests;
        }
        try {
            for (String lineName : performerLines)
                races.addAll(raceRepository.findAllByLineNameAndRaceStateIs(lineName, RaceState.SCHEDULED));
        } catch (Exception e) {
            throw new InternalServerErrorException(e);
        }

        for (Race race : races) {
            for (Companion c : race.getCompanions()) {
                CompanionRequest cr = new CompanionRequest(c.getUserDetails().getUsername(), race.getLineName(), race.getDirection(), race.getDate(), pediStopToClientPediStop(c.getInitialStop()), pediStopToClientPediStop(c.getFinalStop()), c.getState());
                companionRequests.add(cr);
            }
        }
        return companionRequests;
    }

    public static class SortType {
        private static final String MAIL = "Username";
        private static final String NAME = "Name";
        private static final String SURNAME = "Surname";

        public static String get(String type) {
            if (type.equals("MAIL"))
                return MAIL;
            if (type.equals("NAME"))
                return NAME;
            if (type.equals("SURNAME"))
                return SURNAME;
            else return null;
        }
    }
}
