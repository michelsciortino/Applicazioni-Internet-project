import { Component, OnInit, AfterViewInit, ChangeDetectorRef, ViewChild } from "@angular/core";
import { CompanionService } from 'src/app/services/companion/companion.service';
import { Race, DirectionType, RaceState } from 'src/app/models/race';
import { NguCarouselConfig, NguCarousel } from '@ngu/carousel';
import { LineService } from 'src/app/services/lines/line-races.service';
import { Router, ActivatedRoute } from '@angular/router';
import { Child } from 'src/app/models/child';
import { Passenger, PassengerState } from 'src/app/models/passenger';
import { state } from '@angular/animations';
import { MatRadioButton } from '@angular/material';
import { Stop } from 'src/app/models/stop';
import Utils from 'src/app/utils/utils'
import { timeout, timeInterval } from 'rxjs/operators';

enum StopState {
  REACHED = "REACHED",
  UNREACHED = "UNREACHED",
  UNREACHABLE = "UNREACHABLE"
}

class TiledStop {
  isEditable: boolean;
  index: number;
  name: string;
  timespanFromStart: number;
  reservedChildren: Passenger[];
  nonReservedChildren: Passenger[];
  reached: boolean;
  reachedAt: any;
  left: boolean;
  leftAt: any;
  state: StopState
  stopRef: Stop;
}


@Component({
  selector: 'app-companion-running-race',
  templateUrl: './running-race.component.html',
  styleUrls: ['./running-race.component.scss']
})
export class RunningRaceComponent implements AfterViewInit {
  race: Race;
  lineName: string;
  date: Date;
  direction: DirectionType;
  currentStop: TiledStop;
  takenChildren: Passenger[];
  takeableChildren: Passenger[];

  private loading: boolean;

  private carouselToken: string;
  @ViewChild('lineCarousel', { static: false }) carousel: NguCarousel<any>;

  // Shown Data
  tiledStops: TiledStop[];
  otherChildren: Passenger[];

  public carouselTile: NguCarouselConfig = {
    grid: { xs: 1, sm: 1, md: 1, lg: 1, all: 0 },
    slide: 1,
    speed: 450,
    point: {
      visible: true
    },
    load: 3,
    velocity: 0,
    touch: true,
    easing: 'cubic-bezier(0, 0, 0.2, 1)'
  };

  constructor(private companionSvc: CompanionService, private lineSvc: LineService, private router: Router, private route: ActivatedRoute, private cdRef: ChangeDetectorRef) {
    this.tiledStops = [];
    this.otherChildren = [];
    this.takenChildren = [];
    this.takeableChildren = [];
    this.loading = false;
  }

  ngAfterViewInit() {
    this.route.params.subscribe(params => {
      this.lineName = params.lineName;
      this.date = new Date(params.date);
      this.direction = params.direction;
      this.getRace();
    });
  }

  public onConfirm() {

  }

  private getRace() {
    this.loading = true;
    this.lineSvc.getRace(this.lineName, this.date, this.direction)
      .then(race => {
        console.log(race);
        this.race = race;
        this.race.reachedStops = race.reachedStops || [];
        this.cdRef.detectChanges();
        this.populateView();
      })
      .catch(() => { })
      .then(() => this.loading = false);
  }

