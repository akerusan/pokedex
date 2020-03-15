package com.akerusan.pokedex.models

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "pokemon")
class Pokemon {

    @PrimaryKey
    @NonNull
    var name: String? = null

    @ColumnInfo(name="url")
    var url: String? = null

    @ColumnInfo(name="id")
    var id: Int? = null

    @ColumnInfo(name="favorite")
    var favorite = false

    @ColumnInfo(name="back_img")
    @SerializedName("back_default")
    var backDefault: String? = null

    @ColumnInfo(name="front_img")
    @SerializedName("front_default")
    var frontDefault: String? = null

}