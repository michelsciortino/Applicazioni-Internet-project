import { Component, Inject, OnInit, OnDestroy } from "@angular/core";
import { AdminService } from 'src/app/services/admin/admin.service';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { Companion, CompanionState } from 'src/app/models/companion';
import { Line } from 'src/app/models/line';
import { Race, DirectionType } from 'src/app/models/race';
import { LineService } from 'src/app/services/lines/line-races.service';
import Utils from 'src/app/utils/utils';
import { Stop } from 'src/app/models/stop';
import { CompanionService } from 'src/app/services/companion/companion.service';

@Component({
    selector: 'app-edit-race-dialog',
    templateUrl: './edit-race.dialog.html',
    styleUrls: ['./edit-race.dialog.css']
})
export class EditRaceDialog implements OnInit {

    stopCoverage: Map<string, boolean>;
    race: Race;

    constructor(private adminSvc: AdminService, private lineSvc: LineService, public dialogRef: MatDialogRef<EditRaceDialog>, @Inject(MAT_DIALOG_DATA) public data: any) {
        console.log(data);
        this.stopCoverage = new Map<string, boolean>();
    }

    getTime = Utils.getTime;

    getTimeWithSecond = Utils.getTimeWithSecond;

    ngOnInit() {
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

    acceptRequest(race: Race, companion: Companion) {
        this.adminSvc.acceptCompanionRequest(race.line.name, race.direction, race.date, companion.userDetails.mail)
            .toPromise()
            .then(() => companion.state = CompanionState.CHOSEN)
            .catch(() => { });
    }

    rejectRequest(race: Race, companion: Companion) {
        if (companion.state === CompanionState.AVAILABLE)
            this.adminSvc.rejectCompanionRequest(race.line.name, race.direction, race.date, companion.userDetails.mail)
                .toPromise()
                .then(() => this.ngOnInit())
                .catch(() => { });
        else
            this.adminSvc.unAcceptCompanionRequest(race.line.name, race.direction, race.date, companion.userDetails.mail)
                .toPromise()
                .then(() => this.ngOnInit())
                .catch(() => { });
    }
}