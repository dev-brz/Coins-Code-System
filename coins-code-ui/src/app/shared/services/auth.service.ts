import { HttpClient, HttpEvent, HttpHandlerFn, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { User } from '../../user/models/user.model';
import { UsersStateService } from '../../user/services/users-state.service';
import { LOGIN_URL, NO_AUTH_URLS } from '../configs/api.config';
import { LoginForm } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(
    private http: HttpClient,
    private router: Router,
    private userStateService: UsersStateService
  ) {}

  login(form: LoginForm): Observable<void> {
    return this.http.post<User>(LOGIN_URL, form).pipe(
      map(() => {
        this.userStateService.loadUser(form.username);
        localStorage.setItem('currentUser', JSON.stringify(form));
      })
    );
  }

  logout(): boolean {
    localStorage.removeItem('currentUser');
    this.userStateService.clearUser();
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
