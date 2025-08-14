# Location Services Components

A collection of reusable location components with Google Places API integration for Android applications built with Jetpack Compose.

## Features

- 🌍 Google Places API integration with location search
- 📍 Two UI variants: standard and labeled field styles
- 🔍 Autocomplete dropdown with intelligent positioning
- ✅ Form validation with submit handling
- 🌐 Coordinate retrieval (latitude/longitude)

## Setup Requirements

1. **Add Google Places API Key**:
    - Get an API key from the [Google Cloud Console](https://console.cloud.google.com/)
    - Enable the Places API service
    - In Keys and credentials locate the key and press on 3 dots to edit it
    - Add the package name (this format com.chirilglance.androidglancedna) and the SHA1 key (follow guidance or use this command "C:\Program Files\Android\Android Studio3\jbr\bin\keytool" -list -v -keystore "C:\Users\{username}\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android)
    - In Keys and credentials locate and copy the key (show key)
    - Save the key to local.properties MAPS_API_KEY=YOUR_API_KEY_FROM_CONSOLE (alternatively, use the key directly in manifest, less secure)
    - Add the key to your `AndroidManifest.xml`:
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="${MAPS_API_KEY}" />
   ```

2. **Add Dependencies**:
   ```gradle
   // Google Play Services - Places
   implementation 'com.google.android.libraries.places:places:3.0.0'
   ```

## Components

### 1. LocationDropdownField

A full-width location field with Google Places integration and autocomplete dropdown.

```kotlin
LocationDropdownField(
    value = viewModel.location,
    onValueChange = { viewModel.updateLocation(it) },
    onPlaceSelected = { place ->
        // Handle place selection with coordinates
        viewModel.updateLocationWithPlace(place)
    },
    placesSearchResult = viewModel.placesSearchResult,
    errorMessage = viewModel.locationError,
    isLoading = viewModel.isLoading,
    updateSearchQuery = { viewModel.updateLocationSearchQuery(it) },
    modifier = Modifier.fillMaxWidth()
)
```

### 2. LabeledLocationField

A card-style location field with label and input side-by-side, featuring Google Places integration.

```kotlin
LabeledLocationField(
    value = viewModel.location,
    onValueChange = { viewModel.updateLocation(it) },
    onPlaceSelected = { place ->
        // Handle place selection with coordinates
        viewModel.updateLocationWithPlace(place)
    },
    placesSearchResult = viewModel.placesSearchResult,
    label = "Location:",
    hint = "Enter location...",
    errorMessage = viewModel.locationError,
    isLoading = viewModel.isLoading,
    updateSearchQuery = { viewModel.updateLocationSearchQuery(it) },
    modifier = Modifier.fillMaxWidth()
)
```

## Validation

The components support validation to ensure:

1. Location text meets basic requirements (non-empty, minimum length)
2. Location has been selected from the dropdown (not just manually typed)
3. Location has valid coordinates (latitude and longitude)

```kotlin
// Check if location is valid (in ViewModel)
val isValid = viewModel.validateLocation()

// Or use the form submission method
val isFormValid = viewModel.submitLocationForm()
```

## Usage with ViewModel

Use the provided `LocationViewModel` for managing location state and validation:

```kotlin
@HiltViewModel
class LocationViewModel @Inject constructor(
    private val placesRepository: PlacesRepository
) : ViewModel() {
    // Location state
    val location: String
    val locationError: String?
    val latitude: Double?
    val longitude: Double?
    val isLoading: Boolean
    val isFormSubmitted: Boolean
    val formSuccess: Boolean
    val placesSearchResult: StateFlow<Result<List<Place>>>
    
    // Update methods
    fun updateLocation(value: String)
    fun updateLocationWithPlace(place: Place)
    fun updateLocationSearchQuery(query: String)
    
    // Validation methods
    fun validateLocation(): Boolean
    fun submitLocationForm(): Boolean
    fun resetLocation()
    
    // Fetch place details
    suspend fun getPlaceDetails(placeId: String): Result<Place>
}
```

## Implementation Example

A complete location form with validation:

```kotlin
@Composable
fun LocationForm() {
    val viewModel = hiltViewModel<LocationViewModel>()
    val coroutineScope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Location field
        LocationDropdownField(
            value = viewModel.location,
            onValueChange = { viewModel.updateLocation(it) },
            onPlaceSelected = { place ->
                coroutineScope.launch {
                    val result = viewModel.getPlaceDetails(place.id)
                    if (result.isSuccess) {
                        val placeWithCoords = result.getOrNull()
                        if (placeWithCoords != null) {
                            viewModel.updateLocationWithPlace(placeWithCoords)
                        }
                    }
                }
            },
            placesSearchResult = viewModel.placesSearchResult,
            errorMessage = viewModel.locationError,
            isLoading = viewModel.isLoading,
            updateSearchQuery = { viewModel.updateLocationSearchQuery(it) },
            modifier = Modifier.fillMaxWidth()
        )
        
        // Form actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedButton(
                onClick = { viewModel.resetLocation() },
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Text("Reset")
            }
            
            Button(
                onClick = { viewModel.submitLocationForm() }
            ) {
                Text("Submit")
            }
        }
    }
}
```

## Architecture

The location components are built following MVVM architecture:

- **UI Layer**: Composable components with state hoisting
- **ViewModel**: Manages location state, validation, and API interactions
- **Repository**: Handles data operations with Google Places API
- **Model**: Data classes for places and coordinates

## File Structure

Place these files in the appropriate directories in your project:

```
com.yourapp.domain.models/
  ├── Place.kt

com.yourapp.domain.repository/
  ├── PlacesRepository.kt

com.yourapp.domain.services/
  ├── PlacesManager.kt

com.yourapp.data.repository/
  ├── PlacesRepositoryImpl.kt

com.yourapp.di/
  ├── LocationModule.kt

com.yourapp.core.ui.components/
  ├── LocationField.kt
  ├── LocationDropdownField.kt
  ├── LabeledLocationField.kt

com.yourapp.presentation.viewmodels/
  ├── LocationViewModel.kt
```

## Notes

- The dropdown automatically positions itself above or below the field based on available space
- Keyboard visibility is detected to ensure the dropdown remains visible
- Debounced search is implemented to minimize API calls while typing
- Loading indicators show when fetching location data
- Error messages are displayed when validation fails