# State Management

Effective state management is critical for creating maintainable and predictable Android applications. This document outlines the state management patterns used in AndroidGlanceDNA.

## Overview

Our approach uses several patterns depending on the complexity and scope of the state:

- **Basic State** - For simple UI state
- **StateFlow** - For observable state streams
- **UiState Sealed Class** - For representing operation states
- **Composed State** - For complex screens with multiple state components

## State Patterns

### Basic State in ViewModels

For straightforward UI state, we use `mutableStateOf` in ViewModels:

```kotlin
class ProfileViewModel : ViewModel() {
    // UI state
    var firstName by mutableStateOf("")
        private set
    var lastName by mutableStateOf("")
        private set
        
    // Loading state
    var isLoading by mutableStateOf(false)
        private set
        
    // Error state
    var errorMessage by mutableStateOf<String?>(null)
        private set
        
    // Update methods
    fun updateFirstName(name: String) {
        firstName = name
        validateFirstName()
    }
    
    fun updateLastName(name: String) {
        lastName = name
        validateLastName()
    }
}
```

### StateFlow for Complex State

For more complex state that needs to be observed as a stream:

```kotlin
class SearchViewModel : ViewModel() {
    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // Search results
    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val searchResults: StateFlow<List<SearchResult>> = _searchResults.asStateFlow()
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Update search query
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        performSearch()
    }
    
    // Perform search
    private fun performSearch() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val results = repository.search(_searchQuery.value)
                _searchResults.value = results
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
```

### UiState Sealed Class

For representing operation states (loading, success, error, empty):

```kotlin
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
    data object Empty : UiState<Nothing>()
}

class ItemsViewModel : ViewModel() {
    private val _itemsState = MutableStateFlow<UiState<List<Item>>>(UiState.Loading)
    val itemsState: StateFlow<UiState<List<Item>>> = _itemsState.asStateFlow()
    
    init {
        loadItems()
    }
    
    private fun loadItems() {
        viewModelScope.launch {
            _itemsState.value = UiState.Loading
            try {
                val items = repository.getItems()
                _itemsState.value = if (items.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Success(items)
                }
            } catch (e: Exception) {
                _itemsState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
```

### Form State Management

For form handling, we maintain separate state for values and validation errors:

```kotlin
class RegistrationViewModel : ViewModel() {
    // Form values
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var confirmPassword by mutableStateOf("")
        private set
        
    // Validation errors
    var emailError by mutableStateOf<String?>(null)
        private set
    var passwordError by mutableStateOf<String?>(null)
        private set
    var confirmPasswordError by mutableStateOf<String?>(null)
        private set
        
    // Update methods with validation
    fun updateEmail(value: String) {
        email = value
        emailError = ValidationUtils.validateEmail(value)
    }
    
    fun updatePassword(value: String) {
        password = value
        passwordError = ValidationUtils.validatePassword(value)
        // Re-validate confirm password when password changes
        if (confirmPassword.isNotEmpty()) {
            validateConfirmPassword()
        }
    }
    
    fun updateConfirmPassword(value: String) {
        confirmPassword = value
        validateConfirmPassword()
    }
    
    private fun validateConfirmPassword() {
        confirmPasswordError = when {
            confirmPassword.isEmpty() -> "Please confirm your password"
            confirmPassword != password -> "Passwords do not match"
            else -> null
        }
    }
    
    // Validate all fields at once
    fun validateAll(): Boolean {
        updateEmail(email)
        updatePassword(password)
        updateConfirmPassword(confirmPassword)
        return emailError == null &&
               passwordError == null &&
               confirmPasswordError == null
    }
    
    // Submit form
    fun submit() {
        if (validateAll()) {
            // Proceed with registration
        }
    }
}
```

### State Composition

For complex screens, compose multiple state objects:

```kotlin
data class ProfileUiState(
    val personalInfo: PersonalInfoState = PersonalInfoState(),
    val preferences: PreferencesState = PreferencesState(),
    val settings: SettingsState = SettingsState()
)

data class PersonalInfoState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val emailError: String? = null
)

data class PreferencesState(
    val notifications: Boolean = true,
    val darkMode: Boolean = false
)

data class SettingsState(
    val language: String = "English",
    val region: String = "United Kingdom"
)

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    fun updateFirstName(name: String) {
        val firstNameError = ValidationUtils.validateName(name)
        _uiState.update { currentState ->
            currentState.copy(
                personalInfo = currentState.personalInfo.copy(
                    firstName = name,
                    firstNameError = firstNameError
                )
            )
        }
    }
    
    // Other update methods...
}
```

