import { CanActivateFn, Routes } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './shared/services/auth.service';
import { LoginViewComponent } from './user/components/login-view/login-view.component';
import { HomepageViewComponent } from './home/components/homepage-view/homepage-view.component';
import { RegisterViewComponent } from './user/components/register-view/register-view.component';
import { currentUserResolver } from './shared/resolvers/current-user.resolver';

export const canActivateFn: CanActivateFn = () => inject(AuthService).canActivate();
export const logoutFn: CanActivateFn = () => inject(AuthService).logout();
export const canLoginFn: CanActivateFn = () => !inject(AuthService).isLoggedIn();

export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  {
    path: 'home',
    component: HomepageViewComponent,
    canActivate: [canActivateFn],
    resolve: { user: currentUserResolver }
  },
  { path: 'login', component: LoginViewComponent, canActivate: [canLoginFn] },
  { path: 'logout', component: LoginViewComponent, canActivate: [logoutFn] },
  { path: 'register', component: RegisterViewComponent },
  {
    path: 'user/account',
    canMatch: [canActivateFn],
    canActivate: [canActivateFn],
    loadComponent: () =>
      import('./user/components/account-view/account-view.component').then(c => c.AccountViewComponent)
  }
];
