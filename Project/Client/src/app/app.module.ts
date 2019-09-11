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
import { PasswordResetComponent } from './components/authentication/password-reset/password-reset.component';
import { PasswordToggleDirective } from './directives/passwordToggle.directive';
import { ConfirmComponent } from './components/authentication/confirm/confirm.component';
import { MessageComponent } from './components/message/message.component';
import { MessageService } from './services/bridges/message.service';
import { HomeComponent } from './components/home/home.component';
import { UserService } from './services/user/user.service';
import { MessagesComponent } from './components/messages/messages.component';
import { AdminComponent } from './components/admin/admin.component';
import { CompanionComponent } from './components/companion/companion.component';
import { WelcomeComponent } from './components/welcome/welcome.component';
import { LinesComponent } from './components/lines/lines.component';
import { SettingsComponent } from './components/settings/settings.component';
import { RegisterComponent } from './components/admin/register/register.component';
import { IsMobileService } from './services/bridges/is-mobile.service';
import { UserManagementComponent } from './components/admin/users-management/users-management.component';
import { AdminService } from './services/admin/admin.service';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { JwtInterceptor } from './interceptors/jwt.interceptor';
import { ErrorInterceptor } from './interceptors/error.interceptor';
import { ConfirmDialogComponent } from './components/dialogs/confirm-dialog/confirm-dialog.component';
import { MessageDialogComponent } from './components/dialogs/messege-dialog/messege-dialog.component';
import { ViewUserDialog } from './components/admin/users-management/view-user-dialog/view-user.dialog';
import { MAT_DIALOG_DEFAULT_OPTIONS } from '@angular/material/dialog';
import { EditUserDialog } from './components/admin/users-management/edit-user-dialog/edit-user.dialog';

@NgModule({
  declarations: [
    AppComponent, WelcomeComponent,
    // auth components
    LoginComponent, LogoutComponent, RecoveryComponent, PasswordResetComponent, ConfirmComponent, RegisterComponent,
    // home
    HomeComponent, AdminComponent, CompanionComponent, MessagesComponent, LinesComponent, SettingsComponent,

    // admin components
    UserManagementComponent,

    // dialog
    ConfirmDialogComponent, MessageDialogComponent,ViewUserDialog,EditUserDialog,

    // dumb components
    MessageComponent,

    // directives
    PasswordToggleDirective
  ],
  entryComponents: [
    ConfirmDialogComponent, MessageDialogComponent,ViewUserDialog,EditUserDialog
  ],
  imports: [
    BrowserModule, BrowserAnimationsModule,
    MaterialModule,
    AppRoutingModule,
    FormsModule, ReactiveFormsModule,
    HttpClientModule,
    AngularSvgIconModule
  ],
  providers: [
    AuthService,
    UserService,
    AdminService,
    MessageService,
    IsMobileService,
    { provide: LocationStrategy, useClass: HashLocationStrategy },
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
    {provide: MAT_DIALOG_DEFAULT_OPTIONS, useValue: {hasBackdrop: true}}
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
