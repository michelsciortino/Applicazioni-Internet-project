import { Component, OnInit, ViewChild, AfterViewInit, OnDestroy, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { ViewUserDialog } from '../../users-management/view-user-dialog/view-user.dialog';
import { Race } from 'src/app/models/race';


@Component({
    selector: 'view-race-dialog',
    templateUrl: './view-race.dialog.html',
    styleUrls: ['./view-race.dialog.css']
})
export class ViewRaceDialog {

    constructor(public dialogRef: MatDialogRef<ViewRaceDialog>, @Inject(MAT_DIALOG_DATA) public data: Race) {

        // console.log(data.race.date.toLocaleDateString())
        // console.log(data.race.date.toLocaleTimeString())
        //console.log(data.race.date.getMilliseconds()+companion.finalStop.delayInMillis
        //let a = new Date(data.race.date.getTime()).toLocaleTimeString()
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
}
