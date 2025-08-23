package com.shorturl.tokenservice.repository;

import com.shorturl.tokenservice.model.ShortenUrlModel;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShortenUrlRepository extends MongoRepository<ShortenUrlModel, String> {
}
