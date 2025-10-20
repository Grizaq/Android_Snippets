package com.chirilglance.androidglancedna.domain.auth.model

/**
 * Domain model representing a user in the application.
 *
 * This model is flexible to support different authentication methods:
 * - Email/password registration: email required, phoneNumber optional
 * - Phone authentication: phoneNumber required, email optional
 * - OAuth (Google): email usually available, phoneNumber optional
 *
 * All nullable fields should be handled carefully by developers based on their auth flow.
 */
data class User(
    /**
     * Unique user identifier
     * - Firebase mode: Firebase UID
     * - Backend mode: Backend-generated user ID
     */
    val id: String,

    /**
     * User's email address (nullable for phone-based registration)
     */
    val email: String? = null,

    /**
     * User's display name (nullable if not provided during registration)
     */
    val name: String? = null,

    /**
     * URL to user's profile photo (nullable if not provided)
     */
    val photoUrl: String? = null,

    /**
     * User's phone number (nullable for email-based registration)
     */
    val phoneNumber: String? = null,

    /**
     * User's role in the application (e.g., "user", "admin", "premium")
     * Nullable to allow apps to handle roles as needed
     */
    val role: String? = null,

    /**
     * Timestamp when user account was created (milliseconds since epoch)
     * Nullable - will be set by Firestore server timestamp on first save
     */
    val createdAt: Long? = null,

    /**
     * Timestamp of user's last login (milliseconds since epoch)
     * Nullable - can be updated on each login
     */
    val lastLoginAt: Long? = null
) {
    /**
     * Helper to get display name or fallback to email or "User"
     */
    fun getDisplayName(): String {
        return name ?: email?.substringBefore("@") ?: "User"
    }

    /**
     * Check if user has a complete profile (email and name)
     */
    fun hasCompleteProfile(): Boolean {
        return !email.isNullOrBlank() && !name.isNullOrBlank()
    }
}