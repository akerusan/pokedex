package com.akerusan.pokedex.util

import androidx.lifecycle.LiveData
import com.akerusan.pokedex.requests.response.ApiResponse
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

class LiveDataCallAdapter<R>(private var responseType: Type) : CallAdapter<R, LiveData<ApiResponse<R>>> {

    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<R>): LiveData<ApiResponse<R>> {
        return object: LiveData<ApiResponse<R>>() {
            override fun onActive() {
                super.onActive()
                call.enqueue(object: Callback<R>{
                    override fun onFailure(call: Call<R>, t: Throwable) {
                        postValue(ApiResponse.create(t))
                    }
                    override fun onResponse(call: Call<R>, response: Response<R>) {
                        postValue(ApiResponse.create(response))
                    }
                })
            }
        }
    }
}