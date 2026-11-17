package br.edu.positivo.cardgame21.di

import android.content.Context
import br.edu.positivo.cardgame21.data.local.database.AppDatabase
import br.edu.positivo.cardgame21.data.repository.JogadorRepository
import br.edu.positivo.cardgame21.data.repository.PartidaRepository
import br.edu.positivo.cardgame21.ui.viewmodel.GameViewModel
import br.edu.positivo.cardgame21.data.remote.CartasApi
import br.edu.positivo.cardgame21.data.repository.CartaRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

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

    val cartaRepository = CartaRepository(
        Retrofit.Builder()
            .baseUrl("https://deckofcardsapi.com/")
            .client(OkHttpClient.Builder().callTimeout(10, TimeUnit.SECONDS).build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CartasApi::class.java)
    )

    val viewModelFactory = ViewModelFactory().apply {
        register(GameViewModel::class.java) {
            GameViewModel(cartaRepository = cartaRepository, partidaRepository = partidaRepository,
                jogadorRepository = jogadorRepository)
        }
    }

}
