import { CanActivateFn, Routes } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './shared/services/auth.service';
import { HomepageComponent } from './homepage/homepage.component';
import { LoginComponent } from './user/components/login/login.component';

export const canActivateFn: CanActivateFn = () =>
  inject(AuthService).canActivate();
export const logoutFn: CanActivateFn = () => inject(AuthService).logout();
export const canLoginFn: CanActivateFn = () =>
  !inject(AuthService).isLoggedIn();

export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'home', component: HomepageComponent, canActivate: [canActivateFn] },
  { path: 'login', component: LoginComponent, canActivate: [canLoginFn] },
  { path: 'logout', component: LoginComponent, canActivate: [logoutFn] }
];
