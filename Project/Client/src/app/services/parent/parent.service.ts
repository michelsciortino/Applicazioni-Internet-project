import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { environment } from 'src/environments/environment';
import { HttpClient, HttpParams, HttpErrorResponse } from '@angular/common/http';
import { UserInfo } from 'src/app/models/user';
import { LineService } from '../lines/line-races.service';
import { Race } from 'src/app/models/race';
import { reserveChildrenRequest } from 'src/app/models/reserve-children-request';
import { map } from 'rxjs/operators';
import { Line } from 'src/app/models/line';
import { DatePipe } from '@angular/common';


const parentEndpoint = `${environment.baseEndpoint}/parent`;
@Injectable()
export class ParentService {
    private userSbj: BehaviorSubject<UserInfo>;
    private lineSbj: BehaviorSubject<Line[]>;

    private reservedChangeSbj: Subject<string>;
    private reservableChangeSbj: Subject<string>;

    constructor(private lineSvc: LineService, private http: HttpClient) {

        this.userSbj = new BehaviorSubject(null);
        this.lineSbj = new BehaviorSubject<Line[]>([]);

        this.reservedChangeSbj = new Subject()
        this.reservableChangeSbj = new Subject()
    }

    public reserveChildrenToRace(reservation: reserveChildrenRequest) {
        console.log(reservation)
        return this.http.post(`${parentEndpoint}/reserveChildren`, reservation).toPromise()
            .then((result) => {
                // console.log(result);
                this.reservedChanged("Reserve " + reservation.children[0].childDetails.name)
                return result;
            }
            ).catch((error) => console.debug(error));
    }

    public getTodayRaces(): Promise<Race[]> {
        return this.http.get(`${parentEndpoint}/races/${new Date().toISOString()}`).toPromise() as Promise<Race[]>;
    }

    public getParentLines(parentId: string): Observable<Line[]> {
        this.lineSvc.getLines()
            .pipe(
                map(
                    (data: any) => data,
                    (error: HttpErrorResponse) =>
                        console.log(error)
                )
            )
            .subscribe((data: Line[]) => {
                if (data.length == 0) return;
                let parentLines: Line[] = [];
                data.filter(line => {
                    line.subscribedChildren.forEach(child => {
                        if (child.parentId === parentId)
                            if (!parentLines.includes(line)) {
                                parentLines.push(line);
                            }
                    })
                })
                this.lineSbj.next(parentLines)
            })
        return this.lineSbj.asObservable();
    }

    public removeChildrenFromRace(reservation: reserveChildrenRequest) {
        console.log(reservation)
        return this.http.post(`${parentEndpoint}/removeChildrenFromRace`, reservation).toPromise()
            .then((result) => {
                // console.log(result);
                this.reservableChanged("Removed reservation for child " + reservation.children[0].childDetails.name);
                return result;
            }
            ).catch((error) => console.debug(error));
    }

    public async getParentRaces(fromDate: Date, toDate: Date, reserved: boolean, lineName: string, direction: string) {
        // Parameters obj-
        let params = new HttpParams();

        if (lineName != null) {
            params = params.set('lineName', lineName);
        }
        if (direction != null) {
            params = params.set('direction', direction);
        }
        if (fromDate != null && toDate != null) {
            params = params.set('fromDate', fromDate.toISOString());
            params = params.set('toDate', toDate.toISOString());
        }
        if (reserved != null) {
            params = params.set('reserved', String(reserved));
        }
        return await this.http.get(`${parentEndpoint}/races`, { params }).pipe(
            map((races: Race[]) => races.map(race => {
                race.date = new Date(race.date)
                return race;
            }))
        ).toPromise();
    }

    public getUserInfo(): Observable<UserInfo> {
        return this.userSbj.asObservable();
    }

    public getReservedChanges(): Observable<string> {
        return this.reservedChangeSbj.asObservable();
    }

    reservedChanged(reason: string) {
        this.reservedChangeSbj.next(reason);
    }

    reservableChanged(reason: string) {
        this.reservableChangeSbj.next(reason);
    }

    public getReservableChanges(): Observable<string> {
        return this.reservableChangeSbj.asObservable();
    }
}

