# AndroidGlanceDNA

AndroidGlanceDNA is a collection of reusable components, utilities, and best practices for modern Android development using Jetpack Compose. Instead of a rigid library, this project provides a reference implementation that you can easily adapt to your specific needs.

## Overview

This project showcases production-ready implementations of common Android app features:

- 🎨 **UI Components**: Buttons, text fields, cards, snackbars, and more
- 🔄 **State Management**: UiState pattern for consistent state handling
- 🛑 **Error Handling**: Automatic error display with snackbars
- 🔒 **Authentication**: Phone verification and OTP flow
- 📱 **Navigation**: Structured navigation with proper back handling
- 📝 **Form Validation**: Input validation with live feedback
- 📍 **Location Services**: Places API integration

## Getting Started

### Installation

Clone the repository and open it in Android Studio:

```bash
git clone https://github.com/GlaceChiril/AndroidGlanceDNA.git
```

### Usage

This is not a library to be imported, but rather a collection of examples to learn from and copy into your own projects. Browse the code, understand the patterns, and adapt them to your needs.

## Key Features

### [UI Components](app/src/main/java/com/chirilglance/androidglancedna/core/ui/README.md)
Reusable UI components including buttons, text fields, cards, and snackbars with consistent styling and support for loading states.

### [Error Handling](core/domain/error/README.md)
Automatic error handling system that displays network and validation errors as snackbars without boilerplate code.

### [Authentication](presentation/examples/auth/README.md)
Complete, commonly used, phone verification and OTP flow with timer and resend functionality.

### [Form Validation](presentation/examples/forms/README.md)
Real-time input validation with immediate feedback.

### [Navigation](presentation/navigation/README.md)
Type-safe navigation using sealed classes with proper back stack handling and deep link support.

### [State Management](core/domain/model/README.md)
Consistent state management with UiState pattern for predictable UI updates and error handling.

## General Project Structure

```
app/src/main/java/com/chirilglance/androidglancedna/
├── core/                   # Core functionality
│   ├── domain/             # Domain layer core components
│   │   ├── error/          # Error handling
│   │   └── model/          # Core models like UiState
│   └── ui/                 # UI components and extensions
│       ├── components/     # Reusable UI components
│       ├── extensions/     # UI-related extension functions
│       └── scaffold/       # App scaffold components
├── data/                   # Data layer
│   └── repository/         # Repository implementations
├── di/                     # di module
│   └── feature             # Separate package per feature for scalability
├── domain/                 # Business logic
│   ├── models/             # Domain models
│   └── utils/              # Domain utilities
└── presentation/           # UI layer
    ├── examples/           # Example implementations
    │   ├── auth/           # Authentication examples
    │   ├── forms/          # Form validation examples
    │   ├── location/       # Location services examples
    │   └── ui/             # UI component examples
    ├── home/               # Home screen
    ├── navigation/         # Navigation components
    └── ui/                 # UI theme and styling
```
