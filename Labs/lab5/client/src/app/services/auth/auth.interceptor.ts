import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import {AuthService} from './auth.service';

@Injectable({providedIn: 'root'})
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    if (this.authService.isLoggedIn()) {
      let token = this.authService.getUser().token;
      req = req.clone({
        setHeaders: {Authorization: `Bearer ${token}`}
      });
    }
    return next.handle(req);
  }
}