export class ReservableRacesDataSource {

    private reservableRacesSbj: BehaviorSubject<Race[]>;
    private reservableRacesForChildSbj: BehaviorSubject<Race[]>;
    private reservableRacesForChildAndDateSbj: BehaviorSubject<Race[]>;

    private loadingSbj: BehaviorSubject<boolean>;
    private date: Date = new Date();

    constructor(private parentSvc: ParentService, private datePipe: DatePipe) {
        this.reservableRacesSbj = new BehaviorSubject<Race[]>([]);
        this.reservableRacesForChildSbj = new BehaviorSubject<Race[]>([]);
        this.reservableRacesForChildAndDateSbj = new BehaviorSubject<Race[]>([]);
        this.loadingSbj = new BehaviorSubject<boolean>(false);
    }

    loadReservableRaces(lineName: string, direction: string, fromDate: Date, toDate: Date) {
        this.loadingSbj.next(true);
        return this.parentSvc.getParentRaces(fromDate, toDate, false, lineName, direction)
            .then((data: Race[]) => {
                data.map(x => x.date = new Date(x.date));
                console.log(data)
                this.reservableRacesSbj.next(data);
            })
            .finally(() => this.loadingSbj.next(false))
    }

    public getReservableRaces(): Observable<Race[]> {
        return this.reservableRacesSbj.asObservable();
    }

    public getReservableRacesForChild(childCF: string): Observable<Race[]> {
        let reservableRaces: Race[] = [];
        this.reservableRacesSbj.value.forEach(race => {
            race.passengers.forEach(passenger => {
                if (!passenger.reserved && passenger.childDetails.cf === childCF) {
                    console.log(race.date.toString())
                    reservableRaces.push(race);
                }
            })
        })
        console.log("Reservable races for child", childCF, reservableRaces)
        this.reservableRacesForChildSbj.next(reservableRaces)
        return this.reservableRacesForChildSbj.asObservable();
    }

    public getReservableRacesForChildAndDate(childCF: string, date: Date): Observable<Race[]> {
        let reservableRaces: Race[] = [];
        this.reservableRacesForChildSbj.value.forEach(race => {
            let d = new Date(race.date)
            let d1 = new Date(date)
            if (d.getDate() === d1.getDate() && d.getMonth() === d1.getMonth() && d.getFullYear() === d1.getFullYear())
                race.passengers.forEach(passenger => {
                    if (!passenger.reserved && passenger.childDetails.cf === childCF) {
                        console.log(race.date.toString())
                        reservableRaces.push(race);
                    }
                })
        })
        console.log("Reservable races for child and Date", childCF, date.toISOString(), reservableRaces)
        this.reservableRacesForChildAndDateSbj.next(reservableRaces)
        return this.reservableRacesForChildAndDateSbj.asObservable();
    }

    public getReservableDatesForChild(): Observable<string[]> {
        return this.reservableRacesForChildSbj.asObservable()
            .pipe(
                map((races: Race[]) => {
                    let dates: string[] = [];
                    races.forEach(race => {
                        dates.push(this.datePipe.transform(race.date, "yyyy-MM-dd"));
                    })
                    console.log(dates)
                    return dates;
                })
            )
    }

    public setDate(date: Date) {
        console.log("cambio data")
        this.date = new Date(date);
    }
}

export class ReservedRacesDataSource {
    private reservedRacesSbj = new BehaviorSubject<Race[]>([]);
    private loadingSbj: BehaviorSubject<boolean>;

    constructor(private parentSvc: ParentService) {
        this.loadingSbj = new BehaviorSubject<boolean>(false);
    }

    public getRaces(): Observable<Race[]> {
        return this.reservedRacesSbj.asObservable();
    }

    public loadReservedRaces(lineName: string, direction: string, fromDate: Date, toDate: Date) {
        this.loadingSbj.next(true);
        return this.parentSvc.getParentRaces(fromDate, toDate, true, lineName, direction)
            .then((data: Race[]) => {
                data.map(x => x.date = new Date(x.date));
                console.log(data)
                this.reservedRacesSbj.next(data);
            })
            .finally(() => this.loadingSbj.next(false))
    }
}