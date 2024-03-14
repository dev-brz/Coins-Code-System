import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { EMPTY, of } from 'rxjs';
import { LOGIN_ROUTE } from '../../shared/configs/routes.config';
import { CURRENT_USER_KEY } from '../../shared/configs/storage.config';
import { UsersService } from '../../user/services/users.service';
import { GetUserResponseBody } from '../models/http/user.model';
import { UserStore } from './user.store';

describe('UserStore', () => {
  let usersServiceMock: jasmine.SpyObj<UsersService>;
  let routerMock: jasmine.SpyObj<Router>;
  let userStore: InstanceType<typeof UserStore>;

  beforeEach(() => {
    routerMock = jasmine.createSpyObj<Router>(['navigateByUrl']);
    usersServiceMock = jasmine.createSpyObj<UsersService>({
      save: of(void 0),
      updateUserProfileImage: of(void 0),
      findByUsername: EMPTY,
      getProfileImageUrl: of('url')
    });

    TestBed.configureTestingModule({
      providers: [
        UserStore,
        { provide: UsersService, useValue: usersServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    });

    userStore = TestBed.inject(UserStore) as unknown as InstanceType<typeof UserStore>;
  });

  afterEach(() => localStorage.removeItem(CURRENT_USER_KEY));

  it('should store user in the localStorage when loading current user', () => {
    // GIVEN
    const mockForm = { username: 'useranme', password: 'password' };
    usersServiceMock.findByUsername.and.callFake(username => of({ username } as GetUserResponseBody));

    // WHEN
    userStore.loadCurrent(mockForm);

    // THEN
    const storedUser = JSON.parse(localStorage.getItem(CURRENT_USER_KEY)!);
    expect(storedUser).toEqual(mockForm);
  });

  it('should redirect to login page when logging out', () => {
    // GIVEN
    localStorage.setItem('currentUser', 'any');

    // WHEN
    userStore.disposeCurrent();

    // THEN
    expect(localStorage.getItem(CURRENT_USER_KEY)).toBeFalsy();
    expect(routerMock.navigateByUrl).toHaveBeenCalledWith(`/${LOGIN_ROUTE}`);
  });
});
