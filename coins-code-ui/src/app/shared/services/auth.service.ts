import { HttpClient, HttpEvent, HttpHandlerFn, HttpRequest } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { UserStore } from '../../user/store/user.store';
import { LOGIN_URL, NO_AUTH_URLS } from '../configs/api.config';
import { CURRENT_USER_KEY } from '../configs/storage.config';
import { LoginForm } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  readonly userStore = inject(UserStore);

  constructor(private http: HttpClient) {}

  login(form: LoginForm): Observable<void> {
    return this.http.post<void>(LOGIN_URL, form).pipe(tap(() => this.userStore.loadCurrent(form)));
  }

  logout(): void {
    this.userStore.disposeCurrent();
  }

  canActivate(): boolean {
    if (this.isLoggedIn()) {
      return true;
    }

    this.logout();

    return false;
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem(CURRENT_USER_KEY);
  }

  interceptRequsest(req: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> {
    const currentUser = localStorage.getItem(CURRENT_USER_KEY);

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
