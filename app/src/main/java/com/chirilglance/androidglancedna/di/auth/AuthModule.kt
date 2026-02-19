package com.chirilglance.androidglancedna.di.auth

import android.content.Context
import com.chirilglance.androidglancedna.data.auth.remote.BackendAuthApi
import com.chirilglance.androidglancedna.data.auth.remote.FirebaseDataSource
import com.chirilglance.androidglancedna.data.auth.remote.FirebaseEmailAuthDataSource
import com.chirilglance.androidglancedna.data.auth.remote.FirebasePhoneAuthDataSource
import com.chirilglance.androidglancedna.data.auth.remote.GoogleAuthDataSource
import com.chirilglance.androidglancedna.data.auth.remote.MockBackendAuthApi
import com.chirilglance.androidglancedna.data.auth.repository.EmailAuthRepositoryImpl
import com.chirilglance.androidglancedna.data.auth.repository.GoogleAuthRepositoryImpl
import com.chirilglance.androidglancedna.data.repository.auth.AuthRepository
import com.chirilglance.androidglancedna.data.repository.auth.AuthRepositoryImpl
import com.chirilglance.androidglancedna.domain.auth.TokenManager
import com.chirilglance.androidglancedna.domain.auth.repository.EmailAuthRepository
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
 * Complete authentication module for all auth methods.
 *
 * Provides:
 * - Firebase Auth & Firestore instances
 * - Google OAuth authentication
 * - Email/Password authentication
 * - Phone authentication (requires billing)
 */
@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    // ============ Firebase Instances ============

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    // ============ Data Sources ============

    @Provides
    @Singleton
    fun provideGoogleAuthDataSource(
        @ApplicationContext context: Context
    ): GoogleAuthDataSource {
        return GoogleAuthDataSource(context)
    }

    @Provides
    @Singleton
    fun provideFirebaseEmailAuthDataSource(
        firebaseAuth: FirebaseAuth
    ): FirebaseEmailAuthDataSource {
        return FirebaseEmailAuthDataSource(firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideFirebasePhoneAuthDataSource(
        firebaseAuth: FirebaseAuth
    ): FirebasePhoneAuthDataSource {
        return FirebasePhoneAuthDataSource(firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideFirebaseDataSource(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): FirebaseDataSource {
        return FirebaseDataSource(firebaseAuth, firestore)
    }

    @Provides
    @Singleton
    fun provideBackendAuthApi(): BackendAuthApi {
        return MockBackendAuthApi()
    }

    // ============ Repositories ============

    /**
     * Google OAuth repository
     */
    @Provides
    @Singleton
    fun provideGoogleAuthRepository(
        googleAuthDataSource: GoogleAuthDataSource,
        firebaseDataSource: FirebaseDataSource,
        backendAuthApi: BackendAuthApi,
        tokenManager: TokenManager
    ): GoogleAuthRepository {
        return GoogleAuthRepositoryImpl(
            googleAuthDataSource = googleAuthDataSource,
            firebaseDataSource = firebaseDataSource,
            backendAuthApi = backendAuthApi,
            tokenManager = tokenManager
        )
    }

    /**
     * Email/Password repository
     */
    @Provides
    @Singleton
    fun provideEmailAuthRepository(
        emailAuthDataSource: FirebaseEmailAuthDataSource,
        firebaseDataSource: FirebaseDataSource,
        tokenManager: TokenManager
    ): EmailAuthRepository {
        return EmailAuthRepositoryImpl(
            emailAuthDataSource = emailAuthDataSource,
            firebaseDataSource = firebaseDataSource,
            tokenManager = tokenManager
        )
    }

    /**
     * Phone Auth repository (requires billing to use)
     */
    @Provides
    @Singleton
    fun provideAuthRepository(
        firebasePhoneAuthDataSource: FirebasePhoneAuthDataSource,
        firebaseDataSource: FirebaseDataSource,
        tokenManager: TokenManager
    ): AuthRepository {
        return AuthRepositoryImpl(
            firebasePhoneAuthDataSource = firebasePhoneAuthDataSource,
            firebaseDataSource = firebaseDataSource,
            tokenManager = tokenManager
        )
    }
}