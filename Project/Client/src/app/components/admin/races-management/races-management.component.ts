import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { LineService, RacesDataSource } from 'src/app/services/lines/line-races.service';
import { BehaviorSubject, Subscription } from 'rxjs';
import { Line } from 'src/app/models/line';
import { map } from 'rxjs/operators';
import { HttpErrorResponse } from '@angular/common/http';
import { DirectionType, Race } from 'src/app/models/race';
import { IsMobileService } from 'src/app/services/bridges/is-mobile.service';
import { MatSort } from '@angular/material/sort';
import { MatDialog } from '@angular/material';
import { ViewRaceDialog } from './view-race-dialog/view-race.dialog';
import { MessageDialogComponent } from '../../dialogs/messege-dialog/messege-dialog.component';
import { ConfirmDialogComponent } from '../../dialogs/confirm-dialog/confirm-dialog.component';

@Component({
    selector: 'app-races-management',
    templateUrl: './races-management.component.html',
    styleUrls: ['./races-management.component.css']
})
export class RacesManagementComponent implements OnInit, OnDestroy {

    public isMobile: boolean;

    isMobileSub: Subscription;

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
        { def: 'Remove-Action' }];

    @ViewChild(MatSort, { static: true }) sort: MatSort;

    constructor(private lineSvc: LineService, public dialog: MatDialog, private isMobileSvc: IsMobileService) {

        console.log(this.toDateSelected.toString());
        console.log(this.toDateSelected.toISOString());
    }

    ngOnInit() {

        this.dataSource = new RacesDataSource(this.lineSvc, this.sort);

        this.toDateSelected.setMonth(this.toDateSelected.getMonth() + 3);

        this.lineSvc.getLines()
            .pipe(
                map(
                    (data: any) => data,
                    (error: HttpErrorResponse) =>
                        console.log(error)
                )
            )
            .subscribe((data: Line[]) => {
                //console.log(data[0]);
                if (data.length == 0) return;
                this.lines = data;
                //console.log(this.lines[0].name);
                this.lineSelected = this.lines[0];
                this.dataSource.loadRaces(this.lineSelected.name, this.fromDateSelected, this.toDateSelected, null);
            })
    }

    ngOnDestroy() {
        //this.isMobileSub.unsubscribe();
    }

    public search() {
        if (this.directionSelected.id == null)
            this.dataSource.loadRaces(this.lineSelected.name, this.fromDateSelected, this.toDateSelected, null);
        else
            this.dataSource.loadRaces(this.lineSelected.name, this.fromDateSelected, this.toDateSelected, this.directionSelected.id.toString());
    }

    getDisplayedColumns(): string[] {
        return this.columnDefinitions
            .map(cd => cd.def);
    }

    removeRace(race) {
        console.log("REMOVE RACE:", race)
        const dialogRef = this.dialog.open(ConfirmDialogComponent, {
            width: '350px',
            data: {title: 'Confirm?', message: `Are you sure deleted this race: \n- ${race.lineName}\n- ${race.direction}\n- ${race.date.toISOString()}`}
        });
        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                console.log('Yes clicked');
                // delete race
                this.lineSvc.deleteRace(race);
            }
            else
                console.log('No clicked')
        });
    }

    viewRace(race) {
        console.log("VIEW RACE:", race)
        const dialogRef = this.dialog.open(ViewRaceDialog, { data: { race: race } });
    }
}

