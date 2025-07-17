package com.chirilglance.androidglancedna.domain.models.profile

import kotlinx.serialization.Serializable

/**
 * Represents the type of user role in the application.
 * Can be extended to add more role types as needed.
 */
@Serializable
enum class UserRoleType {
    STANDARD,
    PREMIUM,
    ADMIN,
    CREATOR,
    VIEWER;

    companion object {
        fun fromString(value: String): UserRoleType {
            return try {
                valueOf(value)
            } catch (e: Exception) {
                STANDARD // Default to STANDARD if the role type is not recognized
            }
        }
    }
}

/**
 * Represents a user profile in the application.
 * Contains basic user information and profile settings.
 *
 * @property profileId Unique identifier for this profile
 * @property name User's name
 * @property email User's email address
 * @property phoneNumber User's phone number
 * @property gender User's gender (optional)
 * @property imageUrl URL to the user's profile image (optional)
 * @property roleType The type of role this profile represents
 * @property teamId ID of the team this user belongs to (optional)
 * @property teamName Name of the team this user belongs to (optional)
 * @property displayName A user-friendly name for display purposes
 * @property isVerified Whether this profile has been verified
 * @property customAttributes Additional custom attributes as key-value pairs
 */
@Serializable
data class UserProfile(
    val profileId: String,
    val name: String,
    val email: String,
    val phoneNumber: String? = null,
    val gender: String? = null,
    val imageUrl: String? = null,
    val roleType: UserRoleType = UserRoleType.STANDARD,
    val teamId: String? = null,
    val teamName: String? = null,
    val displayName: String = name,
    val isVerified: Boolean = false,
    val customAttributes: Map<String, String> = emptyMap()
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserProfile) return false

        // Compare only by ID for equality
        return profileId == other.profileId
    }

    override fun hashCode(): Int {
        return profileId.hashCode()
    }

    /**
     * Checks if this profile belongs to a team
     */
    fun hasTeam(): Boolean = !teamId.isNullOrEmpty() && !teamName.isNullOrEmpty()

    /**
     * Creates a copy of this profile with updated team information
     */
    fun withTeam(teamId: String, teamName: String): UserProfile {
        return this.copy(
            teamId = teamId,
            teamName = teamName
        )
    }

    /**
     * Creates a copy of this profile with a custom attribute added or updated
     */
    fun withCustomAttribute(key: String, value: String): UserProfile {
        val updatedAttributes = customAttributes.toMutableMap().apply {
            put(key, value)
        }
        return this.copy(customAttributes = updatedAttributes)
    }

    /**
     * Creates a copy of this profile with the specified custom attributes
     */
    fun withCustomAttributes(attributes: Map<String, String>): UserProfile {
        return this.copy(customAttributes = attributes)
    }
}

/**
 * Sample user profiles for demonstration purposes.
 */
object SampleProfiles {
    val standardUser = UserProfile(
        profileId = "user-standard-001",
        name = "John Doe",
        email = "john.doe@example.com",
        phoneNumber = "+1234567890",
        gender = "Male",
        imageUrl = "https://i.pravatar.cc/150?u=standard",
        roleType = UserRoleType.STANDARD,
        displayName = "John (Standard User)"
    )

    val premiumUser = UserProfile(
        profileId = "user-premium-001",
        name = "Jane Smith",
        email = "jane.smith@example.com",
        phoneNumber = "+1987654321",
        gender = "Female",
        imageUrl = "https://i.pravatar.cc/150?u=premium",
        roleType = UserRoleType.PREMIUM,
        displayName = "Jane (Premium User)"
    )

    val adminUser = UserProfile(
        profileId = "user-admin-001",
        name = "Admin User",
        email = "admin@example.com",
        phoneNumber = "+1555123456",
        imageUrl = "https://i.pravatar.cc/150?u=admin",
        roleType = UserRoleType.ADMIN,
        displayName = "System Administrator"
    )

    val creatorUser = UserProfile(
        profileId = "user-creator-001",
        name = "Alex Creator",
        email = "alex@creators.com",
        phoneNumber = "+1444123456",
        imageUrl = "https://i.pravatar.cc/150?u=creator",
        roleType = UserRoleType.CREATOR,
        teamId = "team-creators-001",
        teamName = "Content Creators",
        displayName = "Alex (Creator)"
    )

    fun getAllSampleProfiles(): List<UserProfile> = listOf(
        standardUser,
        premiumUser,
        adminUser,
        creatorUser
    )
}