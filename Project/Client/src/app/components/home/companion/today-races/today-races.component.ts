import { Component, OnInit } from '@angular/core';
import { CompanionRacesDataSource, CompanionService } from 'src/app/services/companion/companion.service';
import { Race } from 'src/app/models/race';
import { MatDialog } from '@angular/material';
import Utils from 'src/app/utils/utils';
import { ManageRaceDialog } from 'src/app/components/admin/manage-race/manage-race.dialog';

@Component({
  selector: 'app-companion-today-lines',
  templateUrl: './today-races.component.html',
  styleUrls: ['./today-races.component.css']
})
export class CompanionTodayLinesComponent implements OnInit {

  dataSource: CompanionRacesDataSource;
  displayedColumns = ["lineName", "initialStop", "finalStop", "status"];

  isLoading: boolean;

  getTime = Utils.getTime;

  getTimeWithSecond = Utils.getTimeWithSecond;

  constructor(private companionSvc: CompanionService, public dialog: MatDialog) {
    this.dataSource = new CompanionRacesDataSource(this.companionSvc);
    this.isLoading = false;
  }

  ngOnInit() {
    this.dataSource.loadRaces(new Date());
    this.dataSource.loading$.subscribe((isLoading) => this.isLoading = isLoading);
  }

  public getDisplayedColumns() {
    return this.displayedColumns;
  }

  viewRace(race: Race) {
    console.log("VIEW RACE:", race)
    this.dialog.open(ManageRaceDialog, {
      data: {
        lineName: race.line.name,
        date: race.date,
        direction: race.direction
      }
    });
  }
}
