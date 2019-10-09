import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { UserService } from 'src/app/services/user/user.service';
import { UserInfo } from 'src/app/models/user';
import { DirectionType } from 'src/app/models/race';
import { ParentService, ReservedRacesDataSource } from 'src/app/services/parent/parent.service';
import { Child } from 'src/app/models/child';
import { MatDialog } from '@angular/material';
import { ChildRace } from 'src/app/models/child-race';
import { Line } from 'src/app/models/line';
import { Passenger, PassengerState } from 'src/app/models/passenger';
import { reserveChildrenRequest } from 'src/app/models/reserve-children-request';
import { ConfirmDialog } from '../../dialogs/confirm-dialog/confirm.dialog';
import { ViewParentRaceDialog } from '../view-race/view-race-parent.dialog';
import Utils from 'src/app/utils/utils';

@Component({
    selector: 'app-reserved-races',
    templateUrl: './reserved-races.html',
    styleUrls: ['./reserved-races.css']
})
export class ReservedRacesComponent implements OnInit, OnDestroy {
    userInfo: UserInfo = new UserInfo;
    private userSub: Subscription;

    children: Child[] = [];
    lines: Line[] = [];
    lineSelected: Line;
    fromDateSelected: Date;
    toDateSelected: Date;

    _ReserveChangeSub: Subscription;

    racesReserved: ChildRace[];
    dataSource: ReservedRacesDataSource;

    columnDefinitions = [
        { def: 'Child' },
        { def: 'LineName' },
        { def: 'Direction' },
        { def: 'Date' }
    ];

    directions = [
        { id: null, text: "All" },
        { id: DirectionType.OUTWARD, text: "Outward" },
        { id: DirectionType.RETURN, text: "Return" }
    ]
    directionSelected = this.directions[0];

    constructor(private userSvc: UserService, private parentSvc: ParentService, public dialog: MatDialog) {
        this.dataSource = new ReservedRacesDataSource(this.parentSvc);
        this.racesReserved = [];

        this.fromDateSelected = new Date();
        this.toDateSelected = new Date();
        this.toDateSelected.setMonth(this.fromDateSelected.getMonth() + 1);
    }

    getTimeString = Utils.getTimeString;

    ngOnInit() {
        this.userSub = this.userSvc.getUserInfo().subscribe(
            (info: UserInfo) => {
                if (info != null) {
                    this.userInfo = info;
                    this.children = this.userInfo.children;
                    this.children.forEach(child => {
                        child.parentId = this.userInfo.mail;
                    })

                    this.parentSvc.getParentLines(this.userInfo.mail).subscribe((data: Line[]) => {
                        this.lines = data;
                        this.lineSelected = this.lines[0];
                        this.directionSelected = this.directions[0];

                        this.search();
                    })
                }
            }
        )

        this._ReserveChangeSub = this.parentSvc.getReservedChanges().subscribe((value) => {
            this.search();
            console.log(value);
        });
    }

    ngOnDestroy() {
        this.userSub.unsubscribe();
        this._ReserveChangeSub.unsubscribe();
    }

    removeChildFromRace(childRace: ChildRace): void {
        let passengers: Passenger[] = [];
        let passenger: Passenger = new Passenger;
        passenger.childDetails = childRace.child;
        passenger.reserved = false;
        passenger.state = PassengerState.NULL;
        passengers.push(passenger);

        let reservation: reserveChildrenRequest = new reserveChildrenRequest;
        reservation.clientRace = childRace.race;
        reservation.children = passengers;

        this.dialog.open(ConfirmDialog, {
            width: '300px',
            data: {
                title: 'Remove Child', message: `Are you sure to remove this child?
                                                - ${reservation.children[0].childDetails.name} ${reservation.children[0].childDetails.surname}\n 
                                                In race: ${reservation.clientRace.line.name}, ${reservation.clientRace.direction}, ${reservation.clientRace.date.toDateString()}`, YES: true, CANCEL: true
            }
        })
            .afterClosed().subscribe(result => {
                switch (result) {
                    case "YES":
                        this.parentSvc.removeChildrenFromRace(reservation).then(() => {
                            this.parentSvc.reservedChanged("Remove Child " + reservation.children[0].childDetails.name)
                            this.parentSvc.reservableChanged("Remove Child " + reservation.children[0].childDetails.name)
                        }
                        );
                        break;
                    default:
                        break;
                }
            });
    }

    searchReservedRaces() {
        this.search();
    }

    private search() {
        //console.log(this.lineSelected.name, this.directionSelected.id, this.fromDateSelected, this.toDateSelected)
        if (!this.lineSelected || !this.directionSelected || !this.fromDateSelected || !this.toDateSelected) return;
        this.dataSource.loadReservedRaces(this.lineSelected.name, this.directionSelected.id, this.fromDateSelected, this.toDateSelected)
            .then((data) =>
                this.dataSource.getRaces()
                    .subscribe(((racesReserved) => {
                        let races: ChildRace[] = [];
                        racesReserved.forEach(race => {
                            console.log(race)
                            race.passengers.forEach(pass => {
                                if (pass.reserved && pass.childDetails.parentId === this.userInfo.mail)
                                    races.push(new ChildRace(pass.childDetails, race));
                            })
                        })
                        console.log(races)
                        this.racesReserved = races
                    })).unsubscribe()
            ).catch((err) => { })
    }


    viewRace(childRace: ChildRace) {
        console.log("VIEW LINE:", childRace)
        const dialogRef = this.dialog.open(ViewParentRaceDialog, {
            data: {
                lineName: childRace.race.line.name,
                date: childRace.race.date,
                direction: childRace.race.direction
            }
        });
    }
}
