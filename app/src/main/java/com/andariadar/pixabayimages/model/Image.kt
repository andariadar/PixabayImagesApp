package com.andariadar.pixabayimages.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Image(
    val id: String,
    val webformatURL: String,
    val largeImageURL: String,
    val user: String,
    val user_id: String,
    val likes: Int
): Parcelable {
    val profileUrl get() = "https://pixabay.com/users/$user-$user_id/"
}
