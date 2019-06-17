import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { Line } from 'src/app/models/line';
import { Observable, Subscription } from 'rxjs';
import { AttendancesService } from 'src/app/services/attendances/attendances.service';
import { HttpErrorResponse } from '@angular/common/http';

import { map, catchError } from 'rxjs/operators'
import { Ride, RideStop, Reservation, SubscrivableChild } from 'src/app/models/ride';
import { stringify } from 'querystring';

@Component({
  selector: 'attendances',
  templateUrl: './attendances.component.html'
})
export class AttendancesComponent implements OnInit, OnDestroy {
  subscription: Subscription;
  lines
  ways = [{ id: 'outward', text: "andata" }, { id: 'backward', text: "ritorno" }]
  way = this.ways[0]

  selectedLine
  date = new Date()
  ride: Ride
  unsubscribedChildren


  constructor(private attendecesService: AttendancesService) {

  }

  ngOnInit() {
    this.subscription = this.attendecesService.getLines()
      .pipe(
        map(
          (data: any) => data,
          (error: HttpErrorResponse) =>
            console.log(error)
        )
      ).subscribe(
        (data: Array<any>) => {
          if (data.length == 0) return
          this.lines = data
          this.selectedLine = data[0]
          let _way = this.way;
          let _line = this.selectedLine

          this.attendecesService.getLineReservations(this.selectedLine.name, this.date).subscribe(
            (data) => this.handleReservations(data, _line, _way)
          )
        }
      )
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  public onRideSelected() {
    this.attendecesService
      .getLineReservations(this.selectedLine.name, this.date)
      .subscribe((data) => this.handleReservations(data, this.selectedLine, this.way))
  }

  public setChildPresence(selectedLine, way, reservation) {
    let present = !reservation.present

    let clone : Reservation = reservation.clone()
    clone.present=present

    this.attendecesService.updatePresence(selectedLine.name, this.date, clone).subscribe(
      (response: boolean) => {
        if (response)
          reservation.present = present
      }
    )


  }

  private handleReservations(data, line, way) {
    this.ride = new Ride()

    let subscribedChildren = new Map<String, any>()
    let reservationsMap = new Map<string, Array<any>>()
    let _entries;
    let _linestops;

    if (way.id == 'outward') {
      _entries = Object.entries(data.outwardStopsReservations)
      _linestops = line.outboundStops
    }
    else {
      _entries = Object.entries(data.backStopsReservations)
      _linestops = line.returnStops
    }

    line.subscribedChildren.forEach(child => {
      let _child = new SubscrivableChild()
      _child.childName = child.child.name
      _child.childSurname = child.child.surname
      _child.cf = child.child.cf
      _child.subscribed = false
      subscribedChildren.set(child.child.cf, _child)
    })

    _entries.forEach((entry: Array<any>) => {
      reservationsMap.set(entry[0], entry[1])
    })

    _linestops.forEach(stop => {
      let _stop = new RideStop()
      _stop.name = stop.name
      _stop.time = stop.time
      _stop.reservations = new Array()
      let _reservations = reservationsMap.get(stop.name)
      _reservations.forEach((reservation: any) => {
        let _reservation = new Reservation()
        _reservation.id = reservation.id

        let childRes = subscribedChildren.get(reservation.childCf);
        childRes.subscribed = true
        childRes.parentUsername = reservation.parentUsername
        _reservation.childName = reservation.childName
        _reservation.childCf=reservation.childCf
        _reservation.direction = reservation.direction
        _reservation.childSurname = reservation.childSurname
        _reservation.parentUsername = reservation.parentUsername
        _reservation.present = reservation.present
        _reservation.stopName = reservation.stopName
        _stop.reservations.push(_reservation)
      });
      this.ride.stops.push(_stop)
    });

    this.unsubscribedChildren = new Array()
    subscribedChildren.forEach((value, key) => {
      if (!value.subscribed)
        this.unsubscribedChildren.push(value)
    });
  }

}
