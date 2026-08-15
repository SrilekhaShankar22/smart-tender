package com.smarttender.processing.repository;
import com.smarttender.processing.entity.TenderDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface TenderDocumentRepository extends ElasticsearchRepository<TenderDocument, String> {}
