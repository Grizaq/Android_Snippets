package com.chirilglance.androidglancedna.data.repository.auth

import com.chirilglance.androidglancedna.core.domain.model.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor() : AuthRepository {

    override suspend fun verifyPhoneNumber(phoneNumber: String): Flow<UiState<String>> = flow {
        emit(UiState.Loading)

        try {
            // Simulate API call
            delay(1500)

            // Test error case - if phone number contains "0000"
            if (phoneNumber.contains("0000")) {
                emit(UiState.Error("Service is currently unavailable. Please try again later."))
                return@flow
            }

            // Success case
            emit(UiState.Success(phoneNumber))
        } catch (e: Exception) {
            emit(UiState.Error(e.message ?: "An unexpected error occurred"))
        }
    }

    override suspend fun verifyOtp(phoneNumber: String, otp: String): Flow<UiState<Boolean>> = flow {
        emit(UiState.Loading)

        try {
            // Simulate API call
            delay(1500)

            // Test error case - if OTP is "0000"
            if (otp == "0000") {
                emit(UiState.Error("Invalid verification code. Please try again."))
                return@flow
            }

            // Success case
            emit(UiState.Success(true))
        } catch (e: Exception) {
            emit(UiState.Error(e.message ?: "An unexpected error occurred"))
        }
    }
}