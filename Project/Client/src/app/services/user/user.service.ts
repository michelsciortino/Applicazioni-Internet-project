import { Injectable, OnDestroy } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { UserInfo } from './models/user';
import { environment } from 'src/environments/environment';
import { HttpHeaders, HttpClient, HttpParams } from '@angular/common/http';

const credentialsEndpoint = `${environment.baseEndpoint}/credentials`;
const userEndpoint = `${environment.baseEndpoint}/users`;
@Injectable()
export class UserService implements OnDestroy{
    private userSbj: Subject<UserInfo>;
    private userInfo: UserInfo;
    private authSub: Subscription;

    constructor(private authSvc: AuthService, private router: Router, private http: HttpClient) {
        this.userSbj = new Subject();
        this.userInfo = new UserInfo();
        this.authSub = this.authSvc.observeLoggedStatus().subscribe(
            (status: boolean) => {
                console.log("logged status changed to:",status)
                if (status)
                    this.update();
                else
                    this.userInfo = new UserInfo();
            }
        );
    }

    ngOnDestroy(){
        this.authSub.unsubscribe();
    }

    public getUserInfo(): Observable<UserInfo> {
        return this.userSbj.asObservable();
    }

    public update(): void {
        this.http.get(`${userEndpoint}/${this.authSvc.getCurrentUser().mail}`).toPromise()
            .then(
                (data: UserInfo) => {
                    this.userInfo.name = data.name;
                    this.userInfo.surname = data.surname;
                    this.userInfo.contacts = data.contacts;
                    this.userInfo.children = data.children;
                    this.userInfo.adminlines = data.adminlines;
                    this.userInfo.companionLines = data.companionLines;
                    this.userInfo.roles=data.roles;
                    this.userSbj.next(this.userInfo);
                }
            )
            .catch((error: any) => {
                console.error(error);
            });
    }
}
