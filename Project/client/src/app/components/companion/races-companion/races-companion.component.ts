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
import { GiveAvailabilityDialog } from './give-availability-dialog/give-availability.dialog';
import { CompanionService } from 'src/app/services/companion/companion.service';
import { ManageRaceDialog } from '../../admin/manage-race/manage-race.dialog';
import Utils from 'src/app/utils/utils';

@Component({
    selector: 'app-races-companion',
    templateUrl: './races-companion.component.html',
    styleUrls: ['./races-companion.component.css']
})
export class RacesCompanionComponent implements OnInit, OnDestroy {

    public isMobile: boolean;
    dataSource: RacesDataSource;
    private compaionChangeSub: Subscription;

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
        { def: 'State' },
        { def: 'Availability-Action' }];

    @ViewChild(MatSort, { static: true }) sort: MatSort;

    constructor(private lineSvc: LineService, public dialog: MatDialog, private isMobileSvc: IsMobileService, private companionSvc: CompanionService) {
    }

    getTimeString = Utils.getTimeString;

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
                // console.log(data[0]);
                if (data.length == 0) return;
                this.lines = data;
                // console.log(this.lines[0].name);
                this.lineSelected = this.lines[0];
                this.dataSource.loadRaces(this.lineSelected.name, this.fromDateSelected, this.toDateSelected, null);
            })

        this.compaionChangeSub = this.companionSvc.getCompanionInfoChanges().subscribe((value) => {
            this.search();
            // console.log(value);
        });
    }

    ngOnDestroy() {
        this.compaionChangeSub.unsubscribe();
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
        // console.log("GIVE AVAILABILITY:", race)
        const dialogRef = this.dialog.open(GiveAvailabilityDialog, { data: { race: race } });
    }

    viewRace(race: Race) {
        // console.log("VIEW RACE:", race)
        this.dialog.open(ManageRaceDialog, {
            data: {
                lineName: race.line.name,
                date: race.date,
                direction: race.direction
            }
        });
    }
}

