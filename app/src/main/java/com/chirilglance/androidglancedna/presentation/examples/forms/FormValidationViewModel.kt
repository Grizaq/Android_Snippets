package com.chirilglance.androidglancedna.presentation.examples.forms

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chirilglance.androidglancedna.core.domain.model.UiState
import com.chirilglance.androidglancedna.core.validation.ValidationResult
import com.chirilglance.androidglancedna.core.validation.ValidationUtils
import com.chirilglance.androidglancedna.domain.models.CountryCode
import com.chirilglance.androidglancedna.domain.utils.CountryCodeProvider
import com.chirilglance.androidglancedna.domain.validators.PhoneNumberFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class FormValidationViewModel @Inject constructor(
    val phoneNumberValidator: PhoneNumberFormatter
) : ViewModel() {

    // Basic form fields
    var fullName by mutableStateOf("")
        private set

    var email by mutableStateOf("")
        private set

    var phoneNumber by mutableStateOf("")
        private set

    var selectedCountry by mutableStateOf(CountryCodeProvider.getDefaultCountry())
        private set

    var bio by mutableStateOf("")
        private set

    // Additional form fields
    var birthDate by mutableStateOf<LocalDate?>(null)
        private set

    var eventDate by mutableStateOf<LocalDate?>(null)
        private set

    var eventTime by mutableStateOf<LocalTime?>(null)
        private set

    var numberValue by mutableStateOf("")
        private set

    // Password fields
    var password by mutableStateOf("")
        private set

    var passwordConfirmation by mutableStateOf("")
        private set

    // UI state for form submission
    private val _formSubmissionState = MutableStateFlow<UiState<Unit>>(UiState.Empty)
    val formSubmissionState: StateFlow<UiState<Unit>> = _formSubmissionState.asStateFlow()

    // Field validation errors - map of field names to error messages
    private val _fieldErrors = MutableStateFlow<Map<String, String?>>(emptyMap())
    val fieldErrors: StateFlow<Map<String, String?>> = _fieldErrors.asStateFlow()

    // Track which fields have been "touched" (user interacted with them)
    private val _touchedFields = mutableSetOf<String>()

    // -------- Field update methods --------

    fun updateFullName(value: String) {
        fullName = value
        validateFieldIfTouched("fullName")
    }

    fun updateEmail(value: String) {
        email = value
        validateFieldIfTouched("email")
    }

    fun updatePhoneNumber(value: String) {
        phoneNumber = value
        validateFieldIfTouched("phone")
    }

    fun updateSelectedCountry(country: CountryCode) {
        selectedCountry = country
        validateFieldIfTouched("phone")
    }

    fun updateBio(value: String) {
        bio = value
        validateFieldIfTouched("bio")
    }

    fun updateBirthDate(date: LocalDate?) {
        birthDate = date
        validateField("birthDate")
    }

    fun updateEventDate(date: LocalDate?) {
        eventDate = date
        validateField("eventDate")
    }

    fun updateEventTime(time: LocalTime?) {
        eventTime = time
    }

    fun updateNumber(value: String) {
        numberValue = value
        validateFieldIfTouched("number")
    }

    fun updatePassword(value: String) {
        password = value
        validateFieldIfTouched("password")

        // If password confirmation is already touched, revalidate it
        if (_touchedFields.contains("passwordConfirmation")) {
            validateField("passwordConfirmation")
        }
    }

    fun updatePasswordConfirmation(value: String) {
        passwordConfirmation = value
        validateFieldIfTouched("passwordConfirmation")
    }

    // -------- Field validation methods --------

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
            "fullName" -> ValidationUtils.validateName(fullName, "Full name").errorMessage
            "email" -> ValidationUtils.validateEmail(email).errorMessage
            "phone" -> ValidationUtils.validatePhoneNumber(phoneNumber, selectedCountry).errorMessage
            "bio" -> ValidationUtils.validateBio(bio).errorMessage
            "birthDate" -> validateBirthDate().errorMessage
            "eventDate" -> validateEventDate().errorMessage
            "number" -> ValidationUtils.validateNumber(numberValue, "Number").errorMessage
            "password" -> ValidationUtils.validatePassword(password).errorMessage
            "passwordConfirmation" -> ValidationUtils.validatePasswordConfirmation(password, passwordConfirmation).errorMessage
            else -> null
        }

        _fieldErrors.update { errors ->
            errors.toMutableMap().apply { put(fieldName, errorMessage) }
        }
    }

    // Original validation methods (now used by validateField)
    private fun validateBirthDate(): ValidationResult {
        return if (birthDate == null) {
            ValidationResult.Invalid("Date of birth is required")
        } else {
            // Validate that birth date is in the past
            val currentDate = LocalDate.now()
            if (birthDate!!.isAfter(currentDate)) {
                ValidationResult.Invalid("Birth date cannot be in the future")
            } else {
                ValidationResult.Valid
            }
        }
    }

    private fun validateEventDate(): ValidationResult {
        return if (eventDate == null) {
            ValidationResult.Invalid("Event date is required")
        } else {
            ValidationResult.Valid
        }
    }

    // -------- Form actions --------

    fun validateAllFields(): Boolean {
        // Validate all fields and mark all as touched
        listOf(
            "fullName", "email", "phone", "bio", "birthDate",
            "eventDate", "number", "password", "passwordConfirmation"
        ).forEach {
            _touchedFields.add(it)
            validateField(it)
        }

        return _fieldErrors.value.values.all { it == null }
    }

    fun submitForm() {
        if (!validateAllFields()) {
            _formSubmissionState.update { UiState.Error("Please fix the validation errors") }
            return
        }

        _formSubmissionState.update { UiState.Loading }

        // Simulate API call
        viewModelScope.launch {
            try {
                // Simulate network delay
                delay(1500)

                // Simulate success
                _formSubmissionState.update { UiState.Success(Unit) }
            } catch (e: Exception) {
                _formSubmissionState.update { UiState.Error("Error submitting form: ${e.message}") }
            }
        }
    }

    fun resetForm() {
        // Reset basic fields
        fullName = ""
        email = ""
        phoneNumber = ""
        selectedCountry = CountryCodeProvider.getDefaultCountry()
        bio = ""

        // Reset additional fields
        birthDate = null
        eventDate = null
        eventTime = null
        numberValue = ""
        password = ""
        passwordConfirmation = ""

        // Reset validation states
        _touchedFields.clear()
        _fieldErrors.update { emptyMap() }
        _formSubmissionState.update { UiState.Empty }
    }

    fun resetValidationFlags() {
        // Clear all validation states
        _touchedFields.clear()
        _fieldErrors.update { emptyMap() }
    }
}