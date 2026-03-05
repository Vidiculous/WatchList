package com.watchlist.ui.screen.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watchlist.data.model.StreamingAvailability
import com.watchlist.data.model.WatchlistItem
import com.watchlist.data.preferences.RegionDataStore
import com.watchlist.data.repository.StreamingRepository
import com.watchlist.data.repository.WatchlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SortOrder { DATE_ADDED, SCORE, RELEASE_DATE }
enum class WatchTab { TO_WATCH, WATCHED }

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

    private val _sortOrder = MutableStateFlow(SortOrder.DATE_ADDED)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    private val _activeTab = MutableStateFlow(WatchTab.TO_WATCH)
    val activeTab: StateFlow<WatchTab> = _activeTab.asStateFlow()

    val toWatchList: StateFlow<List<WatchlistItem>> = combine(
        watchlistRepository.unwatchedItems,
        _sortOrder
    ) { items, order -> sortItems(items, order) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val watchedList: StateFlow<List<WatchlistItem>> = combine(
        watchlistRepository.watchedItems,
        _sortOrder
    ) { items, order -> sortItems(items, order) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val availabilityByItem: StateFlow<Map<Long, List<StreamingAvailability>>> =
        regionDataStore.regionCode
            .flatMapLatest { region ->
                streamingRepository.getAvailabilityForCountry(region)
                    .map { list -> list.groupBy { it.watchlistItemId } }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    private fun sortItems(items: List<WatchlistItem>, order: SortOrder): List<WatchlistItem> =
        when (order) {
            SortOrder.DATE_ADDED -> items
            SortOrder.SCORE -> items.sortedByDescending { it.voteAverage ?: -1.0 }
            SortOrder.RELEASE_DATE -> items.sortedByDescending { it.releaseDate ?: "" }
        }

    fun openSearch() { _showSearch.value = true }
    fun closeSearch() { _showSearch.value = false }
    fun setSortOrder(order: SortOrder) { _sortOrder.value = order }
    fun setActiveTab(tab: WatchTab) { _activeTab.value = tab }

    fun removeItem(item: WatchlistItem) {
        viewModelScope.launch { watchlistRepository.removeItem(item.id) }
    }

    fun markWatched(item: WatchlistItem, watched: Boolean) {
        viewModelScope.launch { watchlistRepository.markWatched(item.id, watched) }
    }

    fun setRating(item: WatchlistItem, rating: Int?) {
        viewModelScope.launch { watchlistRepository.setUserRating(item.id, rating) }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            val items = watchlistRepository.watchlist.first()
            val region = regionDataStore.regionCode.first()
            watchlistRepository.refreshMissingMetadata(items)
            streamingRepository.refreshAll(items, region)
            _isRefreshing.value = false
        }
    }
}
