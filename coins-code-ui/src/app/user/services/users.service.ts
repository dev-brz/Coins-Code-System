import { HttpClient, HttpErrorResponse, HttpParams, HttpStatusCode } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, map, of } from 'rxjs';
import {
  USERS_PASSWORD_URL,
  USERS_PROFILE_IMAGE_URL,
  USERS_URL,
  USERS_USER_URL
} from '../../shared/configs/api.config';
import {
  GetUserResponseBody,
  SaveUserRequestBody,
  UpdatePasswordRequestBody,
  UpdateUserRequestBody,
  UserUniqueField
} from '../models/http/user.model';

@Injectable({ providedIn: 'root' })
export class UsersService {
  constructor(private http: HttpClient) {}

  existsBy(field: UserUniqueField, value: string): Observable<boolean> {
    const params = new HttpParams({ fromObject: { [field]: value } });
    return this.http.head(USERS_URL, { observe: 'response', params }).pipe(
      map(() => true),
      catchError((err: HttpErrorResponse) => of(err.status !== HttpStatusCode.NotFound))
    );
  }

  findByUsername(username: string): Observable<GetUserResponseBody> {
    const url = USERS_USER_URL.replace('?1', username);
    return this.http.get<GetUserResponseBody>(url);
  }

  save(user: SaveUserRequestBody): Observable<void> {
    return this.http.post<void>(USERS_URL, user);
  }

  delete(username: string): Observable<void> {
    const url = USERS_USER_URL.replace('?1', username);
    return this.http.delete<void>(url);
  }

  update(body: UpdateUserRequestBody): Observable<void> {
    return this.http.patch<void>(USERS_URL, body);
  }

  updatePassword(update: UpdatePasswordRequestBody): Observable<void> {
    return this.http.patch<void>(USERS_PASSWORD_URL, update);
  }

  updateUserProfileImage(profileImage: File | unknown): Observable<void> {
    if (profileImage instanceof File) {
      const formData = new FormData();
      formData.append('image', profileImage);
      return this.http.post<void>(USERS_PROFILE_IMAGE_URL, formData);
    }
    return of();
  }

  deleteUserProfileImage(): Observable<void> {
    return this.http.delete<void>(USERS_PROFILE_IMAGE_URL);
  }

  getProfileImageUrl(imageName: string): Observable<string> {
    const params = new HttpParams({ fromObject: { imageName } });
    return this.http.get(USERS_PROFILE_IMAGE_URL, { params, responseType: 'blob' }).pipe(map(URL.createObjectURL));
  }
}
