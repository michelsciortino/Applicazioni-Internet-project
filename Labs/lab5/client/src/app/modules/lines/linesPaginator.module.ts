import { Injectable, ChangeDetectorRef } from '@angular/core';
import { MatPaginator, MatPaginatorIntl } from '@angular/material';

@Injectable()
export class LinesPaginator extends MatPaginator {
  constructor(_matPaginatorIntl: MatPaginatorIntl, _changeDetectorRef: ChangeDetectorRef) {
    super(_matPaginatorIntl, _changeDetectorRef)
    this.hidePageSize = true
    this.pageSize = 1
    this.pageSizeOptions = [1]
  }
}