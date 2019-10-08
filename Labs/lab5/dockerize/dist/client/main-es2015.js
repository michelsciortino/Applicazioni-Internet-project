(window["webpackJsonp"] = window["webpackJsonp"] || []).push([["main"],{

/***/ "./$$_lazy_route_resource lazy recursive":
/*!******************************************************!*\
  !*** ./$$_lazy_route_resource lazy namespace object ***!
  \******************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

function webpackEmptyAsyncContext(req) {
	// Here Promise.resolve().then() is used instead of new Promise() to prevent
	// uncaught exception popping up in devtools
	return Promise.resolve().then(function() {
		var e = new Error("Cannot find module '" + req + "'");
		e.code = 'MODULE_NOT_FOUND';
		throw e;
	});
}
webpackEmptyAsyncContext.keys = function() { return []; };
webpackEmptyAsyncContext.resolve = webpackEmptyAsyncContext;
module.exports = webpackEmptyAsyncContext;
webpackEmptyAsyncContext.id = "./$$_lazy_route_resource lazy recursive";

/***/ }),

/***/ "./node_modules/raw-loader/index.js!./src/app/components/app/app.component.html":
/*!*****************************************************************************!*\
  !*** ./node_modules/raw-loader!./src/app/components/app/app.component.html ***!
  \*****************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<mat-toolbar color=\"primary\" class=\"mat-elevation-z6\">\n  <a mat-button [routerLink]=\"['/']\">Pedibus</a>\n  <span class=\"fill\"></span>\n  <a *ngIf=\"!isUserLogged\" mat-button [routerLink]=\"['/login']\">Login</a>\n  <a *ngIf=\"!isUserLogged\" mat-button [routerLink]=\"['/register']\">Registration</a>\n  <a *ngIf=\"isUserLogged\" mat-button [routerLink]=\"['/logout']\">Logout</a>\n</mat-toolbar>\n\n<div id=\"content\" class=\"main-content\">\n  <div class=\"container\">\n    <router-outlet></router-outlet>\n  </div>\n</div>\n"

/***/ }),

/***/ "./node_modules/raw-loader/index.js!./src/app/components/attendances/attendances.component.html":
/*!*********************************************************************************************!*\
  !*** ./node_modules/raw-loader!./src/app/components/attendances/attendances.component.html ***!
  \*********************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<div class=\"centered\">\n    <mat-card class=\"line-card inline-block text-align-left\" *ngIf=\"lines && lines.length>0\" flex=\"50\">\n        <mat-card-content>\n            <div>\n                <!-- line selection -->\n                <mat-form-field class=\"margin8 margin-bottom-0\">\n                    <mat-label>Line</mat-label>\n                    <mat-select [(ngModel)]=\"selectedLine\" *ngIf=\"lines\" (selectionChange)=\"onRideSelected()\">\n                        <mat-option [value]=\"\" disabled>Seleziona Linea</mat-option>\n                        <mat-option *ngFor=\"let line of lines, let index\" [value]=\"index\">{{line?.name}}\n                        </mat-option>\n                    </mat-select>\n                </mat-form-field>\n\n                <!-- Way selection -->\n                <mat-form-field class=\"margin8 margin-bottom-0\">\n                    <mat-label>Direzione</mat-label>\n                    <mat-select *ngIf=\"ways\" [(ngModel)]=\"way\" (selectionChange)=\"onRideSelected()\">\n                        <mat-option *ngFor=\"let way of ways\" [value]=\"way\">{{way.text}}</mat-option>\n                    </mat-select>\n                </mat-form-field>\n\n                <!-- Date selection -->\n                <mat-form-field class=\"margin8 margin-bottom-0\">\n                    <mat-label>Data</mat-label>\n                    <input matInput [matDatepicker]=\"picker\" [(ngModel)]=\"date\" (dateChange)=\"onRideSelected()\">\n                    <mat-datepicker-toggle matSuffix [for]=\"picker\"></mat-datepicker-toggle>\n                    <mat-datepicker #picker [startAt]=\"date\"></mat-datepicker>\n                </mat-form-field>\n\n                <!--<mat-paginator class=\"inline margin8\" [hidePageSize]=true id=\"paginator\" (page)=\"onDateSelected($event)\" [length]=\"nPages\" [pageSize]=\"1\" [pageSizeOptions]=\"[1]\"></mat-paginator>-->\n            </div>\n            <!-- stops -->\n            <div class=\"margin8 margin-bottom-16 timeline\" *ngIf=\"ride\">\n                <div class=\"ride container right\" *ngFor=\"let stop of ride.stops\">\n                    <div class=\"content\">\n                        <div mat-line>\n                            {{stop.time}} {{stop.name}}\n                        </div>\n                        <ul mat-line>\n                            <!-- reservatios -->\n                            <li *ngFor=\"let reservation of stop.reservations; last as isLast\">\n                                <span [ngClass]=\"{'present':reservation.present}\" (click)=\"setChildPresence(selectedLine,way,reservation)\">\n                                    {{reservation.childName}}\n                                    {{reservation.childSurname}}</span>\n                                <span *ngIf=\"!isLast\">, </span>\n                            </li>\n                        </ul>\n                    </div>\n                </div>\n            </div>\n            <div *ngIf=\"unsubscribedChildren\" class=\"margin8 margin-top-24\">\n                <span>Bambini non prenotati:</span>\n                <ul id=\"unsubscribedChildren\" mat-line>\n                    <li *ngFor=\"let child of unsubscribedChildren; last as isLast\">\n                        <span *ngIf=\"!child.subscribed\" (click)=\"setChildPresence(selectedLine,way,child)\">\n                            {{child?.childName}}\n                            {{child?.childSurname}}</span>\n                        <span *ngIf=\"!isLast&&!child.subscribed\">, </span>\n                    </li>\n                </ul>\n            </div>\n        </mat-card-content>\n    </mat-card>\n</div>"

/***/ }),

/***/ "./node_modules/raw-loader/index.js!./src/app/components/auth/auth.component.html":
/*!*******************************************************************************!*\
  !*** ./node_modules/raw-loader!./src/app/components/auth/auth.component.html ***!
  \*******************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<div class=\"padded-content\">\n  <mat-card class=\"compact-card centered\">\n    <nav mat-tab-nav-bar mat-align-tabs=\"center\">\n      <a mat-tab-link routerLink='/login' routerLinkActive='active'>Login</a>\n      <a mat-tab-link routerLink='/register' routerLinkActive='active'>Register</a>\n    </nav>\n    <div class=\"auth-content\">\n      <router-outlet></router-outlet>\n    </div>\n  </mat-card>\n</div>\n"

/***/ }),

