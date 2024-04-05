import { CoinBase } from './http/coin.model';

export interface Coin extends CoinBase {
  imageUrl?: string | File;
}

export interface CoinBaseForm {
  username?: string;
  name: string;
  description: string;
}

export interface CoinTopUpForm {
  coinUid: string;
  username: string;
  amount: number;
  description: string;
}

export interface CoinCreationForm {
  coin: CoinBaseForm;
  image: File | null;
  topUp: CoinTopUpForm;
  coinUid?: string;
}
