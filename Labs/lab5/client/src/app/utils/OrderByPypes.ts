import { Pipe, PipeTransform } from '@angular/core'

@Pipe({
  name: 'OrderByName'
})
export class OrderByName implements PipeTransform {

  transform(array: Array<any>, args: any): Array<any> {
    if (!array || array === undefined || array.length === 0) return null

    return array.sort((a: any, b: any) => {
      if (a.name < b.name) return -1
      else if (a.name > b.name) return 1
      else return 0
    })
  }
}