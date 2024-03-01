import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpHandlerFn, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Router } from '@angular/router';
import { LOGIN_URL, NO_AUTH_URLS } from '../configs/api.config';
import { LoginForm } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

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
      req = req.clone({
        setHeaders: {
          Authorization: `Basic ${user.username}:${user.password}`
        }
      });

      return next(req);
    }

    return next(req);
  }
}
