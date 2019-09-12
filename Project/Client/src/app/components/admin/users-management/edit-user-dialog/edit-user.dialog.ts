import { Component, OnInit, ViewChild, AfterViewInit, OnDestroy, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { UserInfo } from 'src/app/models/user';
import { UserService } from 'src/app/services/user/user.service';
import { tap } from 'rxjs/operators';
import { Subscription } from 'rxjs';
import { ReturnStatement } from '@angular/compiler';
import { AdminService } from 'src/app/services/admin/admin.service';

@Component({
    selector: 'edit-user-dialog',
    templateUrl: './edit-user.dialog.html',
    styleUrls: ['./edit-user.dialog.css']
})
export class EditUserDialog implements OnDestroy {
    dirty:boolean;
    isAdmin: boolean;
    isCompanion: boolean;

    private usrSub: Subscription;

    linesMap: Map<String, { name: string, editable: boolean, admin: boolean }>;

    constructor(private userSvc: UserService, private adminSvc: AdminService, public dialogRef: MatDialogRef<EditUserDialog>, @Inject(MAT_DIALOG_DATA) public data: any) {
        this.linesMap = new Map();
        this.isAdmin = UserInfo.prototype.isAdmin(data.user);
        this.isCompanion = UserInfo.prototype.isCompanion(data.user);
        this.dirty=false;

        this.usrSub = userSvc.getUserInfo().subscribe(
            userInfo => {
                for (const line of userInfo.lines) {
                    this.linesMap.set(line, { name: line, editable: true, admin: false });
                }
                const editableLines = userInfo.lines;
                if (!data.user.lines) return;
                for (const line of data.user.lines) {
                    const prevLine = this.linesMap.get(line);
                    if (!prevLine)
                        this.linesMap.set(line, { name: line, editable: false, admin: true });
                    else {
                        prevLine.admin = true;
                        this.linesMap.set(prevLine.name, prevLine);
                    }
                }
            }
        );
    }

    onIsCompanionChanged(checkbox) {
        checkbox.checked = !checkbox.checked

        if (!this.isCompanion) {
            this.adminSvc.makeCompanion(this.data.user.mail)
                .then(() => {
                    if(!this.dirty) this.dirty=true;
                    this.isCompanion = true;
                    checkbox.checked = true;
                })
                .catch((error) => { });
        }
        else {
            this.adminSvc.removeCompanion(this.data.user.mail)
                .then(() => {
                    if(!this.dirty) this.dirty=true;
                    this.isCompanion = false;
                    checkbox.checked = false;
                })
                .catch((error) => { });
        }
    }

    onLineChecked(checkbox, line) {
        checkbox.checked = !checkbox.checked;
        if (!line.admin) {
            this.adminSvc.makeAdmin(this.data.user.mail, line.name)
                .then(
                    () => {
                        if (!this.isAdmin) this.isAdmin = true;
                        this.linesMap.get(line.name).admin = true;
                        checkbox.checked = true;
                    }
                ).catch((error) => { });
        }
        else {
            this.adminSvc.removeAdmin(this.data.user.mail, line.name)
                .then(
                    () => {
                        this.linesMap.get(line.name).admin = false;
                        checkbox.checked = false;
                    }
                ).catch((error) => { });
        }
    }

    onClose(): void {
        this.dialogRef.close();
    }

    ngOnDestroy() {
        this.usrSub.unsubscribe();
    }
}
