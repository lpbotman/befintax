import { Injectable } from '@angular/core';
import { AssetCreateDto } from '../dtos/asset-create.dto'
import { AssetApiService } from '../../../core/services/asset-api.service';
import { AssetTransactionApiService } from '../../../core/services/asset-transaction-api.service';
import { Asset, AssetTransaction } from '../../../core/models/asset.model';
import { signal } from '@angular/core';
import {AssetTransactionCreateDto} from '../dtos/asset-transaction-create.dto';
import {TaxesApiService} from '../../../core/services/taxes-api.service';
import {firstValueFrom} from 'rxjs';
import {WalletApiService} from '../../../core/services/wallet-api.service';
import {Wallet} from '../../../core/models/wallet.model';

@Injectable({
  providedIn: 'root'
})
export class WalletService {
  wallet = signal<Wallet | null>(null);
  assets = signal<Asset[]>([]);

  constructor(private assetApi: AssetApiService,
              private transactionApi: AssetTransactionApiService,
              private taxesApi: TaxesApiService,
              private walletApi: WalletApiService) {}

  async loadAWallet() {
    const wallet = await firstValueFrom(this.walletApi.getWallet());
    this.wallet.set(wallet);
  }

  async loadAssets() {
    const list = await firstValueFrom(this.assetApi.getAssets());
    this.assets.set(list);
  }

  addAsset(dto: AssetCreateDto) {
    this.assetApi.createAsset(dto).subscribe((asset: Asset) => {
      this.assets.update((old: Asset[]) => [...old, asset]);
      const wallet = this.wallet();
      if(!wallet) this.loadAWallet();
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
