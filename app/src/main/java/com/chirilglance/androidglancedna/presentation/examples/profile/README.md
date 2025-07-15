# Role-Based Profile Management System for Android

This README documents the implementation of a secure, role-based profile management system for Android applications. The system allows users to create, manage, and switch between different profiles with specific roles, with all data securely stored using encryption.

## Overview

The profile management system provides a flexible framework for applications that need to support:

- Multiple user roles/profiles within a single account
- Secure storage of profile data
- Role-based UI and navigation
- Persistence across app restarts
- Profile switching without requiring re-authentication

## Components

### Core Classes

#### SecureStorageManager

- Handles encrypted data storage with automatic corruption recovery
- Uses Android's EncryptedSharedPreferences for AES256 encryption
- Provides safe read/write operations for various data types
- Includes fallback mechanisms for handling storage errors

#### ProfileManager (Interface)

- Defines the contract for profile management operations
- Methods for adding, retrieving, updating, and removing profiles
- Support for active profile selection and persistence

#### ProfileManagerImpl

- Implementation of ProfileManager using SecureStorageManager
- Handles serialization/deserialization of profile data using kotlinx.serialization
- Maintains a list of all profiles and tracks the active profile
- Preserves profile preferences across sessions

#### UserProfile & UserRoleType

- Domain models for profile data
- Support for different role types (STANDARD, PREMIUM, ADMIN, CREATOR, etc.)
- Customizable with attributes, team affiliation, etc.

#### ProfileManagementViewModel

- Provides UI state for profile management screens
- Loads profiles from storage and initializes with samples if needed
- Handles profile switching with navigation reset

### UI Components

#### ProfileManagementScreen

- UI for viewing and selecting profiles
- Shows active profile and available profiles
- Allows clearing all profiles

#### Profile Details & Edit Screens

- View and edit profile information
- Set a profile as active

#### Role-Based Navigation

- Bottom navigation that adapts based on active profile's role
- Different menu items for different roles

#### Profile UI Integration

- Top app bar with profile menu
- Welcome message on home screen based on active profile

## Integration Flow

### Initial Setup

#### Add Required Dependencies

```gradle
// In app/build.gradle
dependencies {
    // Secure storage
    implementation "androidx.security:security-crypto:1.1.0-alpha06"

    // Serialization
    implementation "org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1"
}

// Make sure to apply the serialization plugin
plugins {
    id 'org.jetbrains.kotlin.plugin.serialization' version '1.8.10'
}
```

#### Add Required Annotations

Mark domain models with @Serializable:

```kotlin
@Serializable
data class UserProfile(...)

@Serializable
enum class UserRoleType { ... }
```

### User Authentication Flow

#### User Login

- User authenticates with phone number/OTP
- On successful authentication, the app receives an auth token
- Token is securely stored using TokenManager

#### Profile Initialization

After authentication, check if user has existing profiles:

```kotlin
if (!profileManager.hasProfiles()) {
    // First-time user - show role selection
    navigateToRoleSelection()
} else {
    // Returning user - load profiles and navigate to main screen
    val activeProfile = profileManager.getActiveProfile()
    if (activeProfile == null) {
        // No active profile, select first available
        profileManager.getAllProfiles().firstOrNull()?.let {
            profileManager.saveActiveProfile(it)
        }
    }
    navigateToMainScreen()
}
```

#### Role Selection (First-time)

New user selects a role type (STANDARD, PREMIUM, etc.)
App creates a profile based on selected role:

```kotlin
val newProfile = UserProfile(
    profileId = UUID.randomUUID().toString(),
    name = userName,
    email = userEmail,
    roleType = selectedRoleType,
    displayName = userDisplayName
)

profileManager.addProfile(newProfile)
profileManager.saveActiveProfile(newProfile)
```

#### App Startup (Returning User)

On app startup, MainActivity loads profiles early:

```kotlin
lifecycleScope.launch {
    val activeProfile = profileManager.getActiveProfile()
    if (activeProfile != null) {
        // User has an active profile, proceed to main UI
    } else {
        // No active profile, check if we have any profiles
        val profiles = profileManager.getAllProfiles()
        if (profiles.isNotEmpty()) {
            // Set first available as active
            profileManager.saveActiveProfile(profiles.first())
        } else {
            // No profiles, user needs to authenticate
            navigateToLogin()
        }
    }
}
```

#### Logout

When user logs out, clear token but optionally preserve profile preferences:

```kotlin
// Complete logout (clear everything)
tokenManager.clearToken()
profileManager.clearProfiles()

// OR

// Logout but preserve profile preferences for next login
tokenManager.clearToken()
profileManager.clearProfilesPreservingActivePreference()
```

### Role-Based UI

#### Accessing Active Profile

Use the useActiveProfile() hook from any Composable:

