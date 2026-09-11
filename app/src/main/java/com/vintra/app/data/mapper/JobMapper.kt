package com.vintra.app.data.mapper

import com.vintra.app.data.model.JobDto
import com.vintra.app.domain.model.Job
import com.vintra.app.domain.model.JobTag

fun JobDto.toDomain(id: String): Job = Job(
    id = id,
    publisherUid = publisherUid,
    companyName = companyName,
    companyUsername = companyUsername,
    companyPhotoBase64 = companyPhotoBase64,
    title = title,
    description = description,
    linkUrl = linkUrl,
    buttonLabel = buttonLabel.ifBlank { "Open" },
    tags = tags.mapNotNull { raw ->
        runCatching { JobTag.valueOf(raw) }.getOrNull()
    },
    createdAt = createdAt
)