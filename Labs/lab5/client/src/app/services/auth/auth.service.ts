import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable } from 'rxjs';
import { Router } from '@angular/router';
import { User } from './models/user';


@Injectable()
export class AuthService {
  private loggedIn: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  get isLoggedIn() {
    return this.loggedIn.asObservable();
  }

  constructor(private router: Router) { }

  login(user: User): Observable<boolean> {
    if (user.mail == "admin@mail.com" && user.password == "admin") {
      this.loggedIn.next(true);
      this.router.navigate(['/']);
    }
    else
      this.loggedIn.next(false);
    return this.loggedIn;
  }

  logout() {
    this.loggedIn.next(false);
    this.router.navigate(['/']);
  }

  register(user: User) {
    if (user.mail == "admin" && user.password == "admin") {
      this.loggedIn.next(true);
      this.router.navigate(['/']);
    }
    else
      this.loggedIn.next(false);
  }
}