  private populateView() {
    const stops = this.race.direction == DirectionType.OUTWARD ? this.race.line.outwardStops : this.race.line.returnStops;
    this.tiledStops = [];
    this.currentStop = null;
    this.otherChildren = [];
    this.takenChildren = [];
    let i = 0;
    stops.forEach(
      stop => {
        let tiledStop = new TiledStop();
        tiledStop.index = i;
        tiledStop.isEditable = false;
        tiledStop.name = stop.name;
        tiledStop.timespanFromStart = stop.delayInMillis;
        tiledStop.state = StopState.UNREACHABLE;
        tiledStop.reservedChildren = [];
        tiledStop.nonReservedChildren = [];
        tiledStop.stopRef = stop;
        if (this.race.currentStop && stop.name == this.race.currentStop.name)
          this.currentStop = tiledStop;
        this.tiledStops.push(tiledStop);
        i++;
      }
    )
    let accompanying = false;
    for (let stop of this.tiledStops) {
      if (stop.name == this.race.companion.initialStop.name)
        accompanying = true;
      if (accompanying)
        stop.state = StopState.UNREACHED;
      if (stop.name == this.race.companion.finalStop.name) {
        accompanying = false;
      }
    }
    console.log("inserting reached stops + time")
    for (i = 0; this.race.reachedStops && i < this.race.reachedStops.length; i++) {
      this.tiledStops[i].state = StopState.REACHED;
      this.tiledStops[i].reachedAt = Utils.getTime(this.race.date.getTime() + this.race.reachedStops[i].arrivalDelay)
      this.tiledStops[i].reached = true;
      this.tiledStops[i].left = this.race.reachedStops[i].departureDelay != -1;
    }
    console.log("inserting passengers")
    for (let passenger of this.race.passengers) {
      if (!passenger.stopDelivered.name)
        passenger.stopDelivered = null;
      if (!passenger.stopReserved.name)
        passenger.stopReserved = null;
      if (!passenger.stopTaken.name)
        passenger.stopTaken = null;
      if (passenger.reserved) {
        console.log(passenger)
        let stop = this.tiledStops.find(stop => stop.name == passenger.stopReserved.name);
        if (stop)
          stop.reservedChildren.push(passenger);
      }
      else {
        if (passenger.state == PassengerState.TAKEN || passenger.state == PassengerState.DELIVERED) {
          let stop = this.tiledStops.find(s => s.name == passenger.stopTaken.name);
          if (stop)
            stop.nonReservedChildren.push(passenger);
        }
        else
          this.otherChildren.push(passenger);
      }
      if (passenger.state == "TAKEN")
        this.takenChildren.push(passenger);
    }

    if (this.race.direction == DirectionType.RETURN)
      this.takeableChildren = this.race.passengers.filter(p => p.reserved);

    if (this.currentStop && this.currentStop.index < this.tiledStops.length - 1)
      this.currentStop = this.tiledStops[this.currentStop.index + 1];

    if (this.carousel && this.currentStop) {
      this.moveTo(this.currentStop.index);
    }
  }

  setChildState(radio: MatRadioButton, child: Passenger, stop: TiledStop) {
    event.preventDefault();
    if (stop.reached || radio.disabled) return;
    if (child.state == radio.value) {
      child.state = PassengerState.NULL;
      if (radio.value == "TAKEN")
        child.stopTaken = null;
      else if (radio.value == "DELIVERED")
        child.stopDelivered = null;
      else if (radio.value == "ABSENT") {
        child.stopTaken = null;
        child.stopDelivered = null;
      }
    }
    else {
      child.state = radio.value;
      if (radio.value == "TAKEN")
        child.stopTaken = stop.stopRef;
      else if (radio.value == "DELIVERED")
        child.stopDelivered = stop.stopRef;
    }
    this.cdRef.detectChanges();
  }

  setNonRegisteredState(radio: MatRadioButton, child: Passenger, stop: TiledStop) {
    if (child.state === radio.value) {
      child.state = PassengerState.NULL;
      child.stopTaken = null;
      event.preventDefault();
    }
    else {
      child.state = PassengerState.TAKEN;
      child.stopTaken = stop.stopRef;
    }
    this.cdRef.detectChanges();
  }

  confirmStop() {
    if (!this.tiledStops || !this.carousel) return false;
    const stop = this.tiledStops[this.carousel.currentSlide];
    console.log("sending stop reached");
    this.loading = true;
    this.companionSvc.stopReached(this.race.line.name, this.race.direction, this.race.date, stop.name)
      .then(() => {
        console.log("sending taken children");
        return this.companionSvc.takeChildren(this.race.line.name,
          this.race.direction,
          this.race.date,
          stop.name,
          this.race.direction == DirectionType.OUTWARD ?
            stop.reservedChildren.filter(child => child.state == PassengerState.TAKEN)
              .concat(this.otherChildren.filter(child => child.state == PassengerState.TAKEN && child.stopTaken.name === stop.name))
            : this.takeableChildren.filter(child => child.state == PassengerState.TAKEN));
      })
      .then(() => {
        console.log("sending delivered children");
        return this.companionSvc.deliverChildren(this.race.line.name,
          this.race.direction,
          this.race.date,
          stop.name,
          this.race.direction == DirectionType.OUTWARD ?
            stop.reservedChildren.filter(child => child.state == PassengerState.DELIVERED)
            : this.takeableChildren.filter(child => child.state == PassengerState.DELIVERED));
      })
      .then(() => {
        console.log("sending absent children");
        return this.companionSvc.absentChildren(this.race.line.name,
          this.race.direction,
          this.race.date,
          stop.name,

          this.race.direction == DirectionType.OUTWARD ?
            stop.reservedChildren.filter(child => child.state == PassengerState.ABSENT)
            : this.takeableChildren.filter(child => child.state == PassengerState.ABSENT));
      })
      .then(() => {
        console.log("sendig stop left");
        return this.companionSvc.stopLeft(this.race.line.name, this.race.direction, this.race.date, stop.name);
      })
      .then(() => {
        console.log("refreshing race");
        this.getRace();
      })
      .catch((error) => {
        console.log(error);
      })
      .then(() => this.loading = false);
    console.log(stop);
  }

