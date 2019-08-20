import { Injectable } from '@angular/core';
import { Credentials } from '../../models/credentials';
import { HttpHeaders, HttpClient, HttpParams } from '@angular/common/http';

import { map, catchError } from 'rxjs/operators';
import { ConfirmMail } from 'src/app/models/confirmMail';

@Injectable()
export class AuthService {
    private static readonly authEndpoint = 'http://localhost:8080/auth';
    constructor(private http: HttpClient) {

    }

    isLoggedIn(): boolean {
        return false;
    }

    public login(credentials: Credentials) {
        const reqHeaders = new HttpHeaders({ ContentType: 'application/json' });
        return new Promise((resolve, reject) =>
            this.http.post(`${AuthService.authEndpoint}/login`, credentials, { headers: reqHeaders })
                .subscribe(
                    (data) => {
                        // handle jwt
                        console.log(data);
                        resolve();
                    },
                    (error) => {
                        switch (error.status) {
                            case 0:
                                reject('Server unreachable');
                                break;
                            case 400:
                                console.log(error)
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

    }

    public recovery(recoveryMail: string) {
        const reqHeaders = new HttpHeaders({ ContentType: 'application/json' });
        const reqParams = new HttpParams().set('token', 'TOKEN' );
        return new Promise((resolve, reject) =>
            this.http.post(`${AuthService.authEndpoint}/recovery`, { mail: recoveryMail }, { headers: reqHeaders, params: reqParams })
                .subscribe(
                    () => resolve(),
                    (error) => {
                        switch (error.status) {
                            case 0:
                                reject('Server unreachable');
                                break;
                            case 400:
                                console.log(error)
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

    public resetPassword(password: string) {
        const reqHeaders = new HttpHeaders({ ContentType: 'application/json' });
        return new Promise((resolve, reject) =>
            this.http.post(`${AuthService.authEndpoint}/recovery/reset`, { newPassword: password }, { headers: reqHeaders })
                .subscribe(
                    () => resolve(),
                    (error) => {
                        switch (error.status) {
                            case 0:
                                reject('Server unreachable');
                                break;
                            case 400:
                                console.log(error)
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

    public confirmMail(confirmMail: ConfirmMail) {
        const reqHeaders = new HttpHeaders({ ContentType: 'application/json' });
        const reqParams = new HttpParams().set('token', 'TOKEN' );
        return new Promise((resolve, reject) =>
            this.http.post(`${AuthService.authEndpoint}/confirm`, confirmMail, { headers: reqHeaders, params: reqParams })
                .subscribe(
                    () => resolve(),
                    (error) => {
                        switch (error.status) {
                            case 0:
                                reject('Server unreachable');
                                break;
                            case 400:
                                console.log(error)
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
                                console.log(error)
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
}
