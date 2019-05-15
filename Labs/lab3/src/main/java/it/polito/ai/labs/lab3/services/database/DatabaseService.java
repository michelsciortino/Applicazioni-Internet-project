package it.polito.ai.labs.lab3.services.database;

import it.polito.ai.labs.lab3.controllers.models.LineReservations;
import it.polito.ai.labs.lab3.controllers.models.Reservation;
import it.polito.ai.labs.lab3.files.json.Line;
import it.polito.ai.labs.lab3.files.json.PediStop;
import it.polito.ai.labs.lab3.services.database.models.*;
import it.polito.ai.labs.lab3.services.database.repositories.*;
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
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DatabaseService implements DatabaseServiceInterface {

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

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public boolean insertLine(Line line) throws UnknownServiceException {
        try {
            ArrayList<PediStopMongo> outboundStops = new ArrayList<>();
            for (PediStop p : line.outboundStops)
                outboundStops.add(new PediStopMongo(p.longitude, p.latitude, p.name));

            ArrayList<PediStopMongo> returnStops = new ArrayList<>();
            for (PediStop p : line.returnStops)
                returnStops.add(new PediStopMongo(p.longitude, p.latitude, p.name));

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
    public Line getLine(String lineName) throws UnknownServiceException {
        try {
            LineMongo lineMongo = lineRepository.findLineByName(lineName);
            ArrayList<PediStop> out = new ArrayList<>();
            for (PediStopMongo p : lineMongo.getOutboundStops())
                out.add(new PediStop(p.getName(), p.getLatitude(), p.getLongitude()));
            // out.add(PediStop.builder().name(p.getName()).latitude(p.getLatitude()).longitude(p.getLongitude()).build());
            ArrayList<PediStop> ret = new ArrayList<>();
            for (PediStopMongo p : lineMongo.getReturnStops())
                ret.add(new PediStop(p.getName(), p.getLatitude(), p.getLongitude()));
            // ret.add(PediStop.builder().name(p.getName()).latitude(p.getLatitude()).longitude(p.getLongitude()).build());
            return new Line(lineMongo.getName(), out, ret);
            //return Line.builder().name(lineMongo.getName()).outboundStops(out).returnStops(ret).build();
        } catch (Exception e) {
            throw new UnknownServiceException(e.getMessage());
        }
    }

    @Override
    public LineReservations getLineReservations(String lineName, LocalDateTime dateTime) throws UnknownServiceException {
        try {
            LineMongo lineMongo = lineRepository.findLineByName(lineName);

            Map<String, Collection<String>> outwardStopsReservations = new HashMap<>();
            Map<String, Collection<String>> backStopsReservations = new HashMap<>();

            for (PediStopMongo s : lineMongo.getOutboundStops()) {
                List<String> names = new ArrayList<>();
                for (ReservationMongo r : reservationRepository.getReservationByStopNameAndDirection(s.getName(), "outbound", dateTime, lineName))
                    names.add(r.getChildName());
                outwardStopsReservations.put(s.getName(), names);
            }
            for (PediStopMongo s : lineMongo.getReturnStops()) {
                List<String> names = new ArrayList<>();
                for (ReservationMongo r : reservationRepository.getReservationByStopNameAndDirection(s.getName(), "return", dateTime, lineName))
                    names.add(r.getChildName());
                backStopsReservations.put(s.getName(), names);
            }
            return LineReservations.builder().backStopsReservations(backStopsReservations).outwardStopsReservations(outwardStopsReservations).build();
        } catch (Exception e) {
            throw new UnknownServiceException(e.getMessage());
        }
    }

    @Override
    public String addReservation(String UserID, Reservation reservation, String lineName, LocalDateTime dateTime) throws UnknownServiceException {
        try {
            ReservationMongo res;
            if (lineRepository.findLineByName(lineName) != null) {
                res = reservationRepository.save(ReservationMongo.builder().childName(reservation.getChildName()).direction(reservation.getDirection()).stopName(reservation.getStopName()).data(dateTime).userID(UserID).lineName(lineName).build());
                return res.toString();
            } else
                throw new ServiceNotFoundException();
        } catch (Exception e) {
            throw new UnknownServiceException(e.getMessage());
        }

    }

    @Override
    @Transactional
    public boolean updateReservation(String UserID, Reservation reservation, String lineName, LocalDateTime dateTime, String reservationId) throws UnknownServiceException {
        try {
            ReservationMongo rp = reservationRepository.findById(reservationId).get();
            rp.setChildName(reservation.getChildName());
            rp.setDirection(reservation.getDirection());
            rp.setLineName(lineName);
            rp.setStopName(reservation.getStopName());
            reservationRepository.save(rp);
            return true;
        } catch (Exception e) {
            throw new UnknownServiceException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean deleteReservation(String UserID, String lineName, LocalDateTime dateTime, String reservationId) throws UnknownServiceException {
        try {
            reservationRepository.delete(reservationRepository.findById(reservationId).get());
            return true;
        } catch (Exception e) {
            throw new UnknownServiceException(e.getMessage());
        }
    }

    @Override
    public Reservation getReservation(String UserID, String lineName, LocalDateTime dateTime, String reservationId) throws UnknownServiceException {
        try {
            ReservationMongo rm = reservationRepository.findById(reservationId).get();
            return Reservation.builder().childName(rm.getChildName()).direction(rm.getDirection()).stopName(rm.getStopName()).build();
        } catch (Exception e) {
            throw new UnknownServiceException(e.getMessage());
        }
    }

    @Override
    public Credential insertCredential(String username, String password, List<String> role) throws UnknownServiceException {
        try {
            Credential credential = null;
            if (!credentialRepository.findByUsername(username).isPresent())
                credential = credentialRepository.save(new Credential(this.passwordEncoder.encode(password), username, role));
            else
                return null;
            return credential;
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
           // if (credentialRepository.findByUsername(credential.getUsername()).isPresent())
            credentialRepository.save(credential);
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
    public boolean adminmakeAdmin(User user, UserDetails userDetails, String userID) throws UnknownServiceException {
        User userPrincipal = getUserByUsername(userDetails.getUsername());
        if (userPrincipal.getLines() != null && user.getLines() != null && userPrincipal.getLines().contains(user.getLines().get(0)))
        {
            if (getUser(userID) != null) {
                insertUser(user);
                return true;
            }

            return false;

        }
    }

    @Override
    @Transactional
    public boolean superadminmakeAdmin(User user, String userID) throws UnknownServiceException {
        if (getUser(userID) != null)
        {
            insertUser(user);
            return true;
        }
        return  false;
    }



}
