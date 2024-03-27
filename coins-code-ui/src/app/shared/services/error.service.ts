import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MonoTypeOperatorFunction, Subject, filter } from 'rxjs';

export type ErrorType = 'USER-SAVE' | 'USER-UPDATE' | 'USER-UPLOAD-IMAGE' | 'USER-REMOVE-IMAGE';

// TODO - improve [#70]
@Injectable({
  providedIn: 'root'
})
export class ErrorService {
  private lastOcurredError: ErrorType | null = null;

  private readonly errorsSubject: Subject<ErrorType> = new Subject<ErrorType>();

  readonly errors$ = this.errorsSubject.asObservable();

  get lastError(): ErrorType | null {
    return this.lastOcurredError;
  }

  constructor(private snackbarService: MatSnackBar) {}

  dispatchError(error: ErrorType): void {
    this.snackbarService.open($localize`An unknown error occurred`, $localize`ERROR`, { duration: 3000 });
    this.lastOcurredError = error;
    this.errorsSubject.next(error);
  }
}

export function takeError(type: ErrorType): MonoTypeOperatorFunction<ErrorType> {
  return filter(input => input === type);
}
