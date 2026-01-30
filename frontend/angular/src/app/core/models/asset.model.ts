export interface Asset {
  id: number;
  symbol?: string;
  name: string;
  type: AssetType;
  currency: string;
  exchange: string;
  taxRatePercent: number;
  taxCollectedByBroker: boolean;
  priceEnd2025?: number;
  transactions?: AssetTransaction[];
}

export interface AssetTransaction {
  id: number;
  type: TransactionType;
  date: Date;
  quantity: number;
  price: number;
  currency?: string;
}

export enum TransactionType {
  BUY = 'BUY',
  SELL = 'SELL'
}

export enum AssetType {
  STOCK = 'STOCK',
//  BOND = 'BOND',
  ETF = 'ETF',
  CRYPTO = 'CRYPTO',
}
