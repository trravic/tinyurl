package com.shorturl.tokenservice.controller;

import com.shorturl.tokenservice.dto.AnalyticsResponseDTO;
import com.shorturl.tokenservice.dto.ShortenUrlRequestDTO;
import com.shorturl.tokenservice.dto.ShortenUrlResponseDTO;
import com.shorturl.tokenservice.service.AnalyticsService;
import com.shorturl.tokenservice.service.ShortenUrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ShortenUrlController {
    private final ShortenUrlService shortenUrlService;
    private final AnalyticsService  analyticsService;

    @PostMapping("/shorten")
    public ResponseEntity<ShortenUrlResponseDTO> shortenUrl(@RequestBody @Valid ShortenUrlRequestDTO requestDTO) throws Exception {
        ShortenUrlResponseDTO shortenUrlResponseDTO = shortenUrlService.shortenUrl(requestDTO);
        return new ResponseEntity<>(shortenUrlResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<ShortenUrlResponseDTO> getShortenUrl(@PathVariable("shortUrl") String shortUrl, HttpServletRequest request) {
        ShortenUrlResponseDTO shortenUrlResponseDTO = shortenUrlService.getShortenUrl(shortUrl, request);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", shortenUrlResponseDTO.getLongUrl());
        return new ResponseEntity<>(shortenUrlResponseDTO, headers, HttpStatus.MOVED_PERMANENTLY);
    }

    @GetMapping("/analytics/{shortUrl}")
    public ResponseEntity<AnalyticsResponseDTO> getAnalytics(
            @PathVariable("shortUrl") String shortUrl) {
        AnalyticsResponseDTO analyticsResponseDTO = analyticsService.getAnalytics(shortUrl);
        return new ResponseEntity<>(analyticsResponseDTO, HttpStatus.OK);

    }

}