  public deliverAll() {
    if (!this.tiledStops || !this.carousel) return false;
    const stop = this.tiledStops[this.carousel.currentSlide];
    this.loading = true;
    this.companionSvc.stopReached(this.race.line.name, this.race.direction, this.race.date, stop.name)
      .then(() => {
        console.log("sending taken children");
        return this.companionSvc.takeChildren(this.race.line.name,
          this.race.direction,
          this.race.date,
          stop.name,
          stop.reservedChildren.filter(child => child.state == PassengerState.TAKEN)
            .concat(this.otherChildren.filter(child => child.state == PassengerState.TAKEN && child.stopTaken.name === stop.name)));
      })
      .then(() => {
        console.log("delivering all children");
        return this.companionSvc.deliverChildren(this.race.line.name,
          this.race.direction,
          this.race.date,
          stop.name,
          this.takenChildren);
      })
      .then(() => {
        console.log("refreshing race");
        this.getRace();
      })
      .catch((error) => {
        console.log(error);
      })
      .then(() => this.loading = false);
  }

  public endRace() {
    console.log("ending race");
    this.loading = true;
    this.companionSvc.endRace(this.lineName, this.direction, this.date)
      .then(() => {
        console.log("refreshing race");
        this.getRace();
      })
      .catch((error) => {
        console.log(error);
      })
      .then(() => this.loading = false);
  }

  public moveTo(tile) {
    this.carousel.moveTo(tile, false);
  }


  private canConfirm() {
    if (this.loading || !this.tiledStops || !this.carousel || !this.race || this.race.raceState == RaceState.ENDED) return false;
    const stop = this.tiledStops[this.carousel.currentSlide];
    if (this.race.direction == DirectionType.OUTWARD)
      return stop && stop.reservedChildren.filter(c => c.reserved && c.state == PassengerState.NULL).length == 0;
    else
      return this.takeableChildren && this.takeableChildren.filter(c => c.state == PassengerState.NULL).length == 0;
  }

  public canShowConfirmButton(): boolean {
    if (this.loading || !this.tiledStops || !this.carousel || !this.race || this.race.raceState == RaceState.ENDED) return false;
    const stop = this.tiledStops[this.carousel.currentSlide];
    if (stop.index > 0 && this.tiledStops[stop.index - 1].left == false) return false;
    return stop && stop.state == StopState.UNREACHED && this.race.reachedStops && this.race.reachedStops.length == stop.index && (this.race.direction == DirectionType.OUTWARD ? stop.index != this.tiledStops.length - 1 : true) ? true : false;
  }

  public canShowEndRaceButton(): boolean {
    if (this.loading || !this.tiledStops || !this.carousel || !this.race || this.race.raceState == RaceState.ENDED) return false;
    const stop = this.tiledStops[this.carousel.currentSlide + 1];
    return this.race.reachedStops.length == this.tiledStops.length;
  }

  public canShowDeliverAllButton(): boolean {
    if (this.loading || !this.tiledStops || !this.carousel || !this.race || this.race.raceState == RaceState.ENDED || this.race.direction == DirectionType.RETURN) return false;
    const stop = this.tiledStops[this.carousel.currentSlide];
    return stop && stop.index == this.tiledStops.length - 1 && this.race.reachedStops.length == this.tiledStops.length - 1;
  }
}

// http://localhost:4200/#/runningRace/Bruchi_cantanti/2019-11-21T10:44:00.000Z/OUTWARD