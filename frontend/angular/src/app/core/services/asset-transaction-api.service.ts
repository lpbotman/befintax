import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AssetTransaction} from '../models/asset.model';
import {AssetTransactionCreateDto} from '../../features/wallet/dtos/asset-transaction-create.dto';


@Injectable({
  providedIn: 'root'
})
export class AssetTransactionApiService {
  private baseUrl =  environment.apiUrl;

  constructor(private http: HttpClient) {}

  getAssetTransactions(assetId: number) {
    return this.http.get<AssetTransaction[]>(this.baseUrl + '/assets/'+assetId+'/transactions');
  }

  createAssetTransaction(assetId: number, dto: AssetTransactionCreateDto) {
    return this.http.post<AssetTransaction>(this.baseUrl + '/assets/'+assetId+'/transactions', dto);
  }
}
