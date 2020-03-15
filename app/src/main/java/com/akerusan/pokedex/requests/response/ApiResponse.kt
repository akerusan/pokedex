package com.akerusan.pokedex.requests.response

import retrofit2.Response
import java.io.IOException

@Suppress("unused") // T is used in extending classes
sealed class ApiResponse<T> {

    companion object {
        fun <T> create(error: Throwable): ApiResponse<T> {
            return ApiErrorResponse((if (error.message == "") error.message else "Unknown error\nCheck network connection")!!)
        }

        fun <T> create(response: Response<T>): ApiResponse<T> {
            if (response.isSuccessful) {
                val body: T = response.body()!!
                return if (body == null || response.code() == 204) {
                    ApiEmptyResponse()
                } else {
                    ApiSuccessResponse(body)
                }
            } else {
                val errorMsg =
                    try {
                        response.errorBody().toString()
                    } catch (e: IOException) {
                        e.printStackTrace()
                        response.message()
                    }
                return ApiErrorResponse(errorMsg)
            }
        }
    }
}

data class ApiSuccessResponse<T>(val body: T) : ApiResponse<T>()
data class ApiErrorResponse<T>(val errorMessage: String) : ApiResponse<T>()
class ApiEmptyResponse<T> : ApiResponse<T>()