import { Component, OnInit, NgZone } from '@angular/core';
import { Race } from 'src/app/models/race';
import { MatDialog } from '@angular/material';
import { Router } from '@angular/router';
import { ParentService, RacesDataSource } from 'src/app/services/parent/parent.service';
import { ViewParentTodayRaceDialog } from '../view-race/view-race.dialog';
import { Child } from 'src/app/models/child';
import { UserInfo } from 'src/app/models/user';
import { Subscription } from 'rxjs';
import { UserService } from 'src/app/services/user/user.service';
import Utils from 'src/app/utils/utils';

@Component({
  selector: 'app-parent-today-lines',
  templateUrl: './today-parent-races.component.html',
  styleUrls: ['./today-parent-races.component.css']
})
export class ParentTodayLinesComponent implements OnInit {
  userInfo: UserInfo = new UserInfo;
  private userSub: Subscription;

  dataSource: RacesDataSource;
  displayedColumns = ["children", "lineName", "direction", "status"];

  getTime = Utils.getTime;

  getTimeWithSecond = Utils.getTimeWithSecond;

  constructor(private ngZone: NgZone, private router: Router, private userSvc: UserService, private parentSvc: ParentService, public dialog: MatDialog) {
    this.dataSource = new RacesDataSource(this.parentSvc);
  }

  ngOnInit() {
    this.userSub = this.userSvc.getUserInfo().subscribe(
      (info: UserInfo) => {
        if (info != null) {
          this.userInfo = info;
          this.dataSource.loadTodayReservedRaces();
        }
      }
    )
  }

  public getDisplayedColumns() {
    return this.displayedColumns;
  }

  viewRace(race: Race) {
    // console.log("VIEW RACE:", race)
    this.dialog.open(ViewParentTodayRaceDialog, {
      data: {
        lineName: race.line.name,
        date: race.date,
        direction: race.direction
      }
    });
  }

  getChildren(race: Race) {
    let children: Child[] = [];
    race.passengers.forEach(pass => {
      if (pass.reserved && pass.childDetails.parentId === this.userInfo.mail)
        children.push(pass.childDetails);
    })
    // console.log(children);
    return children;
  }
}
