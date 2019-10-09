import { Injectable } from "@angular/core";
import { environment } from 'src/environments/environment';
import { Observable, BehaviorSubject, of, Subject } from 'rxjs';
import { CompanionRequest } from 'src/app/models/companion-request';
import { HttpClient } from '@angular/common/http';
import { map, catchError, finalize } from 'rxjs/operators';
import { CompanionState, Companion } from 'src/app/models/companion';
import { DataSource } from '@angular/cdk/table';
import { CollectionViewer } from '@angular/cdk/collections';
import { Stop } from 'src/app/models/stop';
import { Race } from 'src/app/models/race';
import { Passenger } from 'src/app/models/passenger';

@Injectable()
export class CompanionService {
    private static readonly companionEndpoint = `${environment.baseEndpoint}/companion`;

    private companionInfoChange: Subject<string> = new Subject<string>();

    constructor(private http: HttpClient) { }

    public getCompanionInfoChanges(): Observable<string> {
        return this.companionInfoChange.asObservable();
    }

    public companionInfoChanged(message: string) {
        this.companionInfoChange.next(message);
    }

    public getRequests(): Observable<CompanionRequest[]> {
        return this.http.get(`${CompanionService.companionEndpoint}/companionRequests`)
            .pipe(
                map((requests: CompanionRequest[]) => {
                    requests.forEach(r => r.date = new Date(r.date));
                    return requests;
                })
            ) as Observable<CompanionRequest[]>;
    }

    public cancelRequest(lineName: string, direction: string, date: Date): Observable<any> {
        return this.http.post(`${CompanionService.companionEndpoint}/removeAvailability`,
            {
                lineName: lineName,
                direction: direction,
                date: date
            });
    }

    public confirmAvailability(lineName: string, direction: string, date: Date): Observable<any> {
        return this.http.post(`${CompanionService.companionEndpoint}/confirmAvailability`,
            {
                lineName: lineName,
                direction: direction,
                date: date
            });
    }

    public giveAvailability(lineName: string, direction: string, date: Date, initialStop: Stop, finalStop: Stop) {
        return this.http.post(`${CompanionService.companionEndpoint}/giveAvailability`,
            {
                lineName: lineName,
                direction: direction,
                initialStop: initialStop,
                finalStop: finalStop,
                date: date
            }
        );
    }

    public getRaces(date: Date) {
        return this.http.get(`${CompanionService.companionEndpoint}/races?date=${date.toISOString()}`).pipe(
            map((races: Race[]) => races.map(race => {
                race.date = new Date(race.date)
                return race;
            }))
        );
    }

    public startRace(lineName: string, direction: string, date: Date) {
        return this.http.post(`${CompanionService.companionEndpoint}/startRace/${lineName}/${date.toISOString()}/${direction}`, {})
            .toPromise();
    }

    public endRace(lineName: string, direction: string, date: Date) {
        return this.http.post(`${CompanionService.companionEndpoint}/endRace/${lineName}/${date.toISOString()}/${direction}`, {})
            .toPromise();
    }

    public stopReached(lineName: string, direction: string, date: Date, stopName: string) {
        return this.http.post(`${CompanionService.companionEndpoint}/stopReached/${lineName}/${date.toISOString()}/${direction}/${stopName}`, {})
            .toPromise();
    }

    public stopLeft(lineName: string, direction: string, date: Date, stopName: string) {
        return this.http.post(`${CompanionService.companionEndpoint}/stopLeft/${lineName}/${date.toISOString()}/${direction}/${stopName}`, {})
            .toPromise();
    }

    public takeChildren(lineName: string, direction: string, date: Date, stopName: string, children: Passenger[]) {
        return this.http.post(`${CompanionService.companionEndpoint}/takeChildren`, {
            lineName: lineName,
            direction: direction,
            date: date,
            children: children,
            stopName: stopName
        }).toPromise();
    }

    public deliverChildren(lineName: string, direction: string, date: Date, stopName: string, children: Passenger[]) {
        return this.http.post(`${CompanionService.companionEndpoint}/deliverChildren`, {
            lineName: lineName,
            direction: direction,
            date: date,
            children: children,
            stopName: stopName
        }).toPromise();
    }

    public absentChildren(lineName: string, direction: string, date: Date, stopName: string, children: Passenger[]) {
        return this.http.post(`${CompanionService.companionEndpoint}/absentChildren`, {
            lineName: lineName,
            direction: direction,
            date: date,
            children: children,
            stopName: stopName
        }).toPromise();
    }
}

export class CompanionRequestsDataSource {
    private requestsSbj = new BehaviorSubject<CompanionRequest[]>([]);

    constructor(private companionSvc: CompanionService) {
        this.companionSvc.getRequests().subscribe((requests: CompanionRequest[]) => this.requestsSbj.next(requests));
    }

    disconnect(collectionViewer: CollectionViewer): void {
    }

    public getPendingRequests(): Observable<CompanionRequest[]> {
        return this.requestsSbj.asObservable()
            .pipe(
                map(requests => requests.filter(request => request.state === CompanionState.AVAILABLE))
            );
    }

    public getAcceptedRequests(): Observable<CompanionRequest[]> {
        return this.requestsSbj.asObservable()
            .pipe(
                map(requests => requests.filter(request => request.state === CompanionState.CHOSEN))
            );
    }

    public getConfirmedRequests(): Observable<CompanionRequest[]> {
        return this.requestsSbj.asObservable()
            .pipe(
                map(requests => requests.filter(request => request.state === CompanionState.CONFIRMED))
            );
    }

    public reload() {
        this.companionSvc.getRequests().subscribe((requests: CompanionRequest[]) => this.requestsSbj.next(requests));
    }
}

export class CompanionRacesDataSource implements DataSource<Race>{
    private racesSbj: BehaviorSubject<Race[]>;
    private loadingSbj: BehaviorSubject<boolean>;
    public loading$: Observable<boolean>;

    constructor(private companionSvc: CompanionService) {
        this.racesSbj = new BehaviorSubject<Race[]>([]);
        this.loadingSbj = new BehaviorSubject<boolean>(false);
        this.loading$ = this.loadingSbj.asObservable();
    }

    connect(collectionViewer: CollectionViewer): Observable<Race[]> {
        return this.racesSbj.asObservable();
    }

    disconnect(collectionViewer: CollectionViewer): void {
    }

    loadRaces(date: Date) {
        this.loadingSbj.next(true);
        this.companionSvc.getRaces(date)
            .pipe(
                catchError(() => of([])),
                finalize(() => {
                    // console.log(this.racesSbj.value);
                    this.loadingSbj.next(false);
                })
            ).subscribe((races: Race[]) => {
                // console.log(races)
                this.racesSbj.next(races);
            });
    }

    public isEmpty(): boolean {
        return this.racesSbj && this.racesSbj.value && this.racesSbj.value.length > 0 ? false : true;
    }
}
