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
import com.chirilglance.androidglancedna.domain.validators.PhoneNumberValidator
import com.chirilglance.androidglancedna.presentation.components.form.FormValidationManager
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
    val phoneNumberValidator: PhoneNumberValidator
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

    // Field validation state
    var isFullNameValidated by mutableStateOf(false)
        private set

    var isEmailValidated by mutableStateOf(false)
        private set

    var isPhoneValidated by mutableStateOf(false)
        private set

    var isBirthDateValidated by mutableStateOf(false)
        private set

    var isEventDateValidated by mutableStateOf(false)
        private set

    var isNumberValidated by mutableStateOf(false)
        private set

    var isBioValidated by mutableStateOf(false)
        private set

    var isPasswordValidated by mutableStateOf(false)
        private set

    var isPasswordConfirmationValidated by mutableStateOf(false)
        private set

    // UI state for form submission
    private val _formSubmissionState = MutableStateFlow<UiState<Unit>>(UiState.Empty)
    val formSubmissionState: StateFlow<UiState<Unit>> = _formSubmissionState.asStateFlow()

    // Form validation manager
    val formValidator = FormValidationManager(
        { validateFullName(fullName) },
        { validateEmail(email) },
        { validatePhone(phoneNumber) },
        { validateBio(bio) },
        { validateBirthDate() },
        { validateEventDate() },
        { validateNumber(numberValue) },
        { validatePassword(password) },
        { validatePasswordConfirmation(password, passwordConfirmation) }
    )

    // -------- Field update methods --------

    fun updateFullName(value: String) {
        fullName = value
        // If already validated, validate again on change
        if (isFullNameValidated) {
            validateFullNameField()
        }
    }

    fun updateEmail(value: String) {
        email = value
        if (isEmailValidated) {
            validateEmailField()
        }
    }

    fun updatePhoneNumber(value: String) {
        phoneNumber = value
        if (isPhoneValidated) {
            validatePhoneField()
        }
    }

    fun updateSelectedCountry(country: CountryCode) {
        selectedCountry = country
        if (isPhoneValidated) {
            validatePhoneField()
        }
    }

    fun updateBio(value: String) {
        bio = value
        if (isBioValidated) {
            validateBioField()
        }
    }

    fun updateBirthDate(date: LocalDate?) {
        birthDate = date
        validateBirthDateField()
    }

    fun updateEventDate(date: LocalDate?) {
        eventDate = date
        validateEventDateField()
    }

    fun updateEventTime(time: LocalTime?) {
        eventTime = time
    }

    fun updateNumber(value: String) {
        numberValue = value
        if (isNumberValidated) {
            validateNumberField()
        }
    }

    fun updatePassword(value: String) {
        password = value
        if (isPasswordValidated) {
            validatePasswordField()
        }
        // If password confirmation is already validated, we need to revalidate it
        if (isPasswordConfirmationValidated) {
            validatePasswordConfirmationField()
        }
    }

    fun updatePasswordConfirmation(value: String) {
        passwordConfirmation = value
        if (isPasswordConfirmationValidated) {
            validatePasswordConfirmationField()
        }
    }

    // -------- Validation trigger methods --------

    fun validateFullNameField() {
        isFullNameValidated = true
    }

    fun validateEmailField() {
        isEmailValidated = true
    }

    fun validatePhoneField() {
        isPhoneValidated = true
    }

    fun validateBioField() {
        isBioValidated = true
    }

    fun validateBirthDateField() {
        isBirthDateValidated = true
    }

    fun validateEventDateField() {
        isEventDateValidated = true
    }

    fun validateNumberField() {
        isNumberValidated = true
    }

    fun validatePasswordField() {
        isPasswordValidated = true
    }

    fun validatePasswordConfirmationField() {
        isPasswordConfirmationValidated = true
    }

    // -------- Validation methods --------

    fun validateFullName(name: String): ValidationResult {
        return ValidationUtils.validateName(name, "Full name")
    }

    fun validateEmail(value: String): ValidationResult {
        return ValidationUtils.validateEmail(value)
    }

    fun validatePhone(value: String): ValidationResult {
        return phoneNumberValidator.validate(value, selectedCountry)
    }

    fun validateBio(value: String): ValidationResult {
        return ValidationUtils.validateBio(value)
    }

    fun validateBirthDate(): ValidationResult {
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

    fun validateEventDate(): ValidationResult {
        return if (eventDate == null) {
            ValidationResult.Invalid("Event date is required")
        } else {
            ValidationResult.Valid
        }
    }

    fun validateNumber(value: String): ValidationResult {
        return ValidationUtils.validateNumber(value, "Number")
    }

    fun validatePassword(value: String): ValidationResult {
        return ValidationUtils.validatePassword(value)
    }

    fun validatePasswordConfirmation(password: String, confirmation: String): ValidationResult {
        return ValidationUtils.validatePasswordConfirmation(password, confirmation)
    }

    // -------- Form actions --------

    fun submitForm() {
        if (!formValidator.validateAll()) {
            _formSubmissionState.update { UiState.Error("Please fix the validation errors") }

            // Mark all fields as validated
            isFullNameValidated = true
            isEmailValidated = true
            isPhoneValidated = true
            isBioValidated = true
            isBirthDateValidated = true
            isEventDateValidated = true
            isNumberValidated = true
            isPasswordValidated = true
            isPasswordConfirmationValidated = true

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
        isFullNameValidated = false
        isEmailValidated = false
        isPhoneValidated = false
        isBioValidated = false
        isBirthDateValidated = false
        isEventDateValidated = false
        isNumberValidated = false
        isPasswordValidated = false
        isPasswordConfirmationValidated = false

        formValidator.reset()
        _formSubmissionState.update { UiState.Empty }
    }

    fun resetValidationFlags() {
        isFullNameValidated = false
        isEmailValidated = false
        isPhoneValidated = false
        isBioValidated = false
        isBirthDateValidated = false
        isEventDateValidated = false
        isNumberValidated = false
        isPasswordValidated = false
        isPasswordConfirmationValidated = false

        // Make sure the form validator knows we're not in validation mode
        formValidator.reset()
    }

}