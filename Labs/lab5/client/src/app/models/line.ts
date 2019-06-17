import { Time } from '@angular/common'

export type Child = {
    id: string
    name: string
    isPresent: boolean
}

export type Stop = {
    id: string
    name: string
    time: Time
    children: Child[]
}

export type Line = {
    id: string
    name: string
    date: Date
    backward: boolean
    stops: Stop[]
}