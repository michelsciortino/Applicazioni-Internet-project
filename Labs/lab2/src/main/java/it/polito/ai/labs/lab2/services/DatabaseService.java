package it.polito.ai.labs.lab2.services;

import it.polito.ai.labs.lab2.models.json.Line;
import it.polito.ai.labs.lab2.models.json.PediStop;
import it.polito.ai.labs.lab2.models.mongo.LineMongo;
import it.polito.ai.labs.lab2.models.mongo.PediStopMongo;
import it.polito.ai.labs.lab2.models.mongo.ReservationMongo;
import it.polito.ai.labs.lab2.models.rest.LineReservations;
import it.polito.ai.labs.lab2.models.rest.Reservation;
import it.polito.ai.labs.lab2.repositories.LineRepository;
import it.polito.ai.labs.lab2.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public boolean insertLine(Line line) throws UnknownServiceException {
        try{
            ArrayList<PediStopMongo> outboundStops= new ArrayList<>();
            for (PediStop p:line.outboundStops)
                outboundStops.add(new PediStopMongo(p.longitude,p.latitude,p.name));

            ArrayList<PediStopMongo> returnStops= new ArrayList<>();
            for (PediStop p:line.returnStops)
                returnStops.add(new PediStopMongo(p.longitude,p.latitude,p.name));

            LineMongo lineMongo=new LineMongo(line.name,outboundStops,returnStops);
            lineRepository.save(lineMongo);
            return true;
        }
        catch (Exception e){
            throw new UnknownServiceException(e.toString());
        }
    }

    @Override
    public Collection<String> getLinesNames() {
        List<LineMongo> list=lineRepository.findAllName();
        List<String> names=new ArrayList<>();
        for(LineMongo lineMongo :list)
            names.add(lineMongo.getName());
        return names;
    }

    @Override
    public Line getLine(String lineName) throws UnknownServiceException {
        try {
            LineMongo lineMongo = lineRepository.findLineByName(lineName);
            ArrayList<PediStop> out = new ArrayList<>();
            for (PediStopMongo p : lineMongo.getOutboundStops())
                out.add(PediStop.builder().name(p.getName()).latitude(p.getLatitude()).longitude(p.getLongitude()).build());
            ArrayList<PediStop> ret = new ArrayList<>();
            for (PediStopMongo p : lineMongo.getReturnStops())
                ret.add(PediStop.builder().name(p.getName()).latitude(p.getLatitude()).longitude(p.getLongitude()).build());
            return Line.builder().name(lineMongo.getName()).outboundStops(out).returnStops(ret).build();
        } catch (Exception e) {
            throw new UnknownServiceException(e.toString());
        }
    }

    @Override
    public LineReservations getLineReservations(String lineName, LocalDateTime dateTime) throws UnknownServiceException {
        try {
            LineMongo lineMongo = lineRepository.findLineByName(lineName);

            Map<String,Collection<String>> outwardStopsReservations = new HashMap<>();
            Map<String,Collection<String>> backStopsReservations = new HashMap<>();

            for(PediStopMongo s:lineMongo.getOutboundStops()){
                List<String> names=new ArrayList<>();
                for(ReservationMongo r :reservationRepository.getReservationByStopNameAndDirection(s.getName(),"outbound",dateTime,lineName))
                    names.add(r.getChildName());
                outwardStopsReservations.put(s.getName(),names);
            }
            for(PediStopMongo s:lineMongo.getReturnStops()){
                List<String> names=new ArrayList<>();
                for(ReservationMongo r :reservationRepository.getReservationByStopNameAndDirection(s.getName(),"return",dateTime,lineName))
                    names.add(r.getChildName());
                backStopsReservations.put(s.getName(),names);
            }
            return LineReservations.builder().backStopsReservations(backStopsReservations).outwardStopsReservations(outwardStopsReservations).build();
        } catch (Exception e) {
            throw new UnknownServiceException(e.toString());
        }
    }

    @Override
    public String addReservation(String UserID, Reservation reservation, String lineName, LocalDateTime dateTime) throws UnknownServiceException {
        try{
                if (lineRepository.findLineByName(lineName)!=null)
                    reservationRepository.save(ReservationMongo.builder().childName(reservation.getChildName()).direction(reservation.getDirection()).stopName(reservation.getStopName()).data(dateTime).userID(UserID).lineName(lineName).build());
                else
                    throw new ServiceNotFoundException();
        }
        catch (Exception e){
            throw new UnknownServiceException(e.toString());
        }
        return null;
    }

    @Override
    public boolean updateReservation(String UserID, Reservation reservation, String lineName, LocalDateTime dateTime, String reservationId) throws UnknownServiceException {
        try{
            ReservationMongo rp=reservationRepository.findById(reservationId).get();
            rp.setChildName(reservation.getChildName());
            rp.setDirection(reservation.getDirection());
            rp.setLineName(lineName);
            rp.setStopName(reservation.getStopName());
            reservationRepository.save(rp);
            return true;
        }
        catch (Exception e){
            throw new UnknownServiceException(e.toString());
        }
    }

    @Override
    public boolean deleteReservation(String UserID, String lineName, LocalDateTime dateTime, String reservationId) throws UnknownServiceException {
        try{
              reservationRepository.delete(reservationRepository.findById(reservationId).get());
              return true;
        }
        catch (Exception e){
            throw new UnknownServiceException(e.toString());
        }
    }

    @Override
    public Reservation getReservation(String UserID, String lineName, LocalDateTime dateTime, String reservationId) throws UnknownServiceException {
        try{
            ReservationMongo rm= reservationRepository.findById(reservationId).get();
            return Reservation.builder().childName(rm.getChildName()).direction(rm.getDirection()).stopName(rm.getStopName()).build();
        }
        catch (Exception e){
            throw new UnknownServiceException(e.toString());
        }
    }
}
