import { Component, OnInit, OnDestroy, LOCALE_ID } from '@angular/core';
import { Subscription } from 'rxjs';
import { UserService } from 'src/app/services/user/user.service';
import { UserInfo } from 'src/app/models/user';
import { Race } from 'src/app/models/race';
import { ParentService } from 'src/app/services/parent/parent.service';
import { map } from 'rxjs/operators';
import { HttpErrorResponse } from '@angular/common/http';
import { Child } from 'src/app/models/child';
import { MatDialog } from '@angular/material';
import { ConfirmChildToRaceDialog } from './confirm-child-to-race-dialog/confirm-child-to-race.dialog';
import { DatePipe } from '@angular/common';

@Component({
    selector: 'app-parent',
    templateUrl: './parent.component.html',
    styleUrls: ['./parent.component.css', '../common.css']
})
export class ParentComponent implements OnInit, OnDestroy {
    isCompanion: boolean;
    userInfo: UserInfo = new UserInfo;
    private userSub: Subscription;
    races: Race[];
    childNames: string[] = [];
    children: Child[] = [];
    dates : string[] = [];


    constructor(private userSvc: UserService, private parentSvc: ParentService, public dialog: MatDialog, private datePipe : DatePipe) {
    }

    ngOnInit() {
        this.userSub = this.userSvc.getUserInfo().subscribe(
            (info: UserInfo) => {
                if (info != null) {
                    this.userInfo = info;
                    //console.log("info : " + this.userInfo);
                    this.isCompanion = this.userInfo.isCompanion();
                    this.children = this.userInfo.children;
                    this.children.forEach(child => {
                        child.parentId = this.userInfo.mail;
                    })
                    this.parentSvc.getRaces()
                        .pipe(
                            map(
                                (data: any) => data,
                                (error: HttpErrorResponse) => console.log(error)
                            )
                        )
                        .subscribe((data: Race[]) => {
                            if (data.length == 0) return;
                            this.races = data;
                            this.races.forEach(race => {
                                race.passengers.forEach(passenger => {
                                    for (let i = 0; i < this.children.length; i++) {
                                        if (passenger.childDetails.name === this.children[i].name) {
                                            this.childNames.push(this.children[i].name)
                                            this.dates.push(this.datePipe.transform(race.date, "yyyy-MM-dd hh:mm a", "UTC"));
                                        }   
                                    }
                                }
                                )
                            });
                        });
                }
            }
        );

    }

    ngOnDestroy() {
        this.userSub.unsubscribe();
    }

    openConfirmChildToRaceDialog(): void {
        console.log("OPEN DIALOG ADD CHILD")
        this.dialog.open(ConfirmChildToRaceDialog, { data: { children: this.children } });
    }

}
