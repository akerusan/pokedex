package com.akerusan.pokedex.adapters

interface OnPokemonListener {

    fun onPokemonClick(position: Int)
    fun onFavClick(position: Int, isChecked: Boolean)
}