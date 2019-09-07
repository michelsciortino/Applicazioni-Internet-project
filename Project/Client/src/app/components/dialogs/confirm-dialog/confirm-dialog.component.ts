import { Component, Inject } from '@angular/core';
import { MatDialog, MAT_DIALOG_DATA } from '@angular/material';

export interface DialogData {
    title: '';
    message: '';
  }

@Component({
    selector: 'app-confirm-dialog',
    templateUrl: 'confirm-dialog.component.html',
    styleUrls: ['confirm-dialog.component.css']
})
export class ConfirmDialogComponent {

    constructor(@Inject(MAT_DIALOG_DATA) public data: DialogData) { }

 //  constructor(public dialog: MatDialog) { }

 //  openDialog(): void {
 //      const dialogRef = this.dialog.open(ConfirmDialogComponent, {
 //          width: '350px',
 //          data: "Do you confirm data?"
 //      });
 //      dialogRef.afterClosed().subscribe(result => {
 //          if (result) {
 //              console.log('Yes clicked');
 //              // DO SOMETHING
 //          }
 //          else
 //              console.log('No clicked')
 //      });
 //  }

}
