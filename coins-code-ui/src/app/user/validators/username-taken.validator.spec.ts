import { Observable, of } from 'rxjs';
import { CustomValidators } from '.';
import { UsersService } from '../services/users.service';
import { AbstractControl, ValidationErrors } from '@angular/forms';

describe('UsernameTakenValidator', () => {
  let userServiceMock: jasmine.SpyObj<UsersService>;
  let controlMock: jasmine.SpyObj<AbstractControl>;
  let validatorFn: (control: AbstractControl) => Observable<ValidationErrors | null>;

  beforeEach(() => {
    userServiceMock = jasmine.createSpyObj<UsersService>(['existsByUsername']);
    controlMock = jasmine.createSpyObj<AbstractControl>([], ['value']);
    validatorFn = CustomValidators.usernameTaken(userServiceMock) as typeof validatorFn;
  });

  it('should return proper error when username is taken', () => {
    // GIVEN
    userServiceMock.existsByUsername.and.returnValue(of(true));

    // WHEN
    validatorFn(controlMock).subscribe(errors => {
      // THEN
      expect(errors).not.toBeNull();
      expect(errors!['usernameTaken']).toBeTruthy();
    });
  });

  it('should return null when username is not taken', () => {
    // GIVEN
    userServiceMock.existsByUsername.and.returnValue(of(false));

    // WHEN
    validatorFn(controlMock).subscribe(errors => {
      //THEN
      expect(errors).toBeNull();
    });
  });
});
