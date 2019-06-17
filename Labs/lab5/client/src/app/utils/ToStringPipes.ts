import { Pipe, PipeTransform } from '@angular/core'
import { Time } from '@angular/common'

@Pipe({
    name: 'TimeToString'
})
export class TimeToString implements PipeTransform {
    transform(time: Time): String {
        if (!time || time === undefined || !time.hours || !time.minutes) return null

        var timeString: String
        if (time.hours < 10)
            timeString = '0'
        timeString += time.hours + ':'
        if (time.minutes < 10)
            timeString += '0'
        timeString += '' + time.minutes

        return timeString
    }
}

@Pipe({
    name: 'DateToString'
})
export class DateToString implements PipeTransform {
    transform(date: Date): String {
        if (!date || date === undefined) return null
        return date.toDateString()
    }
}