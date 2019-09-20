import { Component, OnInit, ViewChild, AfterViewInit, OnDestroy, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import Utils from 'src/app/utils/utils';


@Component({
    selector: 'view-race-dialog',
    templateUrl: './view-race.dialog.html',
    styleUrls: ['./view-race.dialog.css']
})
export class ViewRaceDialog {

    constructor(public dialogRef: MatDialogRef<ViewRaceDialog>, @Inject(MAT_DIALOG_DATA) public data: any) { }

    getTime = Utils.getTime;

    getTimeWithSecond = Utils.getTimeWithSecond;

    onCancel(): void {
        this.dialogRef.close();
    }
}