/***/ "./node_modules/raw-loader/index.js!./src/app/components/auth/login/login.component.html":
/*!**************************************************************************************!*\
  !*** ./node_modules/raw-loader!./src/app/components/auth/login/login.component.html ***!
  \**************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<form [formGroup]=\"form\" (ngSubmit)=\"login()\">\n  <mat-form-field>\n    <label>\n      <input matInput type=\"email\" placeholder=\"Mail\" formControlName=\"mail\" required>\n    </label>\n    <mat-error *ngIf=\"!form.controls['mail'].valid\"> Please isert a correct mail address</mat-error>\n  </mat-form-field>\n  <mat-form-field>\n    <label>\n      <input matInput type=\"password\" placeholder=\"Password\" [formControl]=\"form.controls['password']\" required>\n    </label>\n    <mat-error *ngIf=\"!form.controls['password'].valid\"> Insert a valid password</mat-error>\n  </mat-form-field>\n  <mat-spinner [diameter]=\"32\" [style.display]=\"showSpinner ? 'block' : 'none' \"></mat-spinner>\n  <div class=\"form-error\">\n    <mat-error *ngIf=\"invalidCredentials\"> Invalid credentials</mat-error>\n  </div>\n  <button [disabled]=\"!form.valid\" mat-raised-button color=\"primary\" type=\"submit \">Login</button>\n</form>\n"

/***/ }),

/***/ "./node_modules/raw-loader/index.js!./src/app/components/auth/register/register.component.html":
/*!********************************************************************************************!*\
  !*** ./node_modules/raw-loader!./src/app/components/auth/register/register.component.html ***!
  \********************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<form [formGroup]=\"form\" (ngSubmit)=\"register()\">\n    <mat-form-field>\n        <input matInput type=\"email\" placeholder=\"Mail\" formControlName=\"mail\" required>\n        <mat-error *ngIf=\"!form.controls['mail'].valid\"> Please isert a correct mail address</mat-error>\n    </mat-form-field>\n    <mat-form-field>\n        <input matInput type=\"password\" placeholder=\"Password\" formControlName=\"password\" required>\n        <mat-error *ngIf=\"!form.controls['password'].valid\"> Insert a valid password</mat-error>\n    </mat-form-field>\n    <mat-form-field>\n        <input matInput type=\"password\" placeholder=\"Repeat password\" formControlName=\"password2\" required>\n        <mat-error *ngIf=\"!form.controls['password2'].valid\"> Insert a valid password</mat-error>\n    </mat-form-field>\n    <div class=\"form-error\">\n        <mat-error *ngIf=\"form.hasError('passowordsNotEqual')\"> The passwords do not corrispond</mat-error>\n    </div>\n    <mat-spinner [style.display]=\"showSpinner? 'block' : 'none'\"></mat-spinner>\n    <div class=\"form-error\">\n        <mat-error *ngIf=\"hasError\">{{registrationError}}</mat-error>\n    </div>\n    <button [disabled]=\"!form.valid\" mat-raised-button color=\"primary\" type=\"submit\">Register</button>\n</form>"

/***/ }),

/***/ "./src/app/app.module.ts":
/*!*******************************!*\
  !*** ./src/app/app.module.ts ***!
  \*******************************/
/*! exports provided: AppModule */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AppModule", function() { return AppModule; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_platform_browser__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/platform-browser */ "./node_modules/@angular/platform-browser/fesm2015/platform-browser.js");
/* harmony import */ var _modules_app_routing_module__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./modules/app-routing.module */ "./src/app/modules/app-routing.module.ts");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm2015/core.js");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/forms */ "./node_modules/@angular/forms/fesm2015/forms.js");
/* harmony import */ var _modules_material_module__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./modules/material.module */ "./src/app/modules/material.module.ts");
/* harmony import */ var _angular_platform_browser_animations__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! @angular/platform-browser/animations */ "./node_modules/@angular/platform-browser/fesm2015/animations.js");
/* harmony import */ var _components_app_app_component__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ./components/app/app.component */ "./src/app/components/app/app.component.ts");
/* harmony import */ var _components_auth_login_login_component__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ./components/auth/login/login.component */ "./src/app/components/auth/login/login.component.ts");
/* harmony import */ var _components_auth_register_register_component__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ./components/auth/register/register.component */ "./src/app/components/auth/register/register.component.ts");
/* harmony import */ var _components_auth_auth_component__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ./components/auth/auth.component */ "./src/app/components/auth/auth.component.ts");
/* harmony import */ var _components_auth_logout_component__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! ./components/auth/logout.component */ "./src/app/components/auth/logout.component.ts");
/* harmony import */ var _angular_common_http__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! @angular/common/http */ "./node_modules/@angular/common/fesm2015/http.js");
/* harmony import */ var _services_auth_auth_interceptor__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(/*! ./services/auth/auth.interceptor */ "./src/app/services/auth/auth.interceptor.ts");
/* harmony import */ var _components_attendances_attendances_component__WEBPACK_IMPORTED_MODULE_14__ = __webpack_require__(/*! ./components/attendances/attendances.component */ "./src/app/components/attendances/attendances.component.ts");
/* harmony import */ var _modules_lines_linesPaginatorIntl_module__WEBPACK_IMPORTED_MODULE_15__ = __webpack_require__(/*! ./modules/lines/linesPaginatorIntl.module */ "./src/app/modules/lines/linesPaginatorIntl.module.ts");
/* harmony import */ var _modules_lines_linesPaginator_module__WEBPACK_IMPORTED_MODULE_16__ = __webpack_require__(/*! ./modules/lines/linesPaginator.module */ "./src/app/modules/lines/linesPaginator.module.ts");
/* harmony import */ var _utils_OrderByPypes__WEBPACK_IMPORTED_MODULE_17__ = __webpack_require__(/*! ./utils/OrderByPypes */ "./src/app/utils/OrderByPypes.ts");
/* harmony import */ var _utils_ToStringPipes__WEBPACK_IMPORTED_MODULE_18__ = __webpack_require__(/*! ./utils/ToStringPipes */ "./src/app/utils/ToStringPipes.ts");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_19__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm2015/material.js");




















