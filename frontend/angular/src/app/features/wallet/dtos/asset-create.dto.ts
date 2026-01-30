import { AssetType } from '../../../core/models/asset.model';
import {AssetTransactionCreateDto} from './asset-transaction-create.dto';

export interface AssetCreateDto {
  name: string;
  symbol: string;
  type: AssetType;
  taxCollectedByBroker: boolean;
  stockTaxRate: number;
  priceEnd2025: number;
  currency: string;
  exchange: string;
  transactions?: AssetTransactionCreateDto[];
  walletId?: number;
}
