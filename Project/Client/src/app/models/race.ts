import { Passenger } from './passenger';
import { Companion } from './companion';
import { Line } from './line';
import { Stop } from './stop';

export enum DirectionType {
    OUTWARD = "OUTWARD",
    RETURN = "RETURN"
}

export enum RaceState {
    SCHEDULED = "SCHEDULED",
    VALIDATED = "VALIDATED",
    STARTED = "STARTED",
    ENDED = "ENDED"
}

export class Race {
    public line: Line;
    public direction: DirectionType;
    public date: Date;
    public passengers: Passenger[];
    public companions: Companion[];
    public raceState: RaceState;
    public companion: Companion;
    public reachedStops: {stopName:string, arrivalDelay:number, departureDelay:number}[];
    public currentStop: Stop;
    
    constructor() {
        this.line = new Line();
    }
}