import { from } from 'rxjs';
import { AngularWaitBarrier } from 'blocking-proxy/built/lib/angular_wait_barrier';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout'
import { MatCardModule } from '@angular/material/card'
import { MatListModule } from '@angular/material/list'
import { MatPaginatorModule, MatPaginatorIntl, MatPaginator } from '@angular/material/paginator';
import { NoopAnimationsModule } from '@angular/platform-browser/animations'

import { AppComponent } from './app.component';
import { LinesPaginator, LinesPaginatorIntl } from './LinesPaginator'
import { OrderByName } from './Utils/OrderByPypes'
import { TimeToString,DateToString } from './Utils/ToStringPipes'

@NgModule({
  declarations: [
    AppComponent, OrderByName, TimeToString, DateToString
  ],
  imports: [
    MatCardModule, MatListModule, MatPaginatorModule, NoopAnimationsModule, FlexLayoutModule
  ],
  providers: [{
    provide: MatPaginatorIntl,
    useClass: LinesPaginatorIntl
  }, {
    provide: MatPaginator,
    useClass: LinesPaginator,
  }],
  bootstrap: [AppComponent]
})
export class AppModule { }
