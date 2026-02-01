import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {PricePoint} from '../models/market-data.model';
import {environment} from '../../../environments/environment';
import {AssetType} from '../models/asset.model';


@Injectable({
  providedIn: 'root'
})
export class MarketDataApiService {
  private baseUrl =  environment.apiUrl;
  private apiUrl = this.baseUrl + '/market-data';

  constructor(private http: HttpClient) {}

  getHistory(symbol: string, type: string = AssetType.STOCK, exchange: string = ''): Observable<PricePoint[]> {
    return this.http.get<PricePoint[]>(`${this.apiUrl}/${symbol}/history`, {
      params: { type, exchange }
    });
  }

  getPriceLive(symbol: string, exchange: string = ''): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/${symbol}/price/live`, {
      params: { exchange }
    })
  }

  getPriceEnd2025(symbol: string, exchange: string = ''): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/${symbol}/price/end2025`, {
      params: { exchange }
    })
  }
}
