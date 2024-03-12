import { inject } from '@angular/core';
import { EMPTY, Observable, catchError, filter } from 'rxjs';
import { User } from '../../user/models/user.model';
import { UsersStateService } from '../../user/services/users-state.service';
import { AuthService } from '../services/auth.service';

export const currentUserResolver = (): Observable<User> => {
  const usersStateService = inject(UsersStateService);
  const authService = inject(AuthService);

  return usersStateService.user$.pipe(
    filter(Boolean),
    catchError(() => {
      authService.logout();
      return EMPTY;
    })
  );
};
