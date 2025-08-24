package com.shorturl.tokenservice.repository;

import com.shorturl.tokenservice.model.ShortenUrlModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ShortenUrlRepository extends MongoRepository<ShortenUrlModel, String> {
    Optional<ShortenUrlModel> findByDecodedShortCode(Long shortUrl);
}
