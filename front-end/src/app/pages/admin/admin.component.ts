import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { Router } from '@angular/router';


@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  templateUrl: './admin.component.html'
})
export class AdminComponent implements OnInit {

    adminData = new BehaviorSubject<any>(null);

    users = new BehaviorSubject<any[]>([]);

    private readonly postAPI = 'http://localhost:8080/api/post';
    private readonly commentAPI = 'http://localhost:8080/api/comment';
    private readonly userAPI = 'http://localhost:8080/api/user';
    private readonly adminAPI = 'http://localhost:8080/api/admin';

    constructor(private http: HttpClient, private router: Router) {}

    ngOnInit(): void {
        this.getData();
        this.getUsers();
    }

    //get static data
    private getData() {
        this.http.get(`${this.adminAPI}/getData`).subscribe({
            next: data => {
                console.log("Admin data:", data);
                this.adminData.next(data);
            },
            error: err => {
                console.error('Failed to load admin data:', err);
            }
        });
    }

    //get users
    getUsers(): void {
        this.http.get<any[]>(`${this.userAPI}/all`)
        .subscribe({
            next: users => {
            console.log("this is the whole users:", users);
            this.users.next(users);
            },
            error: err => {
            console.log('Failed to load users:', err);
            }
        });
    }

    //redirect to user profile
    goToProfile(id: number) {
        this.router.navigate(['/profile', id]);
    }

    //send request to delete user
    deleteUser(userId: number) {
        this.http.get(`${this.userAPI}/delete/${userId}`).subscribe({
            next: () => {
                console.log("success delete user:");
            },
            error: err => {
                console.error('Failed to delete user :', err);
            }
        });
    }
}