package com.chirilglance.androidglancedna.data.repository.auth

import com.chirilglance.androidglancedna.core.domain.model.UiState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun verifyPhoneNumber(phoneNumber: String): Flow<UiState<String>>
    suspend fun verifyOtp(phoneNumber: String, otp: String): Flow<UiState<Boolean>>
}