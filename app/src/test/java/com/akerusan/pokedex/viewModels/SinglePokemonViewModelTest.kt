package com.akerusan.pokedex.viewModels

import android.app.Application
import com.akerusan.pokedex.repositories.PokeRepository
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

@RunWith(JUnit4::class)
class SinglePokemonViewModelTest {

    private val application = Mockito.mock(Application::class.java)
    private var viewModel = SinglePokemonViewModel(application!!)

    @Test
    fun testNull() {
    }
}