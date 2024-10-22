import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './modules/app-routing.module';
import { NgModule } from '@angular/core';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { MaterialModule } from './modules/material.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { AppComponent } from './components/app/app.component';
import { LoginComponent } from './components/auth/login/login.component';
import { RegisterComponent } from './components/auth/register/register.component';
import { AuthComponent } from './components/auth/auth.component';
import { LogoutComponent } from './components/auth/logout.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { AuthInterceptor } from './services/auth/auth.interceptor';
import { AttendancesComponent } from './components/attendances/attendances.component';

import { LinesPaginatorIntl } from './modules/lines/linesPaginatorIntl.module'
import { LinesPaginator } from './modules/lines/linesPaginator.module';
import { OrderByName } from './utils/OrderByPypes'
import { TimeToString, DateToString } from './utils/ToStringPipes'
import { MatPaginatorIntl, MatPaginator, MatPaginatorModule } from '@angular/material';


@NgModule({
  declarations: [
    AppComponent, AuthComponent, LoginComponent, RegisterComponent, LogoutComponent, AttendancesComponent,
    OrderByName, TimeToString, DateToString
  ],
  imports: [
    BrowserModule,
    FormsModule, ReactiveFormsModule,
    BrowserAnimationsModule, MaterialModule,
    AppRoutingModule, HttpClientModule, MatPaginatorModule
  ],
  providers: [{ provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
  {
    provide: MatPaginatorIntl,
    useClass: LinesPaginatorIntl
  }, {
    provide: MatPaginator,
    useClass: LinesPaginator,
  }],
  bootstrap: [AppComponent]
})
export class AppModule {
}
