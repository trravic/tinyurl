package com.shorturl.tokenservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShortenUrlRequestDTO {
    @NotNull(message = "Long URL is required")
    @Pattern(regexp = "^(http|https)://.*$", message = "URL must start with http:// or https://")
    private String longUrl;

    @Pattern(regexp = "^[a-zA-Z0-9_-]*$", message = "Custom alias can only contain letters, numbers, hyphens, and underscores")
    @Size(min = 3, max = 20, message = "Custom alias must be between 3 and 20 characters")
    private String customAlias;

}
