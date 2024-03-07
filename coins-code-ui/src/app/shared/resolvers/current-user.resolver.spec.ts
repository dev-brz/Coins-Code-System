import { TestBed } from '@angular/core/testing';
import { AuthService } from '../services/auth.service';
import { currentUserResolver } from './current-user.resolver';

describe('CurrentUserResolver', () => {
  let authServiceMock: jasmine.SpyObj<AuthService>;

  beforeEach(() => {
    authServiceMock = jasmine.createSpyObj<AuthService>(['logout', 'getCurrentUser']);

    TestBed.configureTestingModule({
      providers: [{ provide: AuthService, useValue: authServiceMock }]
    });
  });

  fit('should logout if user is not present', done =>
    TestBed.runInInjectionContext(() => {
      // GIVEN
      authServiceMock.getCurrentUser.and.returnValue(null);

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
