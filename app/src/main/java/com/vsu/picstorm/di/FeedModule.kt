package com.vsu.picstorm.di

import com.vsu.picstorm.data.repository.FeedRepositoryImpl
import com.vsu.picstorm.data.service.PublicationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FeedModule {

    @Singleton
    @Provides
    fun provideFeedService(retrofit: Retrofit): PublicationService =
        retrofit.create(PublicationService::class.java)

    @Singleton
    @Provides
    fun providesFeedRepository(
        publicationService: PublicationService
    ) = FeedRepositoryImpl(publicationService)
}