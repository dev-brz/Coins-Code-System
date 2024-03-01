import { AbstractControl, AsyncValidatorFn, ValidationErrors } from '@angular/forms';
import { Observable, map } from 'rxjs';
import { UsersService } from '../services/users.service';

export function _usernameTaken(usersService: UsersService): AsyncValidatorFn {
  return (control: AbstractControl): Observable<ValidationErrors | null> => {
    return usersService
      .existsByUsername(control.value)
      .pipe(map(exists => (exists ? { usernameTaken: { username: control.value } } : null)));
  };
}
