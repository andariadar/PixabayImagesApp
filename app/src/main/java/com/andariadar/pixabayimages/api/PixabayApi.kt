package com.andariadar.pixabayimages.api

import com.andariadar.pixabayimages.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface PixabayApi {

    companion object {
        const val BASE_URL = "https://pixabay.com/"
        const val API_KEY = BuildConfig.API_KEY
    }

    @GET("api/?key=$API_KEY")
    suspend fun getImagesByQuery(@Query("q") q: String,
                                 @Query("colors") colors: String,
                                 @Query("order") order: String,
                                 @Query("category") category: String,
                                 @Query("page") page: Int,
                                 @Query("per_page") perPage: Int ): ResultList
}