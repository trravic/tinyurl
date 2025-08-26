package com.shorturl.tokenservice.service;

import com.shorturl.tokenservice.dto.RangeResponse;
import com.shorturl.tokenservice.dto.ShortenUrlRequestDTO;
import com.shorturl.tokenservice.dto.ShortenUrlResponseDTO;
import com.shorturl.tokenservice.exception.NotFoundException;
import com.shorturl.tokenservice.model.ShortenUrlModel;
import com.shorturl.tokenservice.repository.ShortenUrlRepository;
import com.shorturl.tokenservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ShortenUrlService {
    private final ShortenUrlRepository shortenUrlRepository;
    private final RedisService redisService;

    private static final String CURRENT_KEY = "token-service:range:current";
    private static final String MAX_KEY = "token-service:range:max";

    private synchronized Long nextCounter() {
        Long currentValue = parseLong(redisService.getByKey(CURRENT_KEY));
        Long maxValue = parseLong(redisService.getByKey(MAX_KEY));

        if (currentValue == null || maxValue == null || currentValue >= maxValue) {
            RangeResponse range = fetchNewRangeFromTRS();  // External HTTP call

            currentValue = range.getStart();
            maxValue = range.getEnd();

            redisService.set(CURRENT_KEY, String.valueOf(currentValue + 1));
            redisService.set(MAX_KEY, String.valueOf(maxValue));
        } else {
            redisService.set(CURRENT_KEY, String.valueOf(currentValue + 1));
        }

        return currentValue;
    }

    private Long parseLong(String value) {
        try {
            return value != null ? Long.parseLong(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private RangeResponse fetchNewRangeFromTRS() {
        // TODO: Replace with actual HTTP client code or service call
        return new RangeResponse(10000L, 20000L);
    }

    public ShortenUrlResponseDTO shortenUrl(ShortenUrlRequestDTO requestDTO) {
        Long counter = nextCounter();
        String longUrl = requestDTO.getLongUrl().strip();
        String shortCode = Base62Encoder.encode(counter);
        ShortenUrlModel shortenUrl = this.shortenUrlRepository.save(
                new ShortenUrlModel(null, longUrl, shortCode, counter, Instant.now())
        );
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
