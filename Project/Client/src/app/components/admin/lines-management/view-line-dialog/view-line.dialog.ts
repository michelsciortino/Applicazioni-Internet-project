import { Component, Inject, OnInit, OnDestroy } from "@angular/core";
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material';
import { Line } from 'src/app/models/line';
import { SubscribeChildDialog } from '../add-child-dialog/add-child.dialog';
import { UserService } from 'src/app/services/user/user.service';
import { UserInfo } from 'src/app/models/user';
import { Subscription } from 'rxjs';
import { THIS_EXPR } from '@angular/compiler/src/output/output_ast';
import { AdminService } from 'src/app/services/admin/admin.service';
import { LineService } from 'src/app/services/lines/line-races.service';
import { Child } from 'src/app/models/child';

@Component({
    selector: 'app-view-line-dialog',
    templateUrl: './view-line.dialog.html',
    styleUrls: ['./view-line.dialog.css']
})
export class ViewLineDialog implements OnInit, OnDestroy {

    line: Line;
    userInfo: UserInfo = new UserInfo;

    private userInfoSub: Subscription;
    private linesChangesSub: Subscription;

    constructor(public dialogRef: MatDialogRef<ViewLineDialog>, public dialog: MatDialog, private adminSvc: AdminService, private userSvc: UserService, private lineSvc: LineService, @Inject(MAT_DIALOG_DATA) public data: any) {
        this.line = data.line;
        this.userInfo = new UserInfo();
        this.userInfo.roles = [];
    }

    ngOnInit() {
        this.userInfoSub = this.userSvc.getUserInfo().subscribe(
            (info: UserInfo) => {
                if (info != null) {
                    this.userInfo = info;
                    console.log(this.userInfo)
                }
            }
        );

        this.linesChangesSub = this.adminSvc.getLinesChanges().subscribe((reason) => {
            console.log(reason);
            this.getLine(this.line.name);
        });
    }

    ngOnDestroy() {
        this.userInfoSub.unsubscribe();
        this.linesChangesSub.unsubscribe();
    }

    private getLine(lineName: string) {
        this.lineSvc.getLine(lineName).toPromise()
            .then(
                (line: Line) => this.line = line
            )
            .catch((error) => console.log(error))
    }

    onCancel() {
        this.dialogRef.close();
    }

    addChildtoLine(): void {
        const dialogRef = this.dialog.open(SubscribeChildDialog, { data: { line: this.line } });
    }

    removeChildtoLine(child: Child): void {
        console.log("REMOVE CHILD", child);
    }

    isAdminOfLine() {
        if (this.userInfo != undefined)
            if (this.userInfo.isSystemAdmin())
                return true
            else
                return this.userInfo.isAdminOfLine(this.line.name);
        else
            return false
    }

}