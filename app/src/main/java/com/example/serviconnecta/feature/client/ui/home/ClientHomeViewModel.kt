package com.example.serviconnecta.feature.client.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serviconnecta.feature.client.data.MockClientRepository
import com.example.serviconnecta.feature.client.domain.model.Category
import com.example.serviconnecta.feature.client.domain.model.Provider
import com.example.serviconnecta.feature.client.domain.model.ServiceItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClientHomeUiState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val bestServices: List<ServiceItem> = emptyList(),
    val featuredWorkers: List<Provider> = emptyList(),
    val deliveryAddress: String = "Ate",
    val errorMessage: String? = null
)

class ClientHomeViewModel(
    private val repository: MockClientRepository = MockClientRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientHomeUiState())
    val uiState: StateFlow<ClientHomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val categories = repository.getCategories().getOrDefault(emptyList())
                val services = repository.getServices().getOrDefault(emptyList())
                val workers = repository.getFeaturedWorkers().getOrDefault(emptyList())

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        categories = categories,
                        bestServices = services.take(3),
                        featuredWorkers = workers.take(2),
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al cargar datos"
                    )
                }
            }
        }
    }

    fun refresh() {
        loadData()
    }
}
