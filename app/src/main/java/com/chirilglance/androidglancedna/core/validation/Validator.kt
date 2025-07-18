package com.chirilglance.androidglancedna.core.validation

/**
 * Base interface for all validators
 * @param T Type of the value being validated
 */
interface Validator<T> {
    /**
     * Validates the given value
     * @param value The value to validate
     * @return ValidationResult indicating if the value is valid
     */
    fun validate(value: T): ValidationResult

    /**
     * Combines this validator with another one
     * Both validators must pass for the validation to be successful
     */
    fun and(other: Validator<T>): Validator<T> = CompositeValidator(this, other)

    /**
     * Creates a validator that passes if either this validator or the other passes
     */
    fun or(other: Validator<T>): Validator<T> = AlternativeValidator(this, other)
}

/**
 * Composite validator that runs two validators in sequence
 * @param first First validator to run
 * @param second Second validator to run (only if first passes)
 */
class CompositeValidator<T>(
    private val first: Validator<T>,
    private val second: Validator<T>
) : Validator<T> {
    override fun validate(value: T): ValidationResult {
        val firstResult = first.validate(value)
        return if (firstResult is ValidationResult.Valid) {
            second.validate(value)
        } else {
            firstResult
        }
    }
}

/**
 * Alternative validator that passes if either validator passes
 */
class AlternativeValidator<T>(
    private val first: Validator<T>,
    private val second: Validator<T>
) : Validator<T> {
    override fun validate(value: T): ValidationResult {
        val firstResult = first.validate(value)
        return if (firstResult is ValidationResult.Valid) {
            firstResult
        } else {
            second.validate(value)
        }
    }
}
