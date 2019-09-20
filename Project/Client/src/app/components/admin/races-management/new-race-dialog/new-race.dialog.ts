import { Component, OnInit, ViewChild, AfterViewInit, OnDestroy, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { Race, RaceState, DirectionType } from 'src/app/models/race';
import { Line } from 'src/app/models/line';
import { LineService } from 'src/app/services/lines/line-races.service';
import { Subscription, Observable } from 'rxjs';


@Component({
    selector: 'new-race-dialog',
    templateUrl: './new-race.dialog.html',
    styleUrls: ['./new-race.dialog.css']
})
export class NewRaceDialog implements OnInit, OnDestroy {
    dirty: boolean;
    linesSbj: Subscription;
    lines: Line[];
    lineSelected: Line;
    date: Date;
    directions = [
        { id: DirectionType.OUTWARD, text: "Outward" },
        { id: DirectionType.RETURN, text: "Return" }
    ];
    direction: any;
    hour: string = '08:10';

    constructor(public dialogRef: MatDialogRef<NewRaceDialog>, @Inject(MAT_DIALOG_DATA) public data: any, private lineSvc: LineService) {
        this.date = data.date;
    }

    ngOnInit(): void {
        this.linesSbj = this.lineSvc.getLines()
            .subscribe((data: Line[]) => {
                if (data.length == 0) return;
                this.lines = data;
            })
    }

    ngOnDestroy(): void {
        this.linesSbj.unsubscribe();
    }

    getTime(millisec: number) {
        var options = { hour: '2-digit', minute: '2-digit' };
        return new Date(millisec).toLocaleTimeString([], options)
    }

    getTimeWithSecond(millisec: number) {
        return new Date(millisec).toLocaleTimeString('it-IT')
    }

    onCancel(): void {
        this.dialogRef.close();
    }

    isComplete(): boolean {
        return this.direction != null && this.lineSelected != null;
    }

    addRace(): void {
        let race = new Race();
        let array = this.hour.split(":");
        this.date.setHours(parseInt(array[0]), parseInt(array[1]));
        race.line.name = this.lineSelected.name;
        race.direction = this.direction.id;
        race.date = this.date;
        race.raceState = RaceState.SCHEDULED;
        race.passengers = [];
        race.companions = [];
        if (!this.dirty)
            this.dirty = true;
        this.lineSvc.addRace(race);
        this.dialogRef.close();
    }
}
