package com.smarttender.search.service;
import com.smarttender.common.dto.PagedResponse;
import com.smarttender.search.dto.request.TenderSearchRequest;
import com.smarttender.search.dto.response.TenderSearchResult;
public interface TenderSearchService {
    PagedResponse<TenderSearchResult> search(TenderSearchRequest request);
    TenderSearchResult getById(String tenderId);
}
