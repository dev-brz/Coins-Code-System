import { Component, inject } from '@angular/core';
import { MAT_SNACK_BAR_DATA, MatSnackBarRef } from '@angular/material/snack-bar';
import { Notification } from '../models/notification.model';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';

@Component({
  selector: 'cc-notification',
  standalone: true,
  imports: [MatIcon, MatIconButton],
  template: `
    <!-- Todo #81-->
    <div class="notification">
      <span>{{ data.message }}</span>
      <span>
        <button mat-icon-button (click)="snackBarRef.dismiss()">
          <mat-icon>close</mat-icon>
        </button>
      </span>
    </div>
  `
})
export class NotificationComponent {
  data: Notification = inject(MAT_SNACK_BAR_DATA);
  snackBarRef = inject(MatSnackBarRef);
}
