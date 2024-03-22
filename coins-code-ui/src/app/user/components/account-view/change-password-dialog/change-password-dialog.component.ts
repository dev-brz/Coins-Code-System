import { Component } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormField, MatInputModule } from '@angular/material/input';
import { getErrorMessage } from '../../../../shared/utils/get-error-messages';
import { UpdatePasswordRequestBody } from '../../../models/http/user.model';
import { CustomValidators, ValidatorUtils } from '../../../validators';

type PasswordChange = UpdatePasswordRequestBody;

@Component({
  standalone: true,
  imports: [MatButtonModule, MatDialogModule, MatInputModule, MatFormField, ReactiveFormsModule],
  templateUrl: './change-password-dialog.component.html',
  styleUrl: './change-password-dialog.component.scss'
})
export class ChangePasswordDialogComponent {
  readonly repeatPasswordErrorStateMatcher: ErrorStateMatcher = new ValidatorUtils.PasswordRepeatErrorStateMatcher();

  readonly getErrorMessage = getErrorMessage;

  readonly getPasswordRepeatErrorMessage = ValidatorUtils.getPasswordRepeatErrorMessage;

  readonly form = this.fb.group(
    {
      oldPassword: this.fb.control('', [Validators.required]),
      newPassword: this.fb.control('', [Validators.required, Validators.minLength(8)]),
      newPasswordRepeat: this.fb.control('', [Validators.required])
    },
    { validators: [CustomValidators.passwordRepeat('newPassword', 'newPasswordRepeat')] }
  );

  constructor(private fb: NonNullableFormBuilder) {}

  get passwordChange(): PasswordChange {
    return this.form.getRawValue();
  }
}
