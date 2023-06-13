package com.vsu.picstorm.data.repository

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import javax.inject.Inject
import com.vsu.picstorm.BuildConfig
import com.vsu.picstorm.R
import com.vsu.picstorm.domain.ConfigurationRepository
import kotlinx.coroutines.tasks.await


class ConfigurationRepositoryImpl @Inject constructor() : ConfigurationRepository {
    private val remoteConfig
        get() = Firebase.remoteConfig

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0 else 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
    }

    override suspend fun fetchConfiguration(): Boolean = remoteConfig.fetchAndActivate().await()

    override val hasUploadPhotoButton: Boolean
        get() = remoteConfig[HAS_UPLOAD_PHOTO_BUTTON_KEY].asBoolean()

    companion object {
        private const val HAS_UPLOAD_PHOTO_BUTTON_KEY = "hasUploadPhotoButton"
    }
}
