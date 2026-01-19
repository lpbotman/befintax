import {AssetTransaction, TransactionType} from '../../../core/models/asset.model';

export type AssetTransactionForm = Omit<AssetTransaction, 'id'>;

export const DEFAULT_CREATE_TRANSACTION: AssetTransactionForm = {
  type: TransactionType.BUY,
  date: new Date(),
  quantity: 1,
  price: 0,
  currency: 'EUR'
};
