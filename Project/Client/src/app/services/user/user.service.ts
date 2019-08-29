import { Injectable } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable } from 'rxjs';
import { UserInfo } from './models/user';
import { environment } from 'src/environments/environment';
import { HttpHeaders, HttpClient, HttpParams } from '@angular/common/http';

const credentialsEndpoint = `${environment.baseEndpoint}/credentials`;
const userEndpoint = `${environment.baseEndpoint}/users`;
@Injectable()
export class UserService {
    private userSbj: BehaviorSubject<UserInfo>;

    constructor(private authSvc: AuthService, private router: Router, private http: HttpClient) {
        this.userSbj = new BehaviorSubject(new UserInfo());
    }

    public getUserInfo(): Observable<UserInfo> {
        return this.userSbj.asObservable();
    }

    public update(): void {
        this.http.get(`${credentialsEndpoint}/${this.authSvc.getUserMail()}`)
            .toPromise()
            .then((credentials: any) => {
                const userinfo = this.userSbj.getValue();
                userinfo.roles = credentials.roles;
                userinfo.mail = credentials.mail;
                this.userSbj.next(userinfo);
            })
            .then((): any => this.http.get(`${userEndpoint}/${this.authSvc.getUserMail()}`).toPromise())
            .then(
                (data: any) => {
                    const userinfo = this.userSbj.getValue();
                    userinfo.name = data.name;
                    userinfo.surname = data.surname;
                    userinfo.contacts = data.contacts;
                    userinfo.children = data.children;
                    userinfo.adminlines = data.adminlines;
                    userinfo.companionLines = data.companionLines;
                    this.userSbj.next(userinfo);
                }
            )
            .catch((error: any) => {
                console.error(error);
            });
    }
}
