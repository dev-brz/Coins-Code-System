import { TestBed } from '@angular/core/testing';
import { throwError } from 'rxjs';
import { UsersStateService } from '../../user/services/users-state.service';
import { AuthService } from '../services/auth.service';
import { reSpyOnProperty } from '../utils/test-utils';
import { currentUserResolver } from './current-user.resolver';

describe('CurrentUserResolver', () => {
  let authServiceMock: jasmine.SpyObj<AuthService>;
  let userStateServiceMock: jasmine.SpyObj<UsersStateService>;

  beforeEach(() => {
    authServiceMock = jasmine.createSpyObj<AuthService>(['logout']);
    userStateServiceMock = jasmine.createSpyObj<UsersStateService>([], ['user$']);

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: UsersStateService, useValue: userStateServiceMock }
      ]
    });
  });

  it('should logout on error', done =>
    TestBed.runInInjectionContext(() => {
      // GIVEN
      reSpyOnProperty(userStateServiceMock, 'user$').and.returnValue(throwError(() => new Error()));

      // WHEN
      currentUserResolver().subscribe({
        next: () => fail(),
        complete: () => {
          // THEN
          expect(authServiceMock.logout).toHaveBeenCalledTimes(1);
          done();
        }
      });
    }));
});
