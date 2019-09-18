import {
    Component,
    OnInit,
    AfterViewInit,
    ViewChild,
    Directive
} from "@angular/core";
import { LineService } from "src/app/services/lines/line-races.service";
import { Line } from "src/app/models/line";
import { HttpErrorResponse } from "@angular/common/http";
import { map } from "rxjs/operators";
import { FormControl, Validators } from "@angular/forms";
import { GoogleMapsAPIWrapper, MarkerManager } from "@agm/core";
import { Stop } from "src/app/models/stop";
import { Subscription } from 'rxjs';
import { IsMobileService } from 'src/app/services/is-mobile/is-mobile.service';

declare const google: any;

const markerPosition = "../../../../assets/markers/";

export class Marker {
    lat: any;
    lng: any;
    alpha: number;
    icon: any;
    options: { title: string }

    constructor(lat: number, lng: number, alpha: number, icon: any, title: string) {
        this.lat = lat;
        this.lng = lng;
        this.alpha = alpha;
        this.icon = icon;
        this.options = { title: title }
    }
}

@Component({
    selector: "app-view-map",
    templateUrl: "./view-map.component.html",
    styleUrls: ["./view-map.component.css", "../../common.css"]
})
export class ViewMapComponent implements OnInit, AfterViewInit {
    isMobile: Boolean;
    isMobileSub: Subscription;
    lineControl = new FormControl('', [Validators.required]);
    centerLat = 45.077028;
    centerLng = 7.668474;

    origin: any;
    destination: any;
    waypoints: any[];

    selectedMarker: Marker;
    markers: Marker[];
    lines: Line[];
    lineSelected: Line;
    zoom: number = 14;

    constructor(public lineSvc: LineService, private gmapsApi: GoogleMapsAPIWrapper, private isMobileSvc: IsMobileService) {
        this.markers = [];
        this.waypoints = [];
    }

    ngOnInit() {
        this.isMobileSub = this.isMobileSvc.getIsMobile()
            .subscribe((isMobile) => this.isMobile = isMobile);

        this.lineSvc
            .getLines()
            .pipe(
                map(
                    (data: any) => data,
                    (error: HttpErrorResponse) => console.log(error)
                )
            )
            .subscribe((data: Line[]) => {
                if (data.length == 0) return;
                this.lines = data;
            });
    }

    onLineSelected(event: any) {
        this.markers.splice(0, this.markers.length);
        if (!event.value) this.lineSelected = null;
        else {
            //Add markers
            this.lineSelected.outwardStops.forEach(
                (stop: Stop, index: number, stops: Stop[]) => {
                    switch (index) {
                        case 0: {
                            this.centerLat = stop.latitude;
                            this.centerLng = stop.longitude;
                            this.markers.push(new Marker(stop.latitude, stop.longitude, 1, markerPosition + "marker_blueS.png", stop.name));
                            break;
                        }
                        case stops.length - 1: {
                            this.markers.push(new Marker(stop.latitude, stop.longitude, 1, markerPosition + "flag.png", stop.name));
                            break;
                        }
                        default: {
                            this.markers.push(new Marker(stop.latitude, stop.longitude, 1, markerPosition + "marker_blue" + index + ".png", stop.name));
                            break;
                        }
                    }
                }
            );
        }
    }

    renderDirections() {
        if (!this.lineSelected) return;
        this.lineSelected.outwardStops.forEach(
            (stop: Stop, index: number, stops: Stop[]) => {
                if (index == 0)
                    this.origin = new google.maps.LatLng(stop.latitude, stop.longitude);
                else if (index == stops.length - 1)
                    this.destination = new google.maps.LatLng(stop.latitude, stop.longitude);
                this.waypoints.push(
                    new google.maps.LatLng(stop.latitude, stop.longitude)
                );
            }
        );
    }

    ngAfterViewInit() {
        console.log("AfterViewInit");
        this.gmapsApi
            .getNativeMap()
            .then(map => console.log("AfterViewInit: map: ", map))
            .catch(error => console.log("AfterViewInit: getNativeMap() Error: ", error))
            .then(() => { console.log("Finally"); });
        this.gmapsApi
            .getBounds()
            .then(bounds => console.log("AfterViewInit: bounds: ", bounds))
            .catch(error => console.log("AfterViewInit: getBounds() Error: ", error));
    }

    onMapReady(event: any) {
        console.log("onMapReady");
        this.gmapsApi
            .getNativeMap()
            .then(map => console.log("AfterViewInit: map: ", map))
            .catch(error => console.log("AfterViewInit: getNativeMap() Error: ", error))
            .then(() => { console.log("Finally"); });
    }

    private plotline(line: Line) {
        line.outwardStops.forEach((stop: Stop) => {
            this.markers.push(
                new Marker(stop.latitude, stop.longitude, 1, markerPosition + "marker_blue.png", stop.name)
            );
        });
    }

    addMarker(lat: number, lng: number) {
        this.markers.push(
            new Marker(lat, lng, 1, markerPosition + "marker_blue.png", "")
        );
    }

    selectMarker(event: any) {
        this.selectedMarker = {
            lat: event.latitude,
            lng: event.longitude,
            alpha: event.alpha,
            icon: event.icon,
            options: event.option
        };
    }

    max(coordType: "lat" | "lng"): number {
        return Math.max(...this.markers.map(marker => marker[coordType]));
    }

    min(coordType: "lat" | "lng"): number {
        return Math.min(...this.markers.map(marker => marker[coordType]));
    }
}
