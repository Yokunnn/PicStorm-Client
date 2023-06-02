package com.vsu.picstorm.di

import com.vsu.picstorm.data.repository.ProfileRepositoryImpl
import com.vsu.picstorm.data.service.ProfileService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {

    @Singleton
    @Provides
    fun provideProfileService(retrofit: Retrofit): ProfileService =
        retrofit.create(ProfileService::class.java)

    @Singleton
    @Provides
    fun providesProfileRepository(
        profileService: ProfileService
    ) = ProfileRepositoryImpl(profileService)
}