import { CanActivateFn, Routes } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './shared/services/auth.service';
import { HomepageComponent } from './homepage/homepage.component';
import { LoginViewComponent } from './user/components/login-view/login-view.component';
import { RegisterViewComponent } from './user/components/register-view/register-view.component';

export const canActivateFn: CanActivateFn = () => inject(AuthService).canActivate();
export const logoutFn: CanActivateFn = () => inject(AuthService).logout();
export const canLoginFn: CanActivateFn = () => !inject(AuthService).isLoggedIn();

export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'home', component: HomepageComponent, canActivate: [canActivateFn] },
  { path: 'login', component: LoginViewComponent, canActivate: [canLoginFn] },
  { path: 'logout', component: LoginViewComponent, canActivate: [logoutFn] },
  { path: 'register', component: RegisterViewComponent }
];
