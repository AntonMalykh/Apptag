package com.cleverapp.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.io.InputStream

const val INTENT_IMAGE_TYPE = "image/*"

const val MAX_THUMBNAIL_IMAGE_SIZE = 512000

fun compressImage(originalImageStream: InputStream, desiredImgSize: Int): ByteArray {
    val original = BitmapFactory.decodeStream(originalImageStream)

    val maxDesiredDimen = Math.sqrt(desiredImgSize.toDouble()).toInt()
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
            newWidth = (newHeight / origRatio).toInt()
        }
    }

    val originalResized = Bitmap.createScaledBitmap(original, newWidth, newHeight, true)
    original.recycle()

    var compressedSize: Int
    var quality = 100
    val compressed = ByteArrayOutputStream()
    do {
        compressed.flush()
        compressed.reset()

        originalResized.compress(Bitmap.CompressFormat.JPEG, quality, compressed)
        compressedSize = compressed.size()
        quality -= 5
    }
    while (compressedSize > desiredImgSize && quality > 5)

    return compressed.toByteArray()
}