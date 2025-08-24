package com.shorturl.tokenservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShortenUrlRequestDTO {
    @NotNull(message = "Long URL is required")
    @Pattern(regexp = "^(http|https)://.*$", message = "URL must start with http:// or https://")
    private String longUrl;
}
