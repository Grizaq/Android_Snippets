# Android Form Validation System

A clean, type-safe, and user-friendly validation system for Android applications built with Jetpack Compose.

## Overview

This validation system provides a structured approach to handling form validation in Android applications. It offers a balance between good user experience (with progressive validation) and clean, maintainable code.

## Key Features

- **Type-safe validation results** using sealed classes
- **Progressive validation** that only shows errors after user interaction
- **Centralized validation logic** for consistency across the app
- **Real-time feedback** as users type in fields they've already interacted with
- **Support for complex validation scenarios** like interdependent fields
- **Streamlined error state management** using a map-based approach
- **Compose integration** with validation-aware UI components

## Core Components

### ValidationResult

A sealed class representing the outcome of validation:

```kotlin
sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Invalid(val message: String) : ValidationResult()

    val isValid: Boolean get() = this is Valid
    val errorMessage: String? get() = if (this is Invalid) message else null
}
```

### ValidationUtils

A utility object containing validation logic for common field types:

```kotlin
object ValidationUtils {
    fun validateName(name: String, fieldName: String = "Name"): ValidationResult
    fun validateEmail(email: String): ValidationResult
    fun validatePhoneNumber(phoneNumber: String, countryCode: CountryCode): ValidationResult
    fun validatePassword(password: String): ValidationResult
    // ... and more
}
```

## Usage Example

### 1. Add validation to ViewModel

```kotlin
class RegistrationViewModel : ViewModel() {
    // Form fields
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
        
    // Error state management
    private val _fieldErrors = MutableStateFlow<Map<String, String?>>(emptyMap())
    val fieldErrors: StateFlow<Map<String, String?>> = _fieldErrors.asStateFlow()
    
    // Track which fields have been interacted with
    private val _touchedFields = mutableSetOf<String>()
    
    // Update methods
    fun updateEmail(value: String) {
        email = value
        validateFieldIfTouched("email")
    }
    
    fun updatePassword(value: String) {
        password = value
        validateFieldIfTouched("password")
    }
    
    // Validation methods
    fun markFieldAsTouched(fieldName: String) {
        _touchedFields.add(fieldName)
        validateField(fieldName)
    }
    
    private fun validateFieldIfTouched(fieldName: String) {
        if (_touchedFields.contains(fieldName)) {
            validateField(fieldName)
        }
    }
    
    fun validateField(fieldName: String) {
        val errorMessage = when (fieldName) {
            "email" -> ValidationUtils.validateEmail(email).errorMessage
            "password" -> ValidationUtils.validatePassword(password).errorMessage
            else -> null
        }
        
        _fieldErrors.update { errors ->
            errors.toMutableMap().apply { put(fieldName, errorMessage) }
        }
    }
    
    fun validateAllFields(): Boolean {
        listOf("email", "password").forEach { 
            _touchedFields.add(it)
            validateField(it)
        }
        
        return _fieldErrors.value.values.all { it == null }
    }
    
    fun submitRegistration() {
        if (!validateAllFields()) {
            return
        }
        
        // Proceed with registration
    }
}
```

### 2. Create UI components with validation

```kotlin
@Composable
fun RegistrationScreen(viewModel: RegistrationViewModel) {
    val fieldErrors by viewModel.fieldErrors.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium
        )
        
        // Email field with validation
        LabeledTextField(
            value = viewModel.email,
            onValueChange = viewModel::updateEmail,
            label = "Email Address",
            hint = "your@email.com",
            errorMessage = fieldErrors["email"],
            keyboardType = KeyboardType.Email,
            onFocusChanged = { isFocused ->
                if (!isFocused) {
                    viewModel.markFieldAsTouched("email")
                }
            }
        )
        
        // Password field with validation
        LabeledTextField(
            value = viewModel.password,
            onValueChange = viewModel::updatePassword,
            label = "Password",
            hint = "Enter a secure password",
            errorMessage = fieldErrors["password"],
            keyboardType = KeyboardType.Password,
            visualTransformation = PasswordVisualTransformation(),
            onFocusChanged = { isFocused ->
                if (!isFocused) {
                    viewModel.markFieldAsTouched("password")
                }
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = viewModel::submitRegistration,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
    }
}
```

## Advanced Usage

### Related Field Validation

For fields that depend on each other (like password confirmation):

```kotlin
fun updatePassword(value: String) {
    password = value
    validateFieldIfTouched("password")
    
    // If password confirmation has been touched, revalidate it too
    if (_touchedFields.contains("passwordConfirmation")) {
        validateField("passwordConfirmation")
    }
}
```

### Custom Validation Logic

Extend ValidationUtils with your own validation methods:

```kotlin
// In ValidationUtils
fun validateUsername(username: String): ValidationResult {
    return when {
        username.isBlank() -> ValidationResult.Invalid("Username is required")
        username.length < 3 -> ValidationResult.Invalid("Username must be at least 3 characters")
        !username.matches(Regex("^[a-zA-Z0-9_]+$")) -> 
            ValidationResult.Invalid("Username can only contain letters, numbers, and underscores")
        else -> ValidationResult.Valid
    }
}
```

## Best Practices

1. **Only validate after interaction**: Use the touched fields pattern to avoid overwhelming users with errors before they've had a chance to enter data
2. **Provide clear error messages**: Make error messages specific and actionable
3. **Keep validation logic centralized**: Add new validation methods to ValidationUtils rather than inline validation
4. **Update related fields**: When validating fields that depend on each other, make sure to revalidate all related fields
5. **Use consistent patterns**: Follow the same validation pattern for all form fields
