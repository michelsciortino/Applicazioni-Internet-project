import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../services/auth/auth.service';
import { MatSnackBar } from '@angular/material';


@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
    constructor(private authSvc: AuthService,private _snackBar: MatSnackBar) { }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(request).pipe(catchError(err => {
            switch (err.status) {
                case 0:
                    this._snackBar.open("Unalbe to cotact the server.", null, {
                        duration: 5000, //ms
                    });
                    return throwError("Unalbe to cotact the server.");
                case 403:
                    this.authSvc.logout();
                    location.reload(true);
                    return throwError("JWT token expired or invalid.");
                default:
                    console.error(err);
                    this._snackBar.open(err.error.error + ": " + err.error.message, null, {
                        duration: 5000, //ms
                    });
                    return throwError(err.error.message);
            }
        }));
    }
}
