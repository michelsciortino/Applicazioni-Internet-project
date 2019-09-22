import { Component, OnInit } from '@angular/core';
import { CompanionRacesDataSource, CompanionService } from 'src/app/services/companion/companion.service';
import { Race } from 'src/app/models/race';
import { ViewRaceDialog } from 'src/app/components/dialogs/view-race-dialog/view-race.dialog';
import { MatDialog } from '@angular/material';
import Utils from 'src/app/utils/utils';

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
    const dialogRef = this.dialog.open(ViewRaceDialog, { data: { race: race } });
  }
}
