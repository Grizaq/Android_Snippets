# Authentication Flow

A complete phone verification and OTP authentication flow with a clean architecture and user-friendly UI.

## Overview

The authentication flow in AndroidGlanceDNA demonstrates a modern, mobile-first approach to user verification:

1. Phone Number Entry: Users enter their phone number with country code selection
2. OTP Verification: A 4-digit OTP is sent to the user's phone and must be entered to verify
3. Success Screen: Upon successful verification, users are welcomed to the app

This implementation includes real-time validation, country-specific formatting, automatic OTP field focus management, countdown timer for OTP resend, comprehensive error handling, and nested navigation with type safety.

## Components

### Phone Verification

The phone verification screen allows users to enter their phone number with country code selection.

```kotlin
// Add to your navigation graph
composable(Screen.PhoneVerification.route) {
    PhoneVerificationScreen(
        onVerificationRequested = { phoneNumber ->
            navController.navigate(Screen.OtpVerification.createRoute(phoneNumber))
        }
    )
}
```

Key Features:
- Country code selection with flag emoji display
- Real-time phone number validation
- Country-specific formatting
- Loading state during API calls
- Automatic keyboard hiding when input is complete

### OTP Verification

The OTP verification screen allows users to enter the verification code sent to their phone.

```kotlin
// Add to your navigation graph with parameters
composable(
    route = Screen.OtpVerification.route,
    arguments = listOf(
        navArgument("phoneNumber") { type = NavType.StringType }
    )
) { backStackEntry ->
    val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""
    OtpVerificationScreen(
        phoneNumber = phoneNumber,
        onVerificationComplete = { /* Handle success */ },
        onBackPressed = { navController.popBackStack() }
    )
}
```

Key Features:
- Auto-focusing OTP input fields
- Auto-advancing to next field
- Resend functionality with countdown timer
- Immediate validation feedback
- Back navigation to phone entry

### Welcome Screen

A simple success screen shown after successful verification.

```kotlin
// Add to your navigation graph
composable(Screen.Welcome.route) {
    WelcomeScreen(
        onGetStarted = { /* Navigate to main app */ }
    )
}
```

## Flow Implementation

### Main Authentication Screen

The `AuthenticationScreen` composable sets up the nested navigation for the entire authentication flow:

```kotlin
@Composable
fun AuthenticationScreen(
    mainNavController: NavHostController? = null
) {
    val authNavController = rememberNavController()

    NavHost(
        navController = authNavController,
        startDestination = Screen.PhoneVerification.route
    ) {
        // Phone verification screen
        composable(Screen.PhoneVerification.route) { ... }
        
        // OTP verification screen
        composable(
            route = Screen.OtpVerification.route,
            arguments = listOf(navArgument("phoneNumber") { ... })
        ) { ... }
        
        // Welcome screen
        composable(Screen.Welcome.route) { ... }
    }
}
```

### ViewModels

#### PhoneVerificationViewModel

Manages the phone verification process:

```kotlin
// Inject in your Composable
val viewModel: PhoneVerificationViewModel = hiltViewModel()

// State collection
val phoneNumber by viewModel.phoneNumber.collectAsState()
val selectedCountry by viewModel.selectedCountry.collectAsState()
val phoneNumberError by viewModel.phoneNumberError.collectAsState()

// Actions
viewModel.updatePhoneNumber("1234567890")
viewModel.updateSelectedCountry(country)
viewModel.verifyPhoneNumber { formattedNumber ->
    // Navigate to OTP screen
}
```

#### OtpVerificationViewModel

Manages the OTP verification process:

```kotlin
// Inject in your Composable
val viewModel: OtpVerificationViewModel = hiltViewModel()

// Initialize with phone number
LaunchedEffect(phoneNumber) {
    viewModel.initPhoneNumber(phoneNumber)
}

// State collection
val otpDigits by viewModel.otpDigits.collectAsState()
val remainingSeconds by viewModel.remainingSeconds.collectAsState()

// Actions
viewModel.updateOtpDigit(index, digit)
viewModel.verifyOtp { /* Handle success */ }
viewModel.resendOtp()
```

## Utilities

### PhoneNumberValidator

A utility class for phone number validation and formatting:

```kotlin
@Inject
lateinit var phoneNumberValidator: PhoneNumberValidator

// Validate a phone number
val errorMessage = phoneNumberValidator.validatePhoneNumber(phoneNumber, country)

// Format for API submission
val formattedNumber = phoneNumberValidator.formatForApi(phoneNumber, country)

// Format for display
val displayNumber = phoneNumberValidator.formatForDisplay(phoneNumber, country)
```

### CountryCodeProvider

A utility for country code management:

```kotlin
// Get all available country codes
val countries = CountryCodeProvider.getAllCountryCodes()

// Get default country
val defaultCountry = CountryCodeProvider.getDefaultCountry()

// Find country by code
val country = CountryCodeProvider.findCountryByCode("US")

// Convert country code to emoji flag
val flag = CountryCodeProvider.codeToEmoji("US")
```

## Repository Layer

The `AuthRepository` interface and its implementation handle the API interactions:

```kotlin
interface AuthRepository {
    suspend fun verifyPhoneNumber(phoneNumber: String): Flow<UiState<String>>
    suspend fun verifyOtp(phoneNumber: String, otp: String): Flow<UiState<Boolean>>
}
```

In a production app, you would implement this interface with actual API calls. The sample implementation simulates API behavior with delays and test cases.

## Testing

### Test Cases

The implementation includes test cases for error handling:

- Phone number containing "0000" will trigger an error response
- OTP code "0000" will trigger an invalid code error

These can be used to test the error handling UI without changing code.

## Integration

To integrate this authentication flow into your app:

1. Add the authentication screen to your main navigation graph:

```kotlin
composable(Screen.Authentication.route) {
    AuthenticationScreen(mainNavController = navController)
}
```

2. Navigate to the authentication flow from your app:

```kotlin
navController.navigate(Screen.Authentication.route)
```

3. Implement the `AuthRepository` interface with your actual authentication API.

4. Add appropriate error handling and loading states throughout the flow.

## Best Practices

This authentication flow demonstrates several best practices:

1. Separation of Concerns: UI, business logic, and data access are properly separated
2. State Management: Consistent use of UiState for representing operation states
3. Error Handling: Comprehensive error handling with user-friendly messages
4. User Experience: Smooth transitions, appropriate loading indicators, and clear feedback
5. Validation: Real-time validation with immediate feedback
6. Accessibility: Proper content descriptions and keyboard handling