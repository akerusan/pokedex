package com.akerusan.pokedex.requests.response

import com.akerusan.pokedex.models.Pokemon
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PokemonSingleResponse {
    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("sprites")
    @Expose
    var sprites: Pokemon? = null

    fun getPokemon(): Pokemon {
        sprites!!.name = name
        sprites!!.id = id
        return sprites!!
    }
}