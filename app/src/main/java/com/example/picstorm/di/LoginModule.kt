package com.example.picstorm.di

import com.example.picstorm.data.repository.LoginRepositoryImpl
import com.example.picstorm.data.service.LoginService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoginModule {

    @Singleton
    @Provides
    fun provideLoginService(retrofit: Retrofit): LoginService =
        retrofit.create(LoginService::class.java)

    @Singleton
    @Provides
    fun providesLoginRepository(
        loginService: LoginService
    ) = LoginRepositoryImpl(loginService)
}