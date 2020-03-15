package com.akerusan.pokedex.viewModels

import android.app.Application
import android.widget.CompoundButton
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.akerusan.pokedex.models.Pokemon
import com.akerusan.pokedex.repositories.PokeRepository
import com.akerusan.pokedex.util.Resource
import kotlinx.coroutines.launch

class SinglePokemonViewModel(application: Application?) : AndroidViewModel(application!!) {

    private var pokemonRepository: PokeRepository? = null

    init {
        pokemonRepository = application?.let { PokeRepository.getInstance(it) }
    }

    fun searchSinglePokemonApi(pokemonId: String): LiveData<Resource<Pokemon>> {
        return pokemonRepository!!.searchPokemonApi(pokemonId)
    }

    fun setPokemonFav(pokemonId: Int, pokemonFav: Boolean) {
        viewModelScope.launch {
            pokemonRepository!!.setPokemonFav(pokemonId, pokemonFav)
        }
    }
}