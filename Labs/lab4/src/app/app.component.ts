import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent {
  title = 'lab4';
  line = {
    name: 'Bruchi Cantanti',
    date: 'mer 13 marzo 2019',
    stops: [
      {
        name: 'Piazza Mellano',
        time: '07.35',
        children: ['Benedetta', 'Aurora', 'Chanel', 'Matteo', 'Sara', 'Simone', 'Claudia']
      },
      {
        name: 'Via primo Alpini',
        time: '07.40',
        children: ['Giacomo', 'Emma']
      },
      {
        name: 'Via Vigo',
        time: '07.50',
        children: ['Isabel', 'Mohammed', 'Iaia']
      },
      {
        name: 'Piazza XXV Aprile',
        time: '07.55',
        children: ['Shibo', 'Vittoria']
      },
      {
        name: 'Scuola',
        time: '08.00',
        children: []
      }
    ]
  }
}
