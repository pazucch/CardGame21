
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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {

    // Banco de dados compartilhado
    val database = AppDatabase.getDatabase(context)

    // Repositórios
    val jogadorRepository = JogadorRepository(
        database.jogadorDao()
    )

    val partidaRepository = PartidaRepository(
        database.partidaDao()
    )

    // Sessão do usuário autenticado
    val sessao = SessaoUsuario()

    // Configuração da API de cartas
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://deckofcardsapi.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val deckApi: DeckOfCardsApi =
        retrofit.create(DeckOfCardsApi::class.java)

    val cartaRepository = CartaRepository(deckApi)

    // Registro dos ViewModels
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
    }
}
