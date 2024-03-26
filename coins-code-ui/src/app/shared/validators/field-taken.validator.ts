import { AbstractControl, AsyncValidatorFn, ValidationErrors } from '@angular/forms';
import { Observable, delay, map, of, switchMap } from 'rxjs';

export type ExistFn<T extends string> = (field: T, value: string) => Observable<boolean>;
export const FIELD_TAKEN_DEBOUNCE_TIME_MS = 500;

export function _fieldTaken<T extends string>(
  exist: ExistFn<T>,
  field: T,
  ignoredValues: string[] = []
): AsyncValidatorFn {
  return (control: AbstractControl): Observable<ValidationErrors | null> => {
    if (ignoredValues.includes(control.value)) {
      return of(null);
    } else {
      return of(control.value).pipe(
        delay(FIELD_TAKEN_DEBOUNCE_TIME_MS),
        switchMap(() => exist(field, control.value)),
        map(exists => (exists ? { [`${field}Taken`]: { [field]: control.value } } : null))
      );
    }
  };
}
