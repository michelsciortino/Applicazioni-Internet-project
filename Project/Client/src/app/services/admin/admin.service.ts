import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject } from 'rxjs';
import { UserInfo } from '../user/models/user';
import { UserRole } from '../user/models/roles';
import { environment } from 'src/environments/environment';
import { HttpClient } from '@angular/common/http';
import { Users } from 'src/app/models/responses/users_paginated';

@Injectable()
export class AdminService {
    private static readonly usersEndpoint = `${environment.baseEndpoint}/users`;
    private static readonly adminEndpoint = `${environment.baseEndpoint}/admin`;
    usersSbj: BehaviorSubject<UserInfo[]>;

    constructor(private http: HttpClient) {
        this.usersSbj = new BehaviorSubject([]);

        // init
        this.http.get(AdminService.usersEndpoint).subscribe(
            (users: Users) => this.usersSbj.next(users.content)
        );
    }

    public getUsers(): Observable<UserInfo[]> {
        return this.usersSbj.asObservable();
    }
}
