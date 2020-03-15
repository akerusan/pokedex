package com.akerusan.pokedex.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query
import com.akerusan.pokedex.models.Pokemon

@Dao
interface PokemonDao {

    @Insert(onConflict = IGNORE)
    fun insertPokemonList(pokemon: List<Pokemon>)

    @Query("UPDATE pokemon SET front_img=:front_img, back_img=:back_img, id=:id WHERE name = :name")
    fun updatePokemon(name: String, front_img: String, back_img: String, id: Int)

    @Query("UPDATE pokemon SET favorite=:favorite WHERE id=:id")
    suspend fun updatePokemon(id: Int, favorite: Boolean)

    @Query("SELECT * FROM pokemon ORDER BY id ASC")
    fun getAllPokemon() : LiveData<List<Pokemon>>

    @Query("SELECT * FROM pokemon WHERE id = :id")
    fun getSinglePokemon(id: Int) : LiveData<Pokemon>

}