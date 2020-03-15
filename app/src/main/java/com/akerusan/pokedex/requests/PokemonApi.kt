package com.akerusan.pokedex.requests

import androidx.lifecycle.LiveData
import com.akerusan.pokedex.requests.response.ApiResponse
import com.akerusan.pokedex.requests.response.PokemonListResponse
import com.akerusan.pokedex.requests.response.PokemonSingleResponse
import com.akerusan.pokedex.util.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonApi {

    companion object {
        val ENDPOINT = Constants.BASE_URL
    }

    @GET("pokemon/")
    fun getPokemonResponse(
        @Query("offset") offset: String? = null,
        @Query("limit") limit: String? = null) : LiveData<ApiResponse<PokemonListResponse>>

    @GET("pokemon/{id}")
    fun getSinglePokemonResponse(@Path("id") id: String) : LiveData<ApiResponse<PokemonSingleResponse>>
}