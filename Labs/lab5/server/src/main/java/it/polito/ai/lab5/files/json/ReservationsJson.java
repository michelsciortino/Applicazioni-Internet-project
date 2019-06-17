package it.polito.ai.lab5.files.json;


import org.springframework.boot.jackson.JsonComponent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonComponent
public class ReservationsJson {
  public List<ReservationJson> reservations = new ArrayList<>();

  public ReservationsJson(List<ReservationJson> reservations) {
    this.reservations = reservations;
  }

  public ReservationsJson() {
  }
}


