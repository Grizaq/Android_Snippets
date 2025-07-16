package com.chirilglance.androidglancedna.core.domain.error

import com.chirilglance.androidglancedna.core.ui.components.SnackbarManager
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkErrorHandler @Inject constructor() {

    fun handleError(error: Throwable, retryAction: (() -> Unit)? = null): String {
        return when (error) {
            is IOException -> {
                val message = "Network connection lost"
                SnackbarManager.showError(
                    message = message,
                    actionLabel = if (retryAction != null) "Retry" else null,
                    onAction = retryAction
                )
                message
            }
            is HttpException -> {
                val errorMessage = when (error.code()) {
                    401 -> "Your session has expired. Please log in again."
                    403 -> "You don't have permission to perform this action."
                    404 -> "The requested resource was not found."
                    500, 502, 503, 504 -> "Server error. Please try again later."
                    else -> "An error occurred: ${error.message()}"
                }
                SnackbarManager.showError(
                    message = errorMessage,
                    actionLabel = if (retryAction != null && error.code() !in listOf(401, 403)) "Retry" else null,
                    onAction = retryAction
                )
                errorMessage
            }
            else -> {
                val message = "An unexpected error occurred"
                SnackbarManager.showError(message = message)
                message
            }
        }
    }
}