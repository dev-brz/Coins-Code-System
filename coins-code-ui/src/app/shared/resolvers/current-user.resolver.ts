import { inject } from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { Observable, filter, take, tap } from 'rxjs';
import { User } from '../../user/models/user.model';
import { UserStore } from '../../user/store/user.store';
import { AuthService } from '../services/auth.service';

export const currentUserResolver = (): Observable<User> => {
  const userStore = inject(UserStore);
  const authService = inject(AuthService);

  return toObservable(userStore.currentUser).pipe(
    filter(user => !!user.username),
    take(1),
    tap({ error: () => authService.logout() })
  );
};
