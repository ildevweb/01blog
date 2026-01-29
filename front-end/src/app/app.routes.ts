import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { ProfileComponent } from './pages/profile/profile.component'; 
import { NotificationComponent } from './pages/notification/notification.component'; 
import { AdminComponent } from './pages/admin/admin.component'; 
import { AuthComponent } from './pages/auth/auth.component';
import { NotFoundComponent } from './pages/404/404.component';
import { AuthGuard } from './core/auth/auth.guard';
import { GuestGuard } from './core/auth/guest.guard';


export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' }, 
  { path: 'home', component: HomeComponent, canActivate: [AuthGuard] },
  { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard] },
  { path: 'profile/:id', component: ProfileComponent, canActivate: [AuthGuard] },
  { path: 'notification', component: NotificationComponent, canActivate: [AuthGuard] },
  { path: 'admin', component: AdminComponent, canActivate: [AuthGuard] },
  { path: 'auth', component: AuthComponent, canActivate: [GuestGuard] },
  { path: 'notfound', component: NotFoundComponent },
  { path: '**', redirectTo: 'notfound' }
];
