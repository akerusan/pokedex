package com.akerusan.pokedex

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akerusan.pokedex.adapters.OnPokemonListener
import com.akerusan.pokedex.adapters.PokemonRecyclerAdapter
import com.akerusan.pokedex.util.Resource
import com.akerusan.pokedex.viewModels.PokemonListViewModel
import kotlinx.android.synthetic.main.activity_pokemon_list.*

class PokemonListActivity : AppCompatActivity(), OnPokemonListener {

    private lateinit var mPokemonListViewModel: PokemonListViewModel
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: PokemonRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon_list)

        mRecyclerView = findViewById(R.id.list_recycler_view)
        mPokemonListViewModel = ViewModelProvider(this).get(PokemonListViewModel::class.java)

        initRecyclerView()
        subscribeObserver()
    }

    override fun onResume() {
        super.onResume()
        searchPokemonApi()
    }

    private fun subscribeObserver() {
        mPokemonListViewModel.getPokemon()
            .observe(this, Observer { result ->
                when (result.status) {
                    Resource.Status.SUCCESS -> {
                        progress_bar.visibility = View.GONE
                        result.data.let { mAdapter.setPokemonList(it) }
                    }
                    Resource.Status.LOADING -> progress_bar.visibility = View.VISIBLE
                    Resource.Status.ERROR -> {
                        progress_bar.visibility = View.GONE
                        result.data.let { mAdapter.setPokemonList(it) }
                        Toast.makeText(this@PokemonListActivity, result.message, Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    private fun initRecyclerView() {
        mAdapter = PokemonRecyclerAdapter(this)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!mRecyclerView.canScrollVertically(1)) {
                    mPokemonListViewModel.searchNextOffset()
                }
            }
        })
    }

    private fun searchPokemonApi() {
        mPokemonListViewModel.searchPokemonApi("0", "100")
    }

    override fun onPokemonClick(position: Int) {
        val intent = Intent(this, SinglePokemonActivity::class.java)
        val pokemonId = mAdapter.getPokemonNumber(position)
        intent.putExtra("pokemonNumber", pokemonId)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun onFavClick(position: Int, isChecked: Boolean) {
        val pokemonId = mAdapter.getPokemonNumber(position).toInt()
        mPokemonListViewModel.setPokemonFav(pokemonId, isChecked)
    }
}

