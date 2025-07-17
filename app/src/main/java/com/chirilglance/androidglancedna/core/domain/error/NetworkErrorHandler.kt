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
                val errorMessage = when {
                    // Check for GraphQL-specific error patterns in the response body
                    error.message().contains("JWT expired", ignoreCase = true) ||
                            error.message().contains("token expired", ignoreCase = true) ||
                            error.message().contains("UNAUTHENTICATED", ignoreCase = true) ->
                        "Your session has expired. Please log in again."

                    error.message().contains("FORBIDDEN", ignoreCase = true) ||
                            error.code() == 403 ->
                        "You don't have permission to perform this action."

                    error.message().contains("NOT_FOUND", ignoreCase = true) ||
                            error.code() == 404 ->
                        "The requested resource was not found."

                    // Server errors - both HTTP codes and GraphQL error messages
                    error.code() in listOf(500, 502, 503, 504) ||
                            error.message().contains("INTERNAL_SERVER_ERROR", ignoreCase = true) ->
                        "Server error. Please try again later."

                    // Validation errors common in GraphQL
                    error.message().contains("validation", ignoreCase = true) ||
                            error.message().contains("VALIDATION_ERROR", ignoreCase = true) ->
                        "Invalid input. Please check your data and try again."

                    // Default case
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