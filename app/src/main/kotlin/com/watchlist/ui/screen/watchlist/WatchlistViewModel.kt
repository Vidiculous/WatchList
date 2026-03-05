package com.watchlist.ui.screen.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watchlist.data.model.WatchlistItem
import com.watchlist.data.preferences.RegionDataStore
import com.watchlist.data.repository.StreamingRepository
import com.watchlist.data.repository.WatchlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val watchlistRepository: WatchlistRepository,
    private val streamingRepository: StreamingRepository,
    private val regionDataStore: RegionDataStore
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _showSearch = MutableStateFlow(false)
    val showSearch = _showSearch.asStateFlow()

    val watchlist = watchlistRepository.watchlist
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun openSearch() { _showSearch.value = true }
    fun closeSearch() { _showSearch.value = false }

    fun removeItem(item: WatchlistItem) {
        viewModelScope.launch { watchlistRepository.removeItem(item.id) }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            val region = regionDataStore.regionCode.first()
            streamingRepository.refreshAll(watchlist.value, region)
            _isRefreshing.value = false
        }
    }
}
