import { HttpClient, HttpErrorResponse, HttpParams, HttpStatusCode } from '@angular/common/http';
import { Injectable, Injector } from '@angular/core';
import { Observable, catchError, map, of, switchMap } from 'rxjs';
import { USERS_PROFILE_IMAGE, USERS_URL, USERS_USER_URL } from '../../shared/configs/api.config';
import { AuthService } from '../../shared/services/auth.service';
import { GetUserResponseBody, SaveUserRequestBody } from '../models/http/user.model';

@Injectable({ providedIn: 'root' })
export class UsersService {
  constructor(
    private http: HttpClient,
    private injector: Injector
  ) {}

  existsByUsername(username: string): Observable<boolean> {
    const url = USERS_USER_URL.replace('?1', username);
    return this.http.head(url, { observe: 'response' }).pipe(
      map(() => true),
      catchError((err: HttpErrorResponse) => of(err.status !== HttpStatusCode.NotFound))
    );
  }

  findByUsername(username: string): Observable<GetUserResponseBody> {
    const url = USERS_USER_URL.replace('?1', username);
    return this.http.get<GetUserResponseBody>(url);
  }

  save(user: SaveUserRequestBody): Observable<void> {
    const authService = this.injector.get(AuthService);
    const { profileImage, username, password } = user;
    user.profileImage = null;

    return this.http.post<void>(USERS_URL, user).pipe(
      switchMap(() => authService.login({ username, password })),
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

  getProfileImageUrl(imageName: string): Observable<string> {
    const params = new HttpParams({ fromObject: { imageName } });
    return this.http.get(USERS_PROFILE_IMAGE, { params, responseType: 'blob' }).pipe(map(URL.createObjectURL));
  }
}
