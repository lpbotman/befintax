import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class WalletService {
  assets = signal<Asset[]>([]);

  constructor(private assetApi: AssetApiService) {}

  loadAssets() {
    this.assetApi.getAssets().subscribe(list => this.assets.set(list));
  }

  addAsset(dto: AssetCreateDto) {
    this.assetApi.createAsset(dto).subscribe(asset => {
      this.assets.update((old: Asset[]) => [...old, asset]);
    });
  }
}
