import { Component, Inject, OnInit, OnDestroy } from "@angular/core";
import { AdminService } from 'src/app/services/admin/admin.service';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { Race, DirectionType } from 'src/app/models/race';
import { LineService } from 'src/app/services/lines/line-races.service';
import Utils from 'src/app/utils/utils';
import { Stop } from 'src/app/models/stop';
import { Subscription } from 'rxjs';
import { UserService } from 'src/app/services/user/user.service';
import { NotificationService } from 'src/app/services/notifications/notification.service';
import { Passenger } from 'src/app/models/passenger';
import { UserInfo } from 'src/app/models/user';
import { Notification, NotificationType } from 'src/app/models/notification';

@Component({
    selector: 'app-view-parent-race-dialog',
    templateUrl: './view-race.dialog.html',
    styleUrls: ['./view-race.dialog.css']
})
export class ViewParentTodayRaceDialog implements OnInit, OnDestroy {

    userInfo: UserInfo;

    stopReached: Map<string, boolean>;
    race: Race;
    childrenPassengers: Passenger[];

    userSub: Subscription;
    racesChangesSub: Subscription;

    dirty: boolean;

    constructor(
        private userSvc: UserService,
        private lineSvc: LineService,
        private notificationSvc: NotificationService,
        public dialogRef: MatDialogRef<ViewParentTodayRaceDialog>, @Inject(MAT_DIALOG_DATA) public data: any) {
        this.stopReached = new Map<string, boolean>();
        this.userInfo = new UserInfo();
        this.childrenPassengers = [];
        this.dirty = false;
    }

    getTime = Utils.getTime;

    getTimeWithSecond = Utils.getTimeWithSecond;

    ngOnInit() {
        this.userSub = this.userSvc.getUserInfo().subscribe(userInfo => {
            if (userInfo != null) {
                this.userInfo = userInfo;
                this.lineSvc.getRace(this.data.lineName, this.data.date, this.data.direction)
                    .then(race => {
                        this.race = race;
                        this.updateReachedStop();
                        this.childrenPassengers = [];
                        race.passengers.forEach(pass => {
                            if (pass.reserved && pass.childDetails.parentId === this.userInfo.mail)
                                this.childrenPassengers.push(pass);
                        });
                        this.notificationSvc.subscribeToRace(
                            this.race.line.name,
                            this.race.date,
                            this.race.direction,
                            (value: Notification) => {
                                if (value.type === NotificationType.RACE_ENDED || value.type === NotificationType.RACE_STARTED)
                                    this.dirty = true;
                                this.getRace();
                            }
                        );
                    })
                    .catch(error => console.log(error));
            }
        });

    }

    ngOnDestroy() {
        !this.userSub || this.userSub.unsubscribe();
        !this.racesChangesSub || this.racesChangesSub.unsubscribe();
    }

    private getRace() {
        this.lineSvc.getRace(this.data.lineName, this.data.date, this.data.direction)
            .then(race => {
                this.race = race;
                this.updateReachedStop();
                this.childrenPassengers = [];
                race.passengers.forEach(pass => {
                    if (pass.reserved && pass.childDetails.parentId === this.userInfo.mail)
                        this.childrenPassengers.push(pass);
                });
            })
            .catch(error => console.log(error));
    }

    isStopReached(stop: Stop): boolean {
        return this.stopReached.get(stop.name) || false;
    }

    updateReachedStop() {
        const stops = this.race.direction === DirectionType.OUTWARD ? this.race.line.outwardStops : this.race.line.returnStops;
        this.stopReached.clear();
        stops.forEach(stop => this.stopReached.set(stop.name, false));
        this.race.reachedStops.forEach(stopReached => {
            for (const stop of stops)
                if (stopReached.stopName === stop.name)
                    this.stopReached.set(stop.name, true);
        });
    }

    onCancel() {
        this.dialogRef.close();
    }
}
