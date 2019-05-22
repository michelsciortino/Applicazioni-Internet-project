import { Injectable } from '@angular/core'
import { MatPaginator, MatPaginatorIntl } from '@angular/material'
import { ChangeDetectorRef } from '@angular/core'

@Injectable()
export class LinesPaginatorIntl extends MatPaginatorIntl {
  constructor() {
    super()
    this.nextPageLabel = "Next Line"
    this.previousPageLabel = "Previous Line"
  }
}

@Injectable()
export class LinesPaginator extends MatPaginator {
  constructor(_matPaginatorIntl: MatPaginatorIntl, _changeDetectorRef: ChangeDetectorRef) {
    super(_matPaginatorIntl, _changeDetectorRef)
    this.hidePageSize = true
    this.pageSize = 1
    this.pageSizeOptions = [1]
  }
}