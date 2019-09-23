import { Injectable, OnDestroy } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { environment } from 'src/environments/environment';
import { HttpHeaders, HttpClient, HttpParams } from '@angular/common/http';
import { UserInfo } from 'src/app/models/user';
import { LineService } from '../lines/line-races.service';
import { debug } from 'util';
import { Race } from 'src/app/models/race';
import { reserveChildrenRequest } from 'src/app/models/reserve-children-request';

const credentialsEndpoint = `${environment.baseEndpoint}/credentials`;
const parentEndpoint = `${environment.baseEndpoint}/parent`;
@Injectable()
export class ParentService implements OnDestroy {
    private userSbj: BehaviorSubject<UserInfo>;
    private authSub: Subscription;
    private racesSbj: BehaviorSubject<Race[]>;

    constructor(private authSvc: AuthService, private router: Router, private http: HttpClient) {
        this.userSbj = new BehaviorSubject(null);
        this.racesSbj = new BehaviorSubject<Race[]>([])
        this._getRaces();
    }

    private _getRaces() {
        let date = new Date().toISOString();
        console.log("Chiedo di avere le races da questa data " + date);
        this.http.get(parentEndpoint + "/races", {
            params: {
                date: date
            }
        }).subscribe(
            (races: Race[]) => {
                console.log(races)
                this.racesSbj.next(races)
            }
        );
    }

    confirmChildrenToRace(reservation: reserveChildrenRequest) {
        console.log(reservation)
        return this.http.post(parentEndpoint + "/reserveChildren", reservation).toPromise()
            .then((result) => {
                // console.log(result);
                return result;
            }
            ).catch((error) => console.debug(error));
    }

    ngOnDestroy() {
        this.authSub.unsubscribe();
    }

    public getRaces(): Observable<Race[]> {
        return this.racesSbj.asObservable();
    }

    public getUserInfo(): Observable<UserInfo> {
        return this.userSbj.asObservable();
    }
}
