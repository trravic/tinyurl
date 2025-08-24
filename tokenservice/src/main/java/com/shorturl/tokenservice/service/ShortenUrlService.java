package com.shorturl.tokenservice.service;

import com.shorturl.tokenservice.dto.ShortenUrlRequestDTO;
import com.shorturl.tokenservice.dto.ShortenUrlResponseDTO;
import com.shorturl.tokenservice.exception.NotFoundException;
import com.shorturl.tokenservice.model.ShortenUrlModel;
import com.shorturl.tokenservice.repository.ShortenUrlRepository;
import com.shorturl.tokenservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final RedisService redisService;
    private Long count = 1000L;

    public ShortenUrlResponseDTO shortenUrl(ShortenUrlRequestDTO requestDTO) {
        Long counter = count;
        count++;
        String longUrl = requestDTO.getLongUrl().strip();
        ShortenUrlModel shortenUrl = this.shortenUrlRepository.save(new ShortenUrlModel(null, longUrl, Base62Encoder.encode(counter), counter, Instant.now()));
        return new ShortenUrlResponseDTO(longUrl, shortenUrl.getShortCode());
    }

    public ShortenUrlResponseDTO getShortenUrl(String shortUrl) {
        Long decodedShortUrl = Base62Encoder.decode(shortUrl);
        ShortenUrlModel shortenUrl = this.shortenUrlRepository
                .findByDecodedShortCode(decodedShortUrl)
                .orElseThrow(() -> new NotFoundException("No such Short Url exists"));
        return new ShortenUrlResponseDTO(shortenUrl.getLongUrl(), shortenUrl.getShortCode());
    }
}
