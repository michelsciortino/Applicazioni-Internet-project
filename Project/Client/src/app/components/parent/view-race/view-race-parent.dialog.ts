import { Component, Inject, OnInit, OnDestroy } from "@angular/core";
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { CompanionState } from 'src/app/models/companion';
import { Race, DirectionType } from 'src/app/models/race';
import { LineService } from 'src/app/services/lines/line-races.service';
import Utils from 'src/app/utils/utils';
import { Stop } from 'src/app/models/stop';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-view-race-parent-dialog',
    templateUrl: './view-race-parent.dialog.html',
    styleUrls: ['./view-race-parent.dialog.css']
})
export class ViewParentRaceDialog implements OnInit, OnDestroy {

    stopCoverage: Map<string, boolean>;
    race: Race;

    userSub: Subscription;
    racesChangesSub: Subscription;

    constructor(private lineSvc: LineService, public dialogRef: MatDialogRef<ViewParentRaceDialog>, @Inject(MAT_DIALOG_DATA) public data: any) {
        console.log(data);
        this.stopCoverage = new Map<string, boolean>();
    }

    getTime = Utils.getTime;

    getTimeWithSecond = Utils.getTimeWithSecond;

    ngOnInit() {
        this.getRace();
    }

    ngOnDestroy() {
        !this.userSub || this.userSub.unsubscribe();
    }

    private getRace() {
        this.lineSvc.getRace(this.data.lineName, this.data.date, this.data.direction)
            .then(race => {
                console.log(race);
                this.race = race;
                this.updateCoverage();
            })
            .catch(error => console.log(error));
    }

    isStopCovered(stop: Stop): boolean {
        return this.stopCoverage.get(stop.name) || false;
    }

    updateCoverage() {
        const stops = this.race.direction == DirectionType.OUTWARD ? this.race.line.outwardStops : this.race.line.returnStops;
        this.stopCoverage.clear();
        stops.forEach(stop => this.stopCoverage.set(stop.name, false));
        this.race.companions.forEach(companion => {
            console.log(companion.state);
            if (companion.state === CompanionState.CONFIRMED || companion.state === CompanionState.VALIDATED) {
                let mark = false;
                for (let stop of stops) {
                    if (companion.initialStop.name === stop.name) {
                        mark = true;
                    }
                    !mark || this.stopCoverage.get(stop.name) || this.stopCoverage.set(stop.name, true);
                    if (companion.finalStop.name === stop.name) {
                        break;
                    }
                }
            }
        })
    }

    onCancel() {
        this.dialogRef.close();
    }
}