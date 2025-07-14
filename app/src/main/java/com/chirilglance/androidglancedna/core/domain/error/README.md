# Error Handling

A comprehensive error handling system that automatically displays user-friendly error messages and provides consistent error state management throughout the app.

## Overview

The error handling system in AndroidGlanceDNA is designed to:

1. Automatically display error messages as snackbars
2. Provide a consistent way to represent loading, success, and error states
3. Handle common network errors with appropriate messages
4. Support retry actions for recoverable errors
5. Reduce boilerplate code in ViewModels and UI components

## Components

### UiState

A sealed class that represents the different states of a UI operation:

```kotlin
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
    data object Empty : UiState<Nothing>()
}
```

**Usage in ViewModels:**

```kotlin
class MyViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<MyData>>(UiState.Empty)
    val uiState: StateFlow<UiState<MyData>> = _uiState.asStateFlow()
    
    fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val result = repository.getData()
                _uiState.value = UiState.Success(result)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to load data: ${e.message}")
            }
        }
    }
}
```

### NetworkErrorHandler

A utility class that handles common network errors and displays appropriate snackbar messages:

```kotlin
@Inject
lateinit var errorHandler: NetworkErrorHandler

try {
    apiService.fetchData()
} catch (e: Exception) {
    errorHandler.handleError(e) {
        // Retry action
        apiService.fetchData()
    }
}
```

The `NetworkErrorHandler` handles different types of errors:

- **IOException**: Displayed as "Network connection lost"
- **HttpException**: Custom messages based on HTTP status codes:
    - 401: "Your session has expired. Please log in again."
    - 403: "You don't have permission to perform this action."
    - 404: "The requested resource was not found."
    - 500, 502, 503, 504: "Server error. Please try again later."
- **Other exceptions**: "An unexpected error occurred"

### AutoErrorHandler

A Composable that automatically watches a UiState and displays error messages as snackbars:

```kotlin
@Composable
fun MyScreen(viewModel: MyViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Automatically handle errors from uiState
    AutoErrorHandler(
        state = uiState,
        onRetry = { viewModel.loadData() }
    )
    
    // Rest of the screen content
}
```

This eliminates the need to manually check for error states and display snackbars in each screen.

### ViewModel Extensions

Extension functions for ViewModels to easily show snackbars:

```kotlin
class MyViewModel : ViewModel() {
    fun showError() {
        showErrorSnackbar(
            message = "An error occurred",
            actionLabel = "Retry",
            onAction = { loadData() }
        )
    }
}
```

## Integration Patterns

### Repository Layer

Centralize error handling in repositories:

```kotlin
class MyRepositoryImpl : MyRepository {
    @Inject
    lateinit var errorHandler: NetworkErrorHandler
    
    override suspend fun getData(): Flow<UiState<MyData>> = flow {
        emit(UiState.Loading)
        try {
            val result = apiService.fetchData()
            emit(UiState.Success(result))
        } catch (e: Exception) {
            errorHandler.handleError(e)
            emit(UiState.Error(e.message ?: "Unknown error"))
        }
    }
}
```

### Use Case Layer

For applications with a use case/interactor layer:

```kotlin
class GetDataUseCase @Inject constructor(
    private val repository: MyRepository
) {
    suspend operator fun invoke(): Flow<UiState<MyData>> {
        return repository.getData()
            .catch { e ->
                emit(UiState.Error(e.message ?: "Unknown error"))
            }
    }
}
```

### UI Layer

Combine `AutoErrorHandler` with Compose state collection:

```kotlin
@Composable
fun MyScreen(viewModel: MyViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    
    AutoErrorHandler(
        state = uiState,
        onRetry = { viewModel.loadData() },
        onSuccess = { data -> 
            // Optional success handling
        }
    )
    
    when (uiState) {
        is UiState.Loading -> LoadingIndicator()
        is UiState.Success -> ContentView((uiState as UiState.Success).data)
        is UiState.Error -> {
            // Error UI (optional, since AutoErrorHandler shows a snackbar)
            ErrorPlaceholder()
        }
        is UiState.Empty -> EmptyState()
    }
}
```

## Best Practices

1. **Use UiState consistently** throughout the app for all async operations
2. **Centralize error handling logic** in repositories or use cases
3. **Provide meaningful error messages** rather than technical error details
4. **Use AutoErrorHandler** in all screens to avoid boilerplate
5. **Include retry actions** for recoverable errors
6. **Handle edge cases** like no network, server maintenance, etc.
7. **Log errors** for debugging but don't show logs to users
8. **Consider error recovery strategies** for important operations (e.g., retrying with exponential backoff)

## Extensibility

The error handling system can be extended in several ways:

### Custom Error Types

Create specific error types for different scenarios:

```kotlin
sealed class AppError {
    data class NetworkError(val exception: IOException) : AppError()
    data class AuthError(val message: String) : AppError()
    data class ServerError(val code: Int, val message: String) : AppError()
    data class ValidationError(val field: String, val message: String) : AppError()
    data class UnknownError(val exception: Throwable) : AppError()
}
```

### Error Mappers

Create mappers to convert between different error representations:

```kotlin
object ErrorMapper {
    fun mapThrowableToAppError(throwable: Throwable): AppError {
        return when (throwable) {
            is IOException -> AppError.NetworkError(throwable)
            is HttpException -> {
                when (throwable.code()) {
                    401, 403 -> AppError.AuthError(throwable.message())
                    in 500..599 -> AppError.ServerError(throwable.code(), throwable.message())
                    else -> AppError.UnknownError(throwable)
                }
            }
            else -> AppError.UnknownError(throwable)
        }
    }
    
    fun mapAppErrorToUiState(error: AppError): UiState<Nothing> {
        val message = when (error) {
            is AppError.NetworkError -> "Network connection lost"
            is AppError.AuthError -> error.message
            is AppError.ServerError -> "Server error: ${error.message}"
            is AppError.ValidationError -> "Invalid ${error.field}: ${error.message}"
            is AppError.UnknownError -> "An unexpected error occurred"
        }
        return UiState.Error(message)
    }
}
```