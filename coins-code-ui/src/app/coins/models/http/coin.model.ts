export interface CoinBase {
  uid: string;
  name?: string;
  imageName?: string;
  description?: string;
  amount?: number;
}

export interface CoinsBase {
  coins: CoinBase[];
}
