export default class Utils {
    static getTime(millisec: number) {
        const options = { hour: '2-digit', minute: '2-digit' };
        return new Date(millisec).toLocaleTimeString('it-IT', options);
    }

    static getTimeWithSecond(millisec: number) {
        return new Date(millisec).toLocaleTimeString('it-IT');
    }

    static getTimeString(date: Date) {
        const options = { year: 'numeric', month: 'numeric', day: 'numeric', hour: '2-digit', minute: '2-digit' };
        return date.toLocaleTimeString('it-IT', options);
    }
}
