package com.shorturl.tokenservice.service;

import com.shorturl.tokenservice.dto.AnalyticsResponseDTO;
import com.shorturl.tokenservice.dto.ClickDetailDTO;
import com.shorturl.tokenservice.exception.NotFoundException;
import com.shorturl.tokenservice.model.ShortenUrlModel;
import com.shorturl.tokenservice.model.UrlAnalytics;
import com.shorturl.tokenservice.repository.ShortenUrlRepository;
import com.shorturl.tokenservice.repository.UrlAnalyticsRepository;
import com.shorturl.tokenservice.util.Base62Encoder;
import com.shorturl.tokenservice.util.UserAgentParser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private static final Logger log = LoggerFactory.getLogger(AnalyticsService.class);

    private final UrlAnalyticsRepository analyticsRepository;
    private final ShortenUrlRepository shortenUrlRepository;

    public void trackClick(String shortCode, Long decodedShortCode, HttpServletRequest request) {
        try {
            // Parse user agent
            String userAgentString = request.getHeader("User-Agent");
            UserAgentParser.ParsedUserAgent parsedUA = UserAgentParser.parse(userAgentString);

            // Extract request info
            String ipAddress = extractIpAddress(request);
            String referer = request.getHeader("Referer");

            // Create analytics record
            UrlAnalytics analytics = UrlAnalytics.builder()
                    .shortCode(shortCode)
                    .decodedShortCode(decodedShortCode)
                    .timestamp(Instant.now())
                    .ipAddress(ipAddress)
                    .userAgent(userAgentString)
                    .deviceType(parsedUA.getDeviceType())
                    .browser(parsedUA.getBrowser())
                    .operatingSystem(parsedUA.getOperatingSystem())
                    .build();

            analyticsRepository.save(analytics);
            log.info("Analytics tracked for shortCode: {}", shortCode);

            // Update summary in ShortenUrlModel
            updateClickSummary(decodedShortCode);

        } catch (Exception e) {
            log.error("Error tracking analytics for shortCode: {}", shortCode, e);
        }
    }

    private void updateClickSummary(Long decodedShortCode) {
        shortenUrlRepository.findByDecodedShortCode(decodedShortCode)
                .ifPresent(urlModel -> {
                    urlModel.setTotalClicks((urlModel.getTotalClicks() == null ? 0L : urlModel.getTotalClicks()) + 1);
                    urlModel.setLastAccessedAt(Instant.now());
                    shortenUrlRepository.save(urlModel);
                });
    }

    private String extractIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getHeader("X-Real-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        // Take first IP if multiple exist
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        return ipAddress;
    }

    public AnalyticsResponseDTO getAnalytics(String shortCode) {
        Long decodedShortCode = Base62Encoder.decode(shortCode);

        // Get URL model
        ShortenUrlModel urlModel = shortenUrlRepository.findByDecodedShortCode(decodedShortCode)
                .orElseThrow(() -> new NotFoundException("Short URL not found"));

        // Get all analytics records
        List<UrlAnalytics> allAnalytics = analyticsRepository.findByDecodedShortCodeOrderByTimestampDesc(decodedShortCode);

        // Calculate time-based metrics
        Instant now = Instant.now();
        Instant startOfToday = now.truncatedTo(ChronoUnit.DAYS);
        Instant startOfWeek = now.minus(7, ChronoUnit.DAYS);
        Instant startOfMonth = now.minus(30, ChronoUnit.DAYS);

        long clicksToday = allAnalytics.stream()
                .filter(a -> a.getTimestamp().isAfter(startOfToday))
                .count();

        long clicksThisWeek = allAnalytics.stream()
                .filter(a -> a.getTimestamp().isAfter(startOfWeek))
                .count();

        long clicksThisMonth = allAnalytics.stream()
                .filter(a -> a.getTimestamp().isAfter(startOfMonth))
                .count();

        // Device breakdown
        Map<String, Long> deviceBreakdown = allAnalytics.stream()
                .collect(Collectors.groupingBy(UrlAnalytics::getDeviceType, Collectors.counting()));

        // Browser breakdown
        Map<String, Long> browserBreakdown = allAnalytics.stream()
                .collect(Collectors.groupingBy(UrlAnalytics::getBrowser, Collectors.counting()));

        // OS breakdown
        Map<String, Long> osBreakdown = allAnalytics.stream()
                .collect(Collectors.groupingBy(UrlAnalytics::getOperatingSystem, Collectors.counting()));

        // Recent clicks (last 10)
        List<ClickDetailDTO> recentClicks = allAnalytics.stream()
                .limit(10)
                .map(a -> ClickDetailDTO.builder()
                        .timeStamp(a.getTimestamp())
                        .ipAddress(maskIpAddress(a.getIpAddress()))
                        .deviceType(a.getDeviceType())
                        .browser(a.getBrowser())
                        .build())
                .collect(Collectors.toList());

        return AnalyticsResponseDTO.builder()
                .shortCode(shortCode)
                .longUrl(urlModel.getLongUrl())
                .totalClicks(urlModel.getTotalClicks() != null ? urlModel.getTotalClicks() : 0L)
                .createdAt(urlModel.getCreatedAt())
                .lastAccessedAt(urlModel.getLastAccessedAt())
                .clicksToday(clicksToday)
                .clicksThisWeek(clicksThisWeek)
                .clicksThisMonth(clicksThisMonth)
                .deviceTypeBreakdown(deviceBreakdown)
                .browserBreakdown(browserBreakdown)
                .osBreakdown(osBreakdown)
                .recentClicks(recentClicks)
                .build();
    }

    private String maskIpAddress(String ipAddress) {
        if (ipAddress == null) return "Unknown";
        // Mask last octet for privacy: 192.168.1.1 -> 192.168.1.xxx
        String[] parts = ipAddress.split("\\.");
        if (parts.length == 4) {
            return parts[0] + "." + parts[1] + "." + parts[2] + ".xxx";
        }
        return "xxx.xxx.xxx.xxx";
    }
}