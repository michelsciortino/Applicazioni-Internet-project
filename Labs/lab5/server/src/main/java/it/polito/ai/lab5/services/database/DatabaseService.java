package it.polito.ai.lab5.services.database;

import it.polito.ai.lab5.controllers.models.DirectionType;
import it.polito.ai.lab5.controllers.models.LineReservations;
import it.polito.ai.lab5.controllers.models.Reservation;
import it.polito.ai.lab5.files.json.Line;
import it.polito.ai.lab5.files.json.PediStop;
import it.polito.ai.lab5.services.database.models.*;
import it.polito.ai.lab5.services.database.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.ServiceNotFoundException;
import java.net.UnknownServiceException;
import java.time.LocalDate;
import java.util.*;

@Service
public class DatabaseService implements DatabaseServiceInterface {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private CredentialRepository credentialRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean insertLine(Line line) throws UnknownServiceException {
        try {
            ArrayList<PediStopMongo> outboundStops = new ArrayList<>();
            for (PediStop p : line.outboundStops)
                outboundStops.add(new PediStopMongo(p.longitude, p.latitude, p.name, p.time));

            ArrayList<PediStopMongo> returnStops = new ArrayList<>();
            for (PediStop p : line.returnStops)
                returnStops.add(new PediStopMongo(p.longitude, p.latitude, p.name, p.time));

            LineMongo lineMongo = new LineMongo(line.name, outboundStops, returnStops);
            lineRepository.save(lineMongo);
            return true;
        } catch (Exception e) {
            throw new UnknownServiceException(e.getMessage());
        }
    }

    @Override
    public Collection<String> getLinesNames() throws UnknownServiceException {
        try {
            List<LineMongo> list = lineRepository.findAllName();
            List<String> names = new ArrayList<>();
            for (LineMongo lineMongo : list)
                names.add(lineMongo.getName());
            return names;
        } catch (Exception e) {
            throw new UnknownServiceException(e.getMessage());
        }
    }

    @Override
    public Collection<Line> getLines() throws UnknownServiceException {
        try {
            List<LineMongo> list = lineRepository.findAll();
            List<Line> lines = new ArrayList<>();
            for (LineMongo lineMongo : list)
                lines.add(lineMongoToLine(lineMongo));
            return lines;
        } catch (Exception e) {
            throw new UnknownServiceException(e.getMessage());
        }
    }

