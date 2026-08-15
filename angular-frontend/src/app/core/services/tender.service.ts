import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TenderSearchRequest, TenderSearchResult, PagedResponse, ApiResponse } from '../models/tender.model';

@Injectable({ providedIn: 'root' })
export class TenderService {
  private baseUrl = environment.apiUrls.search;
  constructor(private http: HttpClient) {}

  search(req: TenderSearchRequest): Observable<ApiResponse<PagedResponse<TenderSearchResult>>> {
    let params = new HttpParams();
    Object.entries(req).forEach(([k, v]) => { if (v !== undefined && v !== null && v !== '') params = params.set(k, String(v)); });
    return this.http.get<ApiResponse<PagedResponse<TenderSearchResult>>>(`${this.baseUrl}/tenders/search`, { params });
  }

  getById(id: string): Observable<ApiResponse<TenderSearchResult>> {
    return this.http.get<ApiResponse<TenderSearchResult>>(`${this.baseUrl}/tenders/${id}`);
  }
}
