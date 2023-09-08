package com.example.trivianight.data

import com.example.trivianight.data.model.network.TriviaQuestionsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TriviaApi {

    @GET("api.php")
    suspend fun getTriviaQuestions(
        @Query("amount") numQuestions: Int = 10
    ): Response<TriviaQuestionsResponse>
}