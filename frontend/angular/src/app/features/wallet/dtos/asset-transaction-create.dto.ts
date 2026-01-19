import {TransactionType} from '../../../core/models/asset.model';

export interface AssetTransactionCreateDto {
  date: Date;
  quantity: number;
  price: number;
  type: TransactionType;
  currency?: string;
}

