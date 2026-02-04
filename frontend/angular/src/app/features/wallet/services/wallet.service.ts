import { Injectable } from '@angular/core';
import { AssetApiService } from '../../../core/services/asset-api.service';
import { AssetTransactionApiService } from '../../../core/services/asset-transaction-api.service';
import {Asset, AssetTransaction, TransactionType} from '../../../core/models/asset.model';
import { signal } from '@angular/core';
import {firstValueFrom, forkJoin, map} from 'rxjs';
import {WalletApiService} from '../../../core/services/wallet-api.service';
import {Wallet} from '../../../core/models/wallet.model';
import {MarketDataApiService} from '../../../core/services/market-data-api.service';
import {AssetTransactionCreateDto} from '../dtos/asset-transaction-create.dto';
import {AssetCreateDto} from '../dtos/asset-create.dto';

@Injectable({
  providedIn: 'root'
})
export class WalletService {
  wallet = signal<Wallet | null>(null);
  assets = signal<Asset[]>([]);
  marketPrices = signal<Record<string, number>>({});
  usdEurRate = signal<number>(0.92);

  constructor(private assetApi: AssetApiService,
              private transactionApi: AssetTransactionApiService,
              private walletApi: WalletApiService,
              private marketDataService: MarketDataApiService) {
    this.fetchUsdEurRate();
  }

  async loadAWallet() {
    const wallet = await firstValueFrom(this.walletApi.getWallet());
    this.wallet.set(wallet);
  }

  async loadAssets() {
    const list = await firstValueFrom(this.assetApi.getAssets());
    this.assets.set(list);
    this.refreshAllPrices();
  }

  addAsset(dto: AssetCreateDto) {
    this.assetApi.createAsset(dto).subscribe((asset: Asset) => {
      this.assets.update((old: Asset[]) => [...old, asset]);
      const wallet = this.wallet();
      if(!wallet) this.loadAWallet();
    });
  }

  deleteAsset(assetId: number) {
    this.assetApi.deleteAsset(assetId).subscribe(() => {
      this.assets.update((assets: Asset[]) =>
        assets.filter((asset: Asset) => asset.id !== assetId)
        );
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

  deleteTransaction(assetId: number, transactionId: number) {
    this.transactionApi.deleteAssetTransaction(assetId, transactionId).subscribe(() => {
      this.assets.update((assets: Asset[]) =>
        assets.map(asset =>
            asset.id === assetId
              ? {...asset, transactions: asset.transactions?.filter(
                  transaction => transaction.id !== transactionId
                ),
              }
              : asset
        )
      );
    });
  }

  refreshAllPrices() {
    const assets = this.assets();
    if (assets.length === 0) return;

    const requests = assets.filter(asset => asset.symbol != null).map(asset =>
      this.marketDataService.getPriceLive(asset.symbol!, asset.exchange).pipe(
        map(price => ({
          key: `${asset.symbol}:${asset.exchange}`,
          price
        }))
      )
    );

    forkJoin(requests).subscribe(results => {
      const newPrices: Record<string, number> = {};
      results.forEach(res => {
        newPrices[res.key] = res.price;
      });

      this.marketPrices.set(newPrices);
    });
  }

  calculateTotalValue(): number {
    const assets = this.assets();
    const prices = this.marketPrices();

    if (assets.length === 0) return 0;

    return assets.reduce((acc, asset) => {
      const stats = (asset.transactions ?? []).reduce((subAcc, t) => {
        const isBuy = t.type === TransactionType.BUY;
        return {
          quantity: subAcc.quantity + (isBuy ? t.quantity : -t.quantity),
          invested: subAcc.invested + (isBuy ? t.price : -t.price)
        };
      }, { quantity: 0, invested: 0 });

      const priceKey = `${asset.symbol}:${asset.exchange}`;
      let currentPrice = prices[priceKey] || 0;

      if(asset.currency === 'USD') {
        currentPrice = currentPrice * this.usdEurRate();
      }

      const actualValue = currentPrice > 0
        ? stats.quantity * currentPrice
        : stats.invested;

      return acc + actualValue;
    }, 0);
  }

  fetchUsdEurRate() {
    // On utilise ta méthode getPriceLive avec le ticker adéquat
    this.marketDataService.getPriceLive('EUR=X', 'FOREX').subscribe(rate => {
      if (rate > 0) {
        this.usdEurRate.set(rate);
        console.log(rate);
      }
    });
  }


}
