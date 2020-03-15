package com.akerusan.pokedex.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.akerusan.pokedex.databinding.PokemonListItemBinding
import com.akerusan.pokedex.models.Pokemon


class PokemonRecyclerAdapter(mOnPokemonListener: OnPokemonListener?) : RecyclerView.Adapter<PokemonRecyclerAdapter.PokemonViewHolder>() {

    private var mPokemonList: List<Pokemon>? = null
    private var mOnPokemonListener: OnPokemonListener? = null

    init {
        this.mOnPokemonListener = mOnPokemonListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        return PokemonViewHolder(PokemonListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), mOnPokemonListener!!)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val pokemon = mPokemonList!![position]
        holder.apply {
            bind(pokemon)
            itemView.tag = pokemon
        }
    }

    override fun getItemCount(): Int {
        if (mPokemonList != null) {
            return mPokemonList!!.size
        }
        return 0
    }

    fun setPokemonList(list: List<Pokemon>) {
        mPokemonList = list
        notifyDataSetChanged()
    }

    fun getPokemonNumber(position: Int) : String {
        return mPokemonList!![position].id!!.toString()
    }


    class PokemonViewHolder(private val binding: PokemonListItemBinding, private var onPokemonListener: OnPokemonListener) :
        RecyclerView.ViewHolder(binding.root)  {
        fun bind(item: Pokemon?) {
            binding.apply {
                pokemonList = item
                pokeItemTitle.setOnClickListener{
                    onPokemonListener.onPokemonClick(adapterPosition)
                }
                pokeItemFavorite.setOnClickListener{
                    onPokemonListener.onFavClick(adapterPosition, binding.pokeItemFavorite.isChecked)
                }
                binding.executePendingBindings()
            }
        }
    }
}