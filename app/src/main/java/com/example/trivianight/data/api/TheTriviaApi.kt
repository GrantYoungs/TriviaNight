package com.example.trivianight.data.api

import com.example.trivianight.data.model.network.TheTriviaApiResponse
import retrofit2.Response
import retrofit2.http.GET

interface TheTriviaApi {

    @GET("questions")
    suspend fun getTriviaQuestions(): Response<List<TheTriviaApiResponse>>
}