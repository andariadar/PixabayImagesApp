package com.andariadar.pixabayimages.api

import com.andariadar.pixabayimages.model.Image

data class ResultList (
    val hits: List<Image>
)