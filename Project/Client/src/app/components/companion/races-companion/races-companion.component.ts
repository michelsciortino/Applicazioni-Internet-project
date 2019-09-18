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
import { GiveAvailabilityDialog } from './give-availability-dialog/give-availability.dialog';

@Component({
    selector: 'app-races-companion',
    templateUrl: './races-companion.component.html',
    styleUrls: ['./races-companion.component.css']
})
export class RacesCompanionComponent implements OnInit {

    public isMobile: boolean;
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

    columnDefinitions = [
        { def: 'LineName' },
        { def: 'Direction' },
        { def: 'Date' },
        { def: 'Passengers' },
        { def: 'Companions' },
        { def: 'Availability-Action' }];

    @ViewChild(MatSort, { static: true }) sort: MatSort;

    constructor(private lineSvc: LineService, public dialog: MatDialog, private isMobileSvc: IsMobileService) {
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

    giveAvailability(race: Race) {
        console.log("GIVE AVAILABILITY:", race)
        const dialogRef = this.dialog.open(GiveAvailabilityDialog, { data: { race: race } });
    }
}

