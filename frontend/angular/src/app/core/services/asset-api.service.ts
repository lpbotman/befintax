import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Asset } from '../models/asset.model';
import { AssetCreateDto } from '../../features/wallet/dtos/asset-create.dto';


@Injectable({
  providedIn: 'root'
})
export class AssetApiService {
  private baseUrl =  environment.apiUrl;

  constructor(private http: HttpClient) {}

  getAssets() {
    return this.http.get<Asset[]>(this.baseUrl + '/assets');
  }

  createAsset(dto: AssetCreateDto) {
    return this.http.post<Asset>(this.baseUrl + '/assets', dto);
  }

  deleteAsset(id: number) {
    return this.http.delete(this.baseUrl + '/assets/' + id);
  }
}
