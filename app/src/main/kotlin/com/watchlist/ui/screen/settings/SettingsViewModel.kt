package com.watchlist.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watchlist.data.preferences.RegionDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val regionDataStore: RegionDataStore
) : ViewModel() {

    val regionCode = regionDataStore.regionCode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "US")

    val apiKey = regionDataStore.apiKey
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    fun setRegion(code: String) {
        viewModelScope.launch { regionDataStore.setRegion(code) }
    }

    fun setApiKey(key: String) {
        viewModelScope.launch { regionDataStore.setApiKey(key) }
    }
}
