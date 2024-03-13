import { inject } from '@angular/core';
import { CanActivateFn, Routes } from '@angular/router';
import { HomepageViewComponent } from './home/components/homepage-view/homepage-view.component';
import {
  HOME_ROUTE,
  LOGIN_ROUTE,
  LOGOUT_ROUTE,
  REGISTER_ROUTE,
  USER_ACCOUNT_ROTUE
} from './shared/configs/routes.config';
import { currentUserResolver } from './shared/resolvers/current-user.resolver';
import { AuthService } from './shared/services/auth.service';
import { LoginViewComponent } from './user/components/login-view/login-view.component';
import { RegisterViewComponent } from './user/components/register-view/register-view.component';

export const canActivateFn: CanActivateFn = () => inject(AuthService).canActivate();
export const logoutFn: CanActivateFn = () => inject(AuthService).logout();
export const isUnloggedFn: CanActivateFn = () => !inject(AuthService).isLoggedIn();

export const routes: Routes = [
  { path: '', redirectTo: HOME_ROUTE, pathMatch: 'full' },
  {
    path: HOME_ROUTE,
    component: HomepageViewComponent,
    canActivate: [canActivateFn],
    resolve: { user: currentUserResolver }
  },
  { path: LOGIN_ROUTE, component: LoginViewComponent, canActivate: [isUnloggedFn] },
  { path: LOGOUT_ROUTE, component: LoginViewComponent, canActivate: [logoutFn] },
  { path: REGISTER_ROUTE, component: RegisterViewComponent, canActivate: [isUnloggedFn] },
  {
    path: USER_ACCOUNT_ROTUE,
    canMatch: [canActivateFn],
    canActivate: [canActivateFn],
    loadComponent: () =>
      import('./user/components/account-view/account-view.component').then(c => c.AccountViewComponent)
  }
];
