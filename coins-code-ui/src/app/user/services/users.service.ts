import { HttpClient, HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, map, of, switchMap } from 'rxjs';
import { SaveUserRequestBody } from '../models/save-user-request-body.model';
import { USERS_PROFILE_IMAGE, USERS_URL, USERS_USER_URL } from '../../shared/configs/api.config';
import { AuthService } from '../../shared/services/auth.service';

@Injectable({ providedIn: 'root' })
export class UsersService {
  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  existsByUsername(username: string): Observable<boolean> {
    const url = USERS_USER_URL.replace('?1', username);
    return this.http.head(url, { observe: 'response' }).pipe(
      map(() => true),
      catchError((err: HttpErrorResponse) => of(err.status !== HttpStatusCode.NotFound))
    );
  }

  save(user: SaveUserRequestBody): Observable<void> {
    const { profileImage, username, password } = user;

    user.profileImage = null;
    return this.http.post<void>(USERS_URL, user).pipe(
      switchMap(() => this.authService.login({ username, password })),
      switchMap(() => this.updateUserProfileImage(profileImage))
    );
  }

  updateUserProfileImage(profileImage: File | unknown): Observable<void> {
    if (profileImage instanceof File) {
      const formData = new FormData();
      formData.append('image', profileImage);
      return this.http.post<void>(USERS_PROFILE_IMAGE, formData);
    }
    return of();
  }
}
