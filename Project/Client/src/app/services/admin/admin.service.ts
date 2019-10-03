import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject, of, Subject } from 'rxjs';
import { map, catchError, finalize } from 'rxjs/operators'
import { UserInfo } from '../../models/user';
import { UserRole } from '../../models/roles';
import { environment } from 'src/environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Users } from 'src/app/models/users_paginated';
import { DataSource, CollectionViewer } from '@angular/cdk/collections';
import { CompanionRequest } from 'src/app/models/companion-request';
import { CompanionState } from 'src/app/models/companion';
import { Child } from 'src/app/models/child';

export enum UserSearchFilter {
    MAIL,
    NAME,
    SURNAME,
    ROLE
}

@Injectable()
export class AdminService {
    private static readonly usersEndpoint = `${environment.baseEndpoint}/users`;
    private static readonly adminEndpoint = `${environment.baseEndpoint}/admin`;

    private racesChangeSbj: Subject<string>;
    private linesChangeSbj: Subject<string>;

    constructor(private http: HttpClient) {
        this.racesChangeSbj = new Subject();
        this.linesChangeSbj = new Subject();
    }

    public getUsers(pageNumber: number = 0, pageSize: number = 10, filterBy: UserSearchFilter, filter: string): Observable<Users> {
        let params = new HttpParams();
        params = params.set('pageNumber', pageNumber.toString());
        params = params.set('pageSize', pageSize.toString())
        if (filterBy != null && filter != null) {
            switch (filterBy) {
                case UserSearchFilter.MAIL:
                    params = params.set('filterBy', 'MAIL');
                    break;
                case UserSearchFilter.NAME:
                    params = params.set('filterBy', 'NAME');
                    break;
                case UserSearchFilter.SURNAME:
                    params = params.set('filterBy', 'SURNAME');
                    break;
                case UserSearchFilter.ROLE:
                    params = params.set('filterBy', 'ROLE');
                    break;
            }
            params = params.set('filter', filter);
        }

        return this.http.get(AdminService.usersEndpoint, { params: params })
            .pipe(
                map((data: Users) => data)
            );
    }

    public makeAdmin(user: string, line: string) {
        return this.http.post(`${AdminService.adminEndpoint}/makeAdmin`, { targetName: user, lineName: line }).toPromise();
    }

    public removeAdmin(user: string, line: string) {
        return this.http.post(`${AdminService.adminEndpoint}/removeAdmin`, { targetName: user, lineName: line }).toPromise();
    }

    public makeCompanion(user: string) {
        return this.http.post(`${AdminService.adminEndpoint}/makeCompanion`, { targetName: user }).toPromise();
    }

    public removeCompanion(user: string) {
        return this.http.post(`${AdminService.adminEndpoint}/removeCompanion`, { targetName: user }).toPromise();
    }

    public getCompanionRequests() {
        return this.http.get(`${AdminService.adminEndpoint}/companionRequests`) as Observable<CompanionRequest[]>;
    }

    public acceptCompanionRequest(lineName: string, direction: string, date: Date, username: string) {
        return this.http.post(`${AdminService.adminEndpoint}/acceptCompanionRequest`,
            {
                lineName: lineName,
                direction: direction,
                date: date,
                companion: username
            });
    }

    public unAcceptCompanionRequest(lineName: string, direction: string, date: Date, username: string) {
        return this.http.post(`${AdminService.adminEndpoint}/unAcceptCompanionRequest`,
            {
                lineName: lineName,
                direction: direction,
                date: date,
                companion: username
            });
    }

    public rejectCompanionRequest(lineName: string, direction: string, date: Date, username: string) {
        return this.http.post(`${AdminService.adminEndpoint}/rejectCompanionRequest`,
            {
                lineName: lineName,
                direction: direction,
                date: date,
                companion: username
            });
    }

    public addChildrenToLine(lineName: string, child: Child) {
        return this.http.post(`${AdminService.adminEndpoint}/addChildrenToLine`,
            {
                lineName: lineName,
                child: child
            });
    }

    public removeChildrenToLine(lineName: string, child: Child) {
        return this.http.post(`${AdminService.adminEndpoint}/removeChildrenFromLine`,
            {
                lineName: lineName,
                child: child
            });
    }

    public getRacesChanges(): Observable<string> {
        return this.racesChangeSbj.asObservable();
    }

    racesChanged(reason: string) {
        this.racesChangeSbj.next(reason);
    }

    public getLinesChanges(): Observable<string> {
        return this.linesChangeSbj.asObservable();
    }

    linesChanged(reason: string) {
        this.linesChangeSbj.next(reason);
    }
}

export class UsersDataSource implements DataSource<UserInfo>{
    private usersSbj = new BehaviorSubject<UserInfo[]>([]);
    private nUsersSbj = new BehaviorSubject<number>(0);
    private loadingSbj = new BehaviorSubject<boolean>(false);
    public loading$: Observable<boolean>;
    public nUsers$: Observable<number>;

    constructor(private adminSvc: AdminService) {
        this.loading$ = this.loadingSbj.asObservable();
        this.nUsers$ = this.nUsersSbj.asObservable();
    }

    connect(collectionViewer: CollectionViewer): Observable<UserInfo[]> {
        return this.usersSbj.asObservable();
    }

    disconnect(collectionViewer: CollectionViewer): void {
    }

    loadUsers(pageNumber: number, pageSize: number, filterType: UserSearchFilter = null, filterKeyword: string = null) {
        this.loadingSbj.next(true);
        this.adminSvc.getUsers(pageNumber, pageSize, filterType, filterKeyword)
            .pipe(
                catchError(() => of([])),
                finalize(() => {
                    //console.log("LOADING FALSE")
                    this.loadingSbj.next(false);
                })
            ).subscribe((users: Users) => {
                //console.log(users)
                if (users && users.content) {
                    const usrs = users.content.map(user => new UserInfo(user));
                    this.usersSbj.next(usrs);
                    this.nUsersSbj.next(users.totalElements)
                }
                else {
                    this.usersSbj.next([]);
                    this.nUsersSbj.next(0)
                }
            });
    }
}

export class CompanionRequestsDataSource {
    private requestsSbj = new BehaviorSubject<CompanionRequest[]>([]);

    constructor(private adminSvc: AdminService) {
        this.adminSvc.getCompanionRequests().subscribe((requests: CompanionRequest[]) => this.requestsSbj.next(requests));
    }

    disconnect(collectionViewer: CollectionViewer): void {
    }

    public getPendingRequests(): Observable<CompanionRequest[]> {
        return this.requestsSbj.asObservable()
            .pipe(
                map(requests => requests.map(req => {
                    req.date = new Date(req.date);
                    return req;
                }).filter(request => request.state == CompanionState.AVAILABLE))
            );
    }

    public getAcceptedRequests(): Observable<CompanionRequest[]> {
        return this.requestsSbj.asObservable()
            .pipe(
                map(requests => requests.filter(request => request.state == CompanionState.CHOSEN))
            );
    }

    public getConfirmedRequests(): Observable<CompanionRequest[]> {
        return this.requestsSbj.asObservable()
            .pipe(
                map(requests => requests.filter(request => request.state == CompanionState.CONFIRMED))
            );
    }

    public reload() {
        this.adminSvc.getCompanionRequests().subscribe((requests: CompanionRequest[]) => this.requestsSbj.next(requests));
    }
}