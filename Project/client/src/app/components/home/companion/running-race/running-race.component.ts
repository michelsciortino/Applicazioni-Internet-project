import {
  Component,
  OnInit,
  AfterViewInit,
  ChangeDetectorRef,
  ViewChild
} from '@angular/core';
import { CompanionService } from 'src/app/services/companion/companion.service';
import { Race, DirectionType, RaceState } from 'src/app/models/race';
import { NguCarouselConfig, NguCarousel } from '@ngu/carousel';
import { LineService } from 'src/app/services/lines/line-races.service';
import { Router, ActivatedRoute } from '@angular/router';
import { Passenger, PassengerState } from 'src/app/models/passenger';
import { MatRadioButton } from '@angular/material';
import { Stop } from 'src/app/models/stop';
import Utils from 'src/app/utils/utils';

enum StopState {
  REACHED = 'REACHED',
  UNREACHED = 'UNREACHED',
  UNREACHABLE = 'UNREACHABLE',
  LEFT = 'LEFT'
}

class TiledStop {
  isEditable: boolean;
  index: number;
  name: string;
  time: string;
  reservedChildren: Passenger[];
  nonReservedChildren: Passenger[];
  absentChildren: Passenger[];
  reached: boolean;
  reachedAt: any;
  left: boolean;
  leftAt: any;
  state: StopState;
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

  takenChildren: Passenger[];
  reservedChildren: Passenger[];
  otherChildren: Passenger[];
  absentChildren: Passenger[];

  lastReachedStop: TiledStop;
  currentStop: TiledStop;

  utils = Utils;

  private loading: boolean;

  private carouselToken: string;
  @ViewChild('lineCarousel', { static: false }) carousel: NguCarousel<any>;

  // Shown Data
  tiledStops: TiledStop[];

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

