import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AssetApiService {
  private baseUrl =  environment.apiUrl;

  constructor(private http: HttpClient) {}

  getAssets() {
    return this.http.get<Asset[]>(baseUrl + '/assets');
  }

  createAsset(dto: AssetCreateDTO) {
    return this.http.post<Asset>(baseUrl + '/assets', dto);
  }
}
