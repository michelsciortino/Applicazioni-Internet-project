import { Component, OnInit, ViewChild, AfterViewInit, OnDestroy } from '@angular/core';
import { AdminService, UsersDataSource, UserSearchFilter } from '../../../services/admin/admin.service';
import { MatPaginator, MatPaginatorIntl, MatDialog } from '@angular/material';
import { UsersPaginatorIntl } from './UsersPaginator';
import { tap } from 'rxjs/operators';
import { Subscription } from 'rxjs';
import { ViewUserDialog } from './view-user-dialog/view-user.dialog';
import { EditUserDialog } from './edit-user-dialog/edit-user.dialog';
import { IsMobileService } from 'src/app/services/bridges/is-mobile.service';
import { UserRole } from 'src/app/models/roles';

@Component({
    selector: 'app-users-management',
    templateUrl: './users-management.component.html',
    styleUrls: ['./users-management.component.css'],
    providers: [{
        provide: MatPaginatorIntl,
        useClass: UsersPaginatorIntl
    }]
})
export class UserManagementComponent implements OnInit, AfterViewInit, OnDestroy {

    dataSource: UsersDataSource;
    columnDefinitions = [{ def: 'View-Action', onlyMobile: true },
    { def: 'Mail' },
    { def: 'Name' },
    { def: 'Surname' },
    { def: 'Roles' },
    { def: 'Edit-Action' }];

    filters = [
        { name: "Mail", value: UserSearchFilter.MAIL },
        { name: "Name", value: UserSearchFilter.NAME },
        { name: "Surname", value: UserSearchFilter.SURNAME },
        { name: "Role", value: UserSearchFilter.ROLE }
    ];

    public loading: boolean;
    public nUsers: number;
    public isMobile: boolean;
    public selectedFilter: any;
    public filterKeyword: string;
    public filtered: boolean;

    isMobileSub: Subscription;
    loadingSub: Subscription;
    nUsersSub: Subscription;
    dataSub: Subscription;

    @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;

    constructor(private adminSvc: AdminService, public dialog: MatDialog, private isMobileSvc: IsMobileService) { }

    ngOnInit() {
        this.selectedFilter = this.filters[0].value;
        this.isMobileSub = this.isMobileSvc.isMobile.subscribe((isMobile) => this.isMobile = isMobile);

        this.loading = false;
        this.nUsers = 0;
        this.dataSource = new UsersDataSource(this.adminSvc);

        this.loadingSub = this.dataSource.loading$
            .subscribe((isLoading: boolean) => { this.loading = isLoading; });

        this.nUsersSub = this.dataSource.nUsers$
            .subscribe((nUsers: number) => this.nUsers = nUsers);

        this.dataSource.loadUsers(0, 10);
    }

    ngAfterViewInit() {
        this.paginator.pageIndex = 0;
        this.paginator.length = 0;
        this.dataSub = this.paginator.page
            .pipe(tap(() => {
                if (this.filtered)
                    this.dataSource.loadUsers(this.paginator.pageIndex, this.paginator.pageSize, this.selectedFilter, this.filterKeyword);
                else
                    this.dataSource.loadUsers(this.paginator.pageIndex, this.paginator.pageSize);
            }))
            .subscribe();
    }

    ngOnDestroy() {
        this.isMobileSub.unsubscribe();
        this.dataSub.unsubscribe();
        this.nUsersSub.unsubscribe();
        this.loadingSub.unsubscribe();
    }

    getDisplayedColumns(): string[] {
        return this.columnDefinitions
            .filter(cd => this.isMobile || !cd.onlyMobile)
            .map(cd => cd.def);
    }

    notSysAdmin(element) {
        return element.roles.find(v => v === UserRole.SYSTEM_ADMIN) == null;
    }

    viewUser(user) {
        const dialogRef = this.dialog.open(ViewUserDialog, { data: { user: user } });
    }

    editUser(user) {
        const dialogRef = this.dialog.open(EditUserDialog, { data: { user: user } });
        dialogRef.afterClosed().subscribe(result => {
            if(dialogRef.componentInstance.dirty){
                if (this.filtered)
                    this.dataSource.loadUsers(this.paginator.pageIndex, this.paginator.pageSize, this.selectedFilter, this.filterKeyword);
                else
                    this.dataSource.loadUsers(this.paginator.pageIndex, this.paginator.pageSize);
            }
        });
    }

    doFilter() {
        this.filtered = true;
        this.paginator.pageIndex = 0;
        this.dataSource.loadUsers(this.paginator.pageIndex, this.paginator.pageSize, this.selectedFilter, this.filterKeyword);
    }

    clearFilter() {
        this.filtered = false;
        this.filterKeyword = null;
        this.paginator.pageIndex = 0;
        this.dataSource.loadUsers(0, this.paginator.pageSize);
    }
}
