import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { Child } from 'src/app/models/child';
import { UserService } from 'src/app/services/user/user.service';


@Component({
    selector: 'add-child-dialog',
    templateUrl: './add-child.dialog.html',
    styleUrls: ['./add-child.dialog.css']
})
export class AddChildDialog {
    child = new Child(null, null, null, null);

    cf = "MRONDR94C13B019P"

    constructor(public dialogRef: MatDialogRef<AddChildDialog>, private userSvc: UserService, @Inject(MAT_DIALOG_DATA) public data: any) {
    }

    addChild(): void {
        this.data.user.children.push(this.child);
        this.userSvc.updateUser(this.data.user);
        this.dialogRef.close();
    }

    onCancel(): void {
        this.dialogRef.close();
    }

    isComplete(): boolean {
        return this.child.name != null && this.child.surname != null && this.child.cf != null;
    }

    checkCF() {
        var re = new RegExp(/^[A-Z]{6}\d{2}[A-Z]\d{2}[A-Z]\d{3}[A-Z]$/, 'g');
        if (this.child.cf != null) {
            var match = this.child.cf.match(re);
            if (match == null)
                return false;
            else
                return true;
        }
        return false;
    }
}
