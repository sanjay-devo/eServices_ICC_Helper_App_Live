package com.icc.eserviceshelper.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.icc.eserviceshelper.models.Category
import com.icc.eserviceshelper.repository.CategoryCache
import com.icc.eserviceshelper.repository.FirebaseRepository
import com.icc.eserviceshelper.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FirebaseRepository()
    private val cache = CategoryCache(application)
    
    private val _searchQuery = MutableStateFlow("")
    private val _allCategories = MutableStateFlow<UiState<List<Category>>>(UiState.Loading)

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // 1. Show cached data instantly if available
            val cachedData = cache.getCachedCategories()
            if (cachedData != null) {
                _allCategories.value = UiState.Success(cachedData)
            } else {
                _allCategories.value = UiState.Loading
            }

            // 2. Silently check Firebase version in background
            val remoteVersion = repository.getRemoteVersion()
            val cachedVersion = cache.getCachedVersion()

            // If remote is unavailable or data is same version, do nothing
            if ((remoteVersion != -1L) && (remoteVersion != cachedVersion)) {
                // 3. Version changed, fetch latest data
                repository.getCategoriesOnce()
                    .onSuccess { (categories, version) ->
                        // Only refresh UI if data actually changed
                        if (categories != cachedData) {
                            cache.saveCategories(categories, version)
                            _allCategories.value = UiState.Success(categories)
                        }
                    }
                    .onFailure {
                        if (cachedData == null) {
                            _allCategories.value = UiState.Error(it.message ?: "Connection failed")
                        }
                    }
            } else if (cachedData == null && remoteVersion == -1L) {
                // Error state only if no cache and no internet
                _allCategories.value = UiState.Error("No internet connection")
            }
        }
    }

    // Filtered data computed on a background thread, maintaining UiState
    val filteredCategories = combine(_allCategories, _searchQuery) { state, query ->
        when (state) {
            is UiState.Success -> {
                val categories = state.data
                val filtered = if (query.isBlank()) {
                    categories
                } else {
                    categories.filter { category ->
                        category.title.contains(query, ignoreCase = true) ||
                                category.items?.values?.any { item ->
                                    item.title.contains(query, ignoreCase = true) ||
                                            item.keywords?.any { it.contains(query, ignoreCase = true) } == true
                                } == true
                    }
                }
                UiState.Success(filtered)
            }
            is UiState.Loading -> UiState.Loading
            is UiState.Error -> state
        }
    }.flowOn(Dispatchers.Default)
     .asLiveData()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
