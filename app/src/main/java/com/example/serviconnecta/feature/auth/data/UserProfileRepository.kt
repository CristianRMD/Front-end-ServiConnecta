package com.example.serviconnecta.feature.auth.data

import com.example.serviconnecta.feature.auth.data.remote.UpdatePersonalInfoRequestDto
import com.example.serviconnecta.feature.auth.data.remote.UserApiService

class UserProfileRepository(
    private val userApiService: UserApiService
) {
    suspend fun updatePersonalInfo(
        fullName: String,
        gender: String,
        birthDate: String
    ) {
        val request = UpdatePersonalInfoRequestDto(
            full_name = fullName,
            gender = gender,
            birth_date = birthDate
        )

        val response = userApiService.updatePersonalInfo(request)
        if (!response.success) {
            throw IllegalStateException(response.message)
        }
    }
}