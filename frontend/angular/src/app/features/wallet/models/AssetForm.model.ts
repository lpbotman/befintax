import {Asset, AssetType} from '../../../core/models/asset.model';

export type AssetForm = Omit<Asset, 'id' | 'transactions' | 'priceEnd2025'> & { symbol: string, priceEnd2025: number };

export const DEFAULT_CREATE_ASSET: AssetForm = {
  name: '',
  symbol: '',
  currency: '',
  exchange: '',
  type: AssetType.STOCK,
  taxRatePercent: 0.35,
  taxCollectedByBroker: false,
  priceEnd2025: 0
};
