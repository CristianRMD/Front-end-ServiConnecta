package com.example.serviconnecta.feature.client.ui.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serviconnecta.feature.client.data.MockClientRepository
import com.example.serviconnecta.feature.client.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookingUiState(
    val isLoading: Boolean = false,
    val service: ServiceItem? = null,
    val selectedDate: String? = null,
    val selectedTime: String? = null,
    val selectedLocation: Location? = null,
    val selectedPaymentMethod: PaymentMethod? = null,
    val locations: List<Location> = emptyList(),
    val paymentMethods: List<PaymentMethod> = emptyList(),
    val showPaymentSuccess: Boolean = false,
    val errorMessage: String? = null
)

class BookingViewModel(
    private val repository: MockClientRepository = MockClientRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    fun loadServiceDetail(serviceId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.getServiceDetail(serviceId).fold(
                onSuccess = { service ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            service = service,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Error al cargar servicio"
                        )
                    }
                }
            )
        }
    }

    fun loadLocations() {
        viewModelScope.launch {
            repository.getLocations().fold(
                onSuccess = { locations ->
                    _uiState.update {
                        it.copy(
                            locations = locations,
                            selectedLocation = it.selectedLocation ?: locations.firstOrNull { loc -> loc.isDefault }
                        )
                    }
                },
                onFailure = { }
            )
        }
    }

    fun loadPaymentMethods() {
        viewModelScope.launch {
            repository.getPaymentMethods().fold(
                onSuccess = { methods ->
                    _uiState.update {
                        it.copy(paymentMethods = methods)
                    }
                },
                onFailure = { }
            )
        }
    }

    fun selectDate(date: String) {
        _uiState.update { it.copy(selectedDate = date) }
    }

    fun selectTime(time: String) {
        _uiState.update { it.copy(selectedTime = time) }
    }

    fun selectLocation(location: Location) {
        _uiState.update { it.copy(selectedLocation = location) }
    }

    fun selectPaymentMethod(paymentMethod: PaymentMethod) {
        _uiState.update { it.copy(selectedPaymentMethod = paymentMethod) }
    }

    fun confirmBooking() {
        val state = _uiState.value

        if (state.service == null || state.selectedDate == null ||
            state.selectedTime == null || state.selectedLocation == null ||
            state.selectedPaymentMethod == null) {
            _uiState.update {
                it.copy(errorMessage = "Por favor complete todos los datos")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.createBooking(
                serviceId = state.service.id,
                date = state.selectedDate,
                time = state.selectedTime,
                locationId = state.selectedLocation.id,
                paymentMethodId = state.selectedPaymentMethod.id
            ).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            showPaymentSuccess = true,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Error al crear reserva"
                        )
                    }
                }
            )
        }
    }

    fun dismissSuccessDialog() {
        _uiState.update { it.copy(showPaymentSuccess = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
