package com.chirilglance.androidglancedna.di.storage

import android.content.Context
import com.chirilglance.androidglancedna.core.data.storage.SecureStorageManager
import com.chirilglance.androidglancedna.data.auth.TokenManagerImpl
import com.chirilglance.androidglancedna.data.profile.ProfileManagerImpl
import com.chirilglance.androidglancedna.domain.auth.TokenManager
import com.chirilglance.androidglancedna.domain.profile.ProfileManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing storage-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    /**
     * Provides a singleton instance of SecureStorageManager
     */
    @Provides
    @Singleton
    fun provideSecureStorageManager(@ApplicationContext context: Context): SecureStorageManager {
        return SecureStorageManager(context)
    }

    /**
     * Provides a singleton implementation of TokenManager
     */
    @Provides
    @Singleton
    fun provideTokenManager(
        @ApplicationContext context: Context,
        secureStorageManager: SecureStorageManager
    ): TokenManager {
        return TokenManagerImpl(context, secureStorageManager)
    }

    /**
     * Provides a singleton implementation of ProfileManager
     */
    @Provides
    @Singleton
    fun provideProfileManager(
        @ApplicationContext context: Context,
        secureStorageManager: SecureStorageManager
    ): ProfileManager {
        return ProfileManagerImpl(context, secureStorageManager)
    }
}