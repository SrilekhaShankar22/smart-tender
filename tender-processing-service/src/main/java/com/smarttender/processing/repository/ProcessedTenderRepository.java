package com.smarttender.processing.repository;
import com.smarttender.processing.entity.ProcessedTender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface ProcessedTenderRepository extends JpaRepository<ProcessedTender, Long> {
    Optional<ProcessedTender> findByTenderId(String tenderId);
    boolean existsByTenderId(String tenderId);
    boolean existsByContentHash(String contentHash);
}
