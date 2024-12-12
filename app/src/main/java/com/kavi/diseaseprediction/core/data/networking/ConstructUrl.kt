package com.kavi.diseaseprediction.core.data.networking

import com.kavi.diseaseprediction.BuildConfig

fun constructUrl(url: String): String {
     return when {
        url.contains(BuildConfig.BASE_URL) -> url
        url.startsWith("/") -> BuildConfig.BASE_URL + url.drop(1)
        else -> BuildConfig.BASE_URL + url
     }
}
