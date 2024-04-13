package com.mordent.ua.metadataservice.repository

import com.mordent.ua.metadataservice.model.data.SongMetadata
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface SongMetadataRepository: ReactiveMongoRepository<SongMetadata, Long> {
}