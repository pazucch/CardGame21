
package br.edu.positivo.cardgame21.di

import android.content.Context
import br.edu.positivo.cardgame21.data.local.database.AppDatabase
import br.edu.positivo.cardgame21.data.remote.api.DeckOfCardsApi
import br.edu.positivo.cardgame21.data.repository.CartaRepository
import br.edu.positivo.cardgame21.data.repository.JogadorRepository
import br.edu.positivo.cardgame21.data.repository.PartidaRepository
import br.edu.positivo.cardgame21.data.session.SessaoUsuario
import br.edu.positivo.cardgame21.ui.viewmodel.AdminViewModel
import br.edu.positivo.cardgame21.ui.viewmodel.AuthViewModel
import br.edu.positivo.cardgame21.ui.viewmodel.GameViewModel
import br.edu.positivo.cardgame21.ui.viewmodel.PlacarViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {

    val database = AppDatabase.getDatabase(context)

    val jogadorRepository = JogadorRepository(
        database.jogadorDao()
    )

    val partidaRepository = PartidaRepository(
        database.partidaDao()
    )

    val sessao = SessaoUsuario()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://deckofcardsapi.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val deckApi: DeckOfCardsApi =
        retrofit.create(DeckOfCardsApi::class.java)

    val cartaRepository = CartaRepository(deckApi)

    val viewModelFactory = ViewModelFactory().apply {
        register(AuthViewModel::class.java) {
            AuthViewModel(jogadorRepository, sessao)
        }

        register(AdminViewModel::class.java) {
            AdminViewModel(jogadorRepository, partidaRepository, sessao)
        }

        register(GameViewModel::class.java) {
            GameViewModel(
                cartaRepository = cartaRepository,
                partidaRepository = partidaRepository,
                jogadorRepository = jogadorRepository
            )
        }

        register(PlacarViewModel::class.java) {
            PlacarViewModel(
                partidaRepository = partidaRepository,
                jogadorRepository = jogadorRepository
            )
        }
    }
}