let AppModule = class AppModule {
};
AppModule = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_3__["NgModule"])({
        declarations: [
            _components_app_app_component__WEBPACK_IMPORTED_MODULE_7__["AppComponent"], _components_auth_auth_component__WEBPACK_IMPORTED_MODULE_10__["AuthComponent"], _components_auth_login_login_component__WEBPACK_IMPORTED_MODULE_8__["LoginComponent"], _components_auth_register_register_component__WEBPACK_IMPORTED_MODULE_9__["RegisterComponent"], _components_auth_logout_component__WEBPACK_IMPORTED_MODULE_11__["LogoutComponent"], _components_attendances_attendances_component__WEBPACK_IMPORTED_MODULE_14__["AttendancesComponent"],
            _utils_OrderByPypes__WEBPACK_IMPORTED_MODULE_17__["OrderByName"], _utils_ToStringPipes__WEBPACK_IMPORTED_MODULE_18__["TimeToString"], _utils_ToStringPipes__WEBPACK_IMPORTED_MODULE_18__["DateToString"]
        ],
        imports: [
            _angular_platform_browser__WEBPACK_IMPORTED_MODULE_1__["BrowserModule"],
            _angular_forms__WEBPACK_IMPORTED_MODULE_4__["FormsModule"], _angular_forms__WEBPACK_IMPORTED_MODULE_4__["ReactiveFormsModule"],
            _angular_platform_browser_animations__WEBPACK_IMPORTED_MODULE_6__["BrowserAnimationsModule"], _modules_material_module__WEBPACK_IMPORTED_MODULE_5__["MaterialModule"],
            _modules_app_routing_module__WEBPACK_IMPORTED_MODULE_2__["AppRoutingModule"], _angular_common_http__WEBPACK_IMPORTED_MODULE_12__["HttpClientModule"], _angular_material__WEBPACK_IMPORTED_MODULE_19__["MatPaginatorModule"]
        ],
        providers: [{ provide: _angular_common_http__WEBPACK_IMPORTED_MODULE_12__["HTTP_INTERCEPTORS"], useClass: _services_auth_auth_interceptor__WEBPACK_IMPORTED_MODULE_13__["AuthInterceptor"], multi: true },
            {
                provide: _angular_material__WEBPACK_IMPORTED_MODULE_19__["MatPaginatorIntl"],
                useClass: _modules_lines_linesPaginatorIntl_module__WEBPACK_IMPORTED_MODULE_15__["LinesPaginatorIntl"]
            }, {
                provide: _angular_material__WEBPACK_IMPORTED_MODULE_19__["MatPaginator"],
                useClass: _modules_lines_linesPaginator_module__WEBPACK_IMPORTED_MODULE_16__["LinesPaginator"],
            }],
        bootstrap: [_components_app_app_component__WEBPACK_IMPORTED_MODULE_7__["AppComponent"]]
    })
], AppModule);



/***/ }),

/***/ "./src/app/components/app/app.component.ts":
/*!*************************************************!*\
  !*** ./src/app/components/app/app.component.ts ***!
  \*************************************************/
/*! exports provided: AppComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AppComponent", function() { return AppComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm2015/core.js");
/* harmony import */ var src_app_services_auth_auth_service__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! src/app/services/auth/auth.service */ "./src/app/services/auth/auth.service.ts");



let AppComponent = class AppComponent {
    constructor(authService) {
        this.authService = authService;
        this.isUserLogged = false;
        this.title = 'client';
    }
    ngOnInit() {
        this.authService.getUserObserver().subscribe((user) => {
            this.isUserLogged = user != null;
        });
    }
};
AppComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
        selector: 'app-root',
        template: __webpack_require__(/*! raw-loader!./app.component.html */ "./node_modules/raw-loader/index.js!./src/app/components/app/app.component.html"),
    }),
    tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [src_app_services_auth_auth_service__WEBPACK_IMPORTED_MODULE_2__["AuthService"]])
], AppComponent);



/***/ }),

/***/ "./src/app/components/attendances/attendances.component.ts":
/*!*****************************************************************!*\
  !*** ./src/app/components/attendances/attendances.component.ts ***!
  \*****************************************************************/
/*! exports provided: AttendancesComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AttendancesComponent", function() { return AttendancesComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm2015/core.js");
/* harmony import */ var src_app_services_attendances_attendances_service__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! src/app/services/attendances/attendances.service */ "./src/app/services/attendances/attendances.service.ts");
/* harmony import */ var rxjs_operators__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! rxjs/operators */ "./node_modules/rxjs/_esm2015/operators/index.js");
/* harmony import */ var src_app_models_ride__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! src/app/models/ride */ "./src/app/models/ride.ts");





let AttendancesComponent = class AttendancesComponent {
    constructor(attendecesService) {
        this.attendecesService = attendecesService;
        this.ways = [{ id: 'outward', text: "andata" }, { id: 'backward', text: "ritorno" }];
        this.way = this.ways[0];
        this.date = new Date();
    }
    ngOnInit() {
        this.subscription = this.attendecesService.getLines()
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["map"])((data) => data, (error) => console.log(error))).subscribe((data) => {
            if (data.length == 0)
                return;
            this.lines = data;
            this.selectedLine = data[0];
            let _way = this.way;
            let _line = this.selectedLine;
            this.attendecesService.getLineReservations(this.selectedLine.name, this.date).subscribe((data) => this.handleReservations(data, _line, _way));
        });
    }
    ngOnDestroy() {
        this.subscription.unsubscribe();
    }
    onRideSelected() {
        this.attendecesService
            .getLineReservations(this.selectedLine.name, this.date)
            .subscribe((data) => this.handleReservations(data, this.selectedLine, this.way));
    }
    setChildPresence(selectedLine, way, reservation) {
        let present = !reservation.present;
        let clone = reservation.clone();
        clone.present = present;
        this.attendecesService.updatePresence(selectedLine.name, this.date, clone).subscribe((response) => {
            if (response)
                reservation.present = present;
        });
    }
    handleReservations(data, line, way) {
        this.ride = new src_app_models_ride__WEBPACK_IMPORTED_MODULE_4__["Ride"]();
        let subscribedChildren = new Map();
        let reservationsMap = new Map();
        let _entries;
        let _linestops;
        if (way.id == 'outward') {
            _entries = Object.entries(data.outwardStopsReservations);
            _linestops = line.outboundStops;
        }
        else {
            _entries = Object.entries(data.backStopsReservations);
            _linestops = line.returnStops;
        }
        line.subscribedChildren.forEach(child => {
            let _child = new src_app_models_ride__WEBPACK_IMPORTED_MODULE_4__["SubscrivableChild"]();
            _child.childName = child.child.name;
            _child.childSurname = child.child.surname;
            _child.cf = child.child.cf;
            _child.subscribed = false;
            subscribedChildren.set(child.child.cf, _child);
        });
        _entries.forEach((entry) => {
            reservationsMap.set(entry[0], entry[1]);
        });
        _linestops.forEach(stop => {
            let _stop = new src_app_models_ride__WEBPACK_IMPORTED_MODULE_4__["RideStop"]();
            _stop.name = stop.name;
            _stop.time = stop.time;
            _stop.reservations = new Array();
            let _reservations = reservationsMap.get(stop.name);
            _reservations.forEach((reservation) => {
                let _reservation = new src_app_models_ride__WEBPACK_IMPORTED_MODULE_4__["Reservation"]();
                _reservation.id = reservation.id;
                let childRes = subscribedChildren.get(reservation.childCf);
                childRes.subscribed = true;
                childRes.parentUsername = reservation.parentUsername;
                _reservation.childName = reservation.childName;
                _reservation.childCf = reservation.childCf;
                _reservation.direction = reservation.direction;
                _reservation.childSurname = reservation.childSurname;
                _reservation.parentUsername = reservation.parentUsername;
                _reservation.present = reservation.present;
                _reservation.stopName = reservation.stopName;
                _stop.reservations.push(_reservation);
            });
            this.ride.stops.push(_stop);
        });
        this.unsubscribedChildren = new Array();
        subscribedChildren.forEach((value, key) => {
            if (!value.subscribed)
                this.unsubscribedChildren.push(value);
        });
    }
};
AttendancesComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
        selector: 'attendances',
        template: __webpack_require__(/*! raw-loader!./attendances.component.html */ "./node_modules/raw-loader/index.js!./src/app/components/attendances/attendances.component.html")
    }),
    tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [src_app_services_attendances_attendances_service__WEBPACK_IMPORTED_MODULE_2__["AttendancesService"]])
], AttendancesComponent);



