import {Injectable} from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {Wallet} from '../models/wallet.model';


@Injectable({
  providedIn: 'root'
})
export class WalletApiService {
  private baseUrl =  environment.apiUrl;

  constructor(private http: HttpClient) {}

  getWallet() {
    return this.http.get<Wallet>(this.baseUrl + '/wallet/main');
  }
}
