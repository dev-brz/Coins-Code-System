import { fakeAsync, tick } from '@angular/core/testing';
import { AbstractControl, ValidationErrors } from '@angular/forms';
import { Observable, of } from 'rxjs';

import { ExistFn, FIELD_TAKEN_DEBOUNCE_TIME_MS, _fieldTaken } from './field-taken.validator';

describe('FieldTakenValidator', () => {
  const TEST_FIELD = 'field';
  let existFnSpy: jasmine.Spy<ExistFn<string>>;
  let controlMock: jasmine.SpyObj<AbstractControl>;
  let validatorFn: (control: AbstractControl) => Observable<ValidationErrors | null>;

  beforeEach(() => {
    controlMock = jasmine.createSpyObj<AbstractControl>([], { value: 'testValue' });
    existFnSpy = jasmine.createSpy<ExistFn<string>>();
    validatorFn = _fieldTaken(existFnSpy, TEST_FIELD) as typeof validatorFn;
  });

  it('should return proper error when field is taken', fakeAsync(() => {
    // GIVEN
    existFnSpy.and.returnValue(of(true));

    // WHEN
    validatorFn(controlMock).subscribe(errors => {
      // THEN
      expect(errors).not.toBeNull();
      expect(errors![`${TEST_FIELD}Taken`]).toBeTruthy();
    });

    tick(FIELD_TAKEN_DEBOUNCE_TIME_MS);
  }));

  it('should return null when field is not taken', fakeAsync(() => {
    // GIVEN
    existFnSpy.and.returnValue(of(false));

    // WHEN
    validatorFn(controlMock).subscribe(errors => {
      //THEN
      expect(errors).toBeNull();
    });

    tick(FIELD_TAKEN_DEBOUNCE_TIME_MS);
  }));
});
