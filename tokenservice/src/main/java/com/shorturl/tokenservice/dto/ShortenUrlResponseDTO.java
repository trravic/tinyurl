package com.shorturl.tokenservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ShortenUrlResponseDTO {
    private String longUrl;
    private String shortUrl;
}
