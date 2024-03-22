import { Component, DestroyRef, computed, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatFormField, MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { EMPTY, catchError, filter, switchMap, tap } from 'rxjs';
import { AuthService } from '../../../shared/services/auth.service';
import { FileService } from '../../../shared/services/file.service';
import { UserLogic } from '../../models/user.model';
import { UsersService } from '../../services/users.service';
import { UserStore } from '../../store/user.store';
import { ChangePasswordDialogComponent } from './change-password-dialog/change-password-dialog.component';
import { DeleteAccountDialogComponent } from './delete-account-dialog/delete-account-dialog.component';
import { DeleteImageDialogComponent } from './delete-image-dialog/delete-image-dialog.component';
import { UpdateUserFormComponent } from './update-user-form/update-user-form.component';

@Component({
  selector: 'cc-account-view',
  standalone: true,
  imports: [
    MatCardModule,
    MatButtonModule,
    MatDialogModule,
    MatInputModule,
    MatIconModule,
    MatFormField,
    MatSnackBarModule,
    UpdateUserFormComponent
  ],
  templateUrl: './account-view.component.html',
  styleUrl: './account-view.component.scss'
})
export class AccountViewComponent {
  readonly userStore = inject(UserStore);

  currentUser = computed(() => {
    this.isEditingUserDetails = false;
    const user = this.userStore.currentUser();
    return Object.assign(user, { hasImage: () => UserLogic.hasImage(user) });
  });

  isEditingUserDetails = false;

  constructor(
    private dialog: MatDialog,
    private snackbar: MatSnackBar,
    private destroyRef: DestroyRef,
    private authService: AuthService,
    private userService: UsersService,
    private fileService: FileService
  ) {}

  uploadImage(): void {
    this.fileService
      .selectImage()
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        tap(file => this.userStore.uploadProfileImage(file)),
        tap({ error: () => this.displayError($localize`File is too large`) }),
        catchError(() => EMPTY)
      )
      .subscribe();
  }

  deleteImage(): void {
    this.dialog
      .open(DeleteImageDialogComponent)
      .afterClosed()
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        filter(Boolean),
        tap(() => this.userStore.removeProfileImage())
      )
      .subscribe();
  }

  updateUserDetails(form: UpdateUserFormComponent): void {
    form.disabled = true;
    this.userStore.update({ ...form.value, username: this.currentUser().username });
  }

  toggleUserDetailsEdition(): void {
    this.isEditingUserDetails = !this.isEditingUserDetails;
  }

  changePassword(): void {
    this.dialog
      .open(ChangePasswordDialogComponent)
      .afterClosed()
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        filter(Boolean),
        switchMap(passwordChange => this.userService.updatePassword(passwordChange)),
        catchError(() => EMPTY.pipe(tap({ complete: () => this.displayError($localize`Changing password failed`) }))),
        tap(() => this.displaySuccess($localize`Password changed`))
      )
      .subscribe();
  }

  deleteAccount(): void {
    const { username } = this.currentUser();

    this.dialog
      .open(DeleteAccountDialogComponent)
      .afterClosed()
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        filter(Boolean),
        switchMap(password => this.authService.login({ username, password }, false)),
        switchMap(() => this.userService.delete(username)),
        catchError(() => EMPTY.pipe(tap({ complete: () => this.displayError($localize`Deleting account failed`) }))),
        tap(() => this.authService.logout())
      )
      .subscribe();
  }

  private displayError(message: string): void {
    this.snackbar.open(message, $localize`ERROR`, { duration: 5000 });
  }

  private displaySuccess(message: string): void {
    this.snackbar.open(message, $localize`SUCCESS`, { duration: 5000 });
  }
}
