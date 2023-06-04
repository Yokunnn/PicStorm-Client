package com.vsu.picstorm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vsu.picstorm.databinding.ActivityMainBinding
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        buildYandexMetricsConfig();
        buildYandexMetricsStartSession();

        YandexMetrica.reportEvent(getString(R.string.event_app_started))

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun buildYandexMetricsConfig() {
        val config: YandexMetricaConfig =
            YandexMetricaConfig.newConfigBuilder(getString(R.string.yandex_metrics_key)).build()
        // Initializing the AppMetrica SDK.
        YandexMetrica.activate(applicationContext, config)
        // Automatic tracking of user activity.
        YandexMetrica.enableActivityAutoTracking(this.application)
    }

    private fun buildYandexMetricsStartSession() {
        // Creating an extended library configuration.
        val config: YandexMetricaConfig =
            YandexMetricaConfig.newConfigBuilder(getString(R.string.yandex_metrics_key)) // Setting the length of the session timeout.
                .withSessionTimeout(15)
                .build()
        // Initializing the AppMetrica SDK.
        YandexMetrica.activate(applicationContext, config)
    }
}