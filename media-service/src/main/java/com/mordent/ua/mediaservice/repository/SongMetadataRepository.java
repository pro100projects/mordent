package com.mordent.ua.mediaservice.repository;

import com.mordent.ua.mediaservice.model.data.SongMetadata;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongMetadataRepository extends ReactiveMongoRepository<SongMetadata, Long> {
}
