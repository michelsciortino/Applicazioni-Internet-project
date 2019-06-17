package it.polito.ai.lab5.files.json;


import org.springframework.boot.jackson.JsonComponent;

import java.util.Date;

@JsonComponent
public class ReservationJson {
    public String username;
    public String childName;
    public String childSurname;
    public String childCf;

    public String lineName;
    public String stopName;
    public String direction;
    public Date data;

    public boolean present;

    public ReservationJson(String username, String childName, String childSurname, String childCf, String lineName, String stopName, String direction, Date data, boolean present) {
        this.username = username;
        this.childName = childName;
        this.childSurname = childSurname;
        this.childCf = childCf;
        this.lineName = lineName;
        this.stopName = stopName;
        this.direction = direction;
        this.data = data;
        this.present = present;
    }

    public ReservationJson() {
    }
}


