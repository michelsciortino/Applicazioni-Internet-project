import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthService} from 'src/app/services/auth/auth.service';
import {HttpErrorResponse} from '@angular/common/http';
import {Router} from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['../auth.style.css']
})

export class LoginComponent implements OnInit {
  form: FormGroup;
  invalidCredentials = false;
  showSpinner = false;

  constructor(private auth: AuthService, private router: Router, private fb: FormBuilder) {
  }

  ngOnInit() {
    this.form = this.fb.group({
      mail: ['', Validators.compose([Validators.email, Validators.required])],
      password: ['', Validators.required]
    });
  }

  login(): void {
    if (this.form.valid) {
      this.showSpinner = true;
      this.auth.login(this.form.value).subscribe(
        () => {
          this.showSpinner = false;
          this.invalidCredentials=false;
          this.router.navigate(['/']);
        },
        (error: HttpErrorResponse) => {
          this.showSpinner = false;
          this.invalidCredentials = true;
          console.log(error);
        });
    }
  }
}
