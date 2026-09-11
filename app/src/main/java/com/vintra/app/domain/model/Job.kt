package com.vintra.app.domain.model

data class Job(
    val id: String,
    val publisherUid: String,
    val companyName: String,
    val companyUsername: String,
    val companyPhotoBase64: String?,
    val title: String,
    val description: String,
    val linkUrl: String,
    val buttonLabel: String = "Open",
    val tags: List<JobTag> = emptyList(),
    val createdAt: Long
)