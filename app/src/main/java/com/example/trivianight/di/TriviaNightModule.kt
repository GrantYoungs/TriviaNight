package com.example.trivianight.di

import com.example.trivianight.data.TriviaApi
import com.example.trivianight.data.TriviaRepository
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TriviaNightModule {

    @Provides
    fun provideBaseUrl(): HttpUrl = "https://opentdb.com/".toHttpUrl()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient
        .Builder()
        .build()

    @Singleton
    @Provides
    fun provideBaseRetrofit(): Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
    }

    @Singleton
    @Provides
    fun provideTriviaNightApi(
        baseUrl: HttpUrl,
        okHttpClient: OkHttpClient,
        baseRetrofit: Retrofit.Builder
    ): TriviaApi {
        return baseRetrofit
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .build()
            .create(TriviaApi::class.java)
    }

    @Singleton
    @Provides
    fun provideTriviaRepository(
        triviaApi: TriviaApi
    ): TriviaRepository {
        return TriviaRepository(triviaApi = triviaApi)
    }
}