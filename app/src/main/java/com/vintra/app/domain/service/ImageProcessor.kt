package com.vintra.app.domain.service

import android.net.Uri

sealed interface ImageProcessResult {
    data class Success(val base64: String) : ImageProcessResult
    data class Error(val message: String) : ImageProcessResult
}

interface ImageProcessor {
    suspend fun compressToBase64(
        uri: Uri,
        maxDimensionPx: Int = 320,
        maxBytes: Int = 150_000
    ): ImageProcessResult
}