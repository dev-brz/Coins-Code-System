import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

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
