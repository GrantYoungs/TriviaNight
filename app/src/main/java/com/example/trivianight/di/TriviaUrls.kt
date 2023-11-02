package com.example.trivianight.di

import javax.inject.Qualifier

object TriviaUrls {

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class OpenTDB

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class TheTriviaApi
}