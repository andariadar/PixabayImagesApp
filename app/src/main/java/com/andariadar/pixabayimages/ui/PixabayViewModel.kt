package com.andariadar.pixabayimages.ui

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.andariadar.pixabayimages.data.PixabayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class Multi(val a: String, val b: String, val c: String, val d: String)

@HiltViewModel
@ExperimentalCoroutinesApi
class PixabayViewModel@Inject constructor(
    private val repository: PixabayRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _currentQuery = savedStateHandle.getLiveData(CURRENT_QUERY, DEFAULT_QUERY)
    val currentQuery = _currentQuery
    private val _currentColor = savedStateHandle.getLiveData(CURRENT_COLOR, DEFAULT_COLOR)
    val currentColor = _currentColor
    private val _currentOrder = savedStateHandle.getLiveData(CURRENT_ORDER, DEFAULT_ORDER)
    val currentOrder = _currentOrder
    private val _currentCategory = savedStateHandle.getLiveData(CURRENT_CATEGORY, DEFAULT_CATEGORY)
    val currentCategory = _currentCategory

    /*val test = currentQuery.asFlow().distinctUntilChanged().flatMapLatest { query ->
        repository.getImages(query).cachedIn(viewModelScope)
    }*/

    val imagesFlow = combine(
        _currentQuery.asFlow(),
        _currentColor.asFlow(),
        _currentOrder.asFlow(),
        _currentCategory.asFlow()
    ) { query, color, order, category ->
        Multi(query, color, order, category)
    }.distinctUntilChanged()
     //.debounce(400)
     .flatMapLatest { (query, color, order, category) ->
        repository.getImages(query, color, order, category).cachedIn(viewModelScope)
    }

    fun setQuery(query: String) {
        savedStateHandle.set(CURRENT_QUERY, query)
    }

    fun setColor(color: String) {
        savedStateHandle.set(CURRENT_COLOR, color)
    }

    fun setOrder(order: String) {
        savedStateHandle.set(CURRENT_ORDER, order)
    }

    fun setCategory(category: String) {
        savedStateHandle.set(CURRENT_CATEGORY, category)
    }

    companion object {
        private const val CURRENT_QUERY = "query"
        private const val DEFAULT_QUERY = ""
        private const val CURRENT_COLOR = "color"
        private const val DEFAULT_COLOR = ""
        private const val CURRENT_ORDER = "order"
        private const val DEFAULT_ORDER = "popular"
        private const val CURRENT_CATEGORY = "category"
        private const val DEFAULT_CATEGORY = ""
    }
}
