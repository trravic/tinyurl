package com.shorturl.tokenservice.service;

import com.shorturl.tokenservice.dto.ShortenUrlRequestDTO;
import com.shorturl.tokenservice.dto.ShortenUrlResponseDTO;
import com.shorturl.tokenservice.model.ShortenUrlModel;
import com.shorturl.tokenservice.repository.ShortenUrlRepository;
import com.shorturl.tokenservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

// Get init, current, max range using redis or in-memory.
// If no range or out of range then ask trs for new range.
// atomic increment the range.
// store the new link to db with the integer using base62.

@Service
@RequiredArgsConstructor
public class ShortenUrlService {

    private final ShortenUrlRepository shortenUrlRepository;
    private Integer count = 1000;

    public ShortenUrlResponseDTO shortenUrl(ShortenUrlRequestDTO requestDTO) {
        Integer counter = count;
        count++;
        ShortenUrlModel shortenUrl = this.shortenUrlRepository.save(new ShortenUrlModel(null, requestDTO.getLongUrl(), Base62Encoder.encode(counter), Instant.now()));
        return new ShortenUrlResponseDTO(shortenUrl.getLongUrl(), shortenUrl.getShortUrl());
    }
}
