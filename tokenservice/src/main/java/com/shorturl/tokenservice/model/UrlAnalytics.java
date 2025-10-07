package com.shorturl.tokenservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "url_analytics")
@Data
@Builder
public class UrlAnalytics {
    @Id
    private String id;

    @Indexed
    private String shortCode;

    @Indexed
    private Long decodedShortCode;

    private Instant timestamp;

    //Request Infomation
    private String ipAddress;
    private String userAgent;
    private String referrer;

    // Device Information
    private String deviceType; // MOBILE, DESKTOP, TABLET, BOT
    private String browser;
    private String operatingSystem;

}
