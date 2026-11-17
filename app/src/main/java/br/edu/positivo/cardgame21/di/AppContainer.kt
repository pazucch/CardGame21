package br.edu.positivo.cardgame21.di

import android.content.Context
import br.edu.positivo.cardgame21.data.local.database.AppDatabase
import br.edu.positivo.cardgame21.data.repository.JogadorRepository
import br.edu.positivo.cardgame21.data.repository.PartidaRepository
import br.edu.positivo.cardgame21.ui.viewmodel.GameViewModel

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

    val viewModelFactory = ViewModelFactory().apply {
        register(GameViewModel::class.java) {
            GameViewModel()
        }
    }

}
