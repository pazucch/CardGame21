package br.edu.positivo.cardgame21.ui.viewmodel

import br.edu.positivo.cardgame21.logic.Carta
import br.edu.positivo.cardgame21.logic.Pontuacao

enum class FaseRodada { JOGADOR, BANCA, ENCERRADA }
enum class ResultadoRodada { VITORIA, DERROTA, EMPATE }

data class GameUiState(
    val cartasJogador: List<Carta> = emptyList(),
    val cartasBanca: List<Carta> = emptyList(),
    val aposta: Int = 50,
    val fase: FaseRodada = FaseRodada.JOGADOR,
    val resultado: ResultadoRodada? = null,
    val cartasRestantes: Int = 52,
    val erro: String? = null
) {
    val pontosJogador: Int get() = Pontuacao.calcular(cartasJogador)
    val pontosBanca: Int get() = Pontuacao.calcular(cartasBanca)
    val podeJogar: Boolean get() = fase == FaseRodada.JOGADOR
    val revelarBanca: Boolean get() = fase != FaseRodada.JOGADOR
}
