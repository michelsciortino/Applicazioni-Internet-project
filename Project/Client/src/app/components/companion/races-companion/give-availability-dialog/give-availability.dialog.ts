import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { LineService } from 'src/app/services/lines/line-races.service';
import { map } from 'rxjs/operators';
import { HttpErrorResponse } from '@angular/common/http';
import { Line } from 'src/app/models/line';
import { Stop } from 'src/app/models/stop';
import { DirectionType } from 'src/app/models/race';
import { CompanionService } from 'src/app/services/companion/companion.service';
import Utils from 'src/app/utils/utils';

@Component({
    selector: 'give-availability-dialog',
    templateUrl: './give-availability.dialog.html',
    styleUrls: ['./give-availability.dialog.css']
})
export class GiveAvailabilityDialog {
    dirty: boolean;
    initialStop: Stop;
    finalStop: Stop;
    line: Line;
    stops: Stop[];

    constructor(public dialogRef: MatDialogRef<GiveAvailabilityDialog>, private companionSvc: CompanionService, @Inject(MAT_DIALOG_DATA) public data: any, private lineSvc: LineService) {
        this.lineSvc.getLine(data.race.lineName)
            .pipe(
                map(
                    (data: any) => data,
                    (error: HttpErrorResponse) =>
                        console.log(error)
                )
            )
            .subscribe((lineObj: Line) => {
                //console.log(data[0]);
                if (data == null) return;
                this.line = lineObj;
                if (data.race.direction == DirectionType.OUTWARD) {
                    this.stops = this.line.outwardStops;
                    this.initialStop = this.stops[0];
                    this.finalStop = this.stops[this.stops.length - 1];
                }
                else {
                    this.stops = this.line.returnStops;
                    this.initialStop = this.stops[0];
                    this.finalStop = this.stops[this.stops.length - 1];
                }
            })
    }

    getTime = Utils.getTime;

    getTimeWithSecond = Utils.getTimeWithSecond;

    onCancel(): void {
        this.dialogRef.close();
    }

    send(): void {
        this.companionSvc.giveAvailability(this.data.race.lineName, this.data.race.direction, this.data.race.date, this.initialStop, this.finalStop).toPromise()
            .then((result) => {
                if (!this.dirty)
                    this.dirty = true;
                this.dialogRef.close()
            })
            .catch((error) => this.dialogRef.close());
    }
}
