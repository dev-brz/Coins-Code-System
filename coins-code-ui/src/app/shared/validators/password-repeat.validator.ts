import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { getErrorMessage } from '../utils/get-error-messages';

export function _passwordRepeat(
  passwordFieldName = 'password',
  repeatPasswordFieldName = 'passwordRepeat'
): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const passwordControl = control.get(passwordFieldName);
    const repeatPasswordControl = control.get(repeatPasswordFieldName);
    return passwordControl?.value !== repeatPasswordControl?.value ? { passwordRepeat: true } : null;
  };
}

export class _PasswordRepeatErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: AbstractControl<unknown, unknown> | null): boolean {
    return !!((control?.touched && control?.invalid) || control?.parent?.hasError('passwordRepeat'));
  }
}

export function _getPasswordRepeatErrorMessage(form: FormGroup, formControlName: string): string | null {
  const hasOwnErrors = !!form.controls[formControlName].errors;
  return getErrorMessage(form, hasOwnErrors ? formControlName : '');
}
