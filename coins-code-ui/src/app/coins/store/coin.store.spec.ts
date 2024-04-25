import { CoinStore } from './coin.store';
import { CoinHttpService } from '../services/http/coin.http.service';
import { TestBed } from '@angular/core/testing';
import { patchState, signalStore, withState } from '@ngrx/signals';
import { setEntities } from '@ngrx/signals/entities';
import { CoinBase } from '../models/http/coin.model';
import { UserStore } from '../../user/store/user.store';
import { of } from 'rxjs';
import { COIN_CREATED_MESSAGE } from '../../shared/configs/notification.config';

describe('CoinStore', () => {
  let coinStore: InstanceType<typeof CoinStore>;
  let coinHttpService: jasmine.SpyObj<CoinHttpService>;
  const userStore = signalStore(withState({ currentUser: { username: 'user1' } }));
  const coins: CoinBase[] = [
    { uid: '1', name: 'Coin 1', imageName: 'image1' },
    { uid: '2', name: 'Coin 2', imageName: 'image2' }
  ];

  beforeEach(() => {
    coinHttpService = jasmine.createSpyObj('CoinHttpService', [
      'getCoins',
      'getCoinImage',
      'createCoin',
      'getCoinByUsernameAndName',
      'updateCoinImage',
      'topUpCoin',
      'getCoinByUid'
    ]);

    coinHttpService.getCoins.and.returnValue(of(coins));

    TestBed.configureTestingModule({
      providers: [
        CoinStore,
        { provide: UserStore, useClass: userStore },
        { provide: CoinHttpService, useValue: coinHttpService }
      ]
    });

    coinStore = TestBed.inject(CoinStore);
  });

  it('should replace coins', () => {
    patchState(coinStore, setEntities(coins, { idKey: 'uid', collection: 'coins' }));

    coinStore.replace(0, 1);

    expect(coinStore.coinsEntities()).toEqual([coins[1], coins[0]]);
  });

  it('should load coins', () => {
    const coinHttpServiceGetCoinImageSpy = (coinHttpService.getCoinImage as jasmine.Spy).and.returnValue(of('image'));
    const coinHttpServiceGetCoinsSpy = (coinHttpService.getCoins as jasmine.Spy).and.returnValue(of(coins));

    coinStore.rxLoadCoins();

    expect(coinHttpServiceGetCoinsSpy).toHaveBeenCalledWith('user1');
    expect(coinStore.isLoading()).toBeFalse();
    expect(coinHttpServiceGetCoinImageSpy).toHaveBeenCalled();
  });

  it('should create a coin with side effects', () => {
    const coinForm = {
      coin: {
        name: 'New Coin',
        description: 'Description',
        username: 'user1'
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
    const createdCoin = { uid: 'new', name: 'New Coin' };

    patchState(coinStore, { ...coinStore.coinForm(), coinForm });

    (coinHttpService.createCoin as jasmine.Spy).and.returnValue(of(void 0));
    (coinHttpService.getCoinByUsernameAndName as jasmine.Spy).and.returnValue(of(createdCoin));
    (coinHttpService.updateCoinImage as jasmine.Spy).and.returnValue(of(void 0));
    (coinHttpService.topUpCoin as jasmine.Spy).and.returnValue(of(void 0));
    (coinHttpService.getCoinByUid as jasmine.Spy).and.returnValue(of(createdCoin));
    (coinHttpService.getCoinImage as jasmine.Spy).and.returnValue(of('image'));

    coinStore.createCoin(coinForm);

    expect(coinHttpService.createCoin).toHaveBeenCalledWith(coinForm.coin);
    expect(coinHttpService.getCoinByUsernameAndName).toHaveBeenCalledWith(coinForm.coin.username, coinForm.coin.name);
    expect(coinHttpService.updateCoinImage).toHaveBeenCalledWith(createdCoin.uid, coinForm.image);
    expect(coinHttpService.topUpCoin).toHaveBeenCalledWith({ ...coinForm.topUp, coinUid: createdCoin.uid });
    expect(coinHttpService.getCoinByUid).toHaveBeenCalledWith(createdCoin.uid);
    expect(coinStore.notifications()[0].message).toBe(COIN_CREATED_MESSAGE);
    expect(coinStore.isCreating()).toBeFalse();
  });
});
