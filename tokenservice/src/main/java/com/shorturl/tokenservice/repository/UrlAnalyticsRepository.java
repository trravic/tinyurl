package com.shorturl.tokenservice.repository;

import com.shorturl.tokenservice.model.UrlAnalytics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;

public interface UrlAnalyticsRepository extends MongoRepository<UrlAnalytics, String> {
    List<UrlAnalytics> findByShortCodeOrderByTimestampDesc(String shortCode);

    List<UrlAnalytics> findByDecodedShortCodeOrderByTimestampDesc(Long decodedShortCode);

    @Query("{'shortCode': ?0, 'timestamp': {$gte: ?1, $lte: ?2}}")
    List<UrlAnalytics> findByShortCodeAndTimestampBetween(String shortCode, Instant start, Instant end);

    Long countByShortCode(String shortCode);

    Long countByDecodedShortCode(Long decodedShortCode);

    // Delete analytics for expired URLs
    Long deleteByDecodedShortCode(Long decodedShortCode);

    Long deleteByShortCode(String shortCode);
}
