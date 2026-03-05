package com.watchlist.ui.screen.watchlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
    val watchlist by viewModel.watchlist.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val showSearch by viewModel.showSearch.collectAsState()

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
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.openSearch() }) {
                Icon(Icons.Default.Add, contentDescription = "Add movie or series")
            }
        }
    ) { innerPadding ->
        if (watchlist.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Your watchlist is empty",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Tap + to add movies and series",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
                items(watchlist, key = { it.id }) { item ->
                    WatchlistItemCard(
                        item = item,
                        onDelete = { viewModel.removeItem(item) }
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
