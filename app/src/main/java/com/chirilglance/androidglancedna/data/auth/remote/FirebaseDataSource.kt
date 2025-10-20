package com.chirilglance.androidglancedna.data.auth.remote

import com.chirilglance.androidglancedna.data.auth.config.AuthConfig
import com.chirilglance.androidglancedna.data.auth.model.FirestoreUser
import com.chirilglance.androidglancedna.domain.auth.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source for Firebase Authentication and Firestore operations.
 *
 * Handles:
 * - Firebase Authentication with Google credentials
 * - User data storage in Firestore
 * - Token retrieval
 */
@Singleton
class FirebaseDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    /**
     * Sign in to Firebase using Google ID token.
     *
     * @param idToken The Google ID token from Google Sign-In
     * @return Pair of (User, Firebase ID Token), or null if sign-in fails
     */
    suspend fun signInWithGoogle(idToken: String): Pair<User, String>? {
        return try {
            // Create Firebase credential from Google token
            val credential = GoogleAuthProvider.getCredential(idToken, null)

            // Sign in to Firebase
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: return null

            // Check if this is a new user
            val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false
            android.util.Log.d("Auth", "Firebase sign-in: isNewUser=$isNewUser, userId=${firebaseUser.uid}")

            // Get Firebase ID token
            val firebaseToken = firebaseUser.getIdToken(false).await().token ?: return null

            // Create user object from Firebase user
            val user = User(
                id = firebaseUser.uid,
                email = firebaseUser.email,
                name = firebaseUser.displayName,
                photoUrl = firebaseUser.photoUrl?.toString(),
                phoneNumber = firebaseUser.phoneNumber,
                role = "user" // Default role
            )

            // Save to Firestore if enabled
            if (AuthConfig.STORE_IN_FIRESTORE) {
                android.util.Log.d("Auth", "STORE_IN_FIRESTORE is TRUE, proceeding to save...")

                // Check if user document exists in Firestore
                val userExists = checkIfUserExists(user.id)
                android.util.Log.d("Auth", "User exists in Firestore: $userExists")

                // About to call saveUserToFirestore
                android.util.Log.d("Auth", "About to call saveUserToFirestore with isNewUser=${!userExists}")

                // Save user (isNewUser based on Firestore, not Firebase Auth)
                saveUserToFirestore(user, isNewUser = !userExists)

                android.util.Log.d("Auth", "Returned from saveUserToFirestore")
            } else {
                android.util.Log.w("Auth", "STORE_IN_FIRESTORE is FALSE, skipping Firestore save")
            }

            Pair(user, firebaseToken)
        } catch (e: Exception) {
            android.util.Log.e("Auth", "Firebase sign-in failed: ${e.message}", e)
            null
        }
    }

    /**
     * Check if user document exists in Firestore
     */
    private suspend fun checkIfUserExists(userId: String): Boolean {
        return try {
            val snapshot = firestore.collection(AuthConfig.USERS_COLLECTION)
                .document(userId)
                .get()
                .await()
            snapshot.exists()
        } catch (e: Exception) {
            android.util.Log.e("Auth", "Error checking user existence: ${e.message}", e)
            false
        }
    }

    /**
     * Save user data to Firestore.
     *
     * If user already exists, only updates lastLoginAt.
     * If user is new, creates complete user document with server timestamps.
     *
     * @param user The user to save
     * @param isNewUser Whether this is a new user registration
     */
    suspend fun saveUserToFirestore(user: User, isNewUser: Boolean) {
        try {
            val userRef = firestore.collection(AuthConfig.USERS_COLLECTION)
                .document(user.id)

            android.util.Log.d("Auth", "Saving user to Firestore. isNewUser=$isNewUser")

            if (isNewUser) {
                // New user - create complete document with server timestamps
                val userData = mutableMapOf<String, Any?>(
                    "id" to user.id,
                    "email" to user.email,
                    "name" to user.name,
                    "photo_url" to user.photoUrl,
                    "phone_number" to user.phoneNumber,
                    "role" to (user.role ?: "user")
                )

                android.util.Log.d("Auth", "Creating NEW user document with server timestamps")

                // Use set with merge to add timestamps
                userRef.set(userData, com.google.firebase.firestore.SetOptions.merge()).await()

                // Then update with server timestamps
                userRef.update(
                    mapOf(
                        "created_at" to FieldValue.serverTimestamp(),
                        "last_login_at" to FieldValue.serverTimestamp()
                    )
                ).await()

                android.util.Log.d("Auth", "User document created successfully with timestamps")
            } else {
                // Existing user - only update last login time
                android.util.Log.d("Auth", "Updating EXISTING user's last_login_at")
                userRef.update("last_login_at", FieldValue.serverTimestamp()).await()
                android.util.Log.d("Auth", "Last login updated successfully")
            }
        } catch (e: Exception) {
            // Log error but don't fail auth if Firestore save fails
            android.util.Log.e("Auth", "Failed to save user to Firestore: ${e.message}", e)
            e.printStackTrace()
        }
    }

    /**
     * Get user data from Firestore.
     *
     * @param userId The user ID to fetch
     * @return User object or null if not found
     */
    suspend fun getUserFromFirestore(userId: String): User? {
        return try {
            val snapshot = firestore.collection(AuthConfig.USERS_COLLECTION)
                .document(userId)
                .get()
                .await()

            snapshot.toObject(FirestoreUser::class.java)?.toDomain()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get the current Firebase user.
     *
     * @return Current user or null if not signed in
     */
    fun getCurrentFirebaseUser(): User? {
        val firebaseUser = firebaseAuth.currentUser ?: return null

        return User(
            id = firebaseUser.uid,
            email = firebaseUser.email,
            name = firebaseUser.displayName,
            photoUrl = firebaseUser.photoUrl?.toString(),
            phoneNumber = firebaseUser.phoneNumber
        )
    }

    /**
     * Get fresh Firebase ID token.
     *
     * @param forceRefresh Whether to force token refresh
     * @return Firebase ID token or null
     */
    suspend fun getIdToken(forceRefresh: Boolean = false): String? {
        return try {
            firebaseAuth.currentUser?.getIdToken(forceRefresh)?.await()?.token
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Sign out from Firebase.
     */
    fun signOut() {
        firebaseAuth.signOut()
    }

    /**
     * Check if user is currently signed in to Firebase.
     */
    fun isSignedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
}