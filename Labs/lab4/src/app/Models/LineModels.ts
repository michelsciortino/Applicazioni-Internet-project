import { Time } from '@angular/common'

export class Child {
    constructor(public id: string,public name: string,public isPresent: boolean) { }
}

export class Stop {
    constructor(public id: string, public name: string, public time: Time, public children: Child[]) { }
}

export class Line {
    constructor(public id: string, public name: string, public date: Date, public backward: boolean, public stops: Stop[]) { }
}