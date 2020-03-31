package com.akerusan.pokedex.util

import androidx.lifecycle.LiveData
import com.akerusan.pokedex.requests.response.ApiResponse
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class LiveDataCallAdapterFactory : CallAdapter.Factory() {

    /**
     * This method performs a number of checks and then returns the Response type for the retrofit requests.
     * (@bodyType is the ResponseType. It can be PokemonListResponse or PokemonSingleResponse)
     *
     * CHECK #1) returnType returns LiveData
     * CHECK #2) Type LiveData<T> is of ApiResponse.class
     * CHECK #3) Make sure ApiResponse is parameterized. AKA: ApiResponse<T> exists.
     */

    override operator fun get(returnType: Type?, annotations: Array<Annotation?>?, retrofit: Retrofit?): CallAdapter<*, *>? {

        // CHECK #1
        // Make sure the CallAdapter is returning a type of LiveData
        if (getRawType(returnType!!) != LiveData::class.java) {
            return null
        }

        // CHECK #2
        // Type that LiveData is wrapping
        val observableType: Type = getParameterUpperBound(0, returnType as ParameterizedType)

        // check if it's of type ApiResponse
        val rawObservableType: Type = getRawType(observableType)

        require(rawObservableType == ApiResponse::class.java) {
            "Type must be a defined resource"
        }

        // CHECK3
        // check if ApiResponse is parameterized. AKA: Does ApiResponse<T> exists? (must wrap around T)
        // FYI: T is either PokemonListResponse or T will be a PokemonSingleResponse
        require(observableType is ParameterizedType) {
            "resource must be parameterized"
        }

        val bodyType = getParameterUpperBound(0, observableType)
        return LiveDataCallAdapter<Type>(bodyType)
    }
}