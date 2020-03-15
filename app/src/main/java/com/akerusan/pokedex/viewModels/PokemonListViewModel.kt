package com.akerusan.pokedex.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.akerusan.pokedex.models.Pokemon
import com.akerusan.pokedex.repositories.PokeRepository
import com.akerusan.pokedex.util.Resource
import kotlinx.coroutines.launch

class PokemonListViewModel(application: Application?) : AndroidViewModel(application!!) {

    private var moffset: String? = null
    private var mLimit: String? = null

    private val pokemon: MediatorLiveData<Resource<List<Pokemon>>> = MediatorLiveData()
    private var pokemonRepository: PokeRepository? = null

    init {
        pokemonRepository = application?.let { PokeRepository.getInstance(it) }
    }

    fun getPokemon() : LiveData<Resource<List<Pokemon>>> {
        return pokemon
    }

    fun setPokemonFav(pokemonId: Int, pokemonFav: Boolean) {
        viewModelScope.launch {
            pokemonRepository!!.setPokemonFav(pokemonId, pokemonFav)
        }
    }

    fun searchPokemonApi(offset: String, limit: String) {
        moffset = offset
        mLimit = limit
        val repositorySource = pokemonRepository!!.searchPokemonApi(offset, limit)
        pokemon.addSource(repositorySource, object: Observer<Resource<List<Pokemon>>>{
            override fun onChanged(listResource: Resource<List<Pokemon>>?) {
                if (listResource != null) {
                    if (listResource.status === Resource.Status.SUCCESS) {
                        pokemon.removeSource(repositorySource)
                    } else if (listResource.status === Resource.Status.ERROR) {
                        pokemon.removeSource(repositorySource)
                    }
                    pokemon.value = listResource
                } else {
                    pokemon.removeSource(repositorySource)
                }
            }
        })
    }

    fun searchNextOffset() {
        viewModelScope.launch {
            searchPokemonApi(moffset!!.toString(), (mLimit!!.toInt()+20).toString())
        }
    }
}