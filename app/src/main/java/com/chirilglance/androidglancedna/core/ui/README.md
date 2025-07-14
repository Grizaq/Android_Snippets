# UI Components

A collection of reusable UI components built with Jetpack Compose that follow Material Design 3 guidelines with consistent styling and behavior.

## Table of Contents

- [Text Fields](#text-fields)
- [Buttons](#buttons)
- [Cards](#cards)
- [Specialized Components](#specialized-components)
- [Snackbars](#snackbars)

## Text Fields

### ClubConnectTextField

A standard text field with customizable styling, validation support, and error display.

```kotlin
ClubConnectTextField(
    value = text,
    onValueChange = { text = it },
    label = "Email Address",
    hint = "Enter your email",
    errorMessage = errorState,
    keyboardType = KeyboardType.Email
)
```

**Key Features:**
- Custom label and hint text
- Error message display
- Keyboard type customization
- Max length limit
- Leading and trailing icons
- Character filtering (spaces, special characters)

### NumberTextField

A specialized text field for numeric input with validation for integer or decimal values.

```kotlin
NumberTextField(
    value = amount,
    onValueChange = { amount = it },
    label = "Amount",
    hint = "Enter amount",
    isInteger = false,
    minValue = 0f,
    maxValue = 1000f
)
```

**Key Features:**
- Integer or decimal mode
- Min/max value constraints
- Automatic character filtering
- Error message display

### LabeledTextField

A compact input with label and field arranged horizontally in a card layout.

```kotlin
LabeledTextField(
    value = username,
    onValueChange = { username = it },
    label = "Username:",
    hint = "Enter username",
    elevation = 2.dp
)
```

**Key Features:**
- Space-efficient layout
- Customizable weights for label and field
- Card container with optional elevation
- Error message display

### ClubConnectPhoneField

A specialized text field for phone number input with appropriate keyboard and validation.

```kotlin
ClubConnectPhoneField(
    value = phoneNumber,
    onValueChange = { phoneNumber = it },
    isError = hasError,
    onDone = { /* verify phone number */ }
)
```

**Key Features:**
- Phone keyboard type
- Character filtering for valid phone number formats
- Card container with customizable styling
- Error state support

## Buttons

### PrimaryButton

The main action button with consistent styling and loading state support.

```kotlin
PrimaryButton(
    text = "Continue",
    onClick = { /* action */ },
    isLoading = isSubmitting,
    enabled = formIsValid
)
```

**Key Features:**
- Loading state with spinner
- Consistent height (56dp)
- Optional leading icon
- Custom colors

### SecondaryButton

An outlined button for secondary actions with consistent styling.

```kotlin
SecondaryButton(
    text = "Cancel",
    onClick = { /* action */ },
    borderColor = Color(0xFF3F51B5),
    contentColor = Color(0xFF3F51B5)
)
```

**Key Features:**
- Consistent with PrimaryButton dimensions
- Customizable border and content colors
- Optional leading icon

## Cards

### ClubConnectCard

A flexible card component for content organization with customizable sections.

```kotlin
ClubConnectCard(
    titleContent = ClubConnectCardDefaults.Title("Card Title"),
    subtitleContent = ClubConnectCardDefaults.Subtitle("Subtitle"),
    descriptionContent = ClubConnectCardDefaults.Description("Card content goes here"),
    actions = {
        PrimaryButton(
            text = "Action",
            onClick = { /* action */ },
            modifier = Modifier.fillMaxWidth()
        )
    }
)
```

**Key Features:**
- Flexible content areas (title, subtitle, description, actions)
- Optional leading icon
- Optional header content
- Click handling
- Helper functions for common content types

## Specialized Components

### ClubConnectOtpField

A specialized input field for OTP (One-Time Password) verification with auto-focus behavior.

```kotlin
ClubConnectOtpField(
    otpDigits = otpDigits,
    onDigitChange = viewModel::updateOtpDigit,
    onComplete = { viewModel.verifyOtp() },
    length = 4
)
```

**Key Features:**
- Automatic focus management
- Auto-advance to next field when digit is entered
- Customizable styling
- Completion callback when all digits are filled

### ClubConnectCountryCodeSelector

A dropdown selector for country codes with flag emoji display.

```kotlin
ClubConnectCountryCodeSelector(
    selectedCountry = country,
    onCountrySelected = { country = it },
    showFlagEmoji = true
)
```

**Key Features:**
- Flag emoji display
- Dropdown with country list
- Customizable appearance
- Keyboard management

## Snackbars

### GlanceSnackbarHost

A customizable snackbar host with support for different message types and actions.

```kotlin
// Add to your Scaffold
Scaffold(
    snackbarHost = {
        GlanceSnackbarHost(hostState = snackbarHostState)
    },
    // ...
)

// Show a snackbar
SnackbarManager.showError(
    message = "Failed to connect to server",
    actionLabel = "Retry",
    onAction = { /* retry logic */ }
)
```

**Key Features:**
- Three message types: error, success, info
- Action button support
- Customizable duration
- Dismiss button option
- Automatic styling based on message type

### Snackbar Manager

Utility to manage and display snackbars from anywhere in the app.

```kotlin
// From a ViewModel
showErrorSnackbar(
    message = "An error occurred",
    actionLabel = "Retry",
    onAction = { loadData() }
)

// From anywhere
SnackbarManager.showSuccess("Operation completed successfully!")
```

**Key Features:**
- Centralized snackbar management
- Extension functions for ViewModels
- Duration control
- Three message types: error, success, info

### AutoErrorHandler

Automatically displays errors as snackbars from a UiState.

```kotlin
// In a Composable
AutoErrorHandler(
    state = viewModel.uiState,
    onRetry = { viewModel.loadData() }
)
```

**Key Features:**
- Automatic error handling from UiState
- Optional retry action
- Optional success callback
- Can be enabled/disabled