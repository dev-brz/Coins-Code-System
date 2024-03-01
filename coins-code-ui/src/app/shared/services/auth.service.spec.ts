import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from './auth.service';
import { HttpRequest } from '@angular/common/http';
import { LOGIN_URL, NO_AUTH_URLS } from '../configs/api.config';
import { LoginForm } from '../models/user.model';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should log in successfully', () => {
    const mockForm: LoginForm = { username: 'testuser', password: 'testpass' };

    service.login(mockForm).subscribe(() => {
      const storedUser = JSON.parse(localStorage.getItem('currentUser') ?? '');
      expect(storedUser).toEqual(mockForm);
    });

    const req = httpMock.expectOne(LOGIN_URL);
    expect(req.request.method).toBe('POST');
    req.flush({});

    expect(localStorage.getItem('currentUser')).toBeTruthy();
  });

  it('should log out and navigate to login page', () => {
    spyOn(service['router'], 'navigate');

    const result = service.logout();

    expect(result).toBeTrue();
    expect(localStorage.getItem('currentUser')).toBeFalsy();
    expect(service['router'].navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should allow access if user is logged in', () => {
    localStorage.setItem('currentUser', JSON.stringify({ username: 'testuser', password: 'testpass' }));
    const canActivateResult = service.canActivate();
    expect(canActivateResult).toBeTrue();
  });

  it('should log out and deny access if user is not logged in', () => {
    spyOn(service, 'logout');
    localStorage.removeItem('currentUser');
    const canActivateResult = service.canActivate();
    expect(canActivateResult).toBeFalse();
    expect(service.logout).toHaveBeenCalled();
  });

  it('should add authorization header for authenticated requests', () => {
    const mockReq = new HttpRequest('GET', '/api/data');
    const mockUser = { username: 'testuser', password: 'testpass' };
    const mockNext = jasmine.createSpy('next');
    localStorage.setItem('currentUser', JSON.stringify(mockUser));

    service.interceptRequsest(mockReq, mockNext);

    const expectedReq = new HttpRequest('GET', '/api/data').clone({
      setHeaders: {
        Authorization: `Basic ${mockUser.username}:${mockUser.password}`
      }
    });

    expect(mockNext).toHaveBeenCalledOnceWith(expectedReq);
  });

  it('should skip authorization header for non-authenticated requests', () => {
    const mockReq = new HttpRequest('GET', NO_AUTH_URLS[0]);
    const next = jasmine.createSpy('next');
    service.interceptRequsest(mockReq, next);

    expect(next).toHaveBeenCalledOnceWith(mockReq);
  });
});