/***/ }),

/***/ "./src/app/components/auth/auth.component.ts":
/*!***************************************************!*\
  !*** ./src/app/components/auth/auth.component.ts ***!
  \***************************************************/
/*! exports provided: AuthComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AuthComponent", function() { return AuthComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm2015/core.js");
/* harmony import */ var src_app_services_auth_auth_service__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! src/app/services/auth/auth.service */ "./src/app/services/auth/auth.service.ts");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm2015/router.js");




let AuthComponent = class AuthComponent {
    constructor(auth, router) {
        this.auth = auth;
        this.router = router;
    }
    ngOnInit() {
    }
};
AuthComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
        selector: 'auth',
        template: __webpack_require__(/*! raw-loader!./auth.component.html */ "./node_modules/raw-loader/index.js!./src/app/components/auth/auth.component.html"),
        styles: [__webpack_require__(/*! ./auth.style.css */ "./src/app/components/auth/auth.style.css")]
    }),
    tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [src_app_services_auth_auth_service__WEBPACK_IMPORTED_MODULE_2__["AuthService"], _angular_router__WEBPACK_IMPORTED_MODULE_3__["Router"]])
], AuthComponent);



/***/ }),

/***/ "./src/app/components/auth/auth.style.css":
/*!************************************************!*\
  !*** ./src/app/components/auth/auth.style.css ***!
  \************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ".auth-content {\n  text-align: center;\n  margin: 2em;\n}\n\nmat-card {\n  max-width: 400px;\n  margin: 2em auto;\n  text-align: center;\n}\n\n.signin-content {\n  padding: 60px 1rem;\n}\n\nmat-form-field {\n  padding-left: 8px;\n  padding-right: 8px;\n  width: 80%;\n}\n\n.form-error {\n  margin: 8px;\n}\n\nmat-spinner {\n  text-align: center;\n  margin-left: auto;\n  margin-right: auto;\n  margin-top: 16px;\n  margin-bottom: 16px;\n}\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbInNyYy9hcHAvY29tcG9uZW50cy9hdXRoL2F1dGguc3R5bGUuY3NzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUFBO0VBQ0Usa0JBQWtCO0VBQ2xCLFdBQVc7QUFDYjs7QUFFQTtFQUNFLGdCQUFnQjtFQUNoQixnQkFBZ0I7RUFDaEIsa0JBQWtCO0FBQ3BCOztBQUVBO0VBQ0Usa0JBQWtCO0FBQ3BCOztBQUVBO0VBQ0UsaUJBQWlCO0VBQ2pCLGtCQUFrQjtFQUNsQixVQUFVO0FBQ1o7O0FBRUE7RUFDRSxXQUFXO0FBQ2I7O0FBRUE7RUFDRSxrQkFBa0I7RUFDbEIsaUJBQWlCO0VBQ2pCLGtCQUFrQjtFQUNsQixnQkFBZ0I7RUFDaEIsbUJBQW1CO0FBQ3JCIiwiZmlsZSI6InNyYy9hcHAvY29tcG9uZW50cy9hdXRoL2F1dGguc3R5bGUuY3NzIiwic291cmNlc0NvbnRlbnQiOlsiLmF1dGgtY29udGVudCB7XG4gIHRleHQtYWxpZ246IGNlbnRlcjtcbiAgbWFyZ2luOiAyZW07XG59XG5cbm1hdC1jYXJkIHtcbiAgbWF4LXdpZHRoOiA0MDBweDtcbiAgbWFyZ2luOiAyZW0gYXV0bztcbiAgdGV4dC1hbGlnbjogY2VudGVyO1xufVxuXG4uc2lnbmluLWNvbnRlbnQge1xuICBwYWRkaW5nOiA2MHB4IDFyZW07XG59XG5cbm1hdC1mb3JtLWZpZWxkIHtcbiAgcGFkZGluZy1sZWZ0OiA4cHg7XG4gIHBhZGRpbmctcmlnaHQ6IDhweDtcbiAgd2lkdGg6IDgwJTtcbn1cblxuLmZvcm0tZXJyb3Ige1xuICBtYXJnaW46IDhweDtcbn1cblxubWF0LXNwaW5uZXIge1xuICB0ZXh0LWFsaWduOiBjZW50ZXI7XG4gIG1hcmdpbi1sZWZ0OiBhdXRvO1xuICBtYXJnaW4tcmlnaHQ6IGF1dG87XG4gIG1hcmdpbi10b3A6IDE2cHg7XG4gIG1hcmdpbi1ib3R0b206IDE2cHg7XG59XG4iXX0= */"

