import { Component } from '@angular/core'
import { LinesService } from './lines.service'
import { Line, Child } from './Models/LineModels'

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent {
  title = 'lab4'
  selectedLineIndex = 0
  lines: Line[]

  constructor(private linesService: LinesService) {
    this.lines = linesService.getLines().sort((a, b) => {
      if (a.date < b.date) return -1
      if (a.date > b.date) return 1
      return 0
    })
  }

  changeLineEvent(event) {
    this.selectedLineIndex = (event.pageIndex - 0) * event.pageSize
  }

  changeChildPresence = function (child: Child) {
    child.isPresent = !child.isPresent
  }
}