package com.example.trivianight.di

import com.example.trivianight.data.TriviaRepository
import com.example.trivianight.data.api.OpenTDBApi
import com.example.trivianight.data.api.TheTriviaApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.Retrofit.Builder
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TriviaNightModule {

    @Provides
    @Singleton
    @TriviaUrls.OpenTDB
    fun provideOpenTDBBaseUrl(): HttpUrl = "https://opentdb.com/".toHttpUrl()

    @Provides
    @Singleton
    @TriviaUrls.TheTriviaApi
    fun provideTheTriviaApiBaseUrl(): HttpUrl = "https://the-trivia-api.com/v2/".toHttpUrl()

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
    fun provideOpenTDBApi(
        @TriviaUrls.OpenTDB baseUrl: HttpUrl,
        okHttpClient: OkHttpClient,
        baseRetrofit: Retrofit.Builder
    ): OpenTDBApi {
        return baseRetrofit
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .build()
            .create(OpenTDBApi::class.java)
    }

    @Singleton
    @Provides
    fun provideTheTriviaApi(
        @TriviaUrls.TheTriviaApi baseUrl: HttpUrl,
        okHttpClient: OkHttpClient,
        baseRetrofit: Builder
    ): TheTriviaApi {
        return baseRetrofit
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .build()
            .create(TheTriviaApi::class.java)
    }

    @Singleton
    @Provides
    fun provideTriviaRepository(
        openTDBApi: OpenTDBApi,
        theTriviaApi: TheTriviaApi
    ): TriviaRepository {
        return TriviaRepository(
            openTDBApi = openTDBApi,
            theTriviaApi = theTriviaApi
        )
    }
}