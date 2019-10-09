import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { DirectionType } from 'src/app/models/race';
import { ParentService } from 'src/app/services/parent/parent.service';
import { reserveChildrenRequest } from 'src/app/models/reserve-children-request';
import { Line } from 'src/app/models/line';
import { Child } from 'src/app/models/child';
import { Stop } from 'src/app/models/stop';
import { Passenger } from 'src/app/models/passenger';
import { ChildRace } from 'src/app/models/child-race';
import Utils from 'src/app/utils/utils';

@Component({
    selector: 'app-reserve-child-dialog',
    templateUrl: './reserve-child.dialog.html',
    styleUrls: ['./reserve-child.dialog.css']
})
export class ReserveChildDialog {
    racesReservableChecked: ChildRace[] = [];
    child: Child;
    line: Line;
    stops: Stop[];
    stopSelected: Stop;

    constructor(public dialogRef: MatDialogRef<ReserveChildDialog>, private parentSvc: ParentService, @Inject(MAT_DIALOG_DATA) public data: any) {
        this.child = this.data.child;
        console.log(this.data)
        this.racesReservableChecked = this.data.racesReservableChecked;
        this.line = this.data.line;
        if (this.data.direction.id === DirectionType.OUTWARD) {
            this.stops = this.line.outwardStops;
            this.stopSelected = this.stops[0];
        }
        else {
            this.stops = this.line.returnStops;
            this.stopSelected = this.stops[0];
        }
    }

    getTime = Utils.getTime;

    getTimeWithSecond = Utils.getTimeWithSecond;

    reserveChild(): void {
        this.racesReservableChecked.forEach(race => {
            let passengers: Passenger[] = [];
            let passenger: Passenger = new Passenger;
            passenger.childDetails = this.child;
            passenger.reserved = true;
            passenger.stopReserved = this.stopSelected;
            passengers.push(passenger);
            let reservation: reserveChildrenRequest = new reserveChildrenRequest;
            reservation.children = passengers;
            reservation.clientRace = race.race;
            console.log(reservation)
            this.parentSvc.reserveChildrenToRace(reservation).then((result) => {
                this.parentSvc.reservableChanged("Reserve child" + this.child.cf);
                this.dialogRef.close()
            })
                .catch((error) => this.dialogRef.close());
        })
    }

    onCancel(): void {
        this.dialogRef.close();
    }
}
