package com.chirilglance.androidglancedna.domain.auth.model

/**
 * Authentication modes supported by GlanceDNA.
 *
 * This enum defines how authentication flows work and which services are used.
 * Switch between modes via AuthConfig without code changes.
 */
enum class AuthMode {
    /**
     * Firebase-only authentication
     *
     * Flow:
     * 1. User signs in with Google
     * 2. Authenticate with Firebase
     * 3. Store Firebase ID token in TokenManager
     * 4. Optionally save user data to Firestore
     *
     * Best for: Quick projects, MVP, demos, apps without custom backend
     * Pros: Fast setup, reliable, scalable
     * Cons: Locked into Firebase ecosystem
     */
    FIREBASE_ONLY,

    /**
     * Backend-only authentication
     *
     * Flow:
     * 1. User provides credentials (email/password, phone, etc.)
     * 2. Authenticate with your custom backend
     * 3. Store backend token in TokenManager
     *
     * Best for: Apps avoiding Google services, custom auth requirements
     * Pros: Full control, no third-party dependencies
     * Cons: More backend work required
     */
    BACKEND_ONLY,

    /**
     * Hybrid Firebase + Backend authentication
     *
     * Flow:
     * 1. User signs in with Google
     * 2. Authenticate with Firebase
     * 3. Exchange Firebase token with your backend
     * 4. Store backend token in TokenManager
     * 5. Optionally save user data to Firestore
     *
     * Best for: Apps needing both OAuth convenience and custom backend logic
     * Pros: Best of both worlds, flexible user experience
     * Cons: More complex setup, two services to maintain
     */
    FIREBASE_BACKEND
}