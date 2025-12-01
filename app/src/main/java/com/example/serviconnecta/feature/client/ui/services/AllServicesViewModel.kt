package com.example.serviconnecta.feature.client.ui.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serviconnecta.feature.client.data.MockClientRepository
import com.example.serviconnecta.feature.client.domain.model.ServiceItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AllServicesUiState(
    val isLoading: Boolean = false,
    val services: List<ServiceItem> = emptyList(),
    val error: String? = null
)

class AllServicesViewModel : ViewModel() {

    private val repository = MockClientRepository

    private val _uiState = MutableStateFlow(AllServicesUiState())
    val uiState: StateFlow<AllServicesUiState> = _uiState.asStateFlow()

    init {
        loadAllServices()
    }

    fun loadAllServices() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Cargar todos los servicios sin filtros
            val result = repository.getServices(category = null, search = null)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    services = result.getOrNull() ?: emptyList()
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al cargar servicios"
                )
            }
        }
    }
}
