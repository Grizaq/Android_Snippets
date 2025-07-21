# # Android Glance DNA Utilities

This package contains utility extensions and components to help streamline Android development.

## Contents

- **String Extensions**: String manipulation utilities
- **Date Extensions**: Date formatting and time utilities
- **Compose Extensions**: Compose UI modifier extensions
- **Intent Extensions**: External app/intent utilities
- **ProfileImage**: Reusable profile image component

## Quick Usage Guide

### String Extensions

```kotlin
// Capitalize words
name.capitalizeWords()  // "john smith" → "John Smith"

// Extract initials
name.extractInitials()  // "Jane Doe" → "JD"

// Truncate text
description.truncate(50)  // Limits to 50 chars with "..."
```

### Date Extensions

```kotlin
// Format dates
date.formatForDisplay()  // "21 Jul 2025"
date.formatForApi()      // "2025-07-21T12:30:45.123Z"

// Relative time
date.timeAgoDisplay()    // "2h ago", "Yesterday", etc.

// Time parsing
"13:45".toLocalTime()    // LocalTime object
```

### Compose Extensions

```kotlin
// Prevent double clicks
Modifier.simpleDebounceClickable { /* action */ }

// No ripple effect
Modifier.simpleNoRippleClickable { /* action */ }

// Composable versions (use in @Composable functions)
Modifier.debouncedClick { /* action */ }
Modifier.noRippleClickable { /* action */ }

// Unit conversions
textStyle.fontSize.toDp()  // Convert TextUnit to Dp
24.dp.toPx()               // Convert Dp to pixels
```

### Intent Extensions

```kotlin
// Open URL in Chrome Custom Tabs
context.openUrlInCustomTabs("https://example.com")

// Open email app
context.openEmailApp("support@example.com")

// Share content
context.shareText("Title", "Content to share")
```

### ProfileImage

```kotlin
// Basic usage
ProfileImage(
    photoUrl = user.avatarUrl,
    name = user.name
)

// Customized
ProfileImage(
    photoUrl = user.avatarUrl,
    name = user.name,
    size = 64.dp,
    backgroundColor = MaterialTheme.colorScheme.primary,
    onClick = { /* handle click */ }
)
```

## Dependencies

The utilities require these dependencies:

```kotlin
// For IntentExtensions
implementation(libs.androidx.browser)

// For ProfileImage
implementation(libs.coil.compose)
```

## Best Practices

1. Use consistent date formatting across the app with DateExtensions
2. Use ProfileImage for all user avatars to ensure consistency
3. Use debounce modifiers for buttons that trigger navigation
4. Prefer the @Composable extensions in UI code for better state management
5. Use non-composable extensions in utility functions