```kotlin
@Composable
fun MyScreen() {
    val activeProfile = useActiveProfile()

    // Show different UI based on role
    when (activeProfile?.roleType) {
        UserRoleType.ADMIN -> AdminContent()
        UserRoleType.PREMIUM -> PremiumContent()
        else -> StandardContent()
    }
}
```

#### Navigation Based on Role

Configure bottom navigation items based on role:

```kotlin
@Composable
fun GlanceBottomNavigation(navController: NavController, currentRoute: String) {
    val activeProfile = useActiveProfile()
    val navigationItems = getNavigationItemsForRole(activeProfile)

    NavigationBar {
        navigationItems.forEach { item ->
            // NavigationBarItem implementation
        }
    }
}

private fun getNavigationItemsForRole(activeProfile: UserProfile?): List<NavigationItem> {
    return when (activeProfile?.roleType) {
        UserRoleType.ADMIN -> adminNavigationItems
        UserRoleType.PREMIUM -> premiumNavigationItems
        else -> standardNavigationItems
    }
}
```

#### Profile Switching

When user switches profiles, reset navigation to ensure UI consistency:

```kotlin
fun setActiveProfile(profileId: String, navController: NavController? = null) {
    viewModelScope.launch {
        // Find and set active profile
        profileManager.saveActiveProfile(profile)

        // Reset navigation if provided
        navController?.navigate(Screen.Home.route) {
            popUpTo(Screen.Home.route) { inclusive = true }
        }
    }
}
```

#### UI Recomposition on Profile Change

Force UI recomposition when profile changes:

```kotlin
@Composable
fun GlanceAppScaffold(...) {
    val activeProfile = useActiveProfile()
    val profileKey = activeProfile?.profileId ?: "no-profile"

    key(profileKey) {
        // Scaffold implementation
    }
}
```

## Security Considerations

### Data Encryption

- All profile data is encrypted using AES256 encryption
- Both keys and values are encrypted in SharedPreferences
- Master key is securely stored in Android Keystore

### Corruption Handling

- Automatic detection and recovery from corrupted preferences files
- Fallback mechanisms to prevent data loss

### Error Handling

- Comprehensive error handling in all storage operations
- Detailed logging for debugging issues

### Token Management

- Auth tokens securely stored with expiration handling
- Automatic session expiration detection

## Customization

### Adding New Role Types

To add new role types:

1. Update the UserRoleType enum:

```kotlin
@Serializable
enum class UserRoleType {
    STANDARD,
    PREMIUM,
    ADMIN,
    CREATOR,
    VIEWER,
    NEW_ROLE_TYPE; // Add new role types here

    companion object {
        fun fromString(value: String): UserRoleType {
            return try {
                valueOf(value)
            } catch (e: Exception) {
                STANDARD
            }
        }
    }
}
```

2. Update navigation and UI logic to handle the new role type

### Custom Profile Attributes

The UserProfile model supports custom attributes through a Map<String, String>:

```kotlin
// Adding custom attributes
val profileWithAttributes = existingProfile.copy(
    customAttributes = existingProfile.customAttributes + mapOf(
        "preference_theme" to "dark",
        "preference_notifications" to "enabled"
    )
)

// Or use the convenience method
val updatedProfile = existingProfile.withCustomAttribute("preference_theme", "dark")

// Save the updated profile
profileManager.addProfile(updatedProfile)
```

### Team Affiliation

For apps that need team/group functionality:

```kotlin
// Set team for a profile
profileManager.updateProfileTeam(
    profileId = "user-123",
    teamId = "team-456",
    teamName = "Marketing Team"
)

// Set default team for a role type
profileManager.saveDefaultTeamForRoleType(
    roleType = UserRoleType.CREATOR,
    teamId = "team-creators",
    teamName = "Content Creators"
)
```

## Troubleshooting

### Profile Not Persisting Across App Restarts

- Verify that @Serializable annotation is applied to both UserProfile and UserRoleType
- Check that the Kotlin Serialization plugin is correctly applied
- Add debug logs to ProfileManagerImpl's save/load methods
- Ensure profiles are loaded early in the app lifecycle

### Role-Based UI Not Updating

- Make sure you're using the useActiveProfile() hook to access the active profile
- Verify that the UI is recomposing when the profile changes (using the key() function)
- Check that the ViewModel is correctly updating the UI state

### Encryption Issues

- Check if the device supports EncryptedSharedPreferences
- Verify the app has the correct permissions
- Review SecureStorageManager logs for encryption errors

## Sample Implementation

This system has been implemented in the GlanceDNA application as a demonstration. Key files to review:

- `SecureStorageManager.kt` - Base secure storage functionality
- `ProfileManagerImpl.kt` - Profile management implementation
- `UserProfile.kt` - Domain models for profiles
- `ProfileManagementViewModel.kt` - UI state management
- `ProfileManagementScreen.kt` - UI for profile management
