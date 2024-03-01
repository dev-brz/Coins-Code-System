import { AbstractControl, ValidatorFn } from '@angular/forms';
import { CustomValidators } from '.';

describe('PasswordRepeatValidator', () => {
  let controlMock: jasmine.SpyObj<AbstractControl>;
  let validatorFn: ValidatorFn;

  beforeEach(() => {
    controlMock = jasmine.createSpyObj<AbstractControl>(['get']);
    validatorFn = CustomValidators.passwordRepeat('firstField', 'secondField');
  });

  it('should return proper error when passwords are not equal', () => {
    // GIVEN
    controlMock.get.withArgs('firstField').and.returnValue({ value: 'password' } as AbstractControl);
    controlMock.get.withArgs('secondField').and.returnValue({ value: 'other' } as AbstractControl);

    // WHEN
    const errors = validatorFn(controlMock);

    // THEN
    expect(errors).not.toBeNull();
    expect(errors!['passwordRepeat']).toBeTruthy();
  });

  it('should return null when passwords are equal', () => {
    // GIVEN
    controlMock.get.and.returnValue({ value: 'the same value' } as AbstractControl);

    // WHEN
    const errors = validatorFn(controlMock);

    // THEN
    expect(errors).toBeNull();
  });
});
