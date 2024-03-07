import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpHandlerFn, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Router } from '@angular/router';
import { LOGIN_URL, NO_AUTH_URLS } from '../configs/api.config';
import { LoginForm } from '../models/user.model';
import { UserBase } from '../../user/models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  getCurrentUser(): UserBase | null {
    const user = localStorage.getItem('currentUser');

    // TODO - load user and store it [#43]
    if (user) {
      const userObject = JSON.parse(user) as UserBase;
      return {
        ...userObject,
        firstName: 'Mock',
        lastName: 'Mocking'
      };
    } else {
      return null;
    }
  }

  login(form: LoginForm): Observable<void> {
    return this.http
      .post<void>(LOGIN_URL, form)
      .pipe(map(() => localStorage.setItem('currentUser', JSON.stringify(form))));
  }

  logout(): boolean {
    localStorage.removeItem('currentUser');
    this.router.navigate(['/login']);
    return true;
  }

  canActivate(): boolean {
    if (this.isLoggedIn()) {
      return true;
    }

    this.logout();

    return false;
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('currentUser');
  }

  interceptRequsest(req: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> {
    const currentUser = localStorage.getItem('currentUser');

    if (NO_AUTH_URLS.includes(req.url)) {
      return next(req);
    }

    if (currentUser) {
      const user = JSON.parse(currentUser);
      const token = btoa(`${user.username}:${user.password}`);
      req = req.clone({
        setHeaders: {
          Authorization: `Basic ${token}`
        }
      });

      return next(req);
    }

    return next(req);
  }
}
