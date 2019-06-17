import { Injectable, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { Line } from 'src/app/models/line';
import { take, map, flatMap, toArray } from 'rxjs/operators';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';
import { Reservation } from 'src/app/models/ride';

@Injectable({ providedIn: 'root' })
export class AttendancesService {
    private readonly rootEndpoint = 'http://localhost:8080'
    private linesSubject: BehaviorSubject<Line[]>

    constructor(private http: HttpClient, private authService: AuthService, private router: Router) {
        this.linesSubject = new BehaviorSubject<Line[]>([])
        this._getLines().subscribe(
            (lines: Line[]) =>
                this.linesSubject.next(lines),
            (error: HttpErrorResponse) => {
                if (error.status == 403) {
                    authService.logout()
                    router.navigate(['/login'])
                }
            }
        )
    }

    //#region Lines

    //returns a line
    private _getLines() {
        return this.http.get(`${this.rootEndpoint}/lines`)
    }

    private getLine(lineName: String) {
        return this.http.get(`${this.rootEndpoint}/lines/${lineName}`)
    }

    public getLines(): Observable<Line[]> {
        return this.linesSubject.asObservable()
    }

    //#endregion

    //#region Reservations
    //returns the reservation of a line in a specific date
    public getLineReservations(lineName: String, date: Date) {
        let dateString = this.dateToString(date)
        return this.http.get(`${this.rootEndpoint}/reservations/${lineName}/${dateString}`).pipe(
            map(
                data => data,
                (error: HttpErrorResponse) => {
                    console.log(error)
                    return null
                })
        )
    }

    //returns a specific reservation
    public getReservation(lineName: String, date: Date, reservationId: String) {
        let dateString = this.dateToString(date)
        return this.http.get(`${this.rootEndpoint}/reservations/${lineName}/${date}/${reservationId}`)
    }

    public updatePresence(lineId: string, date: Date, reservation: Reservation) {
        let dateString = this.dateToString(date)
        return this.http.put(`${this.rootEndpoint}/reservations/${lineId}/${dateString}/${reservation.id}`, reservation)
            .pipe(
                map(
                    (data) => true,
                    (error: HttpErrorResponse) => {
                        console.log(error)
                        return false
                    }
                )
            )
    }
    //#endregion



    private dateToString(date: Date): string {
        let options = { day: '2-digit', month: '2-digit', year: 'numeric' };
        return date.toLocaleDateString('it-IT', options).replace(/\//g, '-').split('-').reverse().join('-');
    }
}