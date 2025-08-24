package com.shorturl.tokenservice.controller;

import com.shorturl.tokenservice.dto.ShortenUrlRequestDTO;
import com.shorturl.tokenservice.dto.ShortenUrlResponseDTO;
import com.shorturl.tokenservice.service.ShortenUrlService;
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

    @PostMapping("/shorten")
    public ResponseEntity<ShortenUrlResponseDTO> shortenUrl(@RequestBody @Valid ShortenUrlRequestDTO requestDTO) {
        ShortenUrlResponseDTO shortenUrlResponseDTO = shortenUrlService.shortenUrl(requestDTO);
        return new ResponseEntity<>(shortenUrlResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<ShortenUrlResponseDTO> getShortenUrl(@PathVariable("shortUrl") String shortUrl) {
        ShortenUrlResponseDTO shortenUrlResponseDTO = shortenUrlService.getShortenUrl(shortUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", shortenUrlResponseDTO.getLongUrl());
        return new ResponseEntity<>(shortenUrlResponseDTO, headers, HttpStatus.MOVED_PERMANENTLY);
    }
}
