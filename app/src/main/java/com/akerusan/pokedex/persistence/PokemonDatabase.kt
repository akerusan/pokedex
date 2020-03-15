package com.akerusan.pokedex.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.akerusan.pokedex.models.Pokemon

@Database(entities = [Pokemon::class], version = 1)
abstract class PokemonDatabase : RoomDatabase() {

    companion object {
        val DATABASE_NAME = "pokemon_db"
        private var instance: PokemonDatabase? = null
        fun getInstance(context: Context): PokemonDatabase? {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    PokemonDatabase::class.java,
                    DATABASE_NAME
                ).build()
            }
            return instance
        }
    }

    abstract fun getPokemonDao(): PokemonDao?

}