/***/ }),

/***/ "./src/app/components/auth/login/login.component.ts":
/*!**********************************************************!*\
  !*** ./src/app/components/auth/login/login.component.ts ***!
  \**********************************************************/
/*! exports provided: LoginComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LoginComponent", function() { return LoginComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm2015/core.js");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/forms */ "./node_modules/@angular/forms/fesm2015/forms.js");
/* harmony import */ var src_app_services_auth_auth_service__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! src/app/services/auth/auth.service */ "./src/app/services/auth/auth.service.ts");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm2015/router.js");





let LoginComponent = class LoginComponent {
    constructor(auth, router, fb) {
        this.auth = auth;
        this.router = router;
        this.fb = fb;
        this.invalidCredentials = false;
        this.showSpinner = false;
    }
    ngOnInit() {
        this.form = this.fb.group({
            mail: ['', _angular_forms__WEBPACK_IMPORTED_MODULE_2__["Validators"].compose([_angular_forms__WEBPACK_IMPORTED_MODULE_2__["Validators"].email, _angular_forms__WEBPACK_IMPORTED_MODULE_2__["Validators"].required])],
            password: ['', _angular_forms__WEBPACK_IMPORTED_MODULE_2__["Validators"].required]
        });
    }
    login() {
        if (this.form.valid) {
            this.showSpinner = true;
            this.auth.login(this.form.value).subscribe(() => {
                this.showSpinner = false;
                this.invalidCredentials = false;
                this.router.navigate(['/']);
            }, (error) => {
                this.showSpinner = false;
                this.invalidCredentials = true;
                console.log(error);
            });
        }
    }
};
LoginComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
        selector: 'app-login',
        template: __webpack_require__(/*! raw-loader!./login.component.html */ "./node_modules/raw-loader/index.js!./src/app/components/auth/login/login.component.html"),
        styles: [__webpack_require__(/*! ../auth.style.css */ "./src/app/components/auth/auth.style.css")]
    }),
    tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [src_app_services_auth_auth_service__WEBPACK_IMPORTED_MODULE_3__["AuthService"], _angular_router__WEBPACK_IMPORTED_MODULE_4__["Router"], _angular_forms__WEBPACK_IMPORTED_MODULE_2__["FormBuilder"]])
], LoginComponent);



/***/ }),

/***/ "./src/app/components/auth/logout.component.ts":
/*!*****************************************************!*\
  !*** ./src/app/components/auth/logout.component.ts ***!
  \*****************************************************/
/*! exports provided: LogoutComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LogoutComponent", function() { return LogoutComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm2015/core.js");
/* harmony import */ var src_app_services_auth_auth_service__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! src/app/services/auth/auth.service */ "./src/app/services/auth/auth.service.ts");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm2015/router.js");




let LogoutComponent = class LogoutComponent {
    constructor(auth, router) {
        this.auth = auth;
        this.router = router;
    }
    ngOnInit() {
        this.auth.logout();
        this.router.navigate(['/']);
    }
};
LogoutComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
        template: 'Logging out...'
    }),
    tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [src_app_services_auth_auth_service__WEBPACK_IMPORTED_MODULE_2__["AuthService"], _angular_router__WEBPACK_IMPORTED_MODULE_3__["Router"]])
], LogoutComponent);



/***/ }),

/***/ "./src/app/components/auth/register/register.component.ts":
/*!****************************************************************!*\
  !*** ./src/app/components/auth/register/register.component.ts ***!
  \****************************************************************/
/*! exports provided: PasswordsErrorStateMatcher, RegisterComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "PasswordsErrorStateMatcher", function() { return PasswordsErrorStateMatcher; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RegisterComponent", function() { return RegisterComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm2015/core.js");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/forms */ "./node_modules/@angular/forms/fesm2015/forms.js");
/* harmony import */ var src_app_services_auth_auth_service__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! src/app/services/auth/auth.service */ "./src/app/services/auth/auth.service.ts");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm2015/router.js");





class PasswordsErrorStateMatcher {
    isErrorState(control, form) {
        const invalidCtrl = !!(control && control.invalid && control.parent.dirty);
        const invalidParent = !!(control && control.parent && control.parent.invalid && control.parent.dirty);
        return (invalidCtrl || invalidParent);
    }
}
let RegisterComponent = class RegisterComponent {
    constructor(router, auth, fb) {
        this.router = router;
        this.auth = auth;
        this.fb = fb;
        this.hasError = false;
        this.showSpinner = false;
    }
    ngOnInit() {
        this.form = this.fb.group({
            mail: ['', _angular_forms__WEBPACK_IMPORTED_MODULE_2__["Validators"].compose([_angular_forms__WEBPACK_IMPORTED_MODULE_2__["Validators"].email, _angular_forms__WEBPACK_IMPORTED_MODULE_2__["Validators"].required])],
            password: ['', _angular_forms__WEBPACK_IMPORTED_MODULE_2__["Validators"].required],
            password2: ['', _angular_forms__WEBPACK_IMPORTED_MODULE_2__["Validators"].required],
        }, { validator: this.checkPasswords });
    }
    register() {
        if (!this.form.invalid) {
            this.hasError = false;
            this.auth.register({ mail: this.form.value.mail, password: this.form.value.password, passwordCheck: this.form.value.password2 }).subscribe((data) => {
                this.showSpinner = false;
                this.auth.login({ mail: this.form.value.mail, password: this.form.value.password }).subscribe((data) => {
                    this.showSpinner = false;
                    this.router.navigate(['/']);
                }, error => {
                    this.registrationError = error.error;
                    this.hasError = true;
                    this.showSpinner = false;
                });
            }, (error) => {
                this.showSpinner = false;
                this.hasError = true;
                this.registrationError = error.error;
            });
        }
    }
    checkPasswords(group) {
        let pass1 = group.controls.password;
        let pass2 = group.controls.password2;
        if (pass1.invalid || pass2.invalid) {
            return null;
        }
        return (pass1.valid && pass2.valid && pass1.value === pass2.value) ?
            null : { passowordsNotEqual: true };
    }
};
RegisterComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
        selector: 'app-login',
        template: __webpack_require__(/*! raw-loader!./register.component.html */ "./node_modules/raw-loader/index.js!./src/app/components/auth/register/register.component.html"),
        styles: [__webpack_require__(/*! ../auth.style.css */ "./src/app/components/auth/auth.style.css")]
    }),
    tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_router__WEBPACK_IMPORTED_MODULE_4__["Router"], src_app_services_auth_auth_service__WEBPACK_IMPORTED_MODULE_3__["AuthService"], _angular_forms__WEBPACK_IMPORTED_MODULE_2__["FormBuilder"]])
], RegisterComponent);