  constructor(
    private companionSvc: CompanionService,
    private lineSvc: LineService,
    private router: Router,
    private route: ActivatedRoute,
    private cdRef: ChangeDetectorRef
  ) {
    this.tiledStops = [];
    this.otherChildren = [];
    this.reservedChildren = [];
    this.takenChildren = [];
    this.absentChildren = [];
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

  private getRace() {
    this.loading = true;
    return this.lineSvc
      .getRace(this.lineName, this.date, this.direction)
      .then(race => {
        console.log(race);
        this.race = race;
        this.race.reachedStops = race.reachedStops || [];
        this.cdRef.detectChanges();
        this.populateView();
      })
      .catch(() => { })
      .then(() => (this.loading = false));
  }

  private populateView() {
    const stops =
      this.race.direction === DirectionType.OUTWARD
        ? this.race.line.outwardStops
        : this.race.line.returnStops;
    this.tiledStops = [];
    this.currentStop = null;
    this.otherChildren = [];
    this.takenChildren = [];
    this.absentChildren = [];
    this.reservedChildren = [];
    let i = 0;
    stops.forEach(stop => {
      const tiledStop = new TiledStop();
      tiledStop.index = i;
      tiledStop.isEditable = false;
      tiledStop.name = stop.name;
      tiledStop.time = Utils.getTime(
        this.race.date.getTime() + stop.delayInMillis
      );
      tiledStop.state = StopState.UNREACHABLE;
      tiledStop.reservedChildren = [];
      tiledStop.nonReservedChildren = [];
      tiledStop.absentChildren = [];
      tiledStop.stopRef = stop;
      this.tiledStops.push(tiledStop);
      i++;
    });
    // setting companion stops range
    let accompanying = false;
    for (const stop of this.tiledStops) {
      if (stop.name === this.race.companion.initialStop.name)
        accompanying = true;
      if (accompanying) stop.state = StopState.UNREACHED;
      if (stop.name === this.race.companion.finalStop.name)
        accompanying = false;
    }

    // setting stops state based on reachedStops
    for (i = 0; this.race.reachedStops && i < this.race.reachedStops.length; i++) {
      this.tiledStops[i].reached = true;
      this.currentStop = this.tiledStops[i];
      this.tiledStops[i].reachedAt = Utils.getTime(
        this.race.date.getTime() + this.race.reachedStops[i].arrivalDelay
      );
      if (this.race.reachedStops[i].departureDelay !== -1) {
        this.tiledStops[i].state = StopState.LEFT;
        this.tiledStops[i].left = true;
        this.tiledStops[i].leftAt = Utils.getTime(
          this.race.date.getTime() + this.race.reachedStops[i].departureDelay
        );
        this.lastReachedStop = this.tiledStops[i];
        if (i < this.tiledStops.length - 1)
          this.currentStop = this.tiledStops[i + 1];
      } else {
        this.tiledStops[i].state = StopState.REACHED;
        this.tiledStops[i].left = false;
      }
    }

    // inserting passengers
    for (const passenger of this.race.passengers) {
      if (passenger.reserved) {
        const stop = this.tiledStops.find(s => s.name === passenger.stopReserved.name);
        if (stop) {
          stop.reservedChildren.push(passenger);
          this.reservedChildren.push(passenger);
        }
        if (passenger.state === PassengerState.TAKEN && passenger.stopTaken.name !== passenger.stopReserved.name) {
          const otherStop = this.tiledStops.find(s => s.name === passenger.stopTaken.name);
          if (otherStop) otherStop.absentChildren.push(passenger);
        }
        if (passenger.state === PassengerState.ABSENT)
          this.absentChildren.push(passenger);
      } else if (passenger.state === PassengerState.TAKEN || passenger.state === PassengerState.DELIVERED) {
        // non reserved child that has been taken in a previous stop or has been delivered
        const stop = this.tiledStops.find(
          s => s.name === passenger.stopTaken.name
        );
        if (stop) stop.nonReservedChildren.push(passenger);
      } else this.otherChildren.push(passenger);

      if (passenger.state === 'TAKEN') this.takenChildren.push(passenger);
    }

    if (this.carousel && this.currentStop) this.moveTo(this.currentStop.index);
  }

  setChildState(radio: MatRadioButton, child: Passenger, stop: TiledStop) {
    if (stop.left || radio.disabled) return;
    if (child.state === radio.value) {
      child.state = PassengerState.NULL;
      if (radio.value === 'TAKEN') child.stopTaken = null;
      else if (radio.value === 'DELIVERED') child.stopDelivered = null;
      else if (radio.value === 'ABSENT') {
        child.stopTaken = null;
        child.stopDelivered = null;
      }
    } else {
      child.state = radio.value;
      if (radio.value === 'TAKEN') child.stopTaken = stop.stopRef;
      else if (radio.value === 'DELIVERED') child.stopDelivered = stop.stopRef;
    }
    this.cdRef.detectChanges();
  }

  setAbsentState(radio: MatRadioButton, child: Passenger, stop: TiledStop) {
    if (stop.left || radio.disabled) return;
    if (child.state === radio.value) {
      child.state = PassengerState.NULL;
      child.stopTaken = null;
    } else {
      child.state = radio.value;
      child.stopTaken = stop.stopRef;
    }
    this.cdRef.detectChanges();
  }

  setNonRegisteredState(radio: MatRadioButton, child: Passenger, stop: TiledStop) {
    if (child.state === radio.value) {
      child.state = PassengerState.NULL;
      child.stopTaken = null;
    } else {
      child.state = PassengerState.TAKEN;
      child.stopTaken = stop.stopRef;
    }
    this.cdRef.detectChanges();
  }

  confirmOutwardStop() {
    if (!this.tiledStops || !this.carousel) return;
    const stop = this.tiledStops[this.carousel.currentSlide];
    this.loading = true;
    const confirmPromise = Promise.resolve();

    confirmPromise
      .then(() => {
        if (!stop.reached)
          // console.log('sending stop reached');
          return this.companionSvc.stopReached(
            this.race.line.name,
            this.race.direction,
            this.race.date,
            stop.name
          );
      })
      .then(() => {
        const takenChildrenInStop = stop.reservedChildren
          .filter(child => child.state === PassengerState.TAKEN)
          .concat(
            this.otherChildren.filter(
              child =>
                child.state === PassengerState.TAKEN &&
                child.stopTaken.name === stop.name
            )
          )
          .concat(
            this.absentChildren.filter(
              child =>
                child.state === PassengerState.TAKEN &&
                child.stopTaken.name === stop.name
            )
          );
        if (takenChildrenInStop.length > 0)
          // console.log('sending taken children');
          return this.companionSvc.takeChildren(
            this.race.line.name,
            this.race.direction,
            this.race.date,
            stop.name,
            takenChildrenInStop
          );
      })
      .then(() => {
        const absentChildrenInStop = stop.reservedChildren.filter(
          child => child.state === PassengerState.ABSENT
        );
        if (absentChildrenInStop.length > 0)
          // console.log('sending absent children');
          return this.companionSvc.absentChildren(
            this.race.line.name,
            this.race.direction,
            this.race.date,
            stop.name,
            absentChildrenInStop
          );
      })
      .then(() => {
        // console.log('sendig stop left');
        return this.companionSvc.stopLeft(
          this.race.line.name,
          this.race.direction,
          this.race.date,
          stop.name
        );
      })
      .catch(error => {
        console.log(error);
      })
      .then(() => {
        // console.log('refreshing race');
        this.getRace();
      })
      .catch(() => { });
  }

  public confirmReturnStop() {
    if (!this.tiledStops || !this.carousel) return;
    const stop = this.tiledStops[this.carousel.currentSlide];
    this.loading = true;
    const confirmPromise = Promise.resolve();

    confirmPromise
      .then(() => {
        if (!stop.reached)
          // console.log('sending stop reached');
          return this.companionSvc.stopReached(
            this.race.line.name,
            this.race.direction,
            this.race.date,
            stop.name
          );
      })
      .then(() => {
        if (stop.index > 0) return;
        const takenChildrenInStop = this.reservedChildren.filter(
          child => child.state === PassengerState.TAKEN
        );
        if (takenChildrenInStop.length > 0)
          // console.log('sending taken children');
          return this.companionSvc.takeChildren(
            this.race.line.name,
            this.race.direction,
            this.race.date,
            stop.name,
            takenChildrenInStop
          );
      })
      .then(() => {
        if (stop.index > 0) return;
        const absentChildrenInStop = this.reservedChildren.filter(
          child => child.state === PassengerState.ABSENT
        );
        if (absentChildrenInStop.length > 0)
          // console.log('sending absent children');
          return this.companionSvc.absentChildren(
            this.race.line.name,
            this.race.direction,
            this.race.date,
            stop.name,
            absentChildrenInStop
          );
      })
      .then(() => {
        if (stop.index === 0) return;
        const deliverChildrenInStop = stop.reservedChildren.filter(
          child => child.state === PassengerState.DELIVERED
        );
        if (deliverChildrenInStop.length > 0)
          // console.log('sending delivered children');
          return this.companionSvc.deliverChildren(
            this.race.line.name,
            this.race.direction,
            this.race.date,
            stop.name,
            deliverChildrenInStop
          );
      })
      .then(() => {
        // console.log('sendig stop left');
        return this.companionSvc.stopLeft(
          this.race.line.name,
          this.race.direction,
          this.race.date,
          stop.name
        );
      })
      .catch(error => {
        console.log(error);
      })
      .then(() => {
        // console.log('refreshing race');
        this.getRace();
      })
      .catch(error => {
        console.log(error);
      });
  }

  public deliverAllAtSchool() {
    if (!this.tiledStops || !this.carousel) return false;
    const stop = this.tiledStops[this.carousel.currentSlide];
    this.loading = true;
    this.companionSvc
      .stopReached(
        this.race.line.name,
        this.race.direction,
        this.race.date,
        stop.name
      )
      .then(() => {
        // console.log('delivering all children');
        return this.companionSvc.deliverChildren(
          this.race.line.name,
          this.race.direction,
          this.race.date,
          stop.name,
          this.takenChildren
        );
      })
      .then(() => {
        // console.log('refreshing race');
        this.getRace();
      })
      .catch(error => {
        console.log(error);
      })
      .then(() => (this.loading = false));
  }

  public endRace() {
    // console.log('ending race');
    this.loading = true;
    this.companionSvc
      .endRace(this.lineName, this.direction, this.date)
      .then(() => {
        // console.log('refreshing race');
        this.getRace();
      })
      .catch(error => {
        // console.log(error);
      })
      .then(() => (this.loading = false));
  }

  public moveTo(tile) {
    this.carousel.moveTo(tile, false);
  }

  private canConfirm() {
    if (
      this.loading ||
      !this.tiledStops ||
      !this.carousel ||
      !this.race ||
      this.race.raceState === RaceState.ENDED
    )
      return false;
    const stop = this.tiledStops[this.carousel.currentSlide];
    if (!stop) return false;
    if (this.race.direction === DirectionType.OUTWARD)
      return (
        stop.reservedChildren.filter(
          c => c.reserved && c.state === PassengerState.NULL
        ).length === 0
      );
    else if (stop.index === 0)
      return (
        this.reservedChildren &&
        this.reservedChildren.filter(c => c.state === PassengerState.NULL)
          .length === 0
      );
    else
      return (
        stop &&
        stop.reservedChildren.filter(
          c =>
            c.state !== PassengerState.DELIVERED &&
            c.state !== PassengerState.ABSENT
        ).length === 0
      );
  }

  public canShowConfirmButton(): boolean {
    if (
      this.loading ||
      !this.tiledStops ||
      !this.carousel ||
      !this.race ||
      this.race.raceState === RaceState.ENDED
    )
      return false;
    const stop = this.tiledStops[this.carousel.currentSlide];
    if (
      !stop ||
      stop.left ||
      (stop.index > 0 && this.tiledStops[stop.index - 1].left === false) ||
      (this.race.direction === DirectionType.OUTWARD &&
        stop.index === this.tiledStops.length - 1)
    )
      return false;
    if (
      this.race.reachedStops.filter(s => s.departureDelay !== -1).length ===
      stop.index
    )
      return true;

    /*return stop.state !== StopState.LEFT &&
      this.race.reachedStops &&
      this.race.reachedStops.length === stop.index &&
      (this.race.direction === DirectionType.OUTWARD
        ? stop.index !== this.tiledStops.length - 1
        : true)
      ? true
      : false;*/
  }

  public canShowEndRaceButton(): boolean {
    if (
      this.loading ||
      !this.tiledStops ||
      !this.carousel ||
      !this.race ||
      this.race.raceState === RaceState.ENDED
    )
      return false;
    const stop = this.tiledStops[this.carousel.currentSlide + 1];
    return this.race.reachedStops.length === this.tiledStops.length;
  }

  public canShowDeliverAllButton(): boolean {
    if (
      this.loading ||
      !this.tiledStops ||
      !this.carousel ||
      !this.race ||
      this.race.raceState === RaceState.ENDED ||
      this.race.direction === DirectionType.RETURN
    )
      return false;
    const stop = this.tiledStops[this.carousel.currentSlide];
    return (
      stop &&
      stop.index === this.tiledStops.length - 1 &&
      this.race.reachedStops.length === this.tiledStops.length - 1
    );
  }
}
