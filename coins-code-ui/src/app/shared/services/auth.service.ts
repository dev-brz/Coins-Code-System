import { HttpClient, HttpEvent, HttpHandlerFn, HttpRequest } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { UserStore } from '../../user/store/user.store';
import { LOGIN_URL, NO_AUTH_POST_URLS, NO_AUTH_URLS } from '../configs/api.config';
import { CURRENT_USER_KEY } from '../configs/storage.config';
import { LoginForm } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  readonly userStore = inject(UserStore);

  constructor(private http: HttpClient) {}

  login(form: LoginForm, loadUser = true): Observable<void> {
    return this.http.post<void>(LOGIN_URL, form).pipe(
      tap(() => {
        if (loadUser) {
          this.userStore.loadCurrent(form);
        }
      })
    );
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

  interceptRequest(req: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> {
    const currentUser = localStorage.getItem(CURRENT_USER_KEY);

    if (NO_AUTH_URLS.includes(req.url) || this.isNoAuthPost(req)) {
      return next(req);
    }

    req.method === 'POST' && req.url === LOGIN_URL && this.logout();

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

  private isNoAuthPost(req: HttpRequest<unknown>): boolean {
    return req.method === 'POST' && NO_AUTH_POST_URLS.includes(req.url);
  }
}
