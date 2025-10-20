package com.chirilglance.androidglancedna.data.auth.model

import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import com.chirilglance.androidglancedna.domain.auth.model.User
import java.util.Date

/**
 * Firestore document model for user data.
 *
 * This is the data layer representation that maps directly to Firestore documents.
 * It uses Firestore-specific annotations and types.
 *
 * Firestore document path: users/{userId}
 */
data class FirestoreUser(
    @PropertyName("id")
    val id: String = "",

    @PropertyName("email")
    val email: String? = null,

    @PropertyName("name")
    val name: String? = null,

    @PropertyName("photo_url")
    val photoUrl: String? = null,

    @PropertyName("phone_number")
    val phoneNumber: String? = null,

    @PropertyName("role")
    val role: String? = null,

    @PropertyName("created_at")
    @ServerTimestamp
    val createdAt: Date? = null,

    @PropertyName("last_login_at")
    @ServerTimestamp
    val lastLoginAt: Date? = null
) {
    /**
     * Convert domain User model to Firestore model
     */
    companion object {
        fun fromDomain(user: User): FirestoreUser {
            return FirestoreUser(
                id = user.id,
                email = user.email,
                name = user.name,
                photoUrl = user.photoUrl,
                phoneNumber = user.phoneNumber,
                role = user.role,
                // createdAt and lastLoginAt will be set by @ServerTimestamp
                createdAt = null,
                lastLoginAt = null
            )
        }
    }

    /**
     * Convert Firestore model to domain User model
     */
    fun toDomain(): User {
        return User(
            id = id,
            email = email,
            name = name,
            photoUrl = photoUrl,
            phoneNumber = phoneNumber,
            role = role,
            createdAt = createdAt?.time,
            lastLoginAt = lastLoginAt?.time
        )
    }

    /**
     * Create a map for Firestore updates (without server timestamps)
     */
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "email" to email,
            "name" to name,
            "photo_url" to photoUrl,
            "phone_number" to phoneNumber,
            "role" to role
        ).filterValues { it != null }
    }
}