/***/ }),

/***/ "./src/app/models/ride.ts":
/*!********************************!*\
  !*** ./src/app/models/ride.ts ***!
  \********************************/
/*! exports provided: Reservation, RideStop, SubscrivableChild, Ride */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "Reservation", function() { return Reservation; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RideStop", function() { return RideStop; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SubscrivableChild", function() { return SubscrivableChild; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "Ride", function() { return Ride; });
class Reservation {
    clone() {
        var copy = new Reservation();
        copy.id = this.id;
        copy.childName = this.childName;
        copy.childSurname = this.childSurname;
        copy.childCf = this.childCf;
        copy.parentUsername = this.parentUsername;
        copy.stopName = this.stopName;
        copy.direction = this.direction;
        copy.present = this.present;
        return copy;
    }
}
class RideStop {
    constructor() {
        this.reservations = new Array();
    }
}
class SubscrivableChild {
}
class Ride {
    constructor() {
        this.stops = new Array();
        this.subscrivableChildren = new Array();
    }
}


/***/ }),

/***/ "./src/app/modules/app-routing.module.ts":
/*!***********************************************!*\
  !*** ./src/app/modules/app-routing.module.ts ***!
  \***********************************************/
/*! exports provided: AppRoutingModule */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AppRoutingModule", function() { return AppRoutingModule; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm2015/core.js");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm2015/router.js");
/* harmony import */ var _components_auth_login_login_component__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../components/auth/login/login.component */ "./src/app/components/auth/login/login.component.ts");
/* harmony import */ var _components_auth_register_register_component__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../components/auth/register/register.component */ "./src/app/components/auth/register/register.component.ts");
/* harmony import */ var _services_auth_auth_guard__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../services/auth/auth.guard */ "./src/app/services/auth/auth.guard.ts");
/* harmony import */ var _components_auth_auth_component__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ../components/auth/auth.component */ "./src/app/components/auth/auth.component.ts");
/* harmony import */ var _components_auth_logout_component__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ../components/auth/logout.component */ "./src/app/components/auth/logout.component.ts");
/* harmony import */ var _components_attendances_attendances_component__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ../components/attendances/attendances.component */ "./src/app/components/attendances/attendances.component.ts");









const routes = [
    // basic routes
    { path: '', redirectTo: 'attendaces', pathMatch: 'full' },
    { path: 'attendaces', component: _components_attendances_attendances_component__WEBPACK_IMPORTED_MODULE_8__["AttendancesComponent"], canActivate: [_services_auth_auth_guard__WEBPACK_IMPORTED_MODULE_5__["AuthGuard"]] },
    // authentication
    {
        path: 'login', component: _components_auth_auth_component__WEBPACK_IMPORTED_MODULE_6__["AuthComponent"], canActivate: [_services_auth_auth_guard__WEBPACK_IMPORTED_MODULE_5__["LoggedGuard"]],
        children: [{ path: '', component: _components_auth_login_login_component__WEBPACK_IMPORTED_MODULE_3__["LoginComponent"] }]
    },
    {
        path: 'register', component: _components_auth_auth_component__WEBPACK_IMPORTED_MODULE_6__["AuthComponent"], canActivate: [_services_auth_auth_guard__WEBPACK_IMPORTED_MODULE_5__["LoggedGuard"]],
        children: [{ path: '', component: _components_auth_register_register_component__WEBPACK_IMPORTED_MODULE_4__["RegisterComponent"] }]
    },
    {
        path: 'logout', component: _components_auth_logout_component__WEBPACK_IMPORTED_MODULE_7__["LogoutComponent"]
    }
];
let AppRoutingModule = class AppRoutingModule {
};
AppRoutingModule = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["NgModule"])({
        imports: [_angular_router__WEBPACK_IMPORTED_MODULE_2__["RouterModule"].forRoot(routes)],
        exports: [_angular_router__WEBPACK_IMPORTED_MODULE_2__["RouterModule"]]
    })
], AppRoutingModule);



/***/ }),

/***/ "./src/app/modules/lines/linesPaginator.module.ts":
/*!********************************************************!*\
  !*** ./src/app/modules/lines/linesPaginator.module.ts ***!
  \********************************************************/
/*! exports provided: LinesPaginator */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LinesPaginator", function() { return LinesPaginator; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm2015/core.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm2015/material.js");



let LinesPaginator = class LinesPaginator extends _angular_material__WEBPACK_IMPORTED_MODULE_2__["MatPaginator"] {
    constructor(_matPaginatorIntl, _changeDetectorRef) {
        super(_matPaginatorIntl, _changeDetectorRef);
        this.hidePageSize = true;
        this.pageSize = 1;
        this.pageSizeOptions = [1];
    }
};
LinesPaginator = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Injectable"])(),
    tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_material__WEBPACK_IMPORTED_MODULE_2__["MatPaginatorIntl"], _angular_core__WEBPACK_IMPORTED_MODULE_1__["ChangeDetectorRef"]])
], LinesPaginator);



/***/ }),

/***/ "./src/app/modules/lines/linesPaginatorIntl.module.ts":
/*!************************************************************!*\
  !*** ./src/app/modules/lines/linesPaginatorIntl.module.ts ***!
  \************************************************************/
/*! exports provided: LinesPaginatorIntl */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LinesPaginatorIntl", function() { return LinesPaginatorIntl; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm2015/core.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm2015/material.js");



let LinesPaginatorIntl = class LinesPaginatorIntl extends _angular_material__WEBPACK_IMPORTED_MODULE_2__["MatPaginatorIntl"] {
    constructor() {
        super();
        this.nextPageLabel = "Next Line";
        this.previousPageLabel = "Previous Line";
    }
};
LinesPaginatorIntl = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Injectable"])(),
    tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [])
], LinesPaginatorIntl);



/***/ }),

/***/ "./src/app/modules/material.module.ts":
/*!********************************************!*\
  !*** ./src/app/modules/material.module.ts ***!
  \********************************************/
