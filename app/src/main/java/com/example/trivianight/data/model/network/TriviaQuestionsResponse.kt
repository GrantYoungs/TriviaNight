package com.example.trivianight.data.model.network

import android.os.Build
import android.text.Html
import com.example.trivianight.data.model.domain.Question
import com.example.trivianight.data.model.domain.TriviaQuestions
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TriviaQuestionsResponse(
    @Json(name = "response_code")
    val responseCode: Int,

    @Json(name = "results")
    val results: List<QuestionResponse>
)

@JsonClass(generateAdapter = true)
data class QuestionResponse(
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

fun TriviaQuestionsResponse.toDomain(): TriviaQuestions {
    return TriviaQuestions(
        responseCode = responseCode,
        results = results.map { it.toDomain() }
    )
}

fun QuestionResponse.toDomain(): Question {
    return Question(
        category = fromHtml(category),
        type = fromHtml(type),
        difficulty = fromHtml(difficulty),
        question = fromHtml(question),
        correctAnswer = fromHtml(correctAnswer),
        incorrectAnswers = incorrectAnswers.map { fromHtml(it) }
    )
}

private fun fromHtml(htmlString: String): String {
    return Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY).toString()
}
