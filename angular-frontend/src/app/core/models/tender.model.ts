export interface TenderSearchRequest {
  keyword?: string; organisation?: string; state?: string; category?: string;
  sourceType?: string; tenderStatus?: string; closingDateFrom?: string; closingDateTo?: string;
  sortBy?: string; sortDirection?: string; page?: number; size?: number;
}
export interface TenderSearchResult {
  tenderId: string; title: string; tenderRefNo: string; organisationName: string;
  productCategory: string; sourceType: string; tenderStatus: string;
  publishedDate: string; bidSubmissionClosingDate: string; daysUntilClosing: number;
  relevanceScore: number; detailUrl: string; extractedKeywords: string[];
}
export interface PagedResponse<T> {
  content: T[]; page: number; size: number; totalElements: number; totalPages: number; first: boolean; last: boolean;
}
export interface ApiResponse<T> { success: boolean; message: string; errorCode?: string; data: T; timestamp: string; }
