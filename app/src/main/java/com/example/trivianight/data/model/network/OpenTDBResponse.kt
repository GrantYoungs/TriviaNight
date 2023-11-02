package com.example.trivianight.data.model.network

import android.text.Html
import com.example.trivianight.data.model.domain.Answer
import com.example.trivianight.data.model.domain.Question
import com.example.trivianight.data.model.domain.TriviaQuestions
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

enum class ResponseCode(val value: Int) {
    SUCCESS(0),
    NO_RESPONSE(1),
    INVALID_PARAMETER(2),
    TOKEN_NOT_FOUND(3),
    TOKEN_EMPTY(4);

    companion object {
        fun getResponseCode(code: Int): ResponseCode = ResponseCode.values().first { it.value == code }
    }
}

@JsonClass(generateAdapter = true)
data class OpenTDBResponse(
    @Json(name = "response_code")
    val responseCode: Int,

    @Json(name = "results")
    val results: List<OpenTDBQuestionResponse>
)

@JsonClass(generateAdapter = true)
data class OpenTDBQuestionResponse(
    @Json(name = "category")
    val category: String,

    @Json(name = "type")
    val type: String,

    @Json(name = "difficulty")
    val difficulty: String,

    @Json(name = "question")
    val question: String,

    @Json(name = "correct_answer")
    val correctAnswer: String,

    @Json(name = "incorrect_answers")
    val incorrectAnswers: List<String>
)

fun OpenTDBResponse.toDomain(): TriviaQuestions {
    return TriviaQuestions(
        responseCode = responseCode,
        results = results.map { it.toDomain() }
    )
}

fun OpenTDBQuestionResponse.toDomain(): Question {
    return Question(
        category = fromHtml(category),
        type = fromHtml(type),
        difficulty = fromHtml(difficulty),
        question = fromHtml(question),
        correctAnswer = Answer(value = fromHtml(correctAnswer), isCorrect = true),
        incorrectAnswers = incorrectAnswers.map {
            Answer(value = fromHtml(it), isCorrect = false)
        },

        // TODO possibly move the shuffling to the viewModel
        allAnswers = incorrectAnswers.map {
            Answer(value = fromHtml(it), isCorrect = false)
        }.plus(Answer(value = fromHtml(correctAnswer), isCorrect = true)).shuffled()
    )
}

private fun fromHtml(htmlString: String): String {
    return Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY).toString()
}
