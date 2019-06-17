import { Injectable } from '@angular/core'
import { MatPaginatorIntl } from '@angular/material'

@Injectable()
export class LinesPaginatorIntl extends MatPaginatorIntl {
  constructor() {
    super()
    this.nextPageLabel = "Next Line"
    this.previousPageLabel = "Previous Line"
  }
}