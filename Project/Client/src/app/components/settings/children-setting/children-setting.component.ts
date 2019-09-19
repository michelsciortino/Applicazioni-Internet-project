import { Component, OnInit, OnDestroy } from '@angular/core';
import { UserInfo } from 'src/app/models/user';
import { UserService } from 'src/app/services/user/user.service';
import { Subscription } from 'rxjs';
import { Child } from 'src/app/models/child';
import { MatDialog } from '@angular/material';
import { AddChildDialog } from './add-child-dialog/add-child.dialog';
import { ConfirmDialog } from '../../dialogs/confirm-dialog/confirm.dialog';

@Component({
    selector: 'app-children-setting',
    templateUrl: './children-setting.component.html',
    styleUrls: ['./children-setting.component.css']
})
export class ChildrenSettingComponent implements OnInit, OnDestroy {
    userInfo: UserInfo = new UserInfo;
    children: Child[];
    localInfo: UserInfo = new UserInfo;

    private userInfoSub: Subscription;

    constructor(private userSvc: UserService, public dialog: MatDialog) {
    }

    ngOnInit() {
        this.userInfoSub = this.userSvc.getUserInfo().subscribe(
            (info: UserInfo) => {
                if (info != null) {
                    //console.log(info);
                    this.userInfo = info;
                    this.children = this.userInfo.children;
                    this.localInfo = JSON.parse(JSON.stringify(info));
                }
            }
        );
    }

    ngOnDestroy(): void {
        this.userInfoSub.unsubscribe();
    }

    openAddChildDialog(): void {
        console.log("OPEN DIALOG ADD CHILD")
        this.dialog.open(AddChildDialog, { data: { user: this.userInfo } });
    }

    removeChild(child: Child): void {
        console.log("REMOVE CHILD:", child)

        this.dialog.open(ConfirmDialog, {
            width: '300px',
            data: { title: 'Remove Child', message: `Are you sure deleted this child? \n- ${child.name} ${child.surname}\n`, YES: true, CANCEL: true }
        })
            .afterClosed().subscribe(result => {
                switch (result) {
                    case "YES":
                        //console.log('Yes clicked', this.localInfo.children.map(function (e) { return e.cf; }).indexOf(child.cf));
                        this.localInfo.children.splice(this.localInfo.children.map((x) => { return x.cf; }).indexOf(child.cf), 1);
                        this.userSvc.updateUser(this.localInfo);
                        break;
                    default:
                        break;
                }
            });
    }
}