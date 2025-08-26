package com.shorturl.tokenservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RangeResponse {
    private Long start;
    private Long end;
}
