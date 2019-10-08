export default class Utils {
    static getTime(millisec: number) {
        var options = { hour: '2-digit', minute: '2-digit' };
        return new Date(millisec).toLocaleTimeString([], options);
    }

    static getTimeWithSecond(millisec: number) {
        return new Date(millisec).toLocaleTimeString('it-IT')
    }

    static getTimeString(date: Date) {
        var options = { year: 'numeric', month: 'numeric', day: 'numeric', hour: '2-digit', minute: '2-digit' };
        return date.toLocaleTimeString([], options);
    }
}