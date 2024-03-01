import { TestBed } from '@angular/core/testing';
import { UsersService } from './users.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpStatusCode } from '@angular/common/http';
import { USERS_USER_URL } from '../../shared/configs/api.config';

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

  it('Exists by username should return false if response is 404', () => {
    // GIVEN WHEN
    usersService.existsByUsername('any').subscribe(exists => {
      // THEN
      expect(exists).toBeFalse();
    });
    httpController
      .expectOne(USERS_USER_URL.replace('?1', 'any'))
      .error(new ProgressEvent(''), { status: HttpStatusCode.NotFound });
  });

  it('Exists by username should return true if response is 200', () => {
    // GIVEN WHEN
    usersService.existsByUsername('any').subscribe(exists => {
      // THEN
      expect(exists).toBeTrue();
    });
    httpController
      .expectOne(USERS_USER_URL.replace('?1', 'any'))
      .flush({}, { status: HttpStatusCode.Ok, statusText: '' });
  });
});
