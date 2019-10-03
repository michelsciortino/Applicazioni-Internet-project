import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { LineService, RacesDataSource } from 'src/app/services/lines/line-races.service';
import { BehaviorSubject, Subscription } from 'rxjs';
import { Line } from 'src/app/models/line';
import { map } from 'rxjs/operators';
import { HttpErrorResponse } from '@angular/common/http';
import { DirectionType, Race } from 'src/app/models/race';
import { IsMobileService } from 'src/app/services/is-mobile/is-mobile.service';
import { MatSort } from '@angular/material/sort';
import { MatDialog } from '@angular/material';
import { ConfirmDialog } from '../../dialogs/confirm-dialog/confirm.dialog';
import { NewRaceDialog } from './new-race-dialog/new-race.dialog';
import { UserService } from 'src/app/services/user/user.service';
import { UserInfo } from 'src/app/models/user';
import { AdminService } from 'src/app/services/admin/admin.service';
import { ManageRaceDialog } from '../manage-race/manage-race.dialog';

@Component({
    selector: 'app-races-management',
    templateUrl: './races-management.component.html',
    styleUrls: ['./races-management.component.css']
})
export class RacesManagementComponent implements OnInit, OnDestroy {

    public isMobile: boolean;

    userInfo: UserInfo;
    private userInfoSub: Subscription;

    isMobileSub: Subscription;

    racesChangesSub: Subscription;

    dataSource: RacesDataSource;

    lines: Line[];
    lineSelected: Line = new Line();

    directions = [
        { id: null, text: "All" },
        { id: DirectionType.OUTWARD, text: "Outward" },
        { id: DirectionType.RETURN, text: "Return" }
    ];
    directionSelected = this.directions[0];

    fromDateSelected = new Date();
    toDateSelected = new Date();

    races: Race[];

    columnDefinitions = [
        { def: 'LineName' },
        { def: 'Direction' },
        { def: 'Date' },
        { def: 'Passengers' },
        { def: 'Companions' },
        { def: 'State' },
        { def: 'Remove-Action' }];

    @ViewChild(MatSort, { static: true }) sort: MatSort;

    constructor(private adminSvc: AdminService, private lineSvc: LineService, private userSvc: UserService, public dialog: MatDialog, private isMobileSvc: IsMobileService) { }

    ngOnInit() {

        this.dataSource = new RacesDataSource(this.lineSvc, this.sort);

        this.racesChangesSub = this.adminSvc.getRacesChanges().subscribe((reason) => {
            console.log(reason)
            this.search()
        });

        this.toDateSelected.setMonth(this.toDateSelected.getMonth() + 3);

        this.userInfoSub = this.userSvc.getUserInfo().subscribe(
            (info: UserInfo) => {
                if (info != null) {
                    //console.log(info);
                    this.userInfo = info;
                }
            }
        );

        this.lineSvc.getLines()
            .pipe(
                map(
                    (data: any) => data,
                    (error: HttpErrorResponse) =>
                        console.log(error)
                )
            )
            .subscribe((data: Line[]) => {
                //console.log(data);
                if (data.length == 0) return;
                this.lines = data;
                //console.log(this.lines[0].name);
                this.lineSelected = this.lines[0];
                this.dataSource.loadRaces(this.lineSelected.name, this.fromDateSelected, this.toDateSelected, null);
            });
    }

    ngOnDestroy(): void {
        this.userInfoSub.unsubscribe();
    }

    public search() {
        console.log("searching")
        if (this.directionSelected.id == null)
            this.dataSource.loadRaces(this.lineSelected.name, this.fromDateSelected, this.toDateSelected, null);
        else
            this.dataSource.loadRaces(this.lineSelected.name, this.fromDateSelected, this.toDateSelected, this.directionSelected.id.toString());
    }

    getDisplayedColumns(): string[] {
        return this.columnDefinitions
            .map(cd => cd.def);
    }

    removeRace(race: Race) {
        console.log("REMOVE RACE:", race)
        const dialogRef = this.dialog.open(ConfirmDialog, {
            width: '350px',
            data: {
                title: 'Confirm?',
                message: `Are you sure deleted this race: \n- ${race.line.name}\n- ${race.direction}\n- ${race.date.toISOString()}`,
                YES: true, NO: true
            }
        });
        dialogRef.afterClosed().subscribe(result => {
            switch (result) {
                case "YES":
                    this.lineSvc.deleteRace(race.line.name, race.date, race.direction)
                        .then(() => this.adminSvc.racesChanged("A race has been deleted"))
                        .catch((error) => {
                            console.log(error);
                        });
                    break;
                default: break;
            }
        });
    }

    viewRace(race: Race) {
        console.log("VIEW RACE:", race)
        const dialogRef = this.dialog.open(ManageRaceDialog, {
            data: {
                lineName: race.line.name,
                date: race.date,
                direction: race.direction
            }
        });
    }

    openAddRacesDialog(): void {
        console.log("OPEN DIALOG ADD RACE")
        const dialogRef = this.dialog.open(NewRaceDialog, { data: { date: this.fromDateSelected } });
        dialogRef.afterClosed().subscribe(result => {
            if (dialogRef.componentInstance.dirty)
                this.search();
        });
    }

    isAdminOfLine(race: Race) {
        if (!this.userInfo) return false;
        return this.userInfo.lines.find((line) => line === race.line.name) != null;
    }
}

