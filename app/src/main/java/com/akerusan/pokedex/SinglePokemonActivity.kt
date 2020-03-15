package com.akerusan.pokedex

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.akerusan.pokedex.binding.bindImageFromUrl
import com.akerusan.pokedex.databinding.ActivitySinglePokemonBinding
import com.akerusan.pokedex.models.Pokemon
import com.akerusan.pokedex.util.Resource
import com.akerusan.pokedex.viewModels.SinglePokemonViewModel
import kotlinx.android.synthetic.main.activity_single_pokemon.*

class SinglePokemonActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivitySinglePokemonBinding
    private lateinit var mSinglePokemonViewModel: SinglePokemonViewModel
    private lateinit var mScrollView: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_single_pokemon)

        val pokemonId = intent.getStringExtra("pokemonNumber")

        mScrollView = findViewById(R.id.parent)
        mSinglePokemonViewModel = ViewModelProvider(this).get(SinglePokemonViewModel::class.java)

        mBinding.setOnCheckedChange { _, isChecked ->
            mSinglePokemonViewModel.setPokemonFav(pokemonId!!.toInt(), isChecked)
        }
        getIncomingIntent(pokemonId!!)
    }

    private fun getIncomingIntent(pokemonId: String) {
        if (pokemonId.isNotEmpty()) {
            subscribeObserver(pokemonId)
        }
    }

    private fun subscribeObserver(pokemonId: String) {
        mSinglePokemonViewModel.searchSinglePokemonApi(pokemonId)
            .observe(this, Observer { result ->
                when (result.status) {
                    Resource.Status.SUCCESS -> { populateBinding(result.data) }
                    Resource.Status.LOADING -> progress_bar_single.visibility = View.VISIBLE
                    Resource.Status.ERROR -> {
                        populateBinding(result.data)
                        Toast.makeText(this@SinglePokemonActivity, result.message, Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    private fun populateBinding(pokemon: Pokemon) {
        showScreen()
        mBinding.singlePokemon = pokemon

        if (pokemon.frontDefault == "null") {
            pokemon_img_front.visibility = View.GONE
        } else {
            bindImageFromUrl(mBinding.pokemonImgFront, pokemon.frontDefault)
        }
        if (pokemon.backDefault == "null") {
            pokemon_img_back.visibility = View.GONE
        } else {
            bindImageFromUrl(mBinding.pokemonImgBack, pokemon.backDefault)
        }
    }

    private fun showScreen() {
        mScrollView.visibility = View.VISIBLE
        progress_bar_single.visibility = View.GONE
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}