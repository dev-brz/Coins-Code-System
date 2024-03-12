import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, iif, map, of, shareReplay, switchMap } from 'rxjs';
import { User } from '../models/user.model';
import { UsersService } from './users.service';

@Injectable({ providedIn: 'root' })
export class UsersStateService {
  private usernameSubject = new BehaviorSubject<string | null>(null);
  private userReplayed$ = this.usernameSubject.asObservable().pipe(
    switchMap(username => (username ? this.mapToUser$(username) : of(null))),
    shareReplay(1)
  );

  constructor(private usersService: UsersService) {
    const currentUser = localStorage.getItem('currentUser');
    if (currentUser) {
      const currentUserUsername = JSON.parse(currentUser)['username'];
      this.usernameSubject.next(currentUserUsername);
    }
  }

  get user$(): Observable<User | null> {
    return this.userReplayed$;
  }

  loadUser(username: string): void {
    this.usernameSubject.next(username);
  }

  clearUser(): void {
    this.usernameSubject.next(null);
  }

  private mapToUser$(username: string): Observable<User> {
    return this.usersService
      .findByUsername(username)
      .pipe(
        switchMap(user =>
          iif(() => !!user.imageName, this.usersService.getProfileImageUrl(user.imageName), of(null)).pipe(
            map(imageUrl => ({ ...user, imageUrl, imageName: undefined }))
          )
        )
      );
  }
}
