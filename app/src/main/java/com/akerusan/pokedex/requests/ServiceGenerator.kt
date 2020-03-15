package com.akerusan.pokedex.requests

import com.akerusan.pokedex.util.Constants
import com.akerusan.pokedex.util.LiveDataCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class ServiceGenerator {

    companion object {

        private val client = OkHttpClient.Builder()
            .connectTimeout(Constants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build()

        private val retrofit = Retrofit.Builder()
            .baseUrl(PokemonApi.ENDPOINT)
            .client(client)
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        private val pokemonApi = retrofit.create(PokemonApi::class.java)

        fun getPokemonApi(): PokemonApi {
            return pokemonApi
        }
    }
}