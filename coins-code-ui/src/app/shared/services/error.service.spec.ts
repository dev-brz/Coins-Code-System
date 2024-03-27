import { TestBed } from '@angular/core/testing';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { TestScheduler } from 'rxjs/testing';
import { ErrorService, ErrorType, takeError } from './error.service';

describe('ErrorService', () => {
  let snackbarServiceMock: jasmine.SpyObj<MatSnackBar>;
  let errorService: ErrorService;

  beforeEach(() => {
    snackbarServiceMock = jasmine.createSpyObj<MatSnackBar>(['open']);

    TestBed.configureTestingModule({
      providers: [ErrorService, { provide: MatSnackBar, useValue: snackbarServiceMock }]
    }).overrideModule(MatSnackBarModule, { remove: { providers: [MatSnackBar] } });

    errorService = TestBed.inject(ErrorService);
  });

  it('should dispatch error', () => {
    // GIVEN
    const error = 'USER-SAVE';

    // WHEN
    errorService.errors$.subscribe(emittedError => {
      // THEN
      expect(snackbarServiceMock.open).toHaveBeenCalled();
      expect(errorService.lastError).toEqual(error);
      expect(emittedError).toEqual(error);
    });
    errorService.dispatchError(error);
  });

  fit('takeError should behave correctly', () => {
    // GIVEN
    const scheduler = new TestScheduler((actual, expected) => expect(actual).toEqual(expected));
    const values = { a: 'USER-SAVE', b: 'USER-UPDATE' } as const;
    const observableMarble = '  -a-a-b-|';
    const expectedMarble = '    -a-a---|';
    const subscriptionMarble = '^------!';

    // WHEN
    scheduler.run(({ cold, expectObservable, expectSubscriptions }) => {
      const observable = cold<ErrorType>(observableMarble, values);
      const pipedObservable = observable.pipe(takeError('USER-SAVE'));

      // THEN
      expectObservable(pipedObservable).toBe(expectedMarble, values);
      expectSubscriptions(observable.subscriptions).toBe(subscriptionMarble);
    });
  });
});
