import { Time } from '@angular/common'
import { Subscribable } from 'rxjs';

export class Reservation {
    id: string
    childName: string
    childSurname: string
    childCf: string
    parentUsername: String
    stopName: string
    direction: string
    present: boolean

    public clone(): Reservation {
        var copy = new Reservation()
        copy.id=this.id
        copy.childName=this.childName
        copy.childSurname=this.childSurname
        copy.childCf=this.childCf
        copy.parentUsername=this.parentUsername
        copy.stopName=this.stopName
        copy.direction=this.direction
        copy.present=this.present
        return copy;
    }
}

export class RideStop {
    name: string
    time: string
    reservations: Array<Reservation>
    constructor() {
        this.reservations = new Array<Reservation>()
    }
}

export class SubscrivableChild {
    childName: string
    childSurname: string
    parentUsername: string
    subscribed: boolean
    cf: string
}

export class Ride {
    stops: Array<RideStop>
    subscrivableChildren: Array<SubscrivableChild>

    constructor() {
        this.stops = new Array<RideStop>()
        this.subscrivableChildren = new Array<SubscrivableChild>()
    }
}