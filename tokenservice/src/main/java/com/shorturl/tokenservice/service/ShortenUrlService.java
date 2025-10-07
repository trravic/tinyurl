package com.shorturl.tokenservice.service;

import com.shorturl.tokenservice.dto.RangeResponse;
import com.shorturl.tokenservice.dto.ShortenUrlRequestDTO;
import com.shorturl.tokenservice.dto.ShortenUrlResponseDTO;
import com.shorturl.tokenservice.exception.NotFoundException;
import com.shorturl.tokenservice.model.ShortenUrlModel;
import com.shorturl.tokenservice.repository.ShortenUrlRepository;
import com.shorturl.tokenservice.util.Base62Encoder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ShortenUrlService {
    private static final Logger log = LoggerFactory.getLogger(ShortenUrlService.class);
    private final ShortenUrlRepository shortenUrlRepository;
    private final RedisService redisService;
    private final TokenRangeManagerService tokenRangeManagerService;

    private final AnalyticsService analyticsService;

    private static final String CURRENT_KEY = "token-service:range:current";
    private static final String MAX_KEY = "token-service:range:max";

    private static final String URL_CACHE_PREFIX = "url:";

    private synchronized Long nextCounter() throws Exception {
        Long currentValue = parseLong(redisService.getByKey(CURRENT_KEY));
        Long maxValue = parseLong(redisService.getByKey(MAX_KEY));

        if (currentValue == null || maxValue == null || currentValue >= maxValue) {
            log.info("Redis missing keys or expired. Calling zookeeper for getting new range");
            RangeResponse range = tokenRangeManagerService.fetchNewRangeFromTRMS((new Random().nextInt(10) + 1) * 1000);

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

    public ShortenUrlResponseDTO shortenUrl(ShortenUrlRequestDTO requestDTO) throws Exception {
        Long counter;
        String longUrl = requestDTO.getLongUrl().strip();
        String shortCode;
        ShortenUrlModel shortenUrl;

        //Custom Alias handling
        if(requestDTO.getCustomAlias() != null && !requestDTO.getCustomAlias().isBlank()) {
            shortCode = requestDTO.getCustomAlias().strip();
            Long decodedAlias = Base62Encoder.decode(shortCode);
            if(shortenUrlRepository.findByDecodedShortCode(decodedAlias).isPresent()) {
                throw new IllegalArgumentException("Custom alias already in use. Please choose another one.");
            }
            counter = decodedAlias;

            shortenUrl = this.shortenUrlRepository.save(
                    new ShortenUrlModel(null, longUrl, shortCode, counter, Instant.now())
            );

            // Cache the newly created URL mapping
            String cacheKey = URL_CACHE_PREFIX + shortCode;
            redisService.set(cacheKey, longUrl);
            log.info("Cached Custom Alias new URL mapping: {} -> {}", shortCode, longUrl);

        } else {
            // long url handling - system generated short code
            counter = nextCounter();

            shortCode = Base62Encoder.encode(counter);
            shortenUrl = this.shortenUrlRepository.save(
                    new ShortenUrlModel(null, longUrl, shortCode, counter, Instant.now())
            );
            // Cache the newly created URL mapping
            String cacheKey = URL_CACHE_PREFIX + shortCode;
            redisService.set(cacheKey, longUrl);
            log.info("Cached System-generated new URL mapping: {} -> {}", shortCode, longUrl);
        }
        return new ShortenUrlResponseDTO(longUrl, URL_CACHE_PREFIX + shortenUrl.getShortCode());
    }

    public ShortenUrlResponseDTO getShortenUrl(String shortUrl, HttpServletRequest request) {
        // Try cache first
        String cacheKey = URL_CACHE_PREFIX + shortUrl;
        String cachedLongUrl = redisService.getByKey(cacheKey);
        Long decodedShortUrl = Base62Encoder.decode(shortUrl);

        if (cachedLongUrl != null) {
            log.info("Cache HIT for shortUrl: {}", shortUrl);

            analyticsService.trackClick(shortUrl, decodedShortUrl, request);

            return new ShortenUrlResponseDTO(cachedLongUrl, shortUrl);
        }

        log.info("Cache MISS for shortUrl: {}. Fetching from MongoDB", shortUrl);

        ShortenUrlModel shortenUrl = this.shortenUrlRepository
                .findByDecodedShortCode(decodedShortUrl)
                .orElseThrow(() -> new NotFoundException("No such Short Url exists"));

        // Update cache for future requests
        redisService.set(cacheKey, shortenUrl.getLongUrl());
        log.info("Updated cache for shortUrl: {}", shortUrl);

        analyticsService.trackClick(shortUrl, decodedShortUrl, request);

        return new ShortenUrlResponseDTO(shortenUrl.getLongUrl(), shortenUrl.getShortCode());
    }
}
