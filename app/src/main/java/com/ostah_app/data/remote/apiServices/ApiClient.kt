package com.ostah_app.data.remote.apiServices

import android.content.Context
import android.icu.util.TimeUnit
import com.ostah_app.utiles.Q
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {
    private lateinit var apiService: ApiService

    fun getApiService(context: Context): ApiService {

        // Initialize ApiService if not initialized yet
        if (!::apiService.isInitialized) {
            val retrofit = Retrofit.Builder()
                .baseUrl(Q.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
            .client(okhttpClient(context)) // Add our Okhttp client
                .build()

            apiService = retrofit.create(ApiService::class.java)
        }

        return apiService
    }

    /**
     * Initialize OkhttpClient with our interceptor
     */
    private fun okhttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .addInterceptor(AuthInterceptor(context))
            .build()
    }

}