## UI State Management

### State Hoisting Pattern

We use state hoisting to lift state to the appropriate level:

```kotlin
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel()
) {
    val query by viewModel.searchQuery.collectAsState()
    val results by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    Column {
        SearchBar(
            query = query,
            onQueryChange = viewModel::updateSearchQuery,
            isLoading = isLoading
        )
        
        SearchResults(
            results = results,
            isLoading = isLoading
        )
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    isLoading: Boolean
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        trailingIcon = {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    )
}
```

### Remember for UI-Only State

For state that doesn't need to survive configuration changes:

```kotlin
@Composable
fun ExpandableCard(
    title: String,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column {
            Text(text = title)
            AnimatedVisibility(visible = expanded) {
                content()
            }
        }
    }
}
```

### RememberCoroutineScope

For launching coroutines from composables:

```kotlin
@Composable
fun RefreshableContent(
    onRefresh: suspend () -> Unit,
    content: @Composable () -> Unit
) {
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = {
            coroutineScope.launch {
                isRefreshing = true
                onRefresh()
                isRefreshing = false
            }
        }
    ) {
        content()
    }
}
```

## Error Handling State

### Unified Error Handling

We use a sealed class for different error presentation types:

```kotlin
sealed class ErrorState {
    data object None : ErrorState()
    data class Toast(val message: String) : ErrorState()
    data class Snackbar(val message: String) : ErrorState()
    data class Dialog(
        val title: String,
        val message: String,
        val primaryAction: String = "OK",
        val secondaryAction: String? = null,
        val onPrimaryAction: () -> Unit = {},
        val onSecondaryAction: () -> Unit = {}
    ) : ErrorState()
}

class BaseViewModel : ViewModel() {
    private val _errorState = MutableStateFlow<ErrorState>(ErrorState.None)
    val errorState: StateFlow<ErrorState> = _errorState.asStateFlow()
    
    fun showToastError(message: String) {
        _errorState.value = ErrorState.Toast(message)
    }
    
    fun showSnackbarError(message: String) {
        _errorState.value = ErrorState.Snackbar(message)
    }
    
    fun showDialogError(
        title: String,
        message: String,
        primaryAction: String = "OK",
        secondaryAction: String? = null,
        onPrimaryAction: () -> Unit = {},
        onSecondaryAction: () -> Unit = {}
    ) {
        _errorState.value = ErrorState.Dialog(
            title, message, primaryAction, secondaryAction,
            onPrimaryAction, onSecondaryAction
        )
    }
    
    fun clearError() {
        _errorState.value = ErrorState.None
    }
}
```

## Integration with Snackbar Management

Our state management integrates with the SnackbarManager for displaying transient messages:

```kotlin
class ProfileViewModel : ViewModel() {
    // Other fields and methods...
    
    fun saveProfile() {
        viewModelScope.launch {
            try {
                val result = repository.updateProfile(profile)
                SnackbarManager.showSuccess(
                    message = "Profile updated successfully",
                    showDismissButton = false  // Success messages don't need dismiss button
                )
                // Handle successful update
            } catch (e: Exception) {
                // Show error snackbar with retry action and dismiss button
                SnackbarManager.showError(
                    message = "Failed to update profile: ${e.message}",
                    actionLabel = "Retry",
                    onAction = { saveProfile() },
                    showDismissButton = true
                )
            }
        }
    }
}
```

## Best Practices

1. **Single Source of Truth**: Keep state in one place to avoid synchronization issues
2. **Immutable State**: Use immutable data classes for state objects
3. **State Hoisting**: Lift state to the appropriate level based on its scope
4. **Unidirectional Data Flow**: Follow a clear flow of state from source to UI
5. **Separate UI and Business Logic**: Keep business logic in ViewModels, UI logic in Composables
6. **Predictable State Updates**: Use structured patterns for updating state
7. **Error State Handling**: Include error states in your state model
8. **Composition over Inheritance**: Compose state objects rather than extending them
9. **State Validation**: Validate state at the appropriate times (on change, on submit)
10. **Consistent State Patterns**: Use consistent patterns across the application