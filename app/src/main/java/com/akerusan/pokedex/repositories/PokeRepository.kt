package com.akerusan.pokedex.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import com.akerusan.pokedex.AppExecutors
import com.akerusan.pokedex.models.Pokemon
import com.akerusan.pokedex.persistence.PokemonDao
import com.akerusan.pokedex.persistence.PokemonDatabase
import com.akerusan.pokedex.requests.ServiceGenerator
import com.akerusan.pokedex.requests.response.ApiResponse
import com.akerusan.pokedex.requests.response.PokemonListResponse
import com.akerusan.pokedex.requests.response.PokemonSingleResponse
import com.akerusan.pokedex.util.Constants
import com.akerusan.pokedex.util.NetworkBoundResource
import com.akerusan.pokedex.util.Resource


class PokeRepository private constructor(context: Context) {

    private var pokemonDao: PokemonDao? = null

    init {
        pokemonDao = PokemonDatabase.getInstance(context)!!.getPokemonDao()
    }

    companion object {
        private var instance: PokeRepository? = null
        fun getInstance(context: Context) : PokeRepository {
            if (instance == null) {
                instance = PokeRepository(context)
            }
            return instance!!
        }
    }

    // method to decide weather to hit the cache or the network
    fun searchPokemonApi(offset: String, limit: String) : LiveData<Resource<List<Pokemon>>>{
        return object: NetworkBoundResource<List<Pokemon>, PokemonListResponse>(AppExecutors().getInstance()!!){
            override fun saveCallResult(item: PokemonListResponse) {
                val pokemonList = item.getPokemon()
                var index = 0
                for (pokemon in pokemonList) {
                    if (pokemon.id == null) {
                        val url: String = pokemon.url!!
                        val id = (url.substringAfter(Constants.BASE_URL+"pokemon/")).substringBefore("/")
                        pokemonList[index].id = id.toInt()
                    }
                    index++
                }
                pokemonDao!!.insertPokemonList(pokemonList)
            }
            override fun shouldFetch(data: List<Pokemon>?): Boolean {
                return true
            }
            override fun loadFromDb(): LiveData<List<Pokemon>> {
                return pokemonDao!!.getAllPokemon()
            }
            // return retrofit call as liveData object
            override fun createCall(): LiveData<ApiResponse<PokemonListResponse>> {
                return ServiceGenerator.getPokemonApi().getPokemonResponse(offset, limit)
            }
        }.asLiveData()
    }

    fun searchPokemonApi(pokemonId: String) : LiveData<Resource<Pokemon>> {
        return object: NetworkBoundResource<Pokemon, PokemonSingleResponse>(AppExecutors().getInstance()!!){
            override fun saveCallResult(item: PokemonSingleResponse) {
                val pokemon = item.getPokemon()
                if (pokemon.frontDefault == null) {
                    pokemon.frontDefault = "null"
                }
                if (pokemon.backDefault == null) {
                    pokemon.backDefault = "null"
                }
                pokemonDao!!.updatePokemon(pokemon.name!!, pokemon.frontDefault!!, pokemon.backDefault!!, pokemon.id!!)
            }
            override fun shouldFetch(data: Pokemon?): Boolean {
                return true
            }
            override fun loadFromDb(): LiveData<Pokemon> {
                return pokemonDao!!.getSinglePokemon(pokemonId.toInt())
            }
            override fun createCall(): LiveData<ApiResponse<PokemonSingleResponse>> {
                return ServiceGenerator.getPokemonApi().getSinglePokemonResponse(pokemonId)
            }
        }.asLiveData()
    }

    suspend fun setPokemonFav(pokemonId: Int, pokemonFav: Boolean) {
        pokemonDao!!.updatePokemon(pokemonId, pokemonFav)
    }
}