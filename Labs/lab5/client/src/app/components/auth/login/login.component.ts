import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { AuthService } from 'src/app/services/auth/auth.service';
import { User } from 'src/app/services/auth/models/user';
import { take } from 'rxjs/operators';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ["../auth.style.css"]
})

export class LoginComponent implements OnInit {
  form: FormGroup;

  constructor(private auth: AuthService,private fb: FormBuilder) { }

  ngOnInit() {
    this.form = this.fb.group({
      mail: ['', Validators.compose([Validators.email,Validators.required])],
      password: ['', Validators.required]
    });
  }

  login(): void {
    if(this.form.valid){
      this.auth.login(this.form.value).subscribe((data:any)=>{
        if(data==false)
          this.form.setErrors({ invalidCredentials: true })
      })
    }
  }
}
