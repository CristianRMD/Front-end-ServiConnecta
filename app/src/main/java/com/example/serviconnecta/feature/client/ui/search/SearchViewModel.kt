package com.example.serviconnecta.feature.client.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serviconnecta.feature.client.data.MockClientRepository
import com.example.serviconnecta.feature.client.domain.model.ServiceItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SearchUiState(
    val isLoading: Boolean = false,
    val results: List<ServiceItem> = emptyList(),
    val error: String? = null
)

class SearchViewModel : ViewModel() {

    private val repository = MockClientRepository

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun search(query: String) {
        searchJob?.cancel()

        if (query.isBlank()) {
            _uiState.value = SearchUiState()
            return
        }

        searchJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            delay(300) // Debounce

            val result = repository.getServices(search = query)

            if (result.isSuccess) {
                _uiState.value = SearchUiState(
                    isLoading = false,
                    results = result.getOrNull() ?: emptyList()
                )
            } else {
                _uiState.value = SearchUiState(
                    isLoading = false,
                    error = "Error al buscar servicios"
                )
            }
        }
    }
}
