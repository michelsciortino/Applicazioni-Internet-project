import { Injectable, OnDestroy } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { environment } from 'src/environments/environment';
import { HttpClient } from '@angular/common/http';
import { UserInfo } from 'src/app/models/user';
import { LineService } from '../lines/line-races.service';

const credentialsEndpoint = `${environment.baseEndpoint}/credentials`;
const userEndpoint = `${environment.baseEndpoint}/users`;
@Injectable()
export class UserService implements OnDestroy {
  private userSbj: BehaviorSubject<UserInfo>;
  private userInfo: UserInfo;
  private authSub: Subscription;

  constructor(private authSvc: AuthService, private lineSvc: LineService, private router: Router, private http: HttpClient) {
    this.userSbj = new BehaviorSubject(null);
    this.userInfo = new UserInfo();
    this.authSub = this.authSvc
      .observeLoggedStatus()
      .subscribe((status: boolean) => {
        //console.log("logged status changed to:",status)
        if (status) this.update();
        else {
          this.userInfo = new UserInfo();
          this.userSbj.next(this.userInfo);
        }
      });
  }

  ngOnDestroy() {
    this.authSub.unsubscribe();
  }

  public getUserInfo(): Observable<UserInfo> {
    return this.userSbj.asObservable();
  }

  public updateUser(user: UserInfo) {
    return this.http
      .post(`${userEndpoint}/${user.mail}`, user)
      .toPromise()
      .then(result => {
        // console.log(result);
        this.userInfo = new UserInfo(user);
        this.userSbj.next(this.userInfo);
        return result;
      })
      .catch(error => console.debug(error));
  }

  public update(): void {
    this.http
      .get(`${userEndpoint}/${this.authSvc.getCurrentUser().mail}`)
      .toPromise()
      .then((data: UserInfo) => {
        this.userInfo = new UserInfo(data);
        if (this.userInfo.isSystemAdmin()) {
          this.lineSvc.getLines().subscribe((lines: any) => {
            this.userInfo.lines = lines.map(line => line.name);
            // console.log(this.userInfo.lines)
            this.userSbj.next(this.userInfo);
          });
        } else this.userSbj.next(this.userInfo);
      })
      .catch((error: any) => {
        console.error(error);
      });
  }

  public getNotifications(pageSize: number, pageNumber: number): Promise<any> {
    return this.http
      .get(`${userEndpoint}/${this.authSvc.getCurrentUser().mail}/notifications`)
      .toPromise();
  }

  public readNotification(notificationId: string) {
    return this.http
      .post(`${userEndpoint}/${this.authSvc.getCurrentUser().mail}/notifications/${notificationId}/read`, {})
      .toPromise();
  }
}
