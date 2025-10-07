package com.shorturl.tokenservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "shorten_url")
@Data
@AllArgsConstructor
public class ShortenUrlModel {
    @Id
    private String id;
    @Indexed
    private String longUrl;
    private String shortCode;
    @Indexed(unique = true)
    private Long decodedShortCode;
    private Instant createdAt;

    private Long totalClicks;
    private Instant lastAccessedAt;

    // Constructor for backward compatibility
    public ShortenUrlModel(String id, String longUrl, String shortCode, Long decodedShortCode, Instant createdAt) {
        this.id = id;
        this.longUrl = longUrl;
        this.shortCode = shortCode;
        this.decodedShortCode = decodedShortCode;
        this.createdAt = createdAt;
        this.totalClicks = 0L;
        this.lastAccessedAt = null;
    }

}
