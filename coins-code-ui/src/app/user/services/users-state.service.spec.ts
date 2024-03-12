import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { GetUserResponseBody } from '../models/http/user.model';
import { UsersStateService } from './users-state.service';
import { UsersService } from './users.service';

describe('UsersStateService', () => {
  let usersStateService: UsersStateService;
  let usersServiceMock: jasmine.SpyObj<UsersService>;

  beforeEach(() => {
    usersServiceMock = jasmine.createSpyObj<UsersService>(['findByUsername', 'getProfileImageUrl']);

    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [UsersStateService, { provide: UsersService, useValue: usersServiceMock }]
    });

    usersStateService = TestBed.inject(UsersStateService);
  });

  it('Should fetch user only once and then use cached user', () => {
    // GIVEN
    const userResponseMock = { username: 'username' } as GetUserResponseBody;
    usersServiceMock.findByUsername.and.returnValue(of(userResponseMock));

    // WHEN
    usersStateService.loadUser('username');

    //THEN
    usersStateService.user$.subscribe(() => {
      usersStateService.user$.subscribe(() => {
        expect(usersServiceMock.findByUsername).toHaveBeenCalledTimes(1);
      });
    });
  });

  it('Should return null if username has been cleared', () => {
    // GIVEN
    usersStateService.loadUser('username');

    // WHEN
    usersStateService.clearUser();

    //THEN
    usersStateService.user$.subscribe(user => expect(user).toBeNull());
  });

  it('Should emit users changes with fetching only non nulls', () => {
    // GIVEN
    const loadedUsers: (string | null)[] = [];
    usersServiceMock.findByUsername.and.callFake(username => of({ username } as GetUserResponseBody));

    // WHEN
    usersStateService.user$.subscribe(user => loadedUsers.push(user?.username ?? null));
    usersStateService.loadUser('username1');
    usersStateService.loadUser('username2');
    usersStateService.clearUser();

    // THEN
    expect(loadedUsers).toEqual([null, 'username1', 'username2', null]);
    expect(usersServiceMock.findByUsername).toHaveBeenCalledTimes(2);
  });
});
