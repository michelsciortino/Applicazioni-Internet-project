import { Child } from './child';
import { Stop } from './stop';

export class Line {
    public name: string;
    public outwardStops: Stop[];
    public returnStops: Stop[];
    public subscribedChildren: Child[];
    public admins: string[];
}