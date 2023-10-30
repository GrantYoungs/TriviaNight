package com.example.trivianight.ui.decorators

import com.example.trivianight.data.model.domain.Answer
import com.example.trivianight.data.model.domain.Question

data class QuestionDecorator(
    val category: String = "",
    val type: String = "",
    val difficulty: String = "",
    val question: String = "",
    val correctAnswer: AnswerDecorator = AnswerDecorator(),
    val incorrectAnswers: List<AnswerDecorator> = emptyList(),
    val allAnswers: List<AnswerDecorator> = emptyList()
) {
    companion object {
        fun decorate(question: Question): QuestionDecorator {
            return QuestionDecorator(
                category = question.category,
                type = question.type,
                difficulty = question.difficulty,
                question = question.question,
                correctAnswer = AnswerDecorator.decorate(question.correctAnswer),
                incorrectAnswers = question.incorrectAnswers.map { AnswerDecorator.decorate(it) },
                allAnswers = question.allAnswers.map { AnswerDecorator.decorate(it) }
            )
        }
    }
}

data class AnswerDecorator(
    val value: String = "",
    val isCorrect: Boolean = false
) {
    companion object {
        fun decorate(answer: Answer): AnswerDecorator {
            return AnswerDecorator(
                value = answer.value,
                isCorrect = answer.isCorrect
            )
        }
    }
}