import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {Credentials, User} from './models/user';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {map} from 'rxjs/operators';


@Injectable({providedIn: 'root'})
export class AuthService {
  private static readonly AUTHENTICATED_USER: string = 'authUser';
  private static readonly serverUrl = 'http://localhost:8080';
  private reqHeaders = new HttpHeaders({ContentType: 'application/json'});

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
    return this.http.post(AuthService.serverUrl + '/auth/login', credentials, {headers: this.reqHeaders})
      .pipe(
        map((user: User) => {
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
  register(user: User) {

  }

  //#endregion
}
