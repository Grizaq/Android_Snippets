package com.chirilglance.androidglancedna.di.roles

import com.chirilglance.androidglancedna.domain.profile.ProfileManager
import com.chirilglance.androidglancedna.presentation.examples.profile.ProfileManagementViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
object ProfileManagerViewModelModule {

    @Provides
    @ActivityRetainedScoped
    fun provideProfileManagementViewModel(
        profileManager: ProfileManager
    ): ProfileManagementViewModel {
        return ProfileManagementViewModel(profileManager)
    }
}