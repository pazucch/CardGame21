package br.edu.positivo.cardgame21.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import br.edu.positivo.cardgame21.ui.viewmodel.FaseRodada
import br.edu.positivo.cardgame21.ui.viewmodel.GameViewModel
import br.edu.positivo.cardgame21.ui.viewmodel.ResultadoRodada

@Composable
fun JogoRoute(onSair: () -> Unit, gameViewModel: GameViewModel) {
    val estado by gameViewModel.uiState.collectAsState()
    // Após recriação do processo, não permanece numa partida sem mesa em memória.
    LaunchedEffect(estado.temMesa) {
        if (!estado.temMesa) onSair()
    }
    var confirmarSaida by rememberSaveable { mutableStateOf(false) }
    val sair = {
        gameViewModel.sairDaPartida()
        onSair()
    }
    val solicitarSaida = {
        if (estado.fase == FaseRodada.JOGADOR || estado.fase == FaseRodada.BANCA) {
            confirmarSaida = true
        } else sair()
    }
    BackHandler(onBack = solicitarSaida)
    if (confirmarSaida) {
        AlertDialog(
            onDismissRequest = { confirmarSaida = false },
            title = { Text("Sair da partida?") },
            text = { Text("A rodada atual será abandonada. Sua mesa e aposta serão mantidas na sala.") },
            confirmButton = { TextButton(onClick = { confirmarSaida = false; sair() }) { Text("Sair") } },
            dismissButton = { TextButton(onClick = { confirmarSaida = false }) { Text("Continuar jogando") } }
        )
    }
    val mensagem = estado.erro ?: when (estado.resultado) {
        ResultadoRodada.VITORIA -> "Você venceu!"
        ResultadoRodada.DERROTA -> if (estado.pontosJogador > 21) "Você estourou!" else "A banca venceu"
        ResultadoRodada.EMPATE -> "Empate!"
        null -> if (estado.fase == FaseRodada.BANCA) "Vez da banca" else "Sua vez"
    }
    JogoScreen(
        onSair = solicitarSaida,
        nomeMesa = "Mesa ${estado.codigoMesa}",
        nomeJogador = estado.nomeJogador,
        podeAlterarAposta = estado.podeAlterarAposta,
        onAumentarAposta = gameViewModel::aumentarAposta,
        onDiminuirAposta = gameViewModel::diminuirAposta,
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
