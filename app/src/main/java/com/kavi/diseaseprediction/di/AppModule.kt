package com.kavi.diseaseprediction.di


import com.kavi.diseaseprediction.core.data.networking.HttpClientFactory
import com.kavi.diseaseprediction.weather.data.networking.RemoteDiseaseDataSource
import com.kavi.diseaseprediction.weather.data.networking.RemoteWeatherDataSource
import com.kavi.diseaseprediction.weather.domain.DiseaseDataSource
import com.kavi.diseaseprediction.weather.domain.WeatherDataSource
import com.kavi.diseaseprediction.weather.presentation.weather_List.WeatherListViewModel
import io.ktor.client.engine.cio.CIO
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    // HttpClient instance (reuse the same for all data sources)
    single { HttpClientFactory.create(CIO.create()) }

    // Weather Data Source and ViewModel
    singleOf(::RemoteWeatherDataSource).bind<WeatherDataSource>()
    // Disease Data Source
    singleOf(::RemoteDiseaseDataSource).bind<DiseaseDataSource>()
    // WeatherListViewModel (injecting both WeatherDataSource and DiseaseDataSource)
    viewModel { WeatherListViewModel(get(), get(), get()) }
}
