package com.chirilglance.androidglancedna.di.auth

import android.content.Context
import com.chirilglance.androidglancedna.data.auth.remote.BackendAuthApi
import com.chirilglance.androidglancedna.data.auth.remote.FirebaseDataSource
import com.chirilglance.androidglancedna.data.auth.remote.GoogleAuthDataSource
import com.chirilglance.androidglancedna.data.auth.remote.MockBackendAuthApi
import com.chirilglance.androidglancedna.data.auth.repository.GoogleAuthRepositoryImpl
import com.chirilglance.androidglancedna.domain.auth.repository.GoogleAuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for Google authentication dependencies.
 *
 * This module provides all the dependencies needed for Google OAuth authentication:
 * - Firebase Auth instance
 * - Firestore instance
 * - Data sources (Google, Firebase, Backend)
 * - Repository implementation
 *
 * All dependencies are singletons to ensure consistent state across the app.
 */
@Module
@InstallIn(SingletonComponent::class)
object GoogleAuthModule {

    /**
     * Provides Firebase Auth instance.
     *
     * Firebase Auth is thread-safe and can be safely shared as a singleton.
     */
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    /**
     * Provides Firestore instance.
     *
     * Firestore is thread-safe and can be safely shared as a singleton.
     */
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    /**
     * Provides GoogleAuthDataSource.
     *
     * Handles Google Sign-In operations using Google Identity Services.
     */
    @Provides
    @Singleton
    fun provideGoogleAuthDataSource(
        @ApplicationContext context: Context
    ): GoogleAuthDataSource {
        return GoogleAuthDataSource(context)
    }

    /**
     * Provides FirebaseDataSource.
     *
     * Handles Firebase Authentication and Firestore operations.
     */
    @Provides
    @Singleton
    fun provideFirebaseDataSource(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): FirebaseDataSource {
        return FirebaseDataSource(firebaseAuth, firestore)
    }

    /**
     * Provides BackendAuthApi.
     *
     * Currently provides a mock implementation.
     * Replace with real implementation when backend is ready:
     *
     * @Provides
     * @Singleton
     * fun provideBackendAuthApi(): BackendAuthApi {
     *     return Retrofit.Builder()
     *         .baseUrl(AuthConfig.BACKEND_BASE_URL)
     *         .addConverterFactory(GsonConverterFactory.create())
     *         .build()
     *         .create(BackendAuthApi::class.java)
     * }
     */
    @Provides
    @Singleton
    fun provideBackendAuthApi(): BackendAuthApi {
        // Mock implementation for now
        // Replace with real Retrofit implementation when backend is ready
        return MockBackendAuthApi()
    }

    /**
     * Provides GoogleAuthRepository implementation.
     *
     * This is the main entry point for Google authentication operations.
     * All dependencies are automatically injected by Hilt.
     */
    @Provides
    @Singleton
    fun provideGoogleAuthRepository(
        googleAuthDataSource: GoogleAuthDataSource,
        firebaseDataSource: FirebaseDataSource,
        backendAuthApi: BackendAuthApi,
        tokenManager: com.chirilglance.androidglancedna.domain.auth.TokenManager
    ): GoogleAuthRepository {
        return GoogleAuthRepositoryImpl(
            googleAuthDataSource = googleAuthDataSource,
            firebaseDataSource = firebaseDataSource,
            backendAuthApi = backendAuthApi,
            tokenManager = tokenManager
        )
    }
}