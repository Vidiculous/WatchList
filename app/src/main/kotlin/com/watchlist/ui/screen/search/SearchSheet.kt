package com.watchlist.ui.screen.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.watchlist.data.model.MediaType
import com.watchlist.ui.component.SearchResultItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSheet(
    onDismiss: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(modifier = Modifier.padding(bottom = 12.dp)) {
                FilterChip(
                    selected = state.activeTab == MediaType.MOVIE,
                    onClick = { viewModel.setTab(MediaType.MOVIE) },
                    label = { Text("Movies") },
                    modifier = Modifier.padding(end = 8.dp)
                )
                FilterChip(
                    selected = state.activeTab == MediaType.TV,
                    onClick = { viewModel.setTab(MediaType.TV) },
                    label = { Text("TV Series") }
                )
            }

            OutlinedTextField(
                value = state.query,
                onValueChange = { viewModel.onQueryChange(it) },
                label = { Text(if (state.activeTab == MediaType.MOVIE) "Search movies..." else "Search TV series...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                singleLine = true
            )

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        LazyColumn {
            items(state.results, key = { it.tmdbId }) { result ->
                SearchResultItem(
                    result = result,
                    isAdded = result.tmdbId in state.addedIds,
                    onAdd = { viewModel.addToWatchlist(result) }
                )
            }
        }
    }
}
