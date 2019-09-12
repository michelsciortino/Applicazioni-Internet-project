import { Component, OnInit, OnDestroy } from '@angular/core';
import { LineService, RacesDataSource } from 'src/app/services/lines/line-races.service';
import { BehaviorSubject, Subscription } from 'rxjs';
import { Line } from 'src/app/models/line';
import { map } from 'rxjs/operators';
import { HttpErrorResponse } from '@angular/common/http';
import { DirectionType, Race } from 'src/app/models/race';
import { IsMobileService } from 'src/app/services/bridges/is-mobile.service';

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
        { def: 'Companions' }];

    constructor(private lineSvc: LineService, private isMobileSvc: IsMobileService) {

        console.log(this.toDateSelected.toString());
        console.log(this.toDateSelected.toISOString());
    }

    ngOnInit() {

        this.dataSource = new RacesDataSource(this.lineSvc);

        this.toDateSelected.setMonth(this.toDateSelected.getMonth() + 1);

        this.lineSvc.getLines()
            .pipe(
                map(
                    (data: any) => data,
                    (error: HttpErrorResponse) =>
                        console.log(error)
                )
            )
            .subscribe((data: Line[]) => {
                console.log(data[0]);
                if (data.length == 0) return;
                this.lines = data;
                console.log(this.lines[0].name);
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

}
