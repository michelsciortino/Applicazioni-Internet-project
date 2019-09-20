import { Passenger } from './passenger';
import { Companion } from './companion';
import { Line } from './line';

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
    constructor() {
        this.line = new Line();
    }
}