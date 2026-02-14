import {Asset} from '../../../core/models/asset.model';

export interface AssetTransactionTaxDto {
  id: number;
  Asset: Asset;
  date: string;
  price: number;
  taxRate: number
  taxAmount: number;
}
