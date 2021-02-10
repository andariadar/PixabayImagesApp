package com.andariadar.pixabayimages.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.andariadar.pixabayimages.api.PixabayApi
import com.andariadar.pixabayimages.model.Image
import retrofit2.HttpException
import java.io.IOException

private const val IMAGES_STARTING_PAGE_INDEX = 1
private const val TAG = "PagingSource"

class PagingSource(
        private val api: PixabayApi,
        private val query: String,
        private val colors: String,
        private val order: String,
        private val category: String
        ): PagingSource<Int, Image>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Image> {
        val position = params.key ?: IMAGES_STARTING_PAGE_INDEX
        return try {
            val response = api.getImagesByQuery(query, colors, order, category, position, params.loadSize)
            val repos = response.hits

            Log.i(TAG, "position: $position, load size: ${params.loadSize}")

            val nextKey = if (repos.isEmpty()) {
                null
            } else {
                // initial load size = 3 * NETWORK_PAGE_SIZE
                // ensure we're not requesting duplicating items, at the 2nd request
                position + (params.loadSize / 10)
            }

            LoadResult.Page(
                data = repos,
                prevKey = if (position == IMAGES_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )

        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Image>): Int? {
        return state.anchorPosition
    }
}



