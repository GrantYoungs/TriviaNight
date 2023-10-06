package com.example.trivianight.util.retrofit

import retrofit2.HttpException
import retrofit2.Response

/**
 * Returns the body of nullable type [T] if the server responds with a success status code. If the
 * [Response] is not successful, it will throw an [HttpException]
 *
 * @return The response of type [T] or null if there is no response body.
 * @throws [HttpException] if the response has a non-success status code.
 */
@Throws(HttpException::class)
fun <T> Response<T>.bodyOrError(): T? {
    return if (isSuccessful) body() else throw HttpException(this)
}