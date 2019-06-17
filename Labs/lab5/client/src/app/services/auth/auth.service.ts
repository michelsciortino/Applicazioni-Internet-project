import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Credentials, User } from '../../models/user';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { map, take } from 'rxjs/operators';


@Injectable({ providedIn: 'root' })
export class AuthService {
  private static readonly AUTHENTICATED_USER: string = 'authUser';
  private static readonly authEndpoint = 'http://localhost:8080/auth';

  private userSubject: BehaviorSubject<User>;
  private user: Observable<User>;

  constructor(private http: HttpClient) {
    this.userSubject = new BehaviorSubject<User>(JSON.parse(localStorage.getItem(AuthService.AUTHENTICATED_USER)));
    this.user = this.userSubject.asObservable();
  }

  //#region Properties
  public isLoggedIn(): boolean {
    return this.userSubject.value != null;
  }

  public getUser(): User {
    return this.userSubject.value;
  }

  public getUserObserver(): Observable<User> {
    return this.user;
  }

  //#endregion

  //#region Methods
  login(credentials: Credentials): Observable<object> {
    let reqHeaders = new HttpHeaders({ ContentType: 'application/json' });
    return this.http.post(`${AuthService.authEndpoint}/login`, credentials, { headers: reqHeaders })
      .pipe(
        take(1),
        map((user: any) => {
          if (user != null) {
            localStorage.setItem(AuthService.AUTHENTICATED_USER, JSON.stringify(user));
            this.userSubject.next(user);
          }
          return user;
        })
      );
  }

  logout() {
    localStorage.removeItem(AuthService.AUTHENTICATED_USER);
    this.userSubject.next(null);
  }

  // TODO registration
  register(credentials: any) {
    let reqHeaders = new HttpHeaders({ ContentType: 'application/json' });
    return this.http.post(`${AuthService.authEndpoint}/register`, credentials, { headers: reqHeaders })
  }

  //#endregion
}
