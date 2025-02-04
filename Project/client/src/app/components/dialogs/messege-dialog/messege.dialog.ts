import { Component, Inject, } from '@angular/core';
import { MatDialog, MAT_DIALOG_DATA } from '@angular/material';

export interface DialogData {
    title: '';
    message: '';
  }

@Component({
    selector: 'app-message-dialog',
    templateUrl: 'messege.dialog.html',
    styleUrls: ['messege.dialog.css']
})
export class MessageDialogComponent {
    
    constructor(@Inject(MAT_DIALOG_DATA) public data: DialogData) { }

}
