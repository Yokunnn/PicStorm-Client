package com.vsu.picstorm.di

import com.vsu.picstorm.data.repository.RegisterRepositoryImpl
import com.vsu.picstorm.data.service.RegisterService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RegisterModule {

    @Singleton
    @Provides
    fun provideRegisterService(retrofit: Retrofit): RegisterService =
        retrofit.create(RegisterService::class.java)

    @Singleton
    @Provides
    fun providesRegisterRepository(
        registerService: RegisterService
    ) = RegisterRepositoryImpl(registerService)
}