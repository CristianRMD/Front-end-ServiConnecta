package com.example.serviconnecta.feature.client.ui.provider

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serviconnecta.feature.client.data.MockClientRepository
import com.example.serviconnecta.feature.client.domain.model.Provider
import com.example.serviconnecta.feature.client.domain.model.ServiceItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProviderDetailUiState(
    val isLoading: Boolean = false,
    val provider: Provider? = null,
    val services: List<ServiceItem> = emptyList(),
    val error: String? = null
)

class ProviderDetailViewModel : ViewModel() {

    private val repository = MockClientRepository

    private val _uiState = MutableStateFlow(ProviderDetailUiState())
    val uiState: StateFlow<ProviderDetailUiState> = _uiState.asStateFlow()

    fun loadProvider(providerId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val providerResult = repository.getProviderDetail(providerId)
            val servicesResult = repository.getProviderServices(providerId)

            if (providerResult.isSuccess && servicesResult.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    provider = providerResult.getOrNull(),
                    services = servicesResult.getOrNull() ?: emptyList()
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "No se pudo cargar la información del trabajador"
                )
            }
        }
    }
}
