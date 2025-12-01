package com.example.serviconnecta.feature.worker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serviconnecta.feature.worker.data.MockWorkerRepository
import com.example.serviconnecta.feature.worker.domain.model.Service
import com.example.serviconnecta.feature.worker.domain.model.ServiceRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WorkerHomeUiState(
    val isLoading: Boolean = false,
    val nextRequest: ServiceRequest? = null,
    val services: List<Service> = emptyList(),
    val averageRating: Double = 4.0,
    val errorMessage: String? = null
)

class WorkerHomeViewModel(
    private val repository: MockWorkerRepository = MockWorkerRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkerHomeUiState())
    val uiState: StateFlow<WorkerHomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val servicesResult = repository.getMyServices()
                val nextRequest = repository.getNextRequest()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        nextRequest = nextRequest,
                        services = servicesResult.getOrDefault(emptyList()),
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
