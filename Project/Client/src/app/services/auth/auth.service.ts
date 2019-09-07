import { Injectable } from '@angular/core';
import { Credentials } from '../../models/requests/credentials';
import { HttpHeaders, HttpClient, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { map, catchError } from 'rxjs/operators';
import { ConfirmMail } from 'src/app/models/requests/confirmMail';
import { AuthenticatedUser } from 'src/app/models/responses/authenticated-user';
import { BehaviorSubject, Observable } from 'rxjs';

const AUTHENTICATED_USER = 'authenticated_user';

@Injectable()
export class AuthService {
    private static readonly authEndpoint = `${environment.baseEndpoint}/auth`; // 'http://localhost:8080/auth';
    private authSbj: BehaviorSubject<AuthenticatedUser>;

    constructor(private http: HttpClient) {
        const authenticatedUser = JSON.parse(localStorage.getItem(AUTHENTICATED_USER)) as AuthenticatedUser;
        this.authSbj = new BehaviorSubject<AuthenticatedUser>(authenticatedUser);
    }

    isLoggedIn(): boolean {
        return this.authSbj.value != null;
    }

    //#region Properties

    public getToken(): string {
        return this.authSbj.value.token;
    }

    public getCurrentUser(): AuthenticatedUser {
        return this.authSbj.value;
    }

    public observeLoggedStatus(): Observable<boolean> {
        return this.authSbj.asObservable().pipe(map((data) => data != null));
    }

    //#endregion

    //#region Auth Methods
    public login(credentials: Credentials) {
        const reqHeaders = new HttpHeaders({ ContentType: 'application/json' });
        return new Promise((resolve, reject) =>
            this.http.post(`${AuthService.authEndpoint}/login`, credentials, { headers: reqHeaders })
                .subscribe(
                    (data: AuthenticatedUser) => {
                        // handle jwt
                        localStorage.setItem(AUTHENTICATED_USER, JSON.stringify(data));
                        this.authSbj.next(data);
                        // console.log("Login response:", JSON.stringify(this.authSbj.value));
                        resolve();
                    },
                    (error) => {
                        switch (error.status) {
                            case 0:
                                reject('Server unreachable');
                                break;
                            case 400:
                                console.log(error);
                                reject('Bad request: ' + error.error.message);
                                break;
                            case 403:
                                reject('Invalid credentials');
                                break;
                            case 404:
                                reject('User Not Found');
                                break;
                            default:
                                reject('Unknown error');
                                break;
                        }
                    }
                )
        );
    }

    public logout() {
        localStorage.removeItem(AUTHENTICATED_USER);
        this.authSbj.next(null);
    }

    //#endregion

    //#region Registration Methods

    public register(registerMail: string) {
        const reqHeaders = new HttpHeaders({ ContentType: 'application/json' });
        return new Promise((resolve, reject) =>
            this.http.post(`${AuthService.authEndpoint}/register`, { mail: registerMail }, { headers: reqHeaders })
                .subscribe(
                    () => resolve(),
                    (error) => {
                        switch (error.status) {
                            case 0:
                                reject('Server unreachable');
                                break;
                            case 400:
                                console.log(error);
                                reject('Bad request: ' + error.error.message);
                                break;
                            case 403:
                                reject('Invalid mail');
                                break;
                            default:
                                reject('Unknown error');
                                break;
                        }
                    }
                ));
    }

    public confirmMail(token: string, confirmMail: ConfirmMail) {
        const reqHeaders = new HttpHeaders({ ContentType: 'application/json' });
        return new Promise((resolve, reject) =>
            this.http.post(`${AuthService.authEndpoint}/confirm/${token}`, confirmMail, { headers: reqHeaders })
                .subscribe(
                    () => resolve(),
                    (error) => {
                        switch (error.status) {
                            case 0:
                                reject('Server unreachable');
                                break;
                            case 400:
                                reject('Bad request: ' + error.error.message);
                                break;
                            case 403:
                                reject('Unauthorized');
                                break;
                            default:
                                reject('Unknown error');
                                break;
                        }
                    }
                ));
    }

    //#endregion

    //#region Recovery Methods

    public recovery(mailToRecover: string) {
        const reqHeaders = new HttpHeaders({ ContentType: 'application/json' });
        console.log(mailToRecover);
        return new Promise((resolve, reject) =>
            this.http.post(`${AuthService.authEndpoint}/recovery`, { mail: mailToRecover }, { headers: reqHeaders })
                .subscribe(
                    () => resolve(),
                    (error) => {
                        switch (error.status) {
                            case 0:
                                reject('Server unreachable');
                                break;
                            case 400:
                                console.log(error);
                                reject('Bad request: ' + error.error.message);
                                break;
                            case 403:
                                reject('Invalid mail');
                                break;
                            default:
                                reject('Unknown error');
                                break;
                        }
                    }
                ));
    }

    public resetPassword(token: string, password: string) {
        const reqHeaders = new HttpHeaders({ ContentType: 'application/json' });
        return new Promise((resolve, reject) =>
            this.http.post(`${AuthService.authEndpoint}/recovery/reset/${token}`, { newPassword: password }, { headers: reqHeaders })
                .subscribe(
                    () => resolve(),
                    (error) => {
                        switch (error.status) {
                            case 0:
                                reject('Server unreachable');
                                break;
                            case 400:
                                console.log(error);
                                reject('Bad request: ' + error.error.message);
                                break;
                            case 403:
                                reject('Unauthorized');
                                break;
                            default:
                                reject('Unknown error');
                                break;
                        }
                    }
                ));
    }

    //#endregion
}

