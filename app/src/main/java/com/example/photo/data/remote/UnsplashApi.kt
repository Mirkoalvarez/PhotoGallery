package com.example.photo.data.remote

// --- CORRECCIÓN 1: Importar las clases que creamos ---
import com.example.photo.data.remote.model.RemotePhoto
import com.example.photo.data.remote.model.SearchResponse
// --------------------------------------------------

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashApi {

    @GET("photos")
    suspend fun getLatestPhotos(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("order_by") orderBy: String
    ): List<RemotePhoto>

    // --- AGREGADO: Endpoint de Búsqueda ---
    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): SearchResponse

    // --- CORRECCIÓN 2: Eliminamos la función duplicada que estaba acá ---

    companion object {
        private const val BASE_URL = "https://api.unsplash.com/"

        fun create(accessKey: String): UnsplashApi {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val authInterceptor = Interceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .addHeader("Accept-Version", "v1")
                    .addHeader("Authorization", "Client-ID $accessKey")
                    .build()
                chain.proceed(request)
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(logging)
                .build()

            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(okHttpClient)
                .build()
                .create(UnsplashApi::class.java)
        }
    }
}