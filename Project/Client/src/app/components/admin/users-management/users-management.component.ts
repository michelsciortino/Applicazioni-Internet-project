import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { BehaviorSubject } from 'rxjs';
import { UserInfo } from 'src/app/services/user/models/user';
import { AdminService } from 'src/app/services/admin/admin.service';
import { MatPaginator, MatPaginatorIntl } from '@angular/material';
import { UsersPaginatorIntl } from './UsersPaginator';

@Component({
    selector: 'app-users-management',
    templateUrl: './users-management.component.html',
    styleUrls: ['./users-management.component.css'],
    providers: [{
        provide: MatPaginatorIntl,
        useClass: UsersPaginatorIntl
    }]
})
export class UserManagementComponent implements OnInit {
    displayedColumns: string[] = ['Mail', 'Name', 'Surname', 'Roles', 'Action'];
    dataSource = new MatTableDataSource<UserInfo>();
    @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;

    constructor(private adminSvc: AdminService) {
    }

    applyFilter(filterValue: string) {
        this.dataSource.filter = filterValue.trim().toLowerCase();
        this.dataSource.paginator = this.paginator;
    }

    ngOnInit() {
        this.refresh();
    }

    refresh() {
        this.adminSvc.getUsers().subscribe((users) => this.dataSource.data = users);
    }
}
