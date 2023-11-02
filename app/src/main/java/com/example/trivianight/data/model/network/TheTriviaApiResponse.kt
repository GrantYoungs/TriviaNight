package com.example.trivianight.data.model.network

import com.example.trivianight.data.model.domain.Answer
import com.example.trivianight.data.model.domain.Question
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * JSON response from The Trivia API: https://the-trivia-api.com/v2/
 */
@JsonClass(generateAdapter = true)
data class TheTriviaApiResponse(

    @Json(name = "category")
    val category: String,

    @Json(name = "id")
    val id: String,

    @Json(name = "correctAnswer")
    val correctAnswer: String,

    @Json(name = "incorrectAnswers")
    val incorrectAnswers: List<String>,

    @Json(name = "question")
    val question: String,

    @Json(name = "tags")
    val tags: List<String>,

    @Json(name = "type")
    val type: String,

    @Json(name = "difficulty")
    val difficulty: String,

    @Json(name = "regions")
    val regions: List<String>,

    @Json(name = "isNiche")
    val isNiche: Boolean
)

fun TheTriviaApiResponse.toDomain(): Question {
    return Question(
        category = category,
        type = type,
        difficulty = difficulty,
        question = question,
        correctAnswer = Answer(
            value = correctAnswer,
            isCorrect = true
        ),
        incorrectAnswers = incorrectAnswers.map { answer ->
            Answer(
                value = answer,
                isCorrect = false
            )
        },
        allAnswers = incorrectAnswers.map { answer ->
            Answer(
                value = answer,
                isCorrect = false
            )
        }.plus(Answer(value = correctAnswer, isCorrect = true)).shuffled()
    )
}