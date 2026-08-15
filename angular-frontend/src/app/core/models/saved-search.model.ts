export interface SavedSearchRequest {
  name: string; keywords?: string; organisation?: string; category?: string;
  sourceType?: string; state?: string; alertEnabled?: boolean; alertFrequency?: string;
}
export interface SavedSearchResponse {
  id: number; name: string; keywords: string; organisation: string; category: string;
  sourceType: string; state: string; alertEnabled: boolean; alertFrequency: string; createdAt: string;
}
