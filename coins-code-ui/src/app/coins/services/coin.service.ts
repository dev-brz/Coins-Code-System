import { inject, Injectable } from '@angular/core';
import { CoinBaseForm, CoinTopUpForm } from '../models/coin.model';
import { CoinStore } from '../store/coin.store';
import { UserStore } from '../../user/store/user.store';
import { Observable, of } from 'rxjs';
import { CoinHttpService } from './http/coin.http.service';

@Injectable({
  providedIn: 'root'
})
export class CoinService {
  coinStore = inject(CoinStore);
  userStore = inject(UserStore);
  coinsHttpService = inject(CoinHttpService);
  createCoinAndTopUp(coin: CoinBaseForm, image: File | null, topUp: CoinTopUpForm): void {
    this.coinStore.createCoin({ coin, image, topUp });
  }

  exists(name: string): Observable<boolean> {
    const username = this.userStore.currentUser.username();
    return of(false);
    return this.coinsHttpService.exists(name, username);
  }
}
