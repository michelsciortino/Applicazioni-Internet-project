import { Component, Inject, OnInit, OnDestroy } from "@angular/core";
import { AdminService } from 'src/app/services/admin/admin.service';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { Companion } from 'src/app/models/companion';
import { Line } from 'src/app/models/line';
import { Race } from 'src/app/models/race';
import { LineService } from 'src/app/services/lines/line-races.service';
import Utils from 'src/app/utils/utils';

@Component({
    selector: 'app-edit-race-dialog',
    templateUrl: './edit-race.dialog.html',
    styleUrls: ['./edit-race.dialog.css']
})
export class EditRaceDialog implements OnInit {

    race: Race;

    constructor(private adminSvc: AdminService, private lineSvc: LineService, public dialogRef: MatDialogRef<EditRaceDialog>, @Inject(MAT_DIALOG_DATA) public data: any) {
        console.log(data);
    }

    getTime=Utils.getTime;

    getTimeWithSecond=Utils.getTimeWithSecond;

    ngOnInit() {
        this.lineSvc.getRace(this.data.lineName, this.data.date, this.data.direction)
            .then(race => {
                console.log(race);
                race.date=new Date(race.date);
                this.race = race;
            })
            .catch(error => console.log(error));
    }
}