/*! exports provided: MaterialModule */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "MaterialModule", function() { return MaterialModule; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm2015/core.js");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/common */ "./node_modules/@angular/common/fesm2015/common.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm2015/material.js");




let MaterialModule = class MaterialModule {
};
MaterialModule = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["NgModule"])({
        imports: [
            _angular_common__WEBPACK_IMPORTED_MODULE_2__["CommonModule"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatToolbarModule"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatButtonModule"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatCardModule"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatInputModule"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatDialogModule"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatTableModule"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatMenuModule"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatIconModule"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatProgressSpinnerModule"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatSidenavModule"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatTabsModule"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatSelectModule"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatDatepickerModule"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatNativeDateModule"]
        ],
        exports: [
            _angular_common__WEBPACK_IMPORTED_MODULE_2__["CommonModule"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatToolbarModule"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatButtonModule"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatCardModule"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatInputModule"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatDialogModule"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatTableModule"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatMenuModule"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatIconModule"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatProgressSpinnerModule"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatSidenavModule"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatTabsModule"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatSelectModule"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatDatepickerModule"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatNativeDateModule"]
        ],
    })
], MaterialModule);



/***/ }),

/***/ "./src/app/services/attendances/attendances.service.ts":
/*!*************************************************************!*\
  !*** ./src/app/services/attendances/attendances.service.ts ***!
  \*************************************************************/
/*! exports provided: AttendancesService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AttendancesService", function() { return AttendancesService; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm2015/core.js");
/* harmony import */ var _angular_common_http__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/common/http */ "./node_modules/@angular/common/fesm2015/http.js");
/* harmony import */ var rxjs__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! rxjs */ "./node_modules/rxjs/_esm2015/index.js");
/* harmony import */ var rxjs_operators__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! rxjs/operators */ "./node_modules/rxjs/_esm2015/operators/index.js");
/* harmony import */ var _auth_auth_service__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../auth/auth.service */ "./src/app/services/auth/auth.service.ts");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm2015/router.js");







let AttendancesService = class AttendancesService {
    constructor(http, authService, router) {
        this.http = http;
        this.authService = authService;
        this.router = router;
        this.rootEndpoint = 'http://localhost:8080';
        this.linesSubject = new rxjs__WEBPACK_IMPORTED_MODULE_3__["BehaviorSubject"]([]);
        this._getLines().subscribe((lines) => this.linesSubject.next(lines), (error) => {
            if (error.status == 403) {
                authService.logout();
                router.navigate(['/login']);
            }
        });
    }
    //#region Lines
    //returns a line
    _getLines() {
        return this.http.get(`${this.rootEndpoint}/lines`);
    }
    getLine(lineName) {
        return this.http.get(`${this.rootEndpoint}/lines/${lineName}`);
    }
    getLines() {
        return this.linesSubject.asObservable();
    }
    //#endregion
    //#region Reservations
    //returns the reservation of a line in a specific date
    getLineReservations(lineName, date) {
        let dateString = this.dateToString(date);
        return this.http.get(`${this.rootEndpoint}/reservations/${lineName}/${dateString}`).pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_4__["map"])(data => data, (error) => {
            console.log(error);
            return null;
        }));
    }
    //returns a specific reservation
    getReservation(lineName, date, reservationId) {
        let dateString = this.dateToString(date);
        return this.http.get(`${this.rootEndpoint}/reservations/${lineName}/${date}/${reservationId}`);
    }
    updatePresence(lineId, date, reservation) {
        let dateString = this.dateToString(date);
        return this.http.put(`${this.rootEndpoint}/reservations/${lineId}/${dateString}/${reservation.id}`, reservation)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_4__["map"])((data) => true, (error) => {
            console.log(error);
            return false;
        }));
    }
    //#endregion
    dateToString(date) {
        let options = { day: '2-digit', month: '2-digit', year: 'numeric' };
        return date.toLocaleDateString('it-IT', options).replace(/\//g, '-').split('-').reverse().join('-');
    }
};
AttendancesService = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Injectable"])({ providedIn: 'root' }),
    tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_common_http__WEBPACK_IMPORTED_MODULE_2__["HttpClient"], _auth_auth_service__WEBPACK_IMPORTED_MODULE_5__["AuthService"], _angular_router__WEBPACK_IMPORTED_MODULE_6__["Router"]])
], AttendancesService);



/***/ }),

/***/ "./src/app/services/auth/auth.guard.ts":
/*!*********************************************!*\
  !*** ./src/app/services/auth/auth.guard.ts ***!
  \*********************************************/
/*! exports provided: AuthGuard, LoggedGuard */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AuthGuard", function() { return AuthGuard; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LoggedGuard", function() { return LoggedGuard; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm2015/core.js");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm2015/router.js");
/* harmony import */ var _auth_service__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./auth.service */ "./src/app/services/auth/auth.service.ts");




let AuthGuard = class AuthGuard {
    constructor(authService, router) {
        this.authService = authService;
        this.router = router;
    }
    canActivate(next, state) {
        if (this.authService.isLoggedIn()) {
            return true;
        }
        else {
            this.router.navigate(['/login']);
        }
        return false;
    }
};
AuthGuard = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Injectable"])({ providedIn: 'root' }),
    tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_auth_service__WEBPACK_IMPORTED_MODULE_3__["AuthService"], _angular_router__WEBPACK_IMPORTED_MODULE_2__["Router"]])
], AuthGuard);

let LoggedGuard = class LoggedGuard {
    constructor(authService, router) {
        this.authService = authService;
        this.router = router;
    }
    canActivate(next, state) {
        if (this.authService.isLoggedIn()) {
            this.router.navigate(['/']);
            return false;
        }
        return true;
    }
};
LoggedGuard = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Injectable"])({ providedIn: 'root' }),
    tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_auth_service__WEBPACK_IMPORTED_MODULE_3__["AuthService"], _angular_router__WEBPACK_IMPORTED_MODULE_2__["Router"]])
], LoggedGuard);



/***/ }),

/***/ "./src/app/services/auth/auth.interceptor.ts":
/*!***************************************************!*\
  !*** ./src/app/services/auth/auth.interceptor.ts ***!
  \***************************************************/
/*! exports provided: AuthInterceptor */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AuthInterceptor", function() { return AuthInterceptor; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm2015/core.js");
/* harmony import */ var _auth_service__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./auth.service */ "./src/app/services/auth/auth.service.ts");



