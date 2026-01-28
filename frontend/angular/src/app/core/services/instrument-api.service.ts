import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../../environments/environment';
import {Instrument} from '../models/Instrument.model';


@Injectable({
  providedIn: 'root'
})
export class InstrumentApiService {
  private baseUrl =  environment.apiUrl;

  constructor(private http: HttpClient) {}

  search(query: string, page: number = 0, size: number = 10): Observable<Instrument[]> {
    const params = new HttpParams()
      .set('query', query)
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', 'name,asc');

    return this.http.get<Instrument[]>(this.baseUrl+'/public/instruments/search', { params });
  }
}
