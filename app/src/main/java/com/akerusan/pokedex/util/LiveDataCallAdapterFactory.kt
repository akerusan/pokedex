package com.akerusan.pokedex.util

import androidx.lifecycle.LiveData
import com.akerusan.pokedex.requests.response.ApiResponse
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class LiveDataCallAdapterFactory : CallAdapter.Factory() {

    override operator fun get(returnType: Type?, annotations: Array<Annotation?>?, retrofit: Retrofit?): CallAdapter<*, *>? {

        if (getRawType(returnType!!) != LiveData::class.java) {
            return null
        }

        val observableType: Type = getParameterUpperBound(0, returnType as ParameterizedType)
        val rawObservableType: Type = getRawType(observableType)

        require(rawObservableType == ApiResponse::class.java) {
            "Type must be a defined resource"
        }

        require(observableType is ParameterizedType) {
            "resource must be parameterized"
        }

        val bodyType = getParameterUpperBound(0, observableType)
        return LiveDataCallAdapter<Type>(bodyType)
    }
}