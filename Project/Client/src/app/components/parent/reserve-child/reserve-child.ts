import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { UserService } from 'src/app/services/user/user.service';
import { UserInfo } from 'src/app/models/user';
import { Race, DirectionType } from 'src/app/models/race';
import { ParentService, ReservableRacesDataSource } from 'src/app/services/parent/parent.service';
import { Child } from 'src/app/models/child';
import { MatDialog, MatCalendarCellCssClasses } from '@angular/material';
import { DatePipe } from '@angular/common';;
import { Line } from 'src/app/models/line';
import { Stop } from 'src/app/models/stop';
import { LineService } from 'src/app/services/lines/line-races.service';
import { ReserveChildDialog } from './reserve-child-dialog/reserve-child.dialog';
import { ConfirmDialog } from '../../dialogs/confirm-dialog/confirm.dialog';

export class CheckedRace {
    checked: boolean;
    race: Race;

    public constructor(checked: boolean, race: Race) {
        this.checked = checked;
        this.race = race;
    }

    public setChecked(checked: boolean) {
        this.checked = checked;
    }
}

@Component({
    selector: 'app-reserve-child',
    templateUrl: './reserve-child.html',
    styleUrls: ['./reserve-child.css']
})
export class ReserveChildComponent implements OnInit, OnDestroy {
    userInfo: UserInfo = new UserInfo;
    private userSub: Subscription;

    lines: Line[] = [];
    lineSelected: Line;

    children: Child[] = [];
    childSelected: Child;

    stopSelected: Stop;
    selectedDate: Date = new Date();

    dataSource: ReservableRacesDataSource;
    _ReservableChangeSub: Subscription;
    _ReservedChangeSub: Subscription;

    racesReservable: CheckedRace[];
    racesReservableChecked: CheckedRace[];

    lineDates: string[] = [];

    isDataAvailable: boolean = false;

    directionSelected = null;
    directions = [
        { id: DirectionType.OUTWARD, text: "Outward" },
        { id: DirectionType.RETURN, text: "Return" }
    ];

    constructor(private userSvc: UserService, private parentSvc: ParentService, private lineSvc: LineService, public dialog: MatDialog, private datePipe: DatePipe) {
        this.racesReservable = [];
        this.racesReservableChecked = [];
        this.dataSource = new ReservableRacesDataSource(this.parentSvc, this.datePipe);
    }

    dateClass() {
        return (date: Date): MatCalendarCellCssClasses => {
            //console.log(date)
            const highlightDate = this.lineDates
                .map(strDate => new Date(strDate))
                .some(d => d.getDate() === date.getDate() && d.getMonth() === date.getMonth() && d.getFullYear() === date.getFullYear());
            return highlightDate ? 'special-date' : '';
        };
    }

    ngOnInit() {
        this.userSub = this.userSvc.getUserInfo().subscribe(
            (info: UserInfo) => {
                if (info != null) {
                    this.userInfo = info;
                    this.children = this.userInfo.children;
                    this.childSelected = this.children[0];
                    this.children.forEach(child => {
                        child.parentId = this.userInfo.mail;
                    })

                    this.parentSvc.getParentLines(this.userInfo.mail).subscribe((data: Line[]) => {
                        this.lines = data;
                        this.lineSelected = this.lines[0];
                        this.directionSelected = this.directions[0];
                        this.search();
                        //console.log(this.racesReservable);
                    })
                }
            }
        );

        this._ReservableChangeSub = this.parentSvc.getReservableChanges().subscribe((value) => {
            this.selectedDate = new Date()
            this.racesReservableChecked = [];
            this.lineDates = [];
            this.racesReservable = [];
            this.search();
            //console.log(value);
        });

        this._ReservableChangeSub = this.parentSvc.getReservedChanges().subscribe((value) => {
            this.selectedDate = new Date()
            this.racesReservableChecked = [];
            this.lineDates = [];
            this.racesReservable = [];
            this.search();
            //console.log(value);
        });
    }

    ngOnDestroy() {
        this.userSub.unsubscribe();
        this._ReservableChangeSub.unsubscribe();
    }

    onSelectDate(event) {
        this.selectedDate = event;
        this.racesReservable = [];
        this.dataSource.getReservableRacesForChildAndDate(this.childSelected.cf, this.selectedDate)
            .subscribe(((racesReservable) => {
                //console.log(racesReservable)
                racesReservable.forEach((race) => {
                    let index = this.checkRaceExist(race, this.racesReservableChecked)
                    if (index > -1)
                        this.racesReservable.push(this.racesReservableChecked[index])
                    else
                        this.racesReservable.push(new CheckedRace(false, race))
                }
                )
            }
            )).unsubscribe()
    }

    checkRaceExist(race: Race, racesChecked: CheckedRace[]) {
        let i = -1;
        racesChecked.forEach((raceC, index) => {
            if (race.line.name === raceC.race.line.name && race.direction === raceC.race.direction && race.date.getTime() === raceC.race.date.getTime())
                i = index;
        })
        return i;
    }

    onSelectRace(race: CheckedRace, event) {
        console.log(event, race)
        if (event.checked) {
            this.racesReservable[this.racesReservable.indexOf(race)].setChecked(true);
            this.racesReservableChecked.push(this.racesReservable[this.racesReservable.indexOf(race)]);
        }
        else {
            if (this.racesReservable.includes(race)) {
                this.racesReservable[this.racesReservable.indexOf(race)].setChecked(false);
            }
            const index = this.racesReservableChecked.indexOf(race, 0);
            if (index > -1) {
                this.racesReservableChecked.splice(index, 1);
            }
        }
        console.log(this.racesReservableChecked)
    }

    checkCheckoutBox() {
        if (this.racesReservableChecked.length > 0)
            return false;
        return true;
    }

    searchRaces() {
        let choice: boolean = false;
        if (this.racesReservableChecked.length > 0) {
            this.checkOnlyOneChild()
        }
        else this.search();
    }

    search() {
        this.isDataAvailable = false;

        if (!this.lineSelected || !this.directionSelected) return;

        this.dataSource.loadReservableRaces(this.lineSelected.name, this.directionSelected.id, null, null)
            .then((data) =>
                this.dataSource.getReservableRacesForChild(this.childSelected.cf)
                    .subscribe(((racesReservable) => {
                        console.log(racesReservable)
                        //this.racesReservable = racesReservable
                    }
                    )).unsubscribe()
            ).then((data) =>
                this.dataSource.getReservableDatesForChild()
                    .subscribe(((dates) => {
                        //console.log("Dates", dates)
                        this.lineDates = dates;
                        this.isDataAvailable = true;
                    }))
            ).catch((err) => { })
    }

    checkOnlyOneChild() {
        let choice: boolean = true;
        this.dialog.open(ConfirmDialog, {
            width: '300px',
            data: {
                title: 'Remove Child', message: `Only a child, line, direction. You want delete prewiew selection?`, YES: true, CANCEL: true
            }
        })
            .afterClosed().subscribe(result => {
                switch (result) {
                    case "YES":
                        this.parentSvc.reservableChanged("Search Changed");
                        break;
                    default:
                        break;
                }
            });
        return choice;
    }

    openReserveChildToRaceDialog(): void {
        console.log("OPEN DIALOG ADD CHILD", this.childSelected)
        this.dialog.open(ReserveChildDialog, {
            data: {
                racesReservableChecked: this.racesReservableChecked,
                child: this.childSelected,
                direction: this.directionSelected,
                line: this.lineSelected,
                reserve: this
            }
        })
    }

}
