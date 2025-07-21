# # Navigation System

## Overview

This navigation system is built for the Android Glance DNA application using Jetpack Compose Navigation. It provides a structured way to navigate between different screens in the app while handling route parameters when needed. The system implements both top-level navigation and nested navigation for specific feature sections.

## Components

### 1. `Screen.kt`

This sealed class defines all the navigation destinations in the application:

- **Base screens**: Home, UI Components, Form Validation, etc.
- **Authentication screens**: Phone Verification, OTP Verification, Welcome
- **Profile Management screens**: Profile Management, Profile Details, Profile Edit

Each screen is implemented as a data object that extends the `Screen` class with:
- A `route` property that defines the navigation path, including parameter placeholders when needed
- A `baseRoute` property that extracts the main route without parameters
- An overridable `createRoute()` function that allows dynamic route creation with parameter values

### 2. `AppNavigation.kt`

This composable function sets up the main navigation graph using Jetpack Compose's `NavHost`:

- It configures the `NavHostController` to handle navigation between screens
- Sets the home screen as the start destination
- Defines composable destinations for each screen in the application
- Handles parameter extraction for routes that require parameters (like Profile Details and Edit screens)

### 3. Nested Navigation

The app uses nested navigation for specific feature sections to encapsulate related screens and create isolated navigation flows. For example, the UI Components section has its own nested navigation:

#### `UIComponentsNavigation.kt`

This composable function creates a nested navigation graph specifically for the UI Components section:

- Has its own `NavHostController` separate from the main navigation
- Defines routes for various UI component screens (Buttons, Cards, Dialogs, etc.)
- Uses a separate routes object (`UIComponentsRoutes`) to manage route constants

## Usage Examples

### Basic Navigation

```kotlin
// Navigate to a screen without parameters
navController.navigate(Screen.Home.route)
navController.navigate(Screen.UiComponents.route)
```

### Navigation with Parameters

```kotlin
// Navigate to profile details with a specific profile ID
val profileId = "user123"
navController.navigate(Screen.ProfileDetails.createRoute(profileId))

// Navigate to OTP verification with a phone number
val phoneNumber = "+1234567890"
navController.navigate(Screen.OtpVerification.createRoute(phoneNumber))
```

### Parameter Extraction

For screens that require parameters, extract them from the backstack entry:

```kotlin
composable(Screen.ProfileDetails.route) { backStackEntry ->
    val profileId = backStackEntry.arguments?.getString("profileId") ?: ""
    ProfileDetailsScreen(
        profileId = profileId,
        navController = navController
    )
}
```

### Nested Navigation

To use nested navigation, create a separate NavHost within a screen:

```kotlin
// 1. In the main navigation, define the container screen
composable(Screen.UiComponents.route) {
    UIComponentsScreen()
}

// 2. In the UIComponentsScreen, create a new NavController and host
@Composable
fun UIComponentsScreen() {
    val nestedNavController = rememberNavController()
    
    // Use the nested navigation
    UIComponentsNavigation(nestedNavController)
}

// 3. Navigate within the nested navigation
nestedNavController.navigate(UIComponentsRoutes.BUTTONS)
```

## Adding New Screens

### To Main Navigation

1. Add a new data object to the `Screen` sealed class
2. Define its route (with parameters if needed)
3. Override `createRoute()` if the screen requires parameters
4. Add a new composable destination in `AppNavigation.kt`

### To Nested Navigation

1. Add a new constant to the appropriate routes object (e.g., `UIComponentsRoutes`)
2. Add a new composable destination in the nested navigation function
3. Make sure to keep route naming consistent within the feature domain

## Architecture Decisions

### State Management

The app uses Data Stores rather than shared ViewModels for state management across navigation flows. This approach:

- Keeps navigation concerns separate from state management
- Prevents navigation from becoming overly complex
- Provides a more maintainable and testable architecture
- Works well with both top-level and nested navigation

### Nested Navigation Benefits

Using nested navigation for feature-specific flows provides several advantages:

- **Encapsulation**: Each feature section manages its own navigation logic
- **Isolation**: Navigation events in one section don't affect others
- **Modularity**: Features can be developed and tested independently
- **Cleaner code**: Main navigation remains focused on top-level app structure