import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { SavedSearchRequest, SavedSearchResponse } from '../models/saved-search.model';
import { ApiResponse } from '../models/tender.model';

@Injectable({ providedIn: 'root' })
export class SavedSearchService {
  private baseUrl = environment.apiUrls.profile;
  constructor(private http: HttpClient) {}
  getAll(userId: number): Observable<ApiResponse<SavedSearchResponse[]>> {
    return this.http.get<ApiResponse<SavedSearchResponse[]>>(`${this.baseUrl}/profiles/${userId}/saved-searches`);
  }
  create(userId: number, req: SavedSearchRequest): Observable<ApiResponse<SavedSearchResponse>> {
    return this.http.post<ApiResponse<SavedSearchResponse>>(`${this.baseUrl}/profiles/${userId}/saved-searches`, req);
  }
  update(userId: number, id: number, req: SavedSearchRequest): Observable<ApiResponse<SavedSearchResponse>> {
    return this.http.put<ApiResponse<SavedSearchResponse>>(`${this.baseUrl}/profiles/${userId}/saved-searches/${id}`, req);
  }
  delete(userId: number, id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/profiles/${userId}/saved-searches/${id}`);
  }
}
