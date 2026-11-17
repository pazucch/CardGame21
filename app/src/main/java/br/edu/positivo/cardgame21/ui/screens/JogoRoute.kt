package br.edu.positivo.cardgame21.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.positivo.cardgame21.CardGameApplication
import br.edu.positivo.cardgame21.ui.viewmodel.FaseRodada
import br.edu.positivo.cardgame21.ui.viewmodel.GameViewModel
import br.edu.positivo.cardgame21.ui.viewmodel.ResultadoRodada

@Composable
fun JogoRoute(onSair: () -> Unit) {
    val application = LocalContext.current.applicationContext as CardGameApplication
    val gameViewModel: GameViewModel = viewModel(factory = application.container.viewModelFactory)
    val estado by gameViewModel.uiState.collectAsState()
    val mensagem = estado.erro ?: when (estado.resultado) {
        ResultadoRodada.VITORIA -> "Você venceu!"
        ResultadoRodada.DERROTA -> if (estado.pontosJogador > 21) "Você estourou!" else "A banca venceu"
        ResultadoRodada.EMPATE -> "Empate!"
        null -> if (estado.fase == FaseRodada.BANCA) "Vez da banca" else "Sua vez"
    }
    JogoScreen(
        onSair = onSair,
        nomeMesa = "Blackjack 21",
        apostaAtual = estado.aposta,
        cartasVoce = estado.cartasJogador.map { it.valor.simbolo to it.naipe },
        pontosVoce = estado.pontosJogador,
        cartasBanca = estado.cartasBanca.mapIndexed { indice, carta ->
            (if (indice == 1 && !estado.revelarBanca) "?" else carta.valor.simbolo) to carta.naipe
        },
        pontosBanca = if (estado.revelarBanca) estado.pontosBanca.toString() else "?",
        cartasRestantes = estado.cartasRestantes,
        mensagemStatus = mensagem,
        dicaStatus = if (estado.fase == FaseRodada.ENCERRADA) "Inicie uma nova rodada para jogar novamente"
            else "Chegue o mais perto de 21 sem passar",
        podeJogar = estado.podeJogar,
        rodadaEncerrada = estado.fase == FaseRodada.ENCERRADA,
        onPedirCarta = gameViewModel::pedirCarta,
        onParar = gameViewModel::parar,
        onNovaRodada = gameViewModel::novaRodada
    )
}
