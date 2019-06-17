import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, FormGroupDirective, NgForm, Validators } from '@angular/forms';
import { AuthService } from 'src/app/services/auth/auth.service';
import { ErrorStateMatcher } from '@angular/material';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

export class PasswordsErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const invalidCtrl = !!(control && control.invalid && control.parent.dirty);
    const invalidParent = !!(control && control.parent && control.parent.invalid && control.parent.dirty);
    return (invalidCtrl || invalidParent);
  }
}

@Component({
  selector: 'app-login',
  templateUrl: './register.component.html',
  styleUrls: ['../auth.style.css']
})
export class RegisterComponent implements OnInit {
  passwrodsErrorStateMatcher: PasswordsErrorStateMatcher;
  form: FormGroup;
  hasError = false;
  registrationError: String;
  showSpinner = false;

  constructor(private router: Router, private auth: AuthService, private fb: FormBuilder) {
  }

  ngOnInit() {
    this.form = this.fb.group({
      mail: ['', Validators.compose([Validators.email, Validators.required])],
      password: ['', Validators.required],
      password2: ['', Validators.required],
    }, { validator: this.checkPasswords });
  }

  register(): void {
    if (!this.form.invalid) {
      this.hasError = false;
      this.auth.register({ mail: this.form.value.mail, password: this.form.value.password, passwordCheck: this.form.value.password2 }).subscribe(
        (data: any) => {
          this.showSpinner = false;
          this.auth.login({ mail: this.form.value.mail, password: this.form.value.password }).subscribe(
            (data: any) => {
              this.showSpinner = false;
              this.router.navigate(['/'])
            },
            error => {
              this.registrationError = error.error
              this.hasError = true
              this.showSpinner = false
            }
          )
        },
        (error: HttpErrorResponse) => {
          this.showSpinner = false;
          this.hasError = true;
          this.registrationError = error.error
        });
    }
  }

  private checkPasswords(group: FormGroup) {
    let pass1 = group.controls.password;
    let pass2 = group.controls.password2;

    if (pass1.invalid || pass2.invalid) {
      return null;
    }

    return (pass1.valid && pass2.valid && pass1.value === pass2.value) ?
      null : { passowordsNotEqual: true }
  }
}
