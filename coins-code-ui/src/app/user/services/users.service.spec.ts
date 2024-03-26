import { HttpStatusCode } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { USERS_URL } from '../../shared/configs/api.config';
import { UsersService } from './users.service';

describe('UsersService', () => {
  let usersService: UsersService;
  let httpController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UsersService]
    });
    usersService = TestBed.inject(UsersService);
    httpController = TestBed.inject(HttpTestingController);
  });

  it('Exists should return false if response is 404', () => {
    // GIVEN WHEN
    usersService.existsBy('username', 'any').subscribe(exists => {
      // THEN
      expect(exists).toBeFalse();
    });
    httpController
      .expectOne(req => req.url === USERS_URL)
      .error(new ProgressEvent(''), { status: HttpStatusCode.NotFound });
  });

  it('Exists should return true if response is 200', () => {
    // GIVEN WHEN
    usersService.existsBy('username', 'any').subscribe(exists => {
      // THEN
      expect(exists).toBeTrue();
    });
    httpController.expectOne(req => req.url === USERS_URL).flush({}, { status: HttpStatusCode.Ok, statusText: '' });
  });
});
