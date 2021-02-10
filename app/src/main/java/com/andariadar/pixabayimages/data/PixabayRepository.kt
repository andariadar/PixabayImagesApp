package com.andariadar.pixabayimages.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.andariadar.pixabayimages.api.PixabayApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PixabayRepository@Inject constructor(
    private val api: PixabayApi
) {
    companion object {
        private const val NETWORK_PAGE_SIZE = 10
    }

    fun getImages(query: String, colors: String, order: String, category: String) = Pager(
        config = PagingConfig(
            pageSize = NETWORK_PAGE_SIZE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { PagingSource(api, query, colors, order, category) }
    ).flow
}