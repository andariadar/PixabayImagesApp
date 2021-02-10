package com.andariadar.pixabayimages.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Image(
    val id: String,
    val webformatURL: String,
    val user: String,
    val likes: Int
): Parcelable
