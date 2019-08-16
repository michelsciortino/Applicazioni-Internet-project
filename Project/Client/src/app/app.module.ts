import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HashLocationStrategy, LocationStrategy } from '@angular/common';

import { AppRoutingModule } from './modules/routing.module';
import { AppComponent } from './components/app/app.component';
import { MaterialModule } from './modules/material.module';
import { AuthService } from './services/auth/auth.service';
import { LoginComponent } from './components/authentication/login/login.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { RecoveryComponent } from './components/authentication/recovery/recovery.component';
import { LogoutComponent } from './components/authentication/logout.component';
import { PasswordResetComponent } from './components/authentication/password.reset/password-reset.component';
import { PasswordToggleDirective } from './directives/passwordToggle.directive';

@NgModule({
  declarations: [
    AppComponent, LoginComponent, LogoutComponent, RecoveryComponent, PasswordResetComponent,
    PasswordToggleDirective
  ],
  imports: [
    BrowserModule, BrowserAnimationsModule,
    MaterialModule,
    AppRoutingModule,
    FormsModule, ReactiveFormsModule,
    HttpClientModule,
  ],
  providers: [
    AuthService,
    // { provide: LocationStrategy, useClass: HashLocationStrategy }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
