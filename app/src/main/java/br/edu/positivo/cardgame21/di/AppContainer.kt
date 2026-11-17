package br.edu.positivo.cardgame21.di

import android.content.Context
import br.edu.positivo.cardgame21.data.local.database.AppDatabase
import br.edu.positivo.cardgame21.data.repository.JogadorRepository
import br.edu.positivo.cardgame21.data.repository.PartidaRepository
import br.edu.positivo.cardgame21.data.remote.api.DeckOfCardsApi
import br.edu.positivo.cardgame21.data.repository.CartaRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {

    // Instância compartilhada do banco de dados
    private val database = AppDatabase.getDatabase(context)

    // Repositories utilizados pelos ViewModels
    val jogadorRepository = JogadorRepository(
        database.jogadorDao()
    )

    val partidaRepository = PartidaRepository(
        database.partidaDao()
    )

    val viewModelFactory = ViewModelFactory()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://deckofcardsapi.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val deckApi: DeckOfCardsApi =
        retrofit.create(DeckOfCardsApi::class.java)

    val cartaRepository = CartaRepository(deckApi)

}
