import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent {
  title = 'lab4';
  selectedLineIndex = 0;
  spluced
  lines = [{
    name: 'Bruchi Cantanti',
    date: 'mer 13 marzo 2019',
    backward: false,
    stops: [
      {
        id: 201,
        name: 'Piazza Mellano',
        time: '07.35',
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
        id: 202,
        name: 'Via primo Alpini',
        time: '07.40',
        children: [
          { id: "0xf12", name: 'Giacomo', isPresent: true },
          { id: "0xf13", name: 'Emma', isPresent: true }
        ]
      },
      {
        id: 203,
        name: 'Via Vigo',
        time: '07.50',
        children: [
          { id: "0xf14", name: 'Isabel', isPresent: true },
          { id: "0xf15", name: 'Mohammed', isPresent: true },
          { id: "0xf16", name: 'Iaia', isPresent: false }
        ]
      },
      {
        id: 204,
        name: 'Piazza XXV Aprile',
        time: '07.55',
        children: [
          { id: "0xf17", name: 'Shibo', isPresent: true },
          { id: "0xf18", name: 'Vittoria', isPresent: true }
        ]
      },
      {
        id: 301,
        name: 'Scuola',
        time: '08.00',
        children: []
      }
    ]
  }, {
    name: 'Coccinelle felici',
    date: 'mer 14 aprile 2019',
    backward: false,
    stops: [
      {
        id: 201,
        name: 'Piazza Mellano',
        time: '07.35',
        children: [
          { id: "0xf5", name: 'Benedetta', isPresent: true },
          { id: "0xf6", name: 'Aurora', isPresent: true },
          { id: "0xf7", name: 'Chanel', isPresent: false },
          { id: "0xf8", name: 'Claudia', isPresent: true }
        ]
      },
      {
        id: 202,
        name: 'Via primo Alpini',
        time: '07.40',
        children: [
          { id: "0xf12", name: 'Giacomo', isPresent: true },
          { id: "0xf13", name: 'Emma', isPresent: true }
        ]
      },
      {
        id: 204,
        name: 'Piazza XXV Aprile',
        time: '07.55',
        children: [
          { id: "0xf17", name: 'Shibo', isPresent: true },
          { id: "0xf18", name: 'Vittoria', isPresent: true }
        ]
      },
      {
        id: 301,
        name: 'Scuola',
        time: '08.00',
        children: []
      }
    ]
  }]

  changeLineEvent(event) {
    this.selectedLineIndex = (event.pageIndex - 0) * event.pageSize;
  }

  changeChildPresence = function (child) {
    child.isPresent = !child.isPresent;
  }
}