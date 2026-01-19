import { Injectable } from '@angular/core';
import { AssetCreateDto } from '../dtos/asset-create.dto'
import { AssetApiService } from '../../../core/services/asset-api.service';
import { AssetTransactionApiService } from '../../../core/services/asset-transaction-api.service';
import { Asset, AssetTransaction } from '../../../core/models/asset.model';
import { signal } from '@angular/core';
import {AssetTransactionCreateDto} from '../dtos/asset-transaction-create.dto';

@Injectable({
  providedIn: 'root'
})
export class WalletService {
  assets = signal<Asset[]>([]);

  constructor(private assetApi: AssetApiService, private transactionApi: AssetTransactionApiService) {}

  loadAssets() {
    this.assetApi.getAssets().subscribe((list: Asset[]) => {
      console.log(list);
      this.assets.set(list)
    });
  }

  addAsset(dto: AssetCreateDto) {
    this.assetApi.createAsset(dto).subscribe((asset: Asset) => {
      this.assets.update((old: Asset[]) => [...old, asset]);
    });
  }

  addTransaction(assetId: number, dto: AssetTransactionCreateDto) {
    this.transactionApi.createAssetTransaction(assetId, dto).subscribe((transaction: AssetTransaction) => {
        this.assets.update((assets: Asset[]) =>
          assets.map(asset =>
            asset.id === assetId
              ? {
                ...asset,
                transactions: [...asset.transactions || [], transaction],
              }
              : asset
          )
        );
      });
  }
}
