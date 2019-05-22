import { Injectable } from '@angular/core'
import { Line } from './Models/LineModels'

@Injectable({
  providedIn: 'root'
})
export class LinesService {
  lines: Line[];

  constructor() {
    this.lines = [
      {
        id: '2',
        name: 'Bruchi Cantanti',
        date: new Date(2019, 4, 15),
        backward: false,
        stops: [
          {
            id: '201',
            name: 'Piazza Mellano',
            time: { hours: 7, minutes: 35 },
            children: [
              { id: "0xf5", name: 'Benedetta', isPresent: true },
              { id: "0xf6", name: 'Aurora', isPresent: true },
              { id: "0xf7", name: 'Chanel', isPresent: false },
              { id: "0xf9", name: 'Matteo', isPresent: true },
              { id: "0xf10", name: 'Sara', isPresent: false },
              { id: "0xf11", name: 'Simone', isPresent: true },
              { id: "0xf8", name: 'Claudia', isPresent: true }
            ]
          },
          {
            id: '202',
            name: 'Via primo Alpini',
            time: { hours: 7, minutes: 40 },
            children: [
              { id: "0xf12", name: 'Giacomo', isPresent: true },
              { id: "0xf13", name: 'Emma', isPresent: true }
            ]
          },
          {
            id: '203',
            name: 'Via Vigo',
            time: { hours: 7, minutes: 50 },
            children: [
              { id: "0xf14", name: 'Isabel', isPresent: true },
              { id: "0xf15", name: 'Mohammed', isPresent: true },
              { id: "0xf16", name: 'Iaia', isPresent: false }
            ]
          },
          {
            id: '204',
            name: 'Piazza XXV Aprile',
            time: { hours: 7, minutes: 55 },
            children: [
              { id: "0xf17", name: 'Shibo', isPresent: true },
              { id: "0xf18", name: 'Vittoria', isPresent: true }
            ]
          },
          {
            id: '301',
            name: 'Scuola',
            time: { hours: 8, minutes: 0 },
            children: []
          }
        ]
      },
      {
        id: '1',
        name: 'Coccinelle felici',
        date: new Date(2019, 4, 14),
        backward: false,
        stops: [
          {
            id: '201',
            name: 'Piazza Mellano',
            time: { hours: 7, minutes: 35 },
            children: [
              { id: "0xf5", name: 'Benedetta', isPresent: true },
              { id: "0xf6", name: 'Aurora', isPresent: true },
              { id: "0xf7", name: 'Chanel', isPresent: false },
              { id: "0xf8", name: 'Claudia', isPresent: true }
            ]
          },
          {
            id: '202',
            name: 'Via primo Alpini',
            time: { hours: 7, minutes: 40 },
            children: [
              { id: "0xf12", name: 'Giacomo', isPresent: true },
              { id: "0xf13", name: 'Emma', isPresent: true }
            ]
          },
          {
            id: '204',
            name: 'Piazza XXV Aprile',
            time: { hours: 7, minutes: 55 },
            children: [
              { id: "0xf17", name: 'Shibo', isPresent: true },
              { id: "0xf18", name: 'Vittoria', isPresent: true }
            ]
          },
          {
            id: '301',
            name: 'Scuola',
            time: { hours: 8, minutes: 0 },
            children: []
          }
        ]
      }
    ]
  }


  public getLines(): Line[] {
    return this.lines
  }
}