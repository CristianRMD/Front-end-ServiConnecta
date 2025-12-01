package com.example.serviconnecta.feature.worker.ui.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serviconnecta.feature.worker.data.MockWorkerRepository
import com.example.serviconnecta.feature.worker.domain.model.ServiceRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RequestsUiState(
    val isLoading: Boolean = false,
    val requests: List<ServiceRequest> = emptyList(),
    val selectedRequest: ServiceRequest? = null,
    val showDetailDialog: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class RequestsViewModel(
    private val repository: MockWorkerRepository = MockWorkerRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RequestsUiState())
    val uiState: StateFlow<RequestsUiState> = _uiState.asStateFlow()

    init {
        loadRequests()
    }

    private fun loadRequests() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.getRequests().fold(
                onSuccess = { requests ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            requests = requests,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Error al cargar solicitudes"
                        )
                    }
                }
            )
        }
    }

    fun showRequestDetail(request: ServiceRequest) {
        _uiState.update {
            it.copy(
                selectedRequest = request,
                showDetailDialog = true
            )
        }
    }

    fun hideRequestDetail() {
        _uiState.update {
            it.copy(
                selectedRequest = null,
                showDetailDialog = false
            )
        }
    }

    fun acceptRequest(requestId: String) {
        viewModelScope.launch {
            repository.acceptRequest(requestId).fold(
                onSuccess = { updatedRequest ->
                    _uiState.update {
                        it.copy(
                            requests = it.requests.map { req ->
                                if (req.id == requestId) updatedRequest else req
                            },
                            successMessage = "Solicitud aceptada",
                            showDetailDialog = false,
                            selectedRequest = null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(errorMessage = error.message ?: "Error al aceptar")
                    }
                }
            )
        }
    }

    fun rejectRequest(requestId: String) {
        viewModelScope.launch {
            repository.rejectRequest(requestId).fold(
                onSuccess = { updatedRequest ->
                    _uiState.update {
                        it.copy(
                            requests = it.requests.map { req ->
                                if (req.id == requestId) updatedRequest else req
                            },
                            successMessage = "Solicitud rechazada",
                            showDetailDialog = false,
                            selectedRequest = null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(errorMessage = error.message ?: "Error al rechazar")
                    }
                }
            )
        }
    }

    fun clearMessages() {
        _uiState.update {
            it.copy(errorMessage = null, successMessage = null)
        }
    }
}
