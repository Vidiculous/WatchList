package com.watchlist.ui.screen.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watchlist.data.model.ServiceRanking
import com.watchlist.data.model.StreamingAvailability
import com.watchlist.data.model.WatchlistItem
import com.watchlist.data.preferences.RegionDataStore
import com.watchlist.data.repository.StreamingRepository
import com.watchlist.data.repository.WatchlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ServicesViewModel @Inject constructor(
    private val watchlistRepository: WatchlistRepository,
    private val streamingRepository: StreamingRepository,
    private val regionDataStore: RegionDataStore
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val rankings: StateFlow<List<ServiceRanking>> = regionDataStore.regionCode
        .flatMapLatest { countryCode ->
            combine(
                watchlistRepository.watchlist,
                streamingRepository.getAvailabilityForCountry(countryCode)
            ) { items, availability ->
                buildRankings(items, availability)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val regionCode = regionDataStore.regionCode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "US")

    private fun buildRankings(
        items: List<WatchlistItem>,
        availability: List<StreamingAvailability>
    ): List<ServiceRanking> {
        val itemMap = items.associateBy { it.id }
        val rawRankings = availability
            .groupBy { it.serviceName }
            .map { (service, rows) ->
                ServiceRanking(
                    serviceName = service,
                    logoPath = rows.firstOrNull()?.serviceLogoPath,
                    availableItems = rows.mapNotNull { itemMap[it.watchlistItemId] }
                )
            }

        // Collapse services with identical available items into one entry
        return rawRankings
            .groupBy { ranking -> ranking.availableItems.map { it.id }.sorted() }
            .map { (_, group) ->
                if (group.size == 1) {
                    group.first()
                } else {
                    // Sort by name length so the shortest (primary) name comes first
                    val sorted = group.sortedBy { it.serviceName.length }
                    sorted.first().copy(
                        serviceName = sorted.joinToString(" / ") { it.serviceName }
                    )
                }
            }
            .sortedByDescending { it.count }
    }
}
