package it.polito.ai.project.services.database;

import it.polito.ai.project.exceptions.InternalServerErrorException;
import it.polito.ai.project.generalmodels.ClientLine;
import it.polito.ai.project.generalmodels.ClientPediStop;
import it.polito.ai.project.generalmodels.JsonLine;
import it.polito.ai.project.generalmodels.JsonPediStop;
import it.polito.ai.project.services.database.models.*;
import it.polito.ai.project.services.database.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.net.UnknownServiceException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class DatabaseService implements DatabaseServiceInterface {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private RaceRepository raceRepository;
    @Autowired
    private UserCredentialsRepository userCredentialsRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private UserRepository userRepository;


    @Override
    @Transactional
    public void insertLine(JsonLine line)
    {
        try {
            ArrayList<PediStop> outStops = new ArrayList<>();
            for (JsonPediStop p : line.getOutwardStops())
                outStops.add(new PediStop(p.getName(), p.getLongitude(), p.getLatitude(), p.getDelayinmillis()));
            ArrayList<PediStop> retStops = new ArrayList<>();
            for (JsonPediStop p : line.getReturnStops())
                retStops.add(new PediStop(p.getName(), p.getLongitude(), p.getLatitude(), p.getDelayinmillis()));

            for(String a : line.getAdmins())
            {
                Optional<UserCredentials> res = userCredentialsRepository.findByUsername(a);
                if(res == null)
                {
                    subscribeAdminAndSendMail(a);
                }
                else if(! res.get().getRoles().contains(Roles.prefix + Roles.ADMIN)){
                    res.get().getRoles().add(Roles.prefix + Roles.ADMIN);
                    userCredentialsRepository.save(res.get());
                }

            }
            Line l = new Line(line.getName(), outStops, retStops, line.getAdmins());
            lineRepository.save(l);
        }
        catch (Exception e)
        {
            //TO-DO check unique index Exception
            throw new InternalServerErrorException();
        }
    }
    public boolean subscribeAdminAndSendMail(String username)
    {
        //TO-DO: implementa insert user (admin)
        return true;
    }

    @Override
    public Collection<String> getLinesNames() throws UnknownServiceException
    {
        try {
            List<Line> list = lineRepository.findAllName();
            List<String> names = new ArrayList<>();
            for (Line lineMongo : list)
                names.add(lineMongo.getName());
            return names;
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }

    @Override
    public Collection<ClientLine> getLines() throws UnknownServiceException {
        try {
            List<Line> list = lineRepository.findAll();
            List<ClientLine> lines = new ArrayList<>();
            for (Line lineMongo : list)
                lines.add(LineToClientLine(lineMongo));
            return lines;
        } catch (Exception e) {
            throw new UnknownServiceException(e.getMessage());
        }
    }
    //TO-DO Move to Utils
    private Line LineToClientLine(Line lineMongo) {
        ArrayList<ClientPediStop> out = new ArrayList<>();
        for (PediStop p : lineMongo.getOutwardStops())
            out.add(new ClientPediStop(p.getName(), p.getLatitude(), p.getLongitude(), p.getDelayinmillis()));

        ArrayList<ClientPediStop> ret = new ArrayList<>();
        for (PediStop p : lineMongo.getReturnStops())
            ret.add(new ClientPediStop(p.getName(), p.getLatitude(), p.getLongitude(), p.getDelayinmillis()));

        List<Child> templist = lineMongo.getSubscribedChildren();

        return new ClientLine(lineMongo.getName(), out, ret, lineMongo.getSubscribedChildren(),  lineMongo.getAdmins());

    }
}
