import { Component, OnInit, OnDestroy } from '@angular/core';
import { Line } from 'src/app/models/line';
import { UserInfo } from 'src/app/models/user';
import { Subscription } from 'rxjs';
import { LineService, LinesDataSource } from 'src/app/services/lines/line-races.service';
import { UserService } from 'src/app/services/user/user.service';
import { MatDialog } from '@angular/material';
import { SubscribeChildDialog } from './add-child-dialog/add-child.dialog';
import { UserRole } from 'src/app/models/roles';
import { AdminService } from 'src/app/services/admin/admin.service';

@Component({
    selector: 'app-lines-management',
    templateUrl: './lines-management.component.html',
    styleUrls: ['./lines-management.component.css']
})
export class LinesManagementComponent implements OnInit, OnDestroy {

    userInfo: UserInfo = new UserInfo;
    private userInfoSub: Subscription;
    private linesSub: Subscription;

    private linesChangesSub: Subscription;

    public isMobile: boolean;

    isMobileSub: Subscription;

    dataSource: LinesDataSource;

    lines: Line[];

    columnDefinitions = [
        { def: 'LineName' },
        { def: 'N° Children' },
        { def: 'Add-Child-Action' }
    ];

    constructor(private lineSvc: LineService, private userSvc: UserService, private adminSvc: AdminService, public dialog: MatDialog) {
        this.userInfo = new UserInfo();
        this.userInfo.roles = [];
    }

    ngOnInit(): void {

        this.dataSource = new LinesDataSource(this.lineSvc);

        this.userInfoSub = this.userSvc.getUserInfo().subscribe(
            (info: UserInfo) => {
                if (info != null) {
                    this.userInfo = info;
                    console.log(this.userInfo)
                }
            }
        );

        this.getLines();

        this.linesChangesSub = this.adminSvc.getLinesChanges().subscribe((reason) => {
            console.log(reason);
            this.getLines();
        });
    }

    ngOnDestroy(): void {
        this.userInfoSub.unsubscribe();
        this.linesSub.unsubscribe();
        this.linesChangesSub.unsubscribe();
    }

    private getLines() {
        this.linesSub = this.lineSvc.getLines().subscribe(
            (data: Line[]) => {
                console.log(data);
                this.lines = data;
                this.dataSource.loadLines();
            });
    }

    getDisplayedColumns(): string[] {
        return this.columnDefinitions
            .map(cd => cd.def);
    }

    viewLine(line: Line) {
        console.log("VIEW LINE:", line)
        //const dialogRef = this.dialog.open(ViewLineDialog, { data: { race: race } });
    }

    addChildtoLine(line: Line): void {
        const dialogRef = this.dialog.open(SubscribeChildDialog, { data: { line: line } });
    }

    isAdminOfLine(lineS: Line) {
        if (this.userInfo != undefined)
            if (this.userInfo.isSystemAdmin())
                return true
            else
                return this.userInfo.isAdminOfLine(lineS.name);
        else
            return false
    }
}

