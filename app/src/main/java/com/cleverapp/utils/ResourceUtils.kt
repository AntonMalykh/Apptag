package com.cleverapp.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat

/**
 * Extracts color from context theme by attribute id
 * @param context context which theme is used to resolve the attribute
 * @param colorAttrId id of the attribute
 * @return int color value
 */
@ColorInt
internal fun getColorByAttr(context: Context, @AttrRes colorAttrId: Int): Int {
    val typedValue = TypedValue()
    context.theme.resolveAttribute(colorAttrId, typedValue, true)
    return try {
        ContextCompat.getColor(context, typedValue.resourceId)
    } catch (ex: Resources.NotFoundException) {
        Color.WHITE
    }
}