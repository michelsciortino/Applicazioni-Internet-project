import { Injectable, Output, EventEmitter } from '@angular/core';

@Injectable()
export class IsMobileService {

    @Output()
    isMobile: EventEmitter<Boolean>;

    constructor() {
        this.isMobile = new EventEmitter<Boolean>();
    }

    setIsMobile(bool: Boolean): void {
        this.isMobile.emit(bool)
    }
}