import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { EMPTY, of, throwError } from 'rxjs';
import { DEFAULT_PROFILE_IMAGE_PATH } from '../../shared/configs/assets.config';
import { LOGIN_ROUTE } from '../../shared/configs/routes.config';
import { CURRENT_USER_KEY } from '../../shared/configs/storage.config';
import { UsersService } from '../../user/services/users.service';
import { GetUserResponseBody, SaveUserRequestBody } from '../models/http/user.model';
import { UserStore } from './user.store';
import { ErrorService } from '../../shared/services/error.service';

describe('UserStore', () => {
  let errorServiceMock: jasmine.SpyObj<ErrorService>;
  let usersServiceMock: jasmine.SpyObj<UsersService>;
  let routerMock: jasmine.SpyObj<Router>;
  let userStore: InstanceType<typeof UserStore>;

  beforeEach(() => {
    routerMock = jasmine.createSpyObj<Router>(['navigateByUrl']);
    errorServiceMock = jasmine.createSpyObj<ErrorService>(['dispatchError']);
    usersServiceMock = jasmine.createSpyObj<UsersService>({
      save: of(void 0),
      findByUsername: EMPTY,
      getProfileImageUrl: of('url'),
      update: of(void 0),
      updateUserProfileImage: of(void 0),
      deleteUserProfileImage: of(void 0)
    });

    TestBed.configureTestingModule({
      providers: [
        UserStore,
        { provide: ErrorService, useValue: errorServiceMock },
        { provide: UsersService, useValue: usersServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    });

    userStore = TestBed.inject(UserStore) as unknown as InstanceType<typeof UserStore>;
  });

  afterEach(() => localStorage.removeItem(CURRENT_USER_KEY));

  it('bootstrapCurrent should properly bootstrap user', () => {
    // GIVEN
    const mockForm = { username: 'username', password: 'password' };
    usersServiceMock.findByUsername.and.callFake(username => of({ username } as GetUserResponseBody));
    localStorage.setItem(CURRENT_USER_KEY, JSON.stringify(mockForm));

    // WHEN
    userStore.bootstrapCurrent();
    const storageUser = JSON.parse(localStorage.getItem(CURRENT_USER_KEY)!);
    const currentUser = userStore.currentUser();

    // THEN
    expect(storageUser).toEqual(mockForm);
    expect(currentUser.username).toEqual(mockForm.username);
  });

  it('bootstrapCurrent should handle error occured when fetching user fails', () => {
    // GIVEN
    localStorage.setItem(CURRENT_USER_KEY, JSON.stringify({ username: 'test', password: 'test' }));
    usersServiceMock.findByUsername.and.returnValue(throwError(() => {}));

    // WHEN
    userStore.bootstrapCurrent();

    // THEN
    expect(routerMock.navigateByUrl).toHaveBeenCalledWith(`/${LOGIN_ROUTE}`);
    expect(localStorage.getItem(CURRENT_USER_KEY)).toBeNull();
  });

  it('loadCurrent should properly load user', () => {
    // GIVEN
    const mockForm = { username: 'username', password: 'password' };
    usersServiceMock.findByUsername.and.callFake(username => of({ username } as GetUserResponseBody));

    // WHEN
    userStore.loadCurrent(mockForm);
    const storageUser = JSON.parse(localStorage.getItem(CURRENT_USER_KEY)!);
    const currentUser = userStore.currentUser();

    // THEN
    expect(storageUser).toEqual(mockForm);
    expect(currentUser.username).toEqual(mockForm.username);
  });

  it('loadCurrent should handle error occured when fetching user fails', () => {
    // GIVEN
    const mockForm = { username: 'test', password: 'test' };
    usersServiceMock.findByUsername.and.returnValue(throwError(() => {}));

    // WHEN
    userStore.loadCurrent(mockForm);

    // THEN
    expect(routerMock.navigateByUrl).toHaveBeenCalledWith(`/${LOGIN_ROUTE}`);
    expect(localStorage.getItem(CURRENT_USER_KEY)).toBeNull();
  });

  it('disposeCurrent should redirect to login page', () => {
    // GIVEN
    localStorage.setItem('currentUser', 'any');

    // WHEN
    userStore.disposeCurrent();

    // THEN
    expect(routerMock.navigateByUrl).toHaveBeenCalledWith(`/${LOGIN_ROUTE}`);
    expect(localStorage.getItem(CURRENT_USER_KEY)).toBeNull();
  });

  it('saveNew should call proper methods and redirect', () => {
    // GIVEN
    const mockUser = { username: 'test', profileImage: new File([], '') };

    // WHEN
    userStore.saveNew(mockUser as SaveUserRequestBody);

    // THEN
    expect(usersServiceMock.save).toHaveBeenCalledWith(mockUser as SaveUserRequestBody);
    expect(usersServiceMock.updateUserProfileImage).toHaveBeenCalledWith(mockUser.profileImage);
    expect(routerMock.navigateByUrl).toHaveBeenCalledWith(`/${LOGIN_ROUTE}`);
    expect(localStorage.getItem(CURRENT_USER_KEY)).toBeNull();
  });

  it('saveNew should dispatch error when saving user fails', () => {
    // GIVEN
    const mockUser = { username: 'test', profileImage: new File([], '') };
    usersServiceMock.save.and.returnValue(throwError(() => {}));

    // WHEN
    userStore.saveNew(mockUser as SaveUserRequestBody);

    // THEN
    expect(errorServiceMock.dispatchError).toHaveBeenCalledWith('USER-SAVE');
  });

  it('update should properly patch state', () => {
    // GIVEN
    const initialUser = userStore.currentUser();
    const update = { username: 'username', email: 'random@mail.com' };

    // WHEN
    userStore.update(update);

    // THEN
    expect(userStore.currentUser().email).toEqual(update.email);
    expect(userStore.currentUser().firstName).toEqual(initialUser.firstName);
  });

  it('update should dispatch error when user update fails', () => {
    // GIVEN
    const update = { username: 'username', email: 'random@mail.com' };
    usersServiceMock.update.and.returnValue(throwError(() => {}));

    // WHEN
    userStore.update(update);

    // THEN
    expect(errorServiceMock.dispatchError).toHaveBeenCalledWith('USER-UPDATE');
  });

  it('updateProfileImage should properly update user profile image', () => {
    // GIVEN
    const initialUserImage = userStore.currentUser().imageUrl;

    // WHEN
    userStore.uploadProfileImage(new File([], ''));

    // THEN
    expect(usersServiceMock.updateUserProfileImage).toHaveBeenCalled();
    expect(userStore.currentUser().imageUrl).not.toEqual(initialUserImage);
  });

  it('updateProfileImage should dispatch error when user update fails', () => {
    // GIVEN
    usersServiceMock.updateUserProfileImage.and.returnValue(throwError(() => {}));

    // WHEN
    userStore.uploadProfileImage(new File([], ''));

    // THEN
    expect(errorServiceMock.dispatchError).toHaveBeenCalledWith('USER-UPLOAD-IMAGE');
  });

  it('removeProfileImage should properly remove user profile image', () => {
    // GIVEN
    userStore.uploadProfileImage(new File([], ''));

    // WHEN
    userStore.removeProfileImage();

    // THEN
    expect(usersServiceMock.deleteUserProfileImage).toHaveBeenCalled();
    expect(userStore.currentUser().imageUrl).toEqual(DEFAULT_PROFILE_IMAGE_PATH);
  });

  it('removeProfileImage should dispatch error when user update fails', () => {
    // GIVEN
    usersServiceMock.deleteUserProfileImage.and.returnValue(throwError(() => {}));

    // WHEN
    userStore.removeProfileImage();

    // THEN
    expect(errorServiceMock.dispatchError).toHaveBeenCalledWith('USER-REMOVE-IMAGE');
  });
});
