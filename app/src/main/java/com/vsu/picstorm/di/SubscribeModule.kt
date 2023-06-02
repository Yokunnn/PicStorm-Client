package com.vsu.picstorm.di

import com.vsu.picstorm.data.repository.SubscriptionRepositoryImpl
import com.vsu.picstorm.data.service.SubscriptionService
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
    fun provideSubscribeService(retrofit: Retrofit): SubscriptionService =
        retrofit.create(SubscriptionService::class.java)

    @Singleton
    @Provides
    fun providesSubscribeRepository(
        subscriptionService: SubscriptionService
    ) = SubscriptionRepositoryImpl(subscriptionService)
}