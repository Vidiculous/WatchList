package com.watchlist.ui.screen.watchlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.watchlist.ui.component.WatchlistItemCard
import com.watchlist.ui.screen.search.SearchSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistScreen(
    viewModel: WatchlistViewModel,
    onNavigateToServices: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val toWatchList by viewModel.toWatchList.collectAsState()
    val watchedList by viewModel.watchedList.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val showSearch by viewModel.showSearch.collectAsState()
    val availabilityByItem by viewModel.availabilityByItem.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()

    val currentList = if (activeTab == WatchTab.TO_WATCH) toWatchList else watchedList

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("WatchList") },
                    actions = {
                        IconButton(onClick = { viewModel.refresh() }, enabled = !isRefreshing) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh availability")
                        }
                        IconButton(onClick = onNavigateToServices) {
                            Icon(Icons.Default.Star, contentDescription = "View services")
                        }
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    }
                )
                if (isRefreshing) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = sortOrder == SortOrder.DATE_ADDED,
                        onClick = { viewModel.setSortOrder(SortOrder.DATE_ADDED) },
                        label = { Text("Date added") }
                    )
                    FilterChip(
                        selected = sortOrder == SortOrder.SCORE,
                        onClick = { viewModel.setSortOrder(SortOrder.SCORE) },
                        label = { Text("Score") }
                    )
                    FilterChip(
                        selected = sortOrder == SortOrder.RELEASE_DATE,
                        onClick = { viewModel.setSortOrder(SortOrder.RELEASE_DATE) },
                        label = { Text("Release date") }
                    )
                }
                TabRow(selectedTabIndex = activeTab.ordinal) {
                    Tab(
                        selected = activeTab == WatchTab.TO_WATCH,
                        onClick = { viewModel.setActiveTab(WatchTab.TO_WATCH) },
                        text = { Text("To Watch (${toWatchList.size})") }
                    )
                    Tab(
                        selected = activeTab == WatchTab.WATCHED,
                        onClick = { viewModel.setActiveTab(WatchTab.WATCHED) },
                        text = { Text("Watched (${watchedList.size})") }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.openSearch() }) {
                Icon(Icons.Default.Add, contentDescription = "Add movie or series")
            }
        }
    ) { innerPadding ->
        if (currentList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (activeTab == WatchTab.TO_WATCH) "Your watchlist is empty" else "No watched items yet",
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (activeTab == WatchTab.TO_WATCH) {
                        Text(
                            text = "Tap + to add movies and series",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(currentList, key = { it.id }) { item ->
                    WatchlistItemCard(
                        item = item,
                        services = availabilityByItem[item.id] ?: emptyList(),
                        onDelete = { viewModel.removeItem(item) },
                        onMarkWatched = { watched -> viewModel.markWatched(item, watched) },
                        onSetRating = { rating -> viewModel.setRating(item, rating) }
                    )
                }
            }
        }
    }

    if (showSearch) {
        SearchSheet(
            onDismiss = { viewModel.closeSearch() }
        )
    }
}
