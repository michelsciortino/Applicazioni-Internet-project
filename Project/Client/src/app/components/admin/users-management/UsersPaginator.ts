import { MatPaginatorIntl } from '@angular/material';
import { Injectable } from '@angular/core';

@Injectable()
export class UsersPaginatorIntl extends MatPaginatorIntl {
    constructor() {
        super();
        this.nextPageLabel = 'next';
        this.previousPageLabel = 'previous';
    }
}
