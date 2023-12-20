package com.example.trivianight.data.api

import com.example.trivianight.data.model.network.TheTriviaApiResponse
import retrofit2.Response
import retrofit2.http.GET

interface TheTriviaApi {

    /**
     * Retrieves trivia questions from The Trivia Api.
     *
     * @return [Response]<[List]<[TheTriviaApiResponse]>>
     */
    @GET("questions")
    suspend fun getTriviaQuestions(): Response<List<TheTriviaApiResponse>>
}