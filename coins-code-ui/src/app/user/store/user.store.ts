import { withDevtools } from '@angular-architects/ngrx-toolkit';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { tapResponse } from '@ngrx/operators';
import { StateSignal, patchState, signalStore, withMethods, withState } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { EMPTY, Observable, UnaryFunction, finalize, iif, map, of, pipe, switchMap, tap } from 'rxjs';
import { DEFAULT_PROFILE_IMAGE_PATH } from '../../shared/configs/assets.config';
import { LOGIN_ROUTE } from '../../shared/configs/routes.config';
import { CURRENT_USER_KEY } from '../../shared/configs/storage.config';
import { LoginForm } from '../../shared/models/user.model';
import { SaveUserRequestBody, UpdateUserRequestBody } from '../models/http/user.model';
import { User } from '../models/user.model';
import { UsersService } from '../services/users.service';

export interface UserState {
  currentUser: User;
  isLoading: boolean;
  isLoaded: boolean;
}

const initialUserState: UserState = {
  currentUser: {
    username: '',
    email: '',
    firstName: '',
    lastName: '',
    phoneNumber: '',
    createdAt: '',
    numberOfReceives: 0,
    numberOfSends: 0,
    sendLimits: 0,
    active: false,
    imageUrl: DEFAULT_PROFILE_IMAGE_PATH
  },
  isLoading: false,
  isLoaded: false
};

// TODO - fix [#64]
export const UserStore = signalStore(
  { providedIn: 'root' },
  withDevtools('user'),
  withState(initialUserState),
  withMethods((store, usersService = inject(UsersService), router = inject(Router)) => ({
    bootstrapCurrent: rxMethod<void>(
      pipe(
        map(() => localStorage.getItem(CURRENT_USER_KEY)),
        switchMap(user => (user ? of(JSON.parse(user)) : EMPTY)),
        loadCurrent$(store, usersService, () => router.navigateByUrl(`/${LOGIN_ROUTE}`))
      )
    ),
    loadCurrent: rxMethod<LoginForm>(loadCurrent$(store, usersService)),
    disposeCurrent: rxMethod<void>(
      pipe(
        tap(() => localStorage.removeItem(CURRENT_USER_KEY)),
        tap(() => patchState(store, initialUserState)),
        tap(() => router.navigateByUrl(`/${LOGIN_ROUTE}`))
      )
    ),
    saveNew: rxMethod<SaveUserRequestBody>(
      pipe(
        tap(() => patchState(store, { isLoading: true })),
        switchMap(body => usersService.save(body).pipe(map(() => body))),
        tap(({ username, password }) => localStorage.setItem(CURRENT_USER_KEY, JSON.stringify({ username, password }))),
        switchMap(body => usersService.updateUserProfileImage(body.profileImage)),
        finalize(() => {
          localStorage.removeItem(CURRENT_USER_KEY);
          patchState(store, { isLoading: false });
          router.navigateByUrl(`/${LOGIN_ROUTE}`);
        })
      )
    ),
    update: rxMethod<UpdateUserRequestBody>(
      pipe(
        tap(() => patchState(store, { isLoading: true })),
        switchMap(update => usersService.update(update).pipe(map(() => update))),
        tap(update => patchState(store, { currentUser: { ...store.currentUser(), ...update }, isLoading: false }))
      )
    ),
    uploadProfileImage: rxMethod<File>(
      pipe(
        tap(() => patchState(store, { isLoading: true })),
        switchMap(image => usersService.updateUserProfileImage(image).pipe(map(() => URL.createObjectURL(image)))),
        tap(imageUrl => patchState(store, { currentUser: { ...store.currentUser(), imageUrl }, isLoading: false }))
      )
    ),
    removeProfileImage: rxMethod<void>(
      pipe(
        tap(() => patchState(store, { isLoading: true })),
        switchMap(() => usersService.deleteUserProfileImage().pipe(map(() => DEFAULT_PROFILE_IMAGE_PATH))),
        tap(imageUrl => patchState(store, { currentUser: { ...store.currentUser(), imageUrl }, isLoading: false }))
      )
    )
  }))
);

function loadCurrent$(
  store: StateSignal<object>,
  usersService: UsersService,
  onError = (): void => {}
): UnaryFunction<Observable<LoginForm>, Observable<User>> {
  return pipe(
    tap(() => patchState(store, { isLoading: true })),
    tap(form => localStorage.setItem(CURRENT_USER_KEY, JSON.stringify(form))),
    switchMap(form => mapToUser$(form.username, usersService)),
    // TODO - fix [#64]
    tapResponse({
      next: user => patchState(store, { currentUser: user }),
      error: () => {
        localStorage.removeItem(CURRENT_USER_KEY);
        patchState(store, { isLoaded: false });
        onError();
      },
      complete: () => patchState(store, { isLoaded: true }),
      finalize: () => patchState(store, { isLoading: false })
    })
  );
}

function mapToUser$(username: string, usersService: UsersService): Observable<User> {
  return usersService
    .findByUsername(username)
    .pipe(
      switchMap(user =>
        iif(
          () => !!user.imageName,
          usersService.getProfileImageUrl(user.imageName),
          of(DEFAULT_PROFILE_IMAGE_PATH)
        ).pipe(map(imageUrl => ({ ...user, imageUrl, imageName: undefined })))
      )
    );
}
