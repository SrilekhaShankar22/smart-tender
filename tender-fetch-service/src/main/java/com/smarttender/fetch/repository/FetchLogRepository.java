package com.smarttender.fetch.repository;
import com.smarttender.fetch.entity.FetchLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface FetchLogRepository extends JpaRepository<FetchLog, Long> {
    List<FetchLog> findTop10ByOrderByStartedAtDesc();
    @Query("SELECT SUM(f.newTenders) FROM FetchLog f WHERE f.startedAt >= :since AND f.status = 'SUCCESS'")
    Long sumNewTendersSince(LocalDateTime since);
}
