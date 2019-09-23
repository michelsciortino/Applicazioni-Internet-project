import { Injectable, OnDestroy } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { HttpHeaders, HttpClient, HttpParams, HttpErrorResponse } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Line } from '../../models/line';
import { Race, DirectionType } from 'src/app/models/race';
import { map, merge } from 'rxjs/operators';
import { DataSource } from '@angular/cdk/table';
import { AdminService } from '../admin/admin.service';
import { CollectionViewer } from '@angular/cdk/collections';
import { MatSort } from '@angular/material/sort';

@Injectable()
export class LineService {
    private static readonly lineEndpoint = `${environment.baseEndpoint}/lines`;

    private linesSubj: BehaviorSubject<Line[]>
    private racesSubj: BehaviorSubject<Race[]>


    constructor(private authSvc: AuthService, private http: HttpClient) {
        this.linesSubj = new BehaviorSubject<Line[]>([])
        this.racesSubj = new BehaviorSubject<Race[]>([])
        this._getLines();
    }

    //returns lines
    private _getLines() {
        this.http.get(LineService.lineEndpoint).subscribe(
            (lines: Line[]) => {
                this.linesSubj.next(lines)
            }
        );
    }

    public getLine(lineName: string) {
        return this.http.get(`${LineService.lineEndpoint}/${lineName}`);
    }

    public getLines(): Observable<Line[]> {
        return this.linesSubj.asObservable();
    }

    public async getLinesPromise() {
        return await this.http.get(LineService.lineEndpoint).toPromise();;
    }

    public async getRaces(lineName: string, fromDate: Date, toDate: Date, direction: string) {

        // Parameters obj-
        let params = new HttpParams();

        if (direction != null) {
            params = params.set('direction', direction);
        }
        if (toDate != null) {
            params = params.set('toDate', toDate.toISOString());
        }
        if (fromDate != null) {
            params = params.set('fromDate', fromDate.toISOString());
        }
        return await this.http.get(`${LineService.lineEndpoint}/${lineName}/races`, { params }).toPromise();;
    }

    public addRace(race: Race) {
        // console.log(`${LineService.lineEndpoint}/${race.line.name}/races`);
        return this.http.post(`${LineService.lineEndpoint}/${race.line.name}/races`, race).toPromise()
            .then(
                (result) => {
                    // console.log(result);
                    return result;
                }
            ).catch((error) => console.debug(error));
    }

    public getRace(lineName: string, date: Date, direction: string): Promise<Race> {
        return this.http.get(`${LineService.lineEndpoint}/${lineName}/races/${date.toISOString()}/${direction}`)
            .toPromise()
            .then((race: Race) => {
                race.date = new Date(race.date);
                return race;
            });
    }

    public deleteRace(lineName: string, date: Date, direction: string) {
        return this.http.delete(`${LineService.lineEndpoint}/${lineName}/races/${date.toISOString()}/${direction}`)
            .toPromise();
    }

    public validateRace(lineName: string, date: Date, direction: string) {
        return this.http.post(`${LineService.lineEndpoint}/${lineName}/races/${date.toISOString()}/${direction}/validate`, {})
            .toPromise();
    }
}

export class RacesDataSource implements DataSource<Race>{
    private racesSbj = new BehaviorSubject<Race[]>([]);
    private loadingSbj = new BehaviorSubject<boolean>(false);
    empty: boolean;

    constructor(private lineSvc: LineService, private sort: MatSort) {
        this.empty = false;
    }

    connect(collectionViewer: CollectionViewer): Observable<Race[]> {
        // console.log(this.sort);
        this.sort.sortChange.subscribe(
            (data: any) => {
                let races = this.racesSbj.getValue();
                this.racesSbj.next(this.sortRaces(races, data.direction, data.active));
            }
        )
        return this.racesSbj.asObservable();
    }

    disconnect(collectionViewer: CollectionViewer): void {
        this.racesSbj.complete();
        this.loadingSbj.complete();
    }

    loadRaces(lineName: string, fromDate: Date, toDate: Date, direction: string) {
        this.loadingSbj.next(true);
        this.lineSvc.getRaces(lineName, fromDate, toDate, direction)
            .then((data: Race[]) => {
                data.map(x => x.date = new Date(x.date));
                this.sortRaces(data, "asc", "Date");
                this.racesSbj.next(data);
                if (data.length === 0) {
                    // console.log("zero")
                    this.empty = true;
                }
                // console.log("LOAD_RACES", data);
            })
            .finally(() => this.loadingSbj.next(false))
    }

    isEmpty() {
        return this.empty;
    }

    sortRaces(data: Race[], direction: string, active: string): Race[] {
        switch (active) {
            case "Date":
                // console.log("Sort by date");
                if (direction === "asc")
                    return data.sort((a: Race, b: Race) => (a.date.getTime() - b.date.getTime()));
                else
                    return data.sort((a: Race, b: Race) => (b.date.getTime() - a.date.getTime()));
            case "Direction":
                // console.log("Sort by Direction");
                if (direction === "asc")
                    return data.sort((a: Race, b: Race) => a.direction.localeCompare(b.direction));
                else
                    return data.sort((a: Race, b: Race) => b.direction.localeCompare(a.direction));
            case "LineName":
                // console.log("Sort by LineName");
                if (direction === "asc")
                    return data.sort((a: Race, b: Race) => a.line.name.localeCompare(b.line.name));
                else
                    return data.sort((a: Race, b: Race) => a.line.name.localeCompare(b.line.name));
            default: break; // console.log("Error type sort");;
        }
    }
}


export class LinesDataSource implements DataSource<Line>{
    private linesSbj = new BehaviorSubject<Line[]>([]);
    private loadingSbj = new BehaviorSubject<boolean>(false);
    empty: boolean;

    constructor(private lineSvc: LineService) {
        this.empty = false;
    }

    connect(collectionViewer: CollectionViewer): Observable<Line[]> {
        // console.log(this.sort);
        return this.linesSbj.asObservable();
    }

    disconnect(collectionViewer: CollectionViewer): void {
        this.linesSbj.complete();
        this.loadingSbj.complete();
    }

    loadLines() {
        this.loadingSbj.next(true);
        this.lineSvc.getLinesPromise()
            .then((data: Line[]) => {
                this.linesSbj.next(data);
                if (data.length === 0) {
                    this.empty = true;
                }
            })
            .finally(() => this.loadingSbj.next(false))
    }

    isEmpty() {
        return this.empty;
    }
}
