package com.smarttender.fetch.repository;
import com.smarttender.fetch.entity.FetchedTender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface FetchedTenderRepository extends JpaRepository<FetchedTender, Long> {
    boolean existsByTenderId(String tenderId);
    boolean existsByContentHash(String contentHash);
    Optional<FetchedTender> findByTenderId(String tenderId);
}
