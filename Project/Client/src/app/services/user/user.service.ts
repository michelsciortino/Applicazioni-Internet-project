import { Injectable } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { UserInfo } from './models/user';
import { environment } from 'src/environments/environment';
import { HttpHeaders, HttpClient, HttpParams } from '@angular/common/http';

const credentialsEndpoint = `${environment.baseEndpoint}/credentials`;
const userEndpoint = `${environment.baseEndpoint}/users`;
@Injectable()
export class UserService {
    private userSbj: Subject<UserInfo>;
    private userInfo: UserInfo;

    private authSub: Subscription;

    constructor(private authSvc: AuthService, private router: Router, private http: HttpClient) {
        this.userSbj = new Subject();
        this.userInfo = new UserInfo();
        this.authSub = this.authSvc.observeLoggedStatus().subscribe(
            (status: boolean) => {
                // console.log("logged status changed to:",status)
                if (status)
                    this.update();
                else
                    this.userInfo = new UserInfo();
            }
        )
    }

    public getUserInfo(): Observable<UserInfo> | UserInfo {
        return this.userInfo;
        //this.userSbj.asObservable();
    }

    public observeUserInfo():Observable<UserInfo>{
        return this.userSbj.asObservable();
    }

    public update(): void {
        // console.log("Updating user info");
        this.http.get(`${credentialsEndpoint}/${this.authSvc.getUserMail()}`)
            .toPromise()
            .then((credentials: any) => {
                this.userInfo.roles = credentials.roles;
                this.userInfo.mail = credentials.mail;
                this.userSbj.next(this.userInfo);
            })
            .then((): any => this.http.get(`${userEndpoint}/${this.authSvc.getUserMail()}`).toPromise())
            .then(
                (data: any) => {
                    this.userInfo.name = data.name;
                    this.userInfo.surname = data.surname;
                    this.userInfo.contacts = data.contacts;
                    this.userInfo.children = data.children;
                    this.userInfo.adminlines = data.adminlines;
                    this.userInfo.companionLines = data.companionLines;
                    this.userSbj.next(this.userInfo);
                }
            )
            .catch((error: any) => {
                console.error(error);
            });
    }
}
