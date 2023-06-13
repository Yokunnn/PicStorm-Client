package com.vsu.picstorm.domain

interface ConfigurationRepository {
    suspend fun fetchConfiguration(): Boolean
    val hasUploadPhotoButton: Boolean
}