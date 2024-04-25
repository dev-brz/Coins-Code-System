import { PartialStateUpdater, patchState, signalStore, type, withHooks, withMethods, withState } from '@ngrx/signals';
import { withDevtools } from '@angular-architects/ngrx-toolkit';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { inject } from '@angular/core';
import { forkJoin, map, Observable, switchMap, tap } from 'rxjs';
import { Coin, CoinCreationForm, CoinTopUpForm } from '../models/coin.model';
import { UserStore } from '../../user/store/user.store';
import { tapResponse } from '@ngrx/operators';
import { CoinHttpService } from '../services/http/coin.http.service';
import { addEntity, removeAllEntities, setAllEntities, updateEntity, withEntities } from '@ngrx/signals/entities';
import { notify, withNotifications } from '../../shared/store/notification.store';
import { COIN_CREATED_MESSAGE, COIN_CREATION_FAILED_MESSAGE } from '../../shared/configs/notification.config';
import { NamedEntityState } from '@ngrx/signals/entities/src/models';
import { moveItemInArray } from '@angular/cdk/drag-drop';
import { CoinBase } from '../models/http/coin.model';

type BoleanObject = { [key: string]: boolean };

type CoinState = {
  coinForm: CoinCreationForm;
  isCreating: boolean;
  isLoading: boolean;
};

const initialCoinForm: CoinCreationForm = {
  coin: {
    name: '',
    description: '',
    username: ''
  },
  image: null,
  topUp: {
    coinUid: '',
    username: '',
    amount: 0,
    description: ''
  },
  coinUid: ''
};

const initialFormStatus = {
  isCreating: false
};

const initialCoinState: CoinState = {
  coinForm: initialCoinForm,
  isLoading: false,
  ...initialFormStatus
};

export const CoinStore = signalStore(
  { providedIn: 'root' },
  withDevtools('coins'),
  withState(initialCoinState),
  withNotifications,
  withEntities({
    entity: type<Coin>(),
    collection: 'coins'
  }),
  withMethods((store, coinHttpService = inject(CoinHttpService), userStore = inject(UserStore)) => ({
    replace(indexA: number, indexB: number): void {
      const coins = store.coinsEntities();
      moveItemInArray(coins, indexA, indexB);
      patchState(store, setAllCoins(coins));
    },
    onLogout(): void {
      patchState(store, initializeCoinState());
    },
    clear(): void {
      patchState(store, initializeCoinState());
      patchState(store, removeAllEntities({ collection: 'coins' }));
    },
    rxLoadCoins: rxMethod<void>(stream$ =>
      stream$.pipe(
        tap(() => patchState(store, isLoading())),
        switchMap(() => coinHttpService.getCoins(userStore.currentUser.username())),
        switchMap(coins => forkJoin(coins.map(coin => getCoin(coinHttpService, coin)))),
        tapResponse({
          next: coins => patchState(store, setAllCoins(coins), doneLoading()),
          error: () => patchState(store, doneLoading())
        })
      )
    ),

    createCoin: rxMethod<CoinCreationForm>(stream$ =>
      stream$.pipe(
        tap(coinForm => {
          const username = userStore.currentUser.username();
          coinForm.coin.username = username;
          coinForm.topUp.username = username;
          patchState(store, { ...initialFormStatus, coinForm }, isCreating());
        }),
        switchMap(() => coinHttpService.createCoin(store.coinForm().coin)),
        switchMap(() =>
          coinHttpService.getCoinByUsernameAndName(store.coinForm().coin.username, store.coinForm().coin.name)
        ),
        switchMap(coin => getCoin(coinHttpService, coin)),
        tap(coin => patchState(store, addEntity(coin, { idKey: 'uid', collection: 'coins' }))),
        tap(coin => patchState(store, { coinForm: { ...store.coinForm(), coinUid: coin.uid } })),
        switchMap(() => coinHttpService.updateCoinImage(store.coinForm().coinUid, store.coinForm().image)),
        switchMap(() => coinHttpService.topUpCoin(setTopupUid(store.coinForm().coinUid, store.coinForm.topUp()))),
        switchMap(() => coinHttpService.getCoinByUid(store.coinForm().coinUid)),
        switchMap(coin => getCoin(coinHttpService, coin)),
        tap(coin => patchState(store, updateEntity({ id: coin.uid, changes: coin }, { collection: 'coins' }))),
        tapResponse({
          next: () => {
            patchState(
              store,
              initializeCoinState(),
              doneCreating(),
              notify.success(store.notifications(), COIN_CREATED_MESSAGE)
            );
          },
          error: () =>
            patchState(store, doneCreating(), notify.error(store.notifications(), COIN_CREATION_FAILED_MESSAGE))
        })
      )
    )
  })),

  withHooks({
    onInit({ rxLoadCoins }) {
      rxLoadCoins();
    },
    onDestroy({ clear }) {
      clear();
    }
  })
);

function isCreating(): BoleanObject {
  return { isCreating: true };
}

function isLoading(): BoleanObject {
  return { isLoading: true };
}

function doneCreating(): BoleanObject {
  return { isCreating: false };
}

function doneLoading(): BoleanObject {
  return { isLoading: false };
}

function initializeCoinState(): { coinForm: CoinCreationForm } {
  return { coinForm: initialCoinForm };
}

function setTopupUid(uid: string = '', topUp: CoinTopUpForm): CoinTopUpForm {
  return {
    ...topUp,
    coinUid: uid
  };
}

function setAllCoins(coins: Coin[]): PartialStateUpdater<NamedEntityState<Coin, 'coins'>> {
  return setAllEntities(coins, { idKey: 'uid', collection: 'coins' });
}

function getCoin(coinHttpService: CoinHttpService, coin: CoinBase): Observable<Coin> {
  return coinHttpService.getCoinImage(coin.imageName).pipe(map(imageUrl => ({ ...coin, imageUrl }) as Coin));
}
