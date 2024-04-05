//create notification feature store

import { patchState, signalStoreFeature, withHooks, withMethods, withState } from '@ngrx/signals';
import { Notification } from '../models/notification.model';
import { inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { filter, switchMap } from 'rxjs';
import { tap } from 'rxjs/operators';

export interface NotificationsState {
  notifications: Notification[];
}

export interface NotificationState extends NotificationsState {
  current: Notification;
}

export const initialState: NotificationState = {
  notifications: [],
  current: { message: '', type: 'info' }
};

export const withNotifications = signalStoreFeature(
  withState(initialState),
  withMethods((store, snackBar = inject(MatSnackBar)) => ({
    clear(): void {
      patchState(store, initialState);
    },
    showNotification: rxMethod<Notification>(pipe$ =>
      pipe$.pipe(
        filter(notification => notification && notification?.message !== ''),
        switchMap(notification =>
          snackBar
            .open(notification.message, 'Close', {
              verticalPosition: 'top',
              duration: 3000
            })
            .afterDismissed()
            .pipe(tap(() => patchState(store, patchNotifications(notification, store.notifications()))))
        )
      )
    )
  })),
  withHooks({
    onInit({ showNotification, current }) {
      showNotification(current);
    },
    onDestroy({ clear }) {
      clear();
    }
  })
);

function success(notifications: Notification[], message: string): NotificationsState | NotificationState {
  return push(notifications, { message, type: 'success' });
}

function error(notifications: Notification[], message: string): NotificationsState | NotificationState {
  return push(notifications, { message, type: 'error' });
}

function push(notifications: Notification[], notification: Notification): NotificationsState | NotificationState {
  if (notifications.length === 0) {
    return { current: notification, notifications: [notification] };
  }
  return { notifications: [...notifications, notification] };
}

function patchNotifications(prev: Notification, notifications: Notification[]): NotificationState {
  const index = notifications.indexOf(prev);
  notifications.splice(index, 1);
  return { current: notifications[0], notifications };
}

export const notify = { success, error };
