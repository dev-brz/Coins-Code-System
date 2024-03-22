import { HttpRequest } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed, fakeAsync, flush } from '@angular/core/testing';
import { signalStore, withMethods } from '@ngrx/signals';
import { UserStore } from '../../user/store/user.store';
import { LOGIN_URL, NO_AUTH_URLS } from '../configs/api.config';
import { CURRENT_USER_KEY } from '../configs/storage.config';
import { LoginForm } from '../models/user.model';
import { AuthService } from './auth.service';

const UserStoreMock = signalStore(
  withMethods(() => ({ loadCurrent: jasmine.createSpy(), disposeCurrent: jasmine.createSpy() }))
);

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let userStoreMock: InstanceType<typeof UserStoreMock>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService, { provide: UserStore, useClass: UserStoreMock }]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    userStoreMock = TestBed.inject(UserStore) as unknown as InstanceType<typeof UserStoreMock>;
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.removeItem(CURRENT_USER_KEY);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should log in successfully', fakeAsync(() => {
    // GIVEN
    const mockForm: LoginForm = { username: 'testuser', password: 'testpass' };

    // WHEN
    service.login(mockForm).subscribe();
    const req = httpMock.expectOne(LOGIN_URL);
    req.flush({});
    flush();

    // THEN
    expect(req.request.method).toBe('POST');
    expect(userStoreMock.loadCurrent).toHaveBeenCalledTimes(1);
    expect(userStoreMock.loadCurrent).toHaveBeenCalledWith(mockForm);
  }));

  it('should log out succcesfully', () => {
    // WHEN
    service.logout();

    // THEN
    expect(userStoreMock.disposeCurrent).toHaveBeenCalledTimes(1);
  });

  it('should allow access if user is logged in', () => {
    // GIVEN
    localStorage.setItem(CURRENT_USER_KEY, JSON.stringify({ username: 'testuser', password: 'testpass' }));

    // WHEN
    const canActivateResult = service.canActivate();

    // THEN
    expect(canActivateResult).toBeTrue();
  });

  it('should log out and deny access if user is not logged in', () => {
    // GIVEN
    spyOn(service, 'logout');

    // WHEN
    const canActivateResult = service.canActivate();

    // THEN
    expect(canActivateResult).toBeFalse();
    expect(service.logout).toHaveBeenCalled();
  });

  it('should add authorization header for authenticated requests', () => {
    // GIVEN
    const mockReq = new HttpRequest('GET', '/api/data');
    const mockUser = { username: 'testuser', password: 'testpass' };
    const expectedReq = mockReq.clone({
      setHeaders: { Authorization: `Basic ${btoa(`${mockUser.username}:${mockUser.password}`)}` }
    });
    const mockNext = jasmine.createSpy('next');
    localStorage.setItem(CURRENT_USER_KEY, JSON.stringify(mockUser));

    // WHEN
    service.interceptRequsest(mockReq, mockNext);

    // THEN
    expect(mockNext).toHaveBeenCalledOnceWith(expectedReq);
  });

  it('should skip authorization header for non-authenticated requests', () => {
    // GIVEN
    const mockReq = new HttpRequest('GET', NO_AUTH_URLS[0]);
    const next = jasmine.createSpy('next');

    // WHEN
    service.interceptRequsest(mockReq, next);

    // THEN
    expect(next).toHaveBeenCalledOnceWith(mockReq);
  });
});