let AuthInterceptor = class AuthInterceptor {
    constructor(authService) {
        this.authService = authService;
    }
    intercept(req, next) {
        if (this.authService.isLoggedIn()) {
            const token = this.authService.getUser().token;
            if (token) {
                req = req.clone({ headers: req.headers.set('Authorization', 'Bearer ' + token) });
            }
        }
        if (!req.headers.has('Content-Type')) {
            req = req.clone({ headers: req.headers.set('Content-Type', 'application/json') });
        }
        req = req.clone({ headers: req.headers.set('Accept', 'application/json') });
        let aaa = req.headers.keys();
        return next.handle(req);
    }
};
AuthInterceptor = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Injectable"])({ providedIn: 'root' }),
    tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_auth_service__WEBPACK_IMPORTED_MODULE_2__["AuthService"]])
], AuthInterceptor);



/***/ }),

/***/ "./src/app/services/auth/auth.service.ts":
/*!***********************************************!*\
  !*** ./src/app/services/auth/auth.service.ts ***!
  \***********************************************/
/*! exports provided: AuthService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AuthService", function() { return AuthService; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm2015/core.js");
/* harmony import */ var rxjs__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! rxjs */ "./node_modules/rxjs/_esm2015/index.js");
/* harmony import */ var _angular_common_http__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/common/http */ "./node_modules/@angular/common/fesm2015/http.js");
/* harmony import */ var rxjs_operators__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! rxjs/operators */ "./node_modules/rxjs/_esm2015/operators/index.js");

var AuthService_1;




let AuthService = AuthService_1 = class AuthService {
    constructor(http) {
        this.http = http;
        this.userSubject = new rxjs__WEBPACK_IMPORTED_MODULE_2__["BehaviorSubject"](JSON.parse(localStorage.getItem(AuthService_1.AUTHENTICATED_USER)));
        this.user = this.userSubject.asObservable();
    }
    //#region Properties
    isLoggedIn() {
        return this.userSubject.value != null;
    }
    getUser() {
        return this.userSubject.value;
    }
    getUserObserver() {
        return this.user;
    }
    //#endregion
    //#region Methods
    login(credentials) {
        let reqHeaders = new _angular_common_http__WEBPACK_IMPORTED_MODULE_3__["HttpHeaders"]({ ContentType: 'application/json' });
        return this.http.post(`${AuthService_1.authEndpoint}/login`, credentials, { headers: reqHeaders })
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_4__["take"])(1), Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_4__["map"])((user) => {
            if (user != null) {
                localStorage.setItem(AuthService_1.AUTHENTICATED_USER, JSON.stringify(user));
                this.userSubject.next(user);
            }
            return user;
        }));
    }
    logout() {
        localStorage.removeItem(AuthService_1.AUTHENTICATED_USER);
        this.userSubject.next(null);
    }
    // TODO registration
    register(credentials) {
        let reqHeaders = new _angular_common_http__WEBPACK_IMPORTED_MODULE_3__["HttpHeaders"]({ ContentType: 'application/json' });
        return this.http.post(`${AuthService_1.authEndpoint}/register`, credentials, { headers: reqHeaders });
    }
};
AuthService.AUTHENTICATED_USER = 'authUser';
AuthService.authEndpoint = 'http://localhost:8080/auth';
AuthService = AuthService_1 = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Injectable"])({ providedIn: 'root' }),
    tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_common_http__WEBPACK_IMPORTED_MODULE_3__["HttpClient"]])
], AuthService);



/***/ }),

/***/ "./src/app/utils/OrderByPypes.ts":
/*!***************************************!*\
  !*** ./src/app/utils/OrderByPypes.ts ***!
  \***************************************/
/*! exports provided: OrderByName */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "OrderByName", function() { return OrderByName; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm2015/core.js");


let OrderByName = class OrderByName {
    transform(array, args) {
        if (!array || array === undefined || array.length === 0)
            return null;
        return array.sort((a, b) => {
            if (a.name < b.name)
                return -1;
            else if (a.name > b.name)
                return 1;
            else
                return 0;
        });
    }
};
OrderByName = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Pipe"])({
        name: 'OrderByName'
    })
], OrderByName);



/***/ }),

/***/ "./src/app/utils/ToStringPipes.ts":
/*!****************************************!*\
  !*** ./src/app/utils/ToStringPipes.ts ***!
  \****************************************/
/*! exports provided: TimeToString, DateToString */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "TimeToString", function() { return TimeToString; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "DateToString", function() { return DateToString; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm2015/core.js");


let TimeToString = class TimeToString {
    transform(time) {
        if (!time || time === undefined || !time.hours || !time.minutes)
            return null;
        var timeString;
        if (time.hours < 10)
            timeString = '0';
        timeString += time.hours + ':';
        if (time.minutes < 10)
            timeString += '0';
        timeString += '' + time.minutes;
        return timeString;
    }
};
TimeToString = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Pipe"])({
        name: 'TimeToString'
    })
], TimeToString);

let DateToString = class DateToString {
    transform(date) {
        if (!date || date === undefined)
            return null;
        return date.toDateString();
    }
};
DateToString = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Pipe"])({
        name: 'DateToString'
    })
], DateToString);



/***/ }),

/***/ "./src/environments/environment.ts":
/*!*****************************************!*\
  !*** ./src/environments/environment.ts ***!
  \*****************************************/
/*! exports provided: environment */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "environment", function() { return environment; });
// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.
const environment = {
    production: false
};
/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.


/***/ }),

/***/ "./src/main.ts":
/*!*********************!*\
  !*** ./src/main.ts ***!
  \*********************/
/*! no exports provided */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm2015/core.js");
/* harmony import */ var _angular_platform_browser_dynamic__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/platform-browser-dynamic */ "./node_modules/@angular/platform-browser-dynamic/fesm2015/platform-browser-dynamic.js");
/* harmony import */ var _app_app_module__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./app/app.module */ "./src/app/app.module.ts");
/* harmony import */ var _environments_environment__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./environments/environment */ "./src/environments/environment.ts");




if (_environments_environment__WEBPACK_IMPORTED_MODULE_3__["environment"].production) {
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_0__["enableProdMode"])();
}
Object(_angular_platform_browser_dynamic__WEBPACK_IMPORTED_MODULE_1__["platformBrowserDynamic"])().bootstrapModule(_app_app_module__WEBPACK_IMPORTED_MODULE_2__["AppModule"])
    .catch(err => console.error(err));


/***/ }),

/***/ 0:
/*!***************************!*\
  !*** multi ./src/main.ts ***!
  \***************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

module.exports = __webpack_require__(/*! /home/dragon/Desktop/ProjectServer/Applicazioni-Internet-project/Labs/lab5/client/src/main.ts */"./src/main.ts");


/***/ })

},[[0,"runtime","vendor"]]]);
//# sourceMappingURL=main-es2015.js.map