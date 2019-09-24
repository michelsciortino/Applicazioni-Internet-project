import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { Race, DirectionType } from 'src/app/models/race';
import { LineService } from 'src/app/services/lines/line-races.service';
import { ParentService } from 'src/app/services/parent/parent.service';
import { reserveChildrenRequest } from 'src/app/models/reserve-children-request';
import { pipe } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { Line } from 'src/app/models/line';
import { Child } from 'src/app/models/child';
import { Stop } from 'src/app/models/stop';
import { Passenger, PassengerState } from 'src/app/models/passenger';
import { DatePipe } from '@angular/common';
import { PerChildRace } from 'src/app/models/per-child-race';


@Component({
    selector: 'confirm-child-to-race-dialog',
    templateUrl: './confirm-child-to-race.dialog.html',
    styleUrls: ['./confirm-child-to-race.dialog.css']
})
export class ConfirmChildToRaceDialog implements OnInit {

    reservedRaces: PerChildRace[] = [];
    races: Race[] = [];
    reservation: reserveChildrenRequest;
    children: Child[];
    disableSubmit: boolean = true;
    raceSelected: Race;
    outward: boolean;
    stopSelected: Stop;
    childSelected: Child;
    dates: string[] = [];
    reservedDates: string[] = [];

    constructor(public dialogRef: MatDialogRef<ConfirmChildToRaceDialog>,
        private raceSvc: LineService, private parentSvc: ParentService,
        @Inject(MAT_DIALOG_DATA) public data: any, private datePipe: DatePipe) { }

    ngOnInit() {
        this.children = this.data.children;
        this.raceSvc.getLines()
            .pipe(
                map(
                    (data: any) => data,
                    (error: HttpErrorResponse) => console.log(error)
                )
            )
            .subscribe((data: Line[]) => {
                if (data.length == 0) return;
                let lines = data;
                lines.forEach(line => {
                    let toDate = new Date();
                    toDate.setMonth(toDate.getMonth() + 9);
                    this.raceSvc.getRaces(line.name, new Date(), toDate, null).then(
                        (races: Race[]) => {
                            races.forEach(race => {
                                console.log("race " + race)
                                race.passengers.forEach(passenger => {
                                    for (let i = 0; i < this.children.length; i++) {
                                        if (passenger.childDetails.cf === this.children[i].cf) {
                                            this.races.push(race);
                                            let perChildRace = new PerChildRace;
                                            perChildRace.child = this.children[i];
                                            perChildRace.race = race;
                                            if (passenger.reserved) {
                                                this.reservedRaces.push(perChildRace);
                                                this.reservedDates.push(this.datePipe.transform(race.date, "yyyy-MM-dd hh:mm a"));
                                            }
                                            else {
                                                this.reservedRaces.push(perChildRace);
                                                this.dates.push(this.datePipe.transform(race.date, "yyyy-MM-dd hh:mm a"));
                                            }

                                        }
                                    }
                                })
                            })
                        }
                    ).catch((error) => console.log(error));
                });
            });
    }

    confirmChild(): void {
        let passengers: Passenger[] = [];
        this.childSelected
        let passenger: Passenger = new Passenger;
        passenger.childDetails = this.childSelected;
        passenger.reserved = true;
        passenger.state = PassengerState.PRESENT;
        passenger.stopReserved = this.stopSelected;
        passengers.push(passenger);
        this.reservation.children = passengers;
        this.reservation.clientRace = this.raceSelected;
        console.log(this.reservation)
        this.parentSvc.confirmChildrenToRace(this.reservation).then(
            () => { alert("Reservation eseguita correttamente") }
        );
        this.dialogRef.close();
    }

    onCancel(): void {
        this.dialogRef.close();
    }

    onSelectPerChildRace(perChildRace: PerChildRace) {
        if (perChildRace.race.direction === DirectionType.OUTWARD)
            this.outward = true;
        else
            this.outward = false;
        this.stopSelected = null;
        this.disableSubmit = true;
        if (this.raceSelected == null)
            this.raceSelected = new Race;
        this.raceSelected = perChildRace.race;
        this.childSelected;
        this.childSelected = perChildRace.child;
    }

    onSelectStop() {
        console.log(this.stopSelected)
        this.disableSubmit = false;
    }

    onChildSelected(child: Child, event) {
        this.childSelected = child;
    }

}
