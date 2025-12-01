package com.example.serviconnecta.feature.worker.data

import com.example.serviconnecta.feature.worker.domain.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MockWorkerRepository {

    private val mockServices = mutableListOf(
        Service(
            id = "srv_001",
            workerId = "worker_001",
            title = "Revisión de cables electricos",
            description = "Atendemos fallas eléctricas e instalamos tomacorrientes, interruptores y luminarias. Técnicos certificados, llegada el mismo día, cotización previa y garantía de 30 días. Trabajo seguro y materiales de calidad.",
            category = "Servicio de electricidad",
            price = 20.0,
            imageUrl = null,
            rating = 4.0,
            reviewCount = 52,
            createdAt = "2025-11-20T10:00:00Z"
        ),
        Service(
            id = "srv_002",
            workerId = "worker_001",
            title = "Instalación de tomacorrientes",
            description = "Atendemos fallas eléctricas e instalamos tomacorrientes, interruptores y luminarias. Técnicos certificados, llegada el mismo día, cotización previa y garantía de 30 días. Trabajo seguro y materiales de calidad.",
            category = "Servicio de electricidad",
            price = 20.0,
            imageUrl = null,
            rating = 4.0,
            reviewCount = 35,
            createdAt = "2025-11-18T14:30:00Z"
        ),
        Service(
            id = "srv_003",
            workerId = "worker_001",
            title = "Verificación de fuga electrica",
            description = "Atendemos fallas eléctricas e instalamos tomacorrientes, interruptores y luminarias. Técnicos certificados, llegada el mismo día, cotización previa y garantía de 30 días. Trabajo seguro y materiales de calidad.",
            category = "Servicio de electricidad",
            price = 20.0,
            imageUrl = null,
            rating = 4.0,
            reviewCount = 28,
            createdAt = "2025-11-15T09:15:00Z"
        )
    )

    private val mockRequests = mutableListOf(
        ServiceRequest(
            id = "req_001",
            serviceId = "srv_001",
            serviceName = "Revisión de cables electricos",
            clientId = "client_001",
            clientName = "Ricardo Morales",
            clientPhoto = null,
            date = "15/10/2025",
            time = "15:00 hrs",
            location = "Avenida Test, Lima, San Miguel",
            status = RequestStatus.PENDING,
            paymentMethod = "Efectivo"
        ),
        ServiceRequest(
            id = "req_002",
            serviceId = "srv_001",
            serviceName = "Revisión de cables electricos",
            clientId = "client_002",
            clientName = "Jefferson Marquez",
            clientPhoto = null,
            date = "16/10/2025",
            time = "10:00 hrs",
            location = "Callao, Callao",
            status = RequestStatus.PENDING,
            paymentMethod = "Efectivo"
        ),
        ServiceRequest(
            id = "req_003",
            serviceId = "srv_001",
            serviceName = "Revisión de cables electricos",
            clientId = "client_003",
            clientName = "Alan Escribas",
            clientPhoto = null,
            date = "17/10/2025",
            time = "14:00 hrs",
            location = "Callao, La Perla",
            status = RequestStatus.PENDING,
            paymentMethod = "Efectivo"
        ),
        ServiceRequest(
            id = "req_004",
            serviceId = "srv_001",
            serviceName = "Revisión de cables electricos",
            clientId = "client_004",
            clientName = "Ivan Principe",
            clientPhoto = null,
            date = "18/10/2025",
            time = "11:00 hrs",
            location = "Lima, San Miguel",
            status = RequestStatus.PENDING,
            paymentMethod = "Efectivo"
        ),
        ServiceRequest(
            id = "req_005",
            serviceId = "srv_001",
            serviceName = "Revisión de cables electricos",
            clientId = "client_005",
            clientName = "Rissel Nieto",
            clientPhoto = null,
            date = "19/10/2025",
            time = "16:00 hrs",
            location = "Lima, San Miguel",
            status = RequestStatus.PENDING,
            paymentMethod = "Efectivo"
        )
    )

    private val mockReviews = listOf(
        Review(
            id = "rev_001",
            clientId = "client_001",
            clientName = "Courtney Henry",
            clientPhoto = null,
            serviceId = "srv_001",
            serviceName = "Revisión de cables electricos",
            rating = 5,
            comment = "Revisaron el tablero y reemplazaron el diferencial. Todo con garantía y explicación clara.",
            date = "12/10/25"
        ),
        Review(
            id = "rev_002",
            clientId = "client_002",
            clientName = "Cameron Williamson",
            clientPhoto = null,
            serviceId = "srv_002",
            serviceName = "Instalación de tomacorrientes",
            rating = 4,
            comment = "Comentario de prueba",
            date = "15/09/25"
        ),
        Review(
            id = "rev_003",
            clientId = "client_003",
            clientName = "Jane Cooper",
            clientPhoto = null,
            serviceId = "srv_003",
            serviceName = "Verificación de fuga electrica",
            rating = 3,
            comment = "Comentario de prueba",
            date = "16/08/25"
        )
    )

    suspend fun getMyServices(): Result<List<Service>> {
        delay(500) // Simular latencia de red
        return Result.success(mockServices)
    }

    suspend fun createService(
        title: String,
        description: String,
        category: String,
        price: Double,
        imageBase64: String?
    ): Result<Service> {
        delay(500)
        val newService = Service(
            id = "srv_${System.currentTimeMillis()}",
            workerId = "worker_001",
            title = title,
            description = description,
            category = category,
            price = price,
            imageUrl = imageBase64,
            rating = 0.0,
            reviewCount = 0,
            createdAt = java.time.Instant.now().toString()
        )
        mockServices.add(0, newService)
        return Result.success(newService)
    }

    suspend fun deleteService(serviceId: String): Result<Unit> {
        delay(300)
        mockServices.removeAll { it.id == serviceId }
        return Result.success(Unit)
    }

    suspend fun getRequests(): Result<List<ServiceRequest>> {
        delay(500)
        return Result.success(mockRequests)
    }

    suspend fun acceptRequest(requestId: String): Result<ServiceRequest> {
        delay(500)
        val index = mockRequests.indexOfFirst { it.id == requestId }
        if (index != -1) {
            val updated = mockRequests[index].copy(status = RequestStatus.ACCEPTED)
            mockRequests[index] = updated
            return Result.success(updated)
        }
        return Result.failure(Exception("Request not found"))
    }

    suspend fun rejectRequest(requestId: String): Result<ServiceRequest> {
        delay(500)
        val index = mockRequests.indexOfFirst { it.id == requestId }
        if (index != -1) {
            val updated = mockRequests[index].copy(status = RequestStatus.REJECTED)
            mockRequests[index] = updated
            return Result.success(updated)
        }
        return Result.failure(Exception("Request not found"))
    }

    suspend fun getMyReviews(): Result<List<Review>> {
        delay(500)
        return Result.success(mockReviews)
    }

    fun getNextRequest(): ServiceRequest? {
        return mockRequests.firstOrNull { it.status == RequestStatus.PENDING }
    }
}
