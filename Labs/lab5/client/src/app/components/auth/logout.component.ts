import {Component, OnInit} from '@angular/core';
import {AuthService} from 'src/app/services/auth/auth.service';
import {Router} from '@angular/router';

@Component({
  template: 'Logging out...'
})
export class LogoutComponent implements OnInit {

  constructor(private auth: AuthService, private router: Router) {
  }

  ngOnInit() {
    this.auth.logout();
    this.router.navigate(['/']);
  }
}
