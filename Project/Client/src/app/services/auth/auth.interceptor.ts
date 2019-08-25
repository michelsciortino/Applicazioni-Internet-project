import { Injectable } from '@angular/core';
import {
    HttpEvent,
    HttpInterceptor,
    HttpHandler,
    HttpRequest,
    HttpHeaders
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

const LOG_INTERCEPTOR_HEAD = '[INTERCEPTOR]';

@Injectable({ providedIn: 'root' })
export class AuthInterceptor implements HttpInterceptor {

    constructor(private authService: AuthService) {
    }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        console.log('INTERCEPTOR: handling request');
        if (this.authService.isLoggedIn()) {
            const token: string = this.authService.getToken();
            console.log(LOG_INTERCEPTOR_HEAD, `token: ${token}`);
            if (token)
                req = req.clone({ headers: req.headers.set('Authorization', `Bearer ${token}`) });

        }
        if (!req.headers.has('Content-Type')) {
            req = req.clone({ headers: req.headers.set('Content-Type', 'application/json') });
        }
        req = req.clone({ headers: req.headers.set('Accept', 'application/json') });

        return next.handle(req);
    }
}
