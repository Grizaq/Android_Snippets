package com.chirilglance.androidglancedna.di.registration

import com.chirilglance.androidglancedna.data.repository.auth.AuthRepository
import com.chirilglance.androidglancedna.data.repository.auth.AuthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
}