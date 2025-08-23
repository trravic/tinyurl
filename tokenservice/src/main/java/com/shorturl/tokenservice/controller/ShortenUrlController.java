package com.shorturl.tokenservice.controller;

import com.shorturl.tokenservice.dto.ShortenUrlRequestDTO;
import com.shorturl.tokenservice.dto.ShortenUrlResponseDTO;
import com.shorturl.tokenservice.service.ShortenUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ShortenUrlController {
    private final ShortenUrlService shortenUrlService;
    @PostMapping("/shorten")
    public ResponseEntity<ShortenUrlResponseDTO> shortenUrl(@RequestBody ShortenUrlRequestDTO requestDTO) {
        ShortenUrlResponseDTO shortenUrlResponseDTO = shortenUrlService.shortenUrl(requestDTO);
        return new ResponseEntity<>(shortenUrlResponseDTO, HttpStatus.MOVED_PERMANENTLY);
    }
}
