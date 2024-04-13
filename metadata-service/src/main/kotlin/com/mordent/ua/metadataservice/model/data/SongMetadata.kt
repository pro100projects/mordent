package com.mordent.ua.metadataservice.model.data

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class SongMetadata(
    @Id
    val id: Long,
    val metadata: Map<String, String>
) {
}