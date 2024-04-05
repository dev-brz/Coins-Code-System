import { FormGroup } from '@angular/forms';

export function getErrorMessage(form: FormGroup, formControlName: string): string | null {
  const { errors } = formControlName ? form.controls[formControlName] : form;
  if (errors) {
    if (errors['required']) {
      return $localize`Field is required`;
    }
    if (errors['minlength']) {
      const { requiredLength } = errors['minlength'];
      return $localize`Field must contain at least ${requiredLength} characters`;
    }
    if (errors['usernameTaken']) {
      return $localize`Username ${errors['usernameTaken']['username']} is taken`;
    }
    if (errors['emailTaken']) {
      return $localize`Email ${errors['emailTaken']['email']} is taken`;
    }
    if (errors['phoneNumberTaken']) {
      return $localize`Phone number ${errors['phoneNumberTaken']['phoneNumber']} is taken`;
    }
    if (errors['email']) {
      return $localize`Field must be a valid e-mail address`;
    }
    if (errors['pattern']) {
      return $localize`Field must match the valid pattern`;
    }
    if (errors['maxContentSize']) {
      return $localize`File is too large`;
    }
    if (errors['passwordRepeat']) {
      return $localize`Passwords must be equal`;
    }
    if (errors['nameTaken']) {
      return $localize`${errors['nameTaken']['name']} already exists`;
    }
  }
  return null;
}
