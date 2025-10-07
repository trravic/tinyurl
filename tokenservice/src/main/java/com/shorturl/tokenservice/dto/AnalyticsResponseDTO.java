package com.shorturl.tokenservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponseDTO {
    private String shortCode;
    private String longUrl;
    private Long totalClicks;
    private Instant createdAt;
    private Instant lastAccessedAt;

    // Time-based analytics
    private Long clicksToday;
    private Long clicksThisWeek;
    private Long clicksThisMonth;

    // Device breakdown
    private Map<String, Long> deviceTypeBreakdown;
    private Map<String, Long> browserBreakdown;
    private Map<String, Long> osBreakdown;

    // Top referrers
    private Map<String, Long> referrerBreakdown;

    // Recent clicks (last 10)
    private List<ClickDetailDTO> recentClicks;
}