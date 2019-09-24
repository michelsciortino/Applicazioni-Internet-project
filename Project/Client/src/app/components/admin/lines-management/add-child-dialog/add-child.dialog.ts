import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material';
import { AdminService, UsersDataSource } from 'src/app/services/admin/admin.service';
import { UserService } from 'src/app/services/user/user.service';
import { Users } from 'src/app/models/users_paginated';
import { UserInfo } from 'src/app/models/user';
import { BehaviorSubject } from 'rxjs';
import { Child } from 'src/app/models/child';
import { FormControl } from '@angular/forms';
import { ConfirmDialog } from 'src/app/components/dialogs/confirm-dialog/confirm.dialog';
import { Line } from 'src/app/models/line';

@Component({
    selector: 'add-child-dialog',
    templateUrl: './add-child.dialog.html',
    styleUrls: ['./add-child.dialog.css']
})
export class SubscribeChildDialog implements OnInit {

    disableSelect = new FormControl(true);
    private usersSbj = new BehaviorSubject<UserInfo[]>([]);

    parent: UserInfo;
    children: Child[];
    child: Child;

    constructor(public dialogRef: MatDialogRef<SubscribeChildDialog>, private adminSvc: AdminService, public dialog: MatDialog, @Inject(MAT_DIALOG_DATA) public data: any) {
        this.parent = new UserInfo();
        this.parent.children = [];
        this.parent.mail = "";
        this.children = [];
        this.child = new Child();
    }

    ngOnInit(): void {
        this.adminSvc.getUsers(0, 100, null, null)
            .subscribe((users: Users) => {
                const usrs = users.content.map(user => new UserInfo(user));
                this.usersSbj.next(usrs);
            });
    }

    onCancel(): void {
        this.dialogRef.close();
    }

    addChild(): void {
        this.dialog.open(ConfirmDialog, { data: { title: "Add Child", message: `Are you sure deleted this child? \n- ${this.child.name} ${this.child.surname}\n`, CANCEL: true, YES: true } })
            .afterClosed().subscribe(result => {
                switch (result) {
                    case "YES":
                        let child = new Child(this.child.name, this.child.surname, this.child.cf, this.parent.mail);
                        this.adminSvc.addChildrenToLine(this.data.line.name, child).toPromise()
                            .then(response => {
                                this.adminSvc.linesChanged("Child added");
                                this.dialogRef.close();
                            })
                            .catch(error => console.log(error));
                        break;
                    default:
                        break;
                }
            })
    }

    changeParent(event: any) {
        if (event.isUserInput) {
            //console.log(event.source.value);
            this.disableSelect.setValue(false);
            this.children = event.source.value.children.filter((c1: Child) => !this.data.line.subscribedChildren.find((c: Child) => c.cf == c1.cf))
        }
    }

}
