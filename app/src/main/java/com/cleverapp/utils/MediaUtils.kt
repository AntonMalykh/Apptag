package com.cleverapp.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.io.InputStream

const val INTENT_IMAGE_TYPE = "image/*"

const val MAX_THUMBNAIL_IMAGE_FILE_SIZE = 256000
const val MAX_THUMBNAIL_IMAGE_DIMEN_SIZE = 1024

fun compressImage(originalImageStream: InputStream, desiredImgSize: Int): ByteArray {
    val original = BitmapFactory.decodeStream(originalImageStream)

    val maxDesiredDimen = MAX_THUMBNAIL_IMAGE_DIMEN_SIZE
    val origRatio: Double = original.width.toDouble() / original.height

    val newHeight: Int
    val newWidth: Int
    when {
        origRatio == 1.toDouble() -> {
            newHeight = maxDesiredDimen
            newWidth = maxDesiredDimen
        }
        origRatio > 1 -> {
            newWidth = maxDesiredDimen
            newHeight = (newWidth / origRatio).toInt()
        }
        else -> {
            newHeight = maxDesiredDimen
            newWidth = (newHeight * origRatio).toInt()
        }
    }

    val originalResized = Bitmap.createScaledBitmap(original, newWidth, newHeight, true)
    original.recycle()

    val START_QUALITY = 80
    val QUALITY_CHANGE_STEP = 10

    var compressedSize: Int
    var quality = START_QUALITY
    val compressed = ByteArrayOutputStream()
    do {
        compressed.flush()
        compressed.reset()

        originalResized.compress(Bitmap.CompressFormat.JPEG, quality, compressed)
        compressedSize = compressed.size()
        quality -= QUALITY_CHANGE_STEP
    }
    while (compressedSize > desiredImgSize && quality > QUALITY_CHANGE_STEP)

    return compressed.toByteArray()
}