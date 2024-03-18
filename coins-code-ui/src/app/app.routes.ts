import { inject } from '@angular/core';
import { CanActivateFn, Routes } from '@angular/router';
import { MainViewComponent } from './main/components/main-view/main-view.component';
import { LOGIN_ROUTE, MAIN_ROUTE, REGISTER_ROUTE } from './shared/configs/routes.config';
import { currentUserResolver } from './shared/resolvers/current-user.resolver';
import { AuthService } from './shared/services/auth.service';
import { LoginViewComponent } from './user/components/login-view/login-view.component';
import { RegisterViewComponent } from './user/components/register-view/register-view.component';

export const canActivateFn: CanActivateFn = () => inject(AuthService).canActivate();
export const isUnloggedFn: CanActivateFn = () => !inject(AuthService).isLoggedIn();

export const routes: Routes = [
  { path: '', redirectTo: MAIN_ROUTE, pathMatch: 'full' },
  { path: LOGIN_ROUTE, component: LoginViewComponent, canActivate: [isUnloggedFn] },
  { path: REGISTER_ROUTE, component: RegisterViewComponent, canActivate: [isUnloggedFn] },
  {
    path: MAIN_ROUTE,
    component: MainViewComponent,
    canMatch: [canActivateFn],
    resolve: { user: currentUserResolver },
    loadChildren: () => import('./main/main.routes').then(r => r.routes)
  }
];
