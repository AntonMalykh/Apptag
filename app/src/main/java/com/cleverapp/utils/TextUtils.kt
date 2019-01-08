package com.cleverapp.utils

import com.cleverapp.repository.data.ImageTag

fun List<ImageTag>.toPlainText(): CharSequence {
    return this.fold(StringBuilder()){
        acc, tag ->
        acc.append('#').append(tag.tag.replace(" ", ""))
    }
}