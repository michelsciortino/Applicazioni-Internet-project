package it.polito.ai.project.services.database;

import it.polito.ai.project.exceptions.BadRequestException;
import it.polito.ai.project.exceptions.InternalServerErrorException;
import it.polito.ai.project.exceptions.ResourceNotFoundException;
import it.polito.ai.project.exceptions.UnauthorizedRequestException;
import it.polito.ai.project.generalmodels.*;
import it.polito.ai.project.services.database.models.*;
import it.polito.ai.project.services.database.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                return new ClientUserCredentials(u.getUsername(), u.getRoles(), u.isEnable(), u.isCredentialsNotExpired(), u.isAccountNotLocked(), u.isAccountNotExpired());
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
     * @throws BadRequestException
     */
    @Override
    public ClientUserCredentials getCredentials(String username) {
        try {
            UserCredentials u = userCredentialsRepository.findByUsername(username).get();
            return new ClientUserCredentials(u.getUsername(), u.getRoles(), u.isEnable(), u.isCredentialsNotExpired(), u.isAccountNotLocked(), u.isAccountNotExpired());
        } catch (NoSuchElementException e1) {
            throw new BadRequestException();
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
     * @throws BadRequestException
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
                throw new BadRequestException();
            }
        } catch (BadRequestException e1) {
            throw new BadRequestException();
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }

    /**
     * Function to update user's credential
     *
     * @param clientUserCredentials user new credential
     * @throws InternalServerErrorException
     * @throws BadRequestException
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
                throw new BadRequestException();
            }
        } catch (BadRequestException e1) {
            throw new BadRequestException();
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }

    /**
     * Function to delete credential
     *
     * @param clientUserCredentials user delete credential
     * @throws InternalServerErrorException
     * @throws BadRequestException
     */
    @Override
    @Transactional
    public void deleteCredentials(ClientUserCredentials clientUserCredentials) {
        try {
            Optional<UserCredentials> u = userCredentialsRepository.findByUsername(clientUserCredentials.getUsername());
            if (u.isPresent()) {
                userCredentialsRepository.delete(u.get());
            } else {
                throw new BadRequestException();
            }
        } catch (BadRequestException e1) {
            throw new BadRequestException();
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
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

    public boolean subscribeAdminAndSendMail(String username) {
        //TO-DO: implementa insert user (admin)
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
            Line l;
            l = lineRepository.findLineByName(lineName).get();

            if (!roles.contains(Roles.prefix + Roles.SYSTEM_ADMIN)) {
                if (!l.getAdmins().contains(UserID))
                    throw new UnauthorizedRequestException("Line Admins only can perform this operation");
            }
            if (l != null) {
                Child c = new Child(child.getName(), child.getSurname(), child.getCF(), child.getParentId());

                if (!l.getSubscribedChildren().contains(c)) {

                    l.getSubscribedChildren().add(c);
                    lineRepository.save(l);
                    return LineToClientLine(l);
                } else {
                    throw new BadRequestException();
                }
            } else
                throw new ResourceNotFoundException();
        } catch (Exception e) {
            throw new InternalServerErrorException(e);
        }
    }

    //TO-DO Move to Utils

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

        List<ClientChild> clist = new ArrayList<>();
        for (Child c : lineMongo.getSubscribedChildren()) {
            ClientChild cc = new ClientChild(c.getName(), c.getSurname(), c.getCF(), c.getParentId());
            clist.add(cc);
        }
        return new ClientLine(lineMongo.getName(), out, ret, clist, lineMongo.getAdmins());
    }

    //---------------------------------------------------###Race###---------------------------------------------------//

}
