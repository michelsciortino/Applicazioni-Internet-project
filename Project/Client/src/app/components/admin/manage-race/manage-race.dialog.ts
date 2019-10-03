import { Component, Inject, OnInit, OnDestroy } from "@angular/core";
import { AdminService } from 'src/app/services/admin/admin.service';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { Companion, CompanionState } from 'src/app/models/companion';
import { Line } from 'src/app/models/line';
import { Race, DirectionType, RaceState } from 'src/app/models/race';
import { LineService } from 'src/app/services/lines/line-races.service';
import Utils from 'src/app/utils/utils';
import { Stop } from 'src/app/models/stop';
import { Subscription } from 'rxjs';
import { UserService } from 'src/app/services/user/user.service';
import { UserInfo } from 'src/app/models/user';
import { CompanionService } from 'src/app/services/companion/companion.service';

@Component({
    selector: 'app-manage-race-dialog',
    templateUrl: './manage-race.dialog.html',
    styleUrls: ['./manage-race.dialog.css']
})
export class ManageRaceDialog implements OnInit, OnDestroy {

    isAdmin: boolean;
    stopCoverage: Map<string, boolean>;
    race: Race;

    userSub: Subscription;
    racesChangesSub: Subscription;

    constructor(private adminSvc: AdminService, private userSvc: UserService, private lineSvc: LineService, public dialogRef: MatDialogRef<ManageRaceDialog>, @Inject(MAT_DIALOG_DATA) public data: any) {
        this.stopCoverage = new Map<string, boolean>();
    }

    getTime = Utils.getTime;

    getTimeWithSecond = Utils.getTimeWithSecond;

    ngOnInit() {
        this.userSub = this.userSvc.getUserInfo().subscribe(userInfo =>{
            this.isAdmin = userInfo.isAdminOfLine(this.data.lineName);
            if(this.isAdmin){
                this.racesChangesSub = this.adminSvc.getRacesChanges().subscribe((reason) => {
                    this.getRace();
                });
            }
        })
        this.getRace();
    }

    ngOnDestroy() {
        !this.userSub || this.userSub.unsubscribe();
        !this.racesChangesSub || this.racesChangesSub.unsubscribe();
    }

    private getRace() {
        this.lineSvc.getRace(this.data.lineName, this.data.date, this.data.direction)
            .then(race => {
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
                .then(() => this.adminSvc.racesChanged("An request has been deleted"))
                .catch(() => { });
        else
            this.adminSvc.unAcceptCompanionRequest(race.line.name, race.direction, race.date, companion.userDetails.mail)
                .toPromise()
                .then(() => this.ngOnInit())
                .catch(() => { });
    }

    isRaceValidable() {
        for (let covered of this.stopCoverage.values())
            if (!covered) return false;
        return true;
    }

    onCancel() {
        this.dialogRef.close();
    }

    validate() {
        this.lineSvc.validateRace(this.race.line.name, this.race.date, this.race.direction)
            .then(() => {
                this.race.raceState = RaceState.VALIDATED;
                this.adminSvc.racesChanged("A race has been validated");
            })
            .catch(() => { });
    }
}