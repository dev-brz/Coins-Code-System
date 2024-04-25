import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { CoinBaseForm, CoinTopUpForm } from '../../models/coin.model';
import { catchError, map, Observable, of } from 'rxjs';
import {
  COINS_COIN_IMAGE_URL,
  COINS_COIN_URL,
  COINS_URL,
  TRANSACTION_TOP_UP_URL
} from '../../../shared/configs/api.config';
import { CoinBase, CoinsBase } from '../../models/http/coin.model';

@Injectable({
  providedIn: 'root'
})
export class CoinHttpService {
  http = inject(HttpClient);

  createCoin(coin: CoinBaseForm): Observable<void> {
    return this.http.post<void>(COINS_URL, coin);
  }

  updateCoinImage(coinId: string = '', image: File | null): Observable<boolean> {
    if (image === null || coinId === '') {
      return of(false);
    }
    const url = COINS_COIN_IMAGE_URL.replace('?1', coinId);
    const formData = new FormData();

    formData.append('image', image);

    return this.http.post<unknown>(url, formData).pipe(map(() => true));
  }

  topUpCoin(coinTopUp: CoinTopUpForm): Observable<void> {
    return this.http.post<void>(TRANSACTION_TOP_UP_URL, coinTopUp);
  }

  getCoinByUsernameAndName(username: string = '', name: string): Observable<CoinBase> {
    return this.http.get<CoinsBase>(COINS_URL, { params: { username, name } }).pipe(
      map(res => res.coins),
      map(coins => {
        if (coins.length === 1) {
          return coins[0];
        }
        throw new Error('Coin not found');
      })
    );
  }

  getCoinUidByUsernameAndName(username: string, name: string): Observable<string> {
    return this.getCoinByUsernameAndName(username, name).pipe(map(coin => coin.uid ?? ''));
  }

  getCoins(username: string): Observable<CoinBase[]> {
    return this.http.get<CoinsBase>(COINS_URL, { params: { username } }).pipe(map(res => res.coins));
  }

  getCoinByUid(uid: string = ''): Observable<CoinBase> {
    const url = COINS_COIN_URL.replace('?1', uid);
    return this.http.get<CoinBase>(url);
  }

  exists(name: string, username: string): Observable<boolean> {
    return this.http.head(COINS_URL, { params: { name, username }, observe: 'response' }).pipe(
      map(() => true),
      catchError((err: HttpErrorResponse) => of(err.status !== HttpStatusCode.NotFound))
    );
  }

  getCoinImage(name: string = ''): Observable<string> {
    const url = COINS_COIN_IMAGE_URL.replace('?1', name);
    return this.http.get(url, { responseType: 'blob' }).pipe(map(URL.createObjectURL));
  }
}
