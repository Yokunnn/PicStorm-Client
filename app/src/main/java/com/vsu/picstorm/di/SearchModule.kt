package com.vsu.picstorm.di

import com.vsu.picstorm.data.repository.SearchRepositoryImpl
import com.vsu.picstorm.data.service.SearchService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SearchModule {

    @Singleton
    @Provides
    fun provideSearchService(retrofit: Retrofit): SearchService =
        retrofit.create(SearchService::class.java)

    @Singleton
    @Provides
    fun providesSearchRepository(
        searchService: SearchService
    ) = SearchRepositoryImpl(searchService)
}