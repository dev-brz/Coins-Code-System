import { HttpClient, HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, map, of } from 'rxjs';
import { SaveUserRequestBody } from '../models/save-user-request-body.model';
import { USERS_URL, USERS_USER_URL } from '../../shared/configs/api.config';

@Injectable({ providedIn: 'root' })
export class UsersService {
  constructor(private http: HttpClient) {}

  existsByUsername(username: string): Observable<boolean> {
    const url = USERS_USER_URL.replace('?1', username);
    return this.http.head(url, { observe: 'response' }).pipe(
      map(() => true),
      catchError((err: HttpErrorResponse) => of(err.status !== HttpStatusCode.NotFound))
    );
  }

  save(user: SaveUserRequestBody): Observable<void> {
    return this.http.post<void>(USERS_URL, user);
  }
}
