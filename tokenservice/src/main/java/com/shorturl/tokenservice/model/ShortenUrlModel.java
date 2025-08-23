package com.shorturl.tokenservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "shorten_url")
@Data
@AllArgsConstructor
public class ShortenUrlModel {
    @Id
    private String id;
    private String longUrl;
    private String shortUrl;
    private Instant createdAt;
}
