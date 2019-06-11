import {Component, OnInit} from '@angular/core';
import {AuthService} from 'src/app/services/auth/auth.service';
import {User} from 'src/app/services/auth/models/user';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
})
export class AppComponent implements OnInit {
  isUserLogged = false;

  title = 'client';

  constructor(private authService: AuthService) {
  }

  ngOnInit() {
    this.authService.getUserObserver().subscribe((user: User) => {
      this.isUserLogged = user != null;
    });
  }
}
