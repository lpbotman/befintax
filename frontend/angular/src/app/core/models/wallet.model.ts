import {Asset} from './asset.model';

export interface Wallet {
  id: number;
  name: string;
  assets: Asset[];
}
