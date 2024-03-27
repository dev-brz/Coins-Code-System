import { rxMethod as ngrxRxMethod } from '@ngrx/signals/rxjs-interop';
import { exhaustMap, of } from 'rxjs';

type RxMethodFn<T> = typeof ngrxRxMethod<T>;

export function rxMethod<T>(...[generator, config]: Parameters<RxMethodFn<T>>): ReturnType<RxMethodFn<T>> {
  return ngrxRxMethod<T>(
    exhaustMap(source => of(source).pipe(generator)),
    config
  );
}
