import { Component, OnInit, ViewChild, AfterViewInit, OnDestroy, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { ViewUserDialog } from '../../users-management/view-user-dialog/view-user.dialog';

@Component({
    selector: 'view-race-dialog',
    templateUrl: './view-race.dialog.html',
    styleUrls:['./view-race.dialog.css']
})
export class ViewRaceDialog {

    constructor(public dialogRef: MatDialogRef<ViewUserDialog>,@Inject(MAT_DIALOG_DATA) public data: any) {
     }

    onCancel(): void {
        this.dialogRef.close();
    }
}
