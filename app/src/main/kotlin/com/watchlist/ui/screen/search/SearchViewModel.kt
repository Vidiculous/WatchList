package com.watchlist.ui.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watchlist.data.model.MediaType
import com.watchlist.data.model.SearchResult
import com.watchlist.data.model.WatchlistItem
import com.watchlist.data.repository.SearchRepository
import com.watchlist.data.repository.WatchlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val results: List<SearchResult> = emptyList(),
    val isLoading: Boolean = false,
    val activeTab: MediaType = MediaType.MOVIE,
    val addedIds: Set<Int> = emptySet()
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val watchlistRepository: WatchlistRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _results = MutableStateFlow<List<SearchResult>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _activeTab = MutableStateFlow(MediaType.MOVIE)
    private val _addedIds = MutableStateFlow<Set<Int>>(emptySet())

    val uiState: StateFlow<SearchUiState> = combine(
        _query, _results, _isLoading, _activeTab, _addedIds
    ) { query, results, loading, tab, added ->
        SearchUiState(query, results, loading, tab, added)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SearchUiState())

    private var searchJob: Job? = null

    fun onQueryChange(query: String) {
        _query.value = query
        searchJob?.cancel()
        if (query.length < 2) {
            _results.value = emptyList()
            return
        }
        searchJob = viewModelScope.launch {
            delay(400)
            _isLoading.value = true
            _results.value = searchRepository.search(query, _activeTab.value)
            _isLoading.value = false
        }
    }

    fun setTab(type: MediaType) {
        _activeTab.value = type
        _results.value = emptyList()
        val currentQuery = _query.value
        if (currentQuery.length >= 2) onQueryChange(currentQuery)
    }

    fun addToWatchlist(result: SearchResult) {
        viewModelScope.launch {
            val item = WatchlistItem(
                tmdbId = result.tmdbId,
                title = result.title,
                type = result.type,
                posterPath = result.posterPath
            )
            watchlistRepository.addItem(item)
            _addedIds.update { it + result.tmdbId }
        }
    }
}
