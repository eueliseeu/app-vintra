package com.vintra.app.data.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Base64
import com.vintra.app.domain.service.ImageProcessResult
import com.vintra.app.domain.service.ImageProcessor
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

private const val INITIAL_JPEG_QUALITY = 90
private const val MIN_JPEG_QUALITY = 30
private const val QUALITY_STEP = 10

class AndroidImageProcessor @Inject constructor(
    @ApplicationContext private val context: Context
) : ImageProcessor {

    override suspend fun compressToBase64(
        uri: Uri,
        maxDimensionPx: Int,
        maxBytes: Int
    ): ImageProcessResult = withContext(Dispatchers.IO) {
        try {
            val sampled = decodeSampledBitmap(uri, maxDimensionPx)
                ?: return@withContext ImageProcessResult.Error("Não foi possível ler a imagem selecionada.")

            val rotated = applyExifRotation(uri, sampled)
            val scaled = scaleToFit(rotated, maxDimensionPx)
            if (scaled !== rotated) rotated.recycle()

            val bytes = compressToJpegUnderLimit(scaled, maxBytes)
            scaled.recycle()

            ImageProcessResult.Success(Base64.encodeToString(bytes, Base64.NO_WRAP))
        } catch (exception: Exception) {
            ImageProcessResult.Error(exception.message ?: "Erro ao processar a imagem.")
        }
    }

    private fun decodeSampledBitmap(uri: Uri, maxDimensionPx: Int): Bitmap? {
        val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        val bounds = context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, boundsOptions)
        }
        if (bounds == null && boundsOptions.outWidth <= 0) return null

        var sampleSize = 1
        val halfWidth = boundsOptions.outWidth / 2
        val halfHeight = boundsOptions.outHeight / 2
        while (halfWidth / sampleSize >= maxDimensionPx && halfHeight / sampleSize >= maxDimensionPx) {
            sampleSize *= 2
        }

        val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        return context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, decodeOptions)
        }
    }

    private fun applyExifRotation(uri: Uri, bitmap: Bitmap): Bitmap {
        val degrees = context.contentResolver.openInputStream(uri)?.use { stream ->
            val exif = ExifInterface(stream)
            when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
        } ?: 0f

        if (degrees == 0f) return bitmap

        val matrix = Matrix().apply { postRotate(degrees) }
        val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        if (rotated !== bitmap) bitmap.recycle()
        return rotated
    }

    private fun scaleToFit(bitmap: Bitmap, maxDimensionPx: Int): Bitmap {
        val largestSide = maxOf(bitmap.width, bitmap.height)
        if (largestSide <= maxDimensionPx) return bitmap

        val scale = maxDimensionPx.toFloat() / largestSide
        val targetWidth = (bitmap.width * scale).toInt().coerceAtLeast(1)
        val targetHeight = (bitmap.height * scale).toInt().coerceAtLeast(1)
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }

    private fun compressToJpegUnderLimit(bitmap: Bitmap, maxBytes: Int): ByteArray {
        var quality = INITIAL_JPEG_QUALITY
        var output = ByteArrayOutputStream()

        while (true) {
            output = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, output)
            if (output.size() <= maxBytes || quality <= MIN_JPEG_QUALITY) break
            quality -= QUALITY_STEP
        }

        return output.toByteArray()
    }
}