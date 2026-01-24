import {Injectable} from "@angular/core";
import {environment} from "../../../environments/environment";
import {HttpClient} from '@angular/common/http';
import {AssetTransactionTaxDto} from '../../features/wallet/dtos/asset-transaction-tax.dto';
import {TaxGainCalculationDto} from '../../features/wallet/dtos/TaxGainCalculation.dto';

@Injectable({
  providedIn: 'root'
})
export class TaxesApiService {
  private baseUrl =  environment.apiUrl;

  constructor(private http: HttpClient) {}

  calculateAssetsTransactionTax(beginDate: Date, endDate: Date) {
    const beginStr = beginDate.toISOString().slice(0, 10);
    const endStr = endDate.toISOString().slice(0, 10);

    return this.http.get<AssetTransactionTaxDto[]>(
      `${this.baseUrl}/taxes/assets-transactions?beginDate=${beginStr}&endDate=${endStr}`
    );
  }

  calculateTaxGain(year: number) {
    return this.http.get<TaxGainCalculationDto>(`${this.baseUrl}/taxes/gain/${year}`);
  }
}
