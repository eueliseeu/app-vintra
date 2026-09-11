package com.vintra.app.data.model

data class JobDto @JvmOverloads constructor(
    val publisherUid: String = "",
    val companyName: String = "",
    val companyUsername: String = "",
    val companyPhotoBase64: String? = null,
    val title: String = "",
    val description: String = "",
    val linkUrl: String = "",
    val buttonLabel: String = "Open",
    val tags: List<String> = emptyList(),
    val createdAt: Long = 0L
)