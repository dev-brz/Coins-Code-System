import { UserBase } from '../../user/models/user.model';
import { EMPTY, Observable, of } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { inject } from '@angular/core';

export const currentUserResolver = (): Observable<UserBase> => {
  const authService = inject(AuthService);
  const user = authService.getCurrentUser();

  if (user) {
    return of(user);
  } else {
    authService.logout();
    return EMPTY;
  }
};