    @Override
    public Line getLine(String lineName) throws UnknownServiceException {
        try {
            LineMongo lineMongo = lineRepository.findLineByName(lineName);
            return lineMongoToLine(lineMongo);
            //return Line.builder().name(lineMongo.getName()).outboundStops(out).returnStops(ret).build();
        } catch (Exception e) {
            throw new UnknownServiceException(e.getMessage());
        }
    }

/*
    @Override
    public LineReservations getLineReservationsNames(String lineName, LocalDate date) throws UnknownServiceException {
        try {
            LineMongo lineMongo = lineRepository.findLineByName(lineName);

            Map<String, Collection<String>> outwardStopsReservations = new HashMap<>();
            Map<String, Collection<String>> backStopsReservations = new HashMap<>();
            Date d = convertLocalDateTOMongoDate(date);
            for (PediStopMongo s : lineMongo.getOutboundStops()) {
                List<String> names = new ArrayList<>();
                try {

                    for (ReservationMongo r : reservationRepository.getReservationMongoByStopNameAndDirectionAndData(s.getName(), DirectionType.OUTWARD, d, lineName))
                        names.add(r.getChildName());
                    if (!names.isEmpty())
                        outwardStopsReservations.put(s.getName(), names);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            for (PediStopMongo s : lineMongo.getReturnStops()) {
                List<String> names = new ArrayList<>();
                try {
                    for (ReservationMongo r : reservationRepository.getReservationMongoByStopNameAndDirectionAndData(s.getName(), DirectionType.RETURN, d, lineName))
                        names.add(r.getChildName());
                    if (!names.isEmpty())
                        backStopsReservations.put(s.getName(), names);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            return LineReservations.builder().backStopsReservations(backStopsReservations).outwardStopsReservations(outwardStopsReservations).build();
        } catch (Exception e) {
            throw new UnknownServiceException(e.getMessage());
        }
    }
*/
    @Override
    public LineReservations getLineReservations(String lineName, LocalDate date) throws UnknownServiceException {
        try {
            LineMongo lineMongo = lineRepository.findLineByName(lineName);

            Map<String, Collection<Reservation>> outwardStopsReservations = new HashMap<>();
            Map<String, Collection<Reservation>> backStopsReservations = new HashMap<>();
            Date d = convertLocalDateTOMongoDate(date);
            for (PediStopMongo s : lineMongo.getOutboundStops()) {
                List<Reservation> listRes = new ArrayList<>();
                try {

                    for (ReservationMongo res : reservationRepository.getReservationMongoByStopNameAndDirectionAndData(s.getName(), DirectionType.OUTWARD, d, lineName))
                        listRes.add(Reservation.builder().id(res.getId().toString()).childName(res.getChildName()).childSurname(res.getChildSurname()).childCf(res.getChildCf()).parentUsername(res.getUserID()).stopName(res.getStopName()).direction(res.getDirection()).present(false).build());
                    outwardStopsReservations.put(s.getName(), listRes);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            for (PediStopMongo s : lineMongo.getReturnStops()) {
                List<Reservation> listRes = new ArrayList<>();
                try {
                    for (ReservationMongo res : reservationRepository.getReservationMongoByStopNameAndDirectionAndData(s.getName(), DirectionType.RETURN, d, lineName))
                        listRes.add(Reservation.builder().id(res.getId().toString()).childName(res.getChildName()).childSurname(res.getChildSurname()).childCf(res.getChildCf()).parentUsername(res.getUserID()).stopName(res.getStopName()).direction(res.getDirection()).present(false).build());

                    backStopsReservations.put(s.getName(), listRes);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            return LineReservations.builder().backStopsReservations(backStopsReservations).outwardStopsReservations(outwardStopsReservations).build();
        } catch (Exception e) {
            throw new UnknownServiceException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public Line addSubscriber(String UserID, Child child, String lineName, List<String> roles) throws UnknownServiceException
    {
        try
        {
            if(!isParentorAdmin(UserID, roles, child))
            {
                throw new UnknownServiceException("You can subscribe your children only!");
            }
            LineMongo lm;
            lm = lineRepository.findLineByName(lineName);
            if (lm != null)
            {
                LineSubscribedChild lsc = new LineSubscribedChild();
                lsc.setChild(child);
                lsc.setParentId(UserID);
                if(!lm.getSubscribedChildren().contains(lsc))
                {

                    lm.getSubscribedChildren().add(lsc);
                    lineRepository.save(lm);
                    return lineMongoToLine(lm);
                }
                else
                {
                    throw new UnknownServiceException("Child already subscribed!");
                }
            } else
                throw new ServiceNotFoundException();
        } catch (Exception e)
        {
            throw new UnknownServiceException(e.getMessage());
        }

    }

    @Override
    public Reservation addReservation(List<String> roles, String UserID, Reservation reservation, String lineName, LocalDate date) throws UnknownServiceException {
        try {

            if (!roles.contains(Roles.prefix + Roles.ADMIN)||!roles.contains(Roles.prefix + Roles.SYSTEM_ADMIN))
            {
                User u= getUserByUsername(UserID);
                List<Child> children = u.getChildren();
                Child child;
                boolean flag= false;
                for (Child c : children)
                {

                    if(c.getCF().equals(reservation.getChildCf()))
                    {
                        flag = true;
                        break;
                    }
                }
                if(!flag)
                {
                    throw new UnknownServiceException("You can subscribe your children only!");
                }

            }
            ReservationMongo res;
            if (lineRepository.findLineByName(lineName) != null) {
                res = reservationRepository.save(ReservationMongo.builder().childSurname(reservation.getChildSurname()).childName(reservation.getChildName()).childCf(reservation.getChildCf()).direction(reservation.getDirection()).stopName(reservation.getStopName()).data(convertLocalDateTOMongoDate(date)).userID(UserID).lineName(lineName).present(false).build());
                return Reservation.builder().id(res.getId().toString()).childSurname(res.getChildSurname()).childName(res.getChildName()).childCf(res.getChildCf()).parentUsername(UserID).stopName(res.getStopName()).direction(res.getDirection()).present(false).build();
            } else
                throw new ServiceNotFoundException();
        } catch (Exception e) {
            throw new UnknownServiceException(e.getMessage());
        }

    }

    @Override
    @Transactional
    public Reservation updateReservation(String UserID, Reservation reservation, String lineName, LocalDate date, String reservationId) throws UnknownServiceException {
        try {

            ReservationMongo rp = reservationRepository.findById(reservationId).get();
            rp.setChildName(reservation.getChildName());
            rp.setChildSurname(reservation.getChildSurname());
            rp.setChildCf(reservation.getChildCf());
            rp.setDirection(reservation.getDirection());
            rp.setLineName(lineName);
            rp.setStopName(reservation.getStopName());
            rp.setData(convertLocalDateTOMongoDate(date));
            rp.setPresent(reservation.isPresent());
            ReservationMongo res=reservationRepository.save(rp);
            return Reservation.builder().id(res.getId().toString()).childSurname(res.getChildSurname()).childName(res.getChildName()).childCf(res.getChildCf()).parentUsername(UserID).stopName(res.getStopName()).direction(res.getDirection()).present(reservation.isPresent()).build();
        } catch (Exception e) {
            throw new UnknownServiceException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean deleteReservation(String UserID, String lineName, LocalDate date, String reservationId) throws UnknownServiceException {
        try {
            reservationRepository.delete(reservationRepository.findById(reservationId).get());
            return true;
        } catch (Exception e) {
            throw new UnknownServiceException(e.getMessage());
        }
    }

    @Override
    public Reservation getReservation(String UserID, String lineName, LocalDate date, String reservationId) throws UnknownServiceException {
        try {
            ReservationMongo rm = reservationRepository.findById(reservationId).get();
            return Reservation.builder().id(rm.getId().toString()).childSurname(rm.getChildSurname()).childName(rm.getChildName()).childCf(rm.getChildCf()).parentUsername(UserID).direction(rm.getDirection()).stopName(rm.getStopName()).present(rm.isPresent()).build();
        } catch (Exception e) {
            throw new UnknownServiceException(e.getMessage());
        }
    }

    @Override
    public ReservationMongo getReservationMongo(String UserID, String lineName, LocalDate date, String reservationId) throws UnknownServiceException {
        try {
            return reservationRepository.findById(reservationId).get();
        } catch (Exception e) {
            throw new UnknownServiceException(e.getMessage());
        }
    }

    @Override
    public Credential insertCredential(String username, String password, List<String> role) throws UnknownServiceException {
        try {
            if (!credentialRepository.findByUsername(username).isPresent())
                return credentialRepository.save(new Credential(this.passwordEncoder.encode(password), username, role));
            else
                return null;
        } catch (Exception e) {
            throw new UnknownServiceException(e.getMessage());
        }
    }

    @Override
    public Credential getCredential(String id) throws UnknownServiceException {
        try {
            return credentialRepository.findById(id).get();
        } catch (Exception e) {
            throw new UnknownServiceException(e.getMessage());
        }
    }

    @Override
    public boolean modifyUserPassword(Credential credential, String password) throws UnknownServiceException {
        try {
            if (credentialRepository.findByUsername(credential.getUsername()).isPresent())
                credential.setPassword(this.passwordEncoder.encode(password));
            credentialRepository.save(credential);
            return true;
        } catch (Exception e) {
            throw new UnknownServiceException(e.getMessage());
        }
    }

    @Override
    public boolean updateCredential(Credential credential) throws UnknownServiceException {
        try {
            // if (credentialRepository.findByUsername(credential.getMail()).isPresent())
            credentialRepository.save(credential);
            return true;
        } catch (Exception e) {
            throw new UnknownServiceException(e.getMessage());
        }
    }

    @Override
    public boolean deleteCredential(Credential credential) throws UnknownServiceException {
        try {
            credentialRepository.delete(credential);
            return true;
        } catch (Exception e) {
            throw new UnknownServiceException(e.getMessage());
        }
    }

    @Override
    public boolean insertToken(Token token) throws UnknownServiceException {
        try {
            tokenRepository.save(token);
            return true;
        } catch (Exception e) {
            throw new UnknownServiceException(e.getMessage());
        }
    }

    @Override
    public boolean deleteToken(Token token) throws UnknownServiceException {
        try {
            tokenRepository.delete(token);
            return true;
        } catch (Exception e) {
            throw new UnknownServiceException(e.getMessage());
        }
    }

    @Override
    public Page<User> getUsers(int pageNumber) throws UnknownServiceException {
        try {
            Pageable pageable = PageRequest.of(pageNumber, 10);
            return userRepository.findAllNoCredential(pageable);
        } catch (Exception e) {
            throw new UnknownServiceException(e.getMessage());
        }
    }

    @Override
    public User getUser(String id) throws UnknownServiceException {
        try {
            return userRepository.findById(id).get();
        } catch (Exception e) {
            throw new UnknownServiceException(e.getMessage());
        }
    }

    @Override
    public User getUserByUsername(String username) throws UnknownServiceException {
        try {
            return userRepository.findByUsername(username);
        } catch (Exception e) {
            throw new UnknownServiceException(e.getMessage());
        }
    }

    @Override
    public User insertUser(User user) throws UnknownServiceException {
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw new UnknownServiceException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean makeAdminFromAdmin(User user, UserDetails userDetails, String userID, String line) throws UnknownServiceException {
        User userPrincipal = getUserByUsername(userDetails.getUsername());
        if (userPrincipal.getLines() != null && user.getLines() != null && userPrincipal.getLines().contains(line)) {
            Credential credentialDB = getCredential(userID);
            if (credentialDB != null) {
                List<String> roles = credentialDB.getRoles();
                if (roles.contains(Roles.prefix + Roles.ADMIN))
                    return false;
                else
                    roles.add(Roles.prefix + Roles.ADMIN);
                credentialDB.setRoles(roles);
                updateCredential(credentialDB);
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public boolean makeAdminFromSystemAdmin(User user, String userID) throws UnknownServiceException {
        Credential credentialDB = getCredential(userID);
        if (credentialDB != null) {
            List<String> roles = credentialDB.getRoles();
            if (roles.contains(Roles.prefix + Roles.ADMIN))
                return false;
            else
                roles.add(Roles.prefix + Roles.ADMIN);
            credentialDB.setRoles(roles);
            updateCredential(credentialDB);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean removeAdminFromAdmin(User user, UserDetails userDetails, String userID, String line) throws UnknownServiceException {
        User userPrincipal = getUserByUsername(userDetails.getUsername());
        if (userPrincipal.getLines() != null && user.getLines() != null && userPrincipal.getLines().contains(line)) {
            Credential credentialDB = getCredential(userID);
            if (credentialDB != null) {
                List<String> roles = credentialDB.getRoles();
                if (roles.contains(Roles.prefix + Roles.ADMIN)) {
                    roles.remove(Roles.prefix + Roles.ADMIN);
                    credentialDB.setRoles(roles);
                    updateCredential(credentialDB);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public boolean removeAdminFromSystemAdmin(User user, String userID) throws UnknownServiceException {
        Credential credentialDB = getCredential(userID);
        if (credentialDB != null) {
            List<String> roles = credentialDB.getRoles();
            if (roles.contains(Roles.prefix + Roles.ADMIN)) {
                roles.remove(Roles.prefix + Roles.ADMIN);
                credentialDB.setRoles(roles);
                updateCredential(credentialDB);
                return true;
            }
            return false;
        }
        return false;
    }

    //TO-DO Move to Utils
    private Line lineMongoToLine(LineMongo lineMongo)
    {
        ArrayList<PediStop> out = new ArrayList<>();
        for (PediStopMongo p : lineMongo.getOutboundStops())
            out.add(new PediStop(p.getName(), p.getLatitude(), p.getLongitude(), p.getTime()));
        // out.add(PediStop.builder().name(p.getName()).latitude(p.getLatitude()).longitude(p.getLongitude()).build());
        ArrayList<PediStop> ret = new ArrayList<>();
        for (PediStopMongo p : lineMongo.getReturnStops())
            ret.add(new PediStop(p.getName(), p.getLatitude(), p.getLongitude(),  p.getTime()));
        // ret.add(PediStop.builder().name(p.getName()).latitude(p.getLatitude()).longitude(p.getLongitude()).build());
        List<LineSubscribedChild> templist =lineMongo.getSubscribedChildren();
        ArrayList<Child> children = new ArrayList<>();
        for (LineSubscribedChild l : templist)
        {
            children.add(l.getChild());
        }
        return new Line(lineMongo.getName(), out, ret, children);
    }
    private boolean isParentorAdmin(String UserID, List<String > roles, Child child)
    {
        //TO-DO: Check transactionality needing
        User u = userRepository.findByUsername(UserID);
        boolean find = false;
        if(!roles.contains(Roles.prefix + Roles.ADMIN) && !roles.contains(Roles.prefix + Roles.SYSTEM_ADMIN)  )
        {
            for (Child c : u.getChildren()) {

                if (c.getCF().equals(child.getCF())) {
                    find = true;
                    break;
                }

            }
            if (!find) {
                return false;
            }
            return true;
        }
        return true;
        //
    }

    private Date convertLocalDateTOMongoDate(LocalDate date)
    {
        GregorianCalendar gc = new GregorianCalendar(date.getYear(), date.getMonth().getValue() - 1, date.getDayOfMonth());
        gc.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date d = gc.getTime();
        return d;

    }
}
