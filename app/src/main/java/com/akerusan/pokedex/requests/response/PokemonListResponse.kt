package com.akerusan.pokedex.requests.response

import com.akerusan.pokedex.models.Pokemon
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PokemonListResponse {

    @SerializedName("results")
    @Expose
    var results: List<Pokemon>? = null

    @SerializedName("error")
    @Expose
    private val error: String? = null

    fun getPokemon(): List<Pokemon> {
        return results!!
    }

}