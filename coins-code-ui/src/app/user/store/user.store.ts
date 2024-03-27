import { withDevtools } from '@angular-architects/ngrx-toolkit';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { tapResponse } from '@ngrx/operators';
import { StateSignal, patchState, signalStore, withMethods, withState } from '@ngrx/signals';
import { EMPTY, Observable, iif, map, of, pipe, switchMap, tap } from 'rxjs';
import { DEFAULT_PROFILE_IMAGE_PATH } from '../../shared/configs/assets.config';
import { LOGIN_ROUTE } from '../../shared/configs/routes.config';
import { CURRENT_USER_KEY } from '../../shared/configs/storage.config';
import { LoginForm } from '../../shared/models/user.model';
import { rxMethod } from '../../shared/utils/store-utils';
import { SaveUserRequestBody, UpdateUserRequestBody } from '../models/http/user.model';
import { User } from '../models/user.model';
import { UsersService } from '../services/users.service';
import { ErrorService } from '../../shared/services/error.service';

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

export const UserStore = signalStore(
  { providedIn: 'root' },
  withDevtools('user'),
  withState(initialUserState),
  withMethods(
    (store, usersService = inject(UsersService), router = inject(Router), errorService = inject(ErrorService)) => ({
      bootstrapCurrent: rxMethod<void>(
        pipe(
          tap(() => patchState(store, setLoading())),
          map(() => localStorage.getItem(CURRENT_USER_KEY)),
          switchMap(user => (user ? of(JSON.parse(user)) : EMPTY)),
          switchMap(form => mapToUser$(form.username, usersService)),
          tapLoginResponse(store, router)
        )
      ),
      loadCurrent: rxMethod<LoginForm>(
        pipe(
          tap(() => patchState(store, setLoading())),
          tap(form => localStorage.setItem(CURRENT_USER_KEY, JSON.stringify(form))),
          switchMap(form => mapToUser$(form.username, usersService)),
          tapLoginResponse(store, router)
        )
      ),
      disposeCurrent: rxMethod<void>(
        pipe(
          tap(() => localStorage.removeItem(CURRENT_USER_KEY)),
          tap(() => patchState(store, initialUserState)),
          tap(() => router.navigateByUrl(`/${LOGIN_ROUTE}`))
        )
      ),
      saveNew: rxMethod<SaveUserRequestBody>(
        pipe(
          tap(() => patchState(store, setLoading())),
          switchMap(body => usersService.save(body).pipe(map(() => body))),
          tap(({ username, password }) =>
            localStorage.setItem(CURRENT_USER_KEY, JSON.stringify({ username, password }))
          ),
          switchMap(body => usersService.updateUserProfileImage(body.profileImage)),
          tapResponse({
            next: () => {},
            complete: () => {
              localStorage.removeItem(CURRENT_USER_KEY);
              router.navigateByUrl(`/${LOGIN_ROUTE}`);
            },
            error: () => {
              localStorage.removeItem(CURRENT_USER_KEY);
              errorService.dispatchError('USER-SAVE');
            },
            finalize: () => patchState(store, setLoading(false))
          })
        )
      ),
      update: rxMethod<UpdateUserRequestBody>(
        pipe(
          tap(() => patchState(store, setLoading())),
          switchMap(update => usersService.update(update).pipe(map(() => update))),
          tapResponse({
            next: update => patchState(store, setCurrentUser({ ...store.currentUser(), ...update })),
            error: () => errorService.dispatchError('USER-UPDATE'),
            finalize: () => patchState(store, setLoading(false))
          })
        )
      ),
      uploadProfileImage: rxMethod<File>(
        pipe(
          tap(() => patchState(store, setLoading())),
          switchMap(image => usersService.updateUserProfileImage(image).pipe(map(() => URL.createObjectURL(image)))),
          tapResponse({
            next: imageUrl => patchState(store, setCurrentUser({ ...store.currentUser(), imageUrl })),
            error: () => errorService.dispatchError('USER-UPLOAD-IMAGE'),
            finalize: () => patchState(store, setLoading(false))
          })
        )
      ),
      removeProfileImage: rxMethod<void>(
        pipe(
          tap(() => patchState(store, setLoading())),
          switchMap(() => usersService.deleteUserProfileImage().pipe(map(() => DEFAULT_PROFILE_IMAGE_PATH))),
          tapResponse({
            next: imageUrl => patchState(store, setCurrentUser({ ...store.currentUser(), imageUrl })),
            error: () => errorService.dispatchError('USER-REMOVE-IMAGE'),
            finalize: () => patchState(store, setLoading(false))
          })
        )
      )
    })
  )
);

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

function tapLoginResponse(store: StateSignal<object>, router: Router): ReturnType<typeof tapResponse<User, User>> {
  return tapResponse({
    next: user => patchState(store, setCurrentUser(user)),
    error: () => {
      localStorage.removeItem(CURRENT_USER_KEY);
      patchState(store, setLoaded(false));
      router.navigateByUrl(`/${LOGIN_ROUTE}`);
    },
    complete: () => patchState(store, setLoaded()),
    finalize: () => patchState(store, setLoading(false))
  });
}

function setLoaded(isLoaded = true): { isLoaded: boolean } {
  return { isLoaded };
}

function setLoading(isLoading = true): { isLoading: boolean } {
  return { isLoading };
}

function setCurrentUser(currentUser: User): { currentUser: User } {
  return { currentUser };
}
