import { Component, OnInit, ViewChild, AfterViewInit, OnDestroy, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { UserInfo } from 'src/app/models/user';

@Component({
    selector: 'view-user-dialog',
    templateUrl: './view-user.dialog.html',
    styleUrls:['./view-user.dialog.css']
})
export class ViewUserDialog {

    constructor(public dialogRef: MatDialogRef<ViewUserDialog>,@Inject(MAT_DIALOG_DATA) public data: any) {
     }

    onCancel(): void {
        this.dialogRef.close();
    }
}
