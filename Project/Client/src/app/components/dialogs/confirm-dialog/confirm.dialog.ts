import { Component, Inject } from '@angular/core';
import { MatDialog, MAT_DIALOG_DATA } from '@angular/material';

export interface DialogData {
    title: '';
    message: '';
  }

@Component({
    selector: 'app-confirm-dialog',
    templateUrl: 'confirm.dialog.html',
    styleUrls: ['confirm.dialog.css', '../../common.css']
})
export class ConfirmDialog {

    constructor(@Inject(MAT_DIALOG_DATA) public data: DialogData) { }

    // USAGE

    // constructor(... public dialog: MatDialog ...)

    // openDialog(): void {
    //   this.dialog
    //   .open(ConfirmDialogComponent, {
    //     width: '350px',
    //     data: "Do you confirm data?"
    //   })
    //   .afterClosed().subscribe(result => {
    //     if (result) {
    //       switch (result) {
    //         case "YES":
    //           break;
    //         case "NO":
    //           break;
    //         default:
    //           break;
    //       }
    //     }
    //   });
    // }

}
