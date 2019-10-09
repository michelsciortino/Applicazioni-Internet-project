import { Child } from './child';
import { Race } from './race';

export class ChildRace {
    child: Child;
    race: Race;

    public constructor(child: Child, race: Race) {
        this.child = child;
        this.race = race;
    }
}