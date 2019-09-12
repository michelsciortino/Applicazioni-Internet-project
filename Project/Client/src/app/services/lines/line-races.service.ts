import { Injectable, OnDestroy } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { HttpHeaders, HttpClient, HttpParams, HttpErrorResponse } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Line } from '../../models/line';
import { Race, DirectionType } from 'src/app/models/race';
import { map } from 'rxjs/operators';
import { DataSource } from '@angular/cdk/table';
import { AdminService } from '../admin/admin.service';
import { CollectionViewer } from '@angular/cdk/collections';

const lineEndpoint = `${environment.baseEndpoint}/lines`;
@Injectable()
export class LineService {
    private linesSubj: Subject<Line[]>
    private racesSubj: Subject<Race[]>


    constructor(private authSvc: AuthService, private http: HttpClient) {
        this.linesSubj = new Subject<Line[]>()
        this.racesSubj = new Subject<Race[]>()
        this._getLines();
    }

    //returns lines
    private _getLines() {
        return this.http.get(lineEndpoint).subscribe(
            (lines: Line[]) =>
                this.linesSubj.next(lines)
        );
    }

    private getLine(lineName: String) {
        return this.http.get(`${lineEndpoint}/${lineName}`);
    }

    public getLines(): Observable<Line[]> {
        return this.linesSubj.asObservable();
    }

    public async getRaces(lineName: String, fromDate: Date, toDate: Date, direction: string) {

        // Parameters obj-
        let params = new HttpParams();
            //.set('fromDate', fromDate.toISOString())
            //.set('toDate', toDate.toISOString())
            //.set('direction', direction);

        if (direction != null) {
                params = params.set('direction', direction);
        }
        if (toDate != null) {
            params = params.set('toDate', toDate.toISOString());
        }
        if (fromDate != null) {
            params = params.set('fromDate', fromDate.toISOString());
        }


        return await this.http.get(`${lineEndpoint}/${lineName}/races`, { params }).toPromise();;

    }
}

export class RacesDataSource implements DataSource<Race>{
    private racesSbj = new BehaviorSubject<Race[]>([]);
    private loadingSbj = new BehaviorSubject<boolean>(false);

    constructor(private lineSvc: LineService) {
    }

    connect(collectionViewer: CollectionViewer): Observable<Race[]> {
        return this.racesSbj.asObservable();
    }

    disconnect(collectionViewer: CollectionViewer): void {
        this.racesSbj.complete();
        this.loadingSbj.complete();
    }

    loadRaces(lineName: String, fromDate: Date, toDate: Date, direction: string) {

        this.loadingSbj.next(true);

        this.lineSvc.getRaces(lineName, fromDate, toDate, direction)
            .then((data: Race[]) => {
                if (data.length == 0) return;
                data.map(x => x.date = new Date(x.date));
                //data[0].date=new Date(data[0].date);
                this.racesSbj.next(data);
                console.log(this.racesSbj);
            })
            .finally(() => this.loadingSbj.next(false))
    }
}
