package com.cleverapp.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import androidx.exifinterface.media.ExifInterface.*
import java.io.ByteArrayOutputStream
import java.io.InputStream

const val INTENT_IMAGE_TYPE = "image/*"

const val MAX_THUMBNAIL_IMAGE_FILE_SIZE = 256000
const val MAX_THUMBNAIL_IMAGE_DIMEN_SIZE = 1024

fun getImageRotation(imageInputStream: InputStream): Int {
    return when (ExifInterface(imageInputStream).getAttribute(TAG_ORIENTATION)?.toInt()) {
        ORIENTATION_ROTATE_270 -> 270
        ORIENTATION_ROTATE_180 -> 180
        ORIENTATION_ROTATE_90 -> 90
        else -> 0
    }
}

fun compressImage(originalImageStream: InputStream, desiredImgSize: Int, rotationDegrees: Int  = 0): ByteArray {
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

    val matrix = Matrix().apply {
        setScale(newWidth.toFloat() / original.width, newHeight.toFloat() / original.height)
        setRotate(rotationDegrees.toFloat())
    }

    val originalResized =
            Bitmap.createBitmap(original, 0, 0, original.width, original.height, matrix, true)

    if (originalResized != original)
        original.recycle()


    val START_QUALITY = 80
    var QUALITY_CHANGE_STEP = 10

    var compressedSize: Int
    var quality = START_QUALITY
    val compressed = ByteArrayOutputStream()
    do {
        compressed.flush()
        compressed.reset()

        originalResized.compress(Bitmap.CompressFormat.JPEG, quality, compressed)
        compressedSize = compressed.size()
        if (quality <= QUALITY_CHANGE_STEP)
            QUALITY_CHANGE_STEP /= 2
        quality -= QUALITY_CHANGE_STEP
    }
    while (compressedSize > desiredImgSize && quality > 0)

    originalResized.recycle()
    return compressed.toByteArray()
}