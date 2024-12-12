package com.kavi.diseaseprediction.di


import com.kavi.diseaseprediction.core.data.networking.HttpClientFactory
import com.kavi.diseaseprediction.weather.data.networking.RemoteWeatherDataSource
import com.kavi.diseaseprediction.weather.presentation.weather_List.WeatherListViewModel
import com.kavi.diseaseprediction.weather.domain.WeatherDataSource
import io.ktor.client.engine.cio.CIO
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { HttpClientFactory.create(CIO.create()) }
    singleOf(::RemoteWeatherDataSource).bind<WeatherDataSource>()

    viewModelOf(::WeatherListViewModel)
}