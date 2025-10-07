package com.shorturl.tokenservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClickDetailDTO {
    private Instant timeStamp;
    private String ipAddress;
    private String deviceType;
    private String browser;
}
