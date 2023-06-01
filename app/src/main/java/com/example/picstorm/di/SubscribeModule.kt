package com.example.picstorm.di

import com.example.picstorm.data.repository.SubscribeRepositoryImpl
import com.example.picstorm.data.service.SubscribeService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SubscribeModule {

    @Singleton
    @Provides
    fun provideSubscribeService(retrofit: Retrofit): SubscribeService =
        retrofit.create(SubscribeService::class.java)

    @Singleton
    @Provides
    fun providesSubscribeRepository(
        subscribeService: SubscribeService
    ) = SubscribeRepositoryImpl(subscribeService)
}