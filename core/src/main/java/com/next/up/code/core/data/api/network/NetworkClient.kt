package com.next.up.code.core.data.api.network

import com.next.up.code.core.BuildConfig
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkClient {
    companion object {
        private const val BASE_URL_1 = BuildConfig.BASE_URL
        private const val BASE_URL_2 = "https://putr.sulselprov.go.id/api/"
        private fun createRetrofit(baseUrl: String): Retrofit {
            val hostname = "data-canter.taekwondosulsel.info"
            val certificatePinner = CertificatePinner.Builder()
                .add(hostname, BuildConfig.hostname1)
                .add(hostname, BuildConfig.hostname2)
                .add(hostname, BuildConfig.hostname3)
                .build()

            val loggingInterceptor = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            } else {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
            }

            val client = OkHttpClient.Builder()
                .certificatePinner(certificatePinner)
                .addInterceptor(loggingInterceptor)
                .build()

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
        private val apiService1: ApiService = createRetrofit(BASE_URL_1).create(ApiService::class.java)
        private val apiService2: ApiServiceIntegration = createRetrofit(BASE_URL_2).create(ApiServiceIntegration::class.java)

        fun getApiServiceForBaseUrl1(): ApiService {
            return apiService1
        }

        fun getApiServiceForBaseUrl2(): ApiServiceIntegration {
            return apiService2
        }

    }
}