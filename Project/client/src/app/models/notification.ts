import { Race } from './race';

export enum NotificationType {
    CHILD_TAKEN = 'CHILD_TAKEN',
    CHILD_ABSENT = 'CHILD_ABSENT',
    CHILD_DELIVERED = 'CHILD_DELIVERED',
    COMPANION_STATEAVAILABILITY = 'COMPANION_STATEAVAILABILITY',
    COMPANION_REMOVEAVAILABILITY = 'COMPANION_REMOVEAVAILABILITY',
    COMPANION_CHOSEN = 'COMPANION_CHOSEN',
    COMPANION_CONFIRMCHOSEN = 'COMPANION_CONFIRMCHOSEN',
    COMPANION_REMOVECHOSEN = 'COMPANION_REMOVECHOSEN',
    RACE_CONFIRMED = 'RACE_CONFIRMED',
    RACE_DELETED = 'RACE_DELETED',
    RACE_STARTED = 'RACE_STARTED',
    RACE_ENDED = 'RACE_ENDED',
    STOP_REACHED = 'STOP_REACHED',
    STOP_LEFT = 'STOP_LEFT'
}

export class Notification {
    id: string;
    date: Date;
    sender: string;
    targetUser: string;
    targetRace: Race;
    message: string;
    type: NotificationType;
    unread: boolean;
}

export class IncomingNotification {
    id: string;
    performerUsername: string;
    targetUsername: string;
    type: NotificationType;
    date: Date;
    referredRace: Race;
    message: string;
    isRead: boolean;
}
