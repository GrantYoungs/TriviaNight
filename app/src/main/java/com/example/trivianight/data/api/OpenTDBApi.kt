package com.example.trivianight.data.api

import com.example.trivianight.data.model.network.OpenTDBResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenTDBApi {

    /**
     * Retrieves trivia questions to be displayed to the user.
     *
     * @param numQuestions The number of questions to retrieve.
     *
     * @return [OpenTDBResponse]
     */
    @GET("api.php")
    suspend fun getTriviaQuestions(
        @Query("amount") numQuestions: Int
    ): Response<OpenTDBResponse>
}