package br.edu.positivo.cardgame21.ui.viewmodel

import br.edu.positivo.cardgame21.logic.Carta
import br.edu.positivo.cardgame21.logic.Pontuacao

enum class FaseRodada { AGUARDANDO, JOGADOR, BANCA, ENCERRADA }
enum class ResultadoRodada { VITORIA, DERROTA, EMPATE }

data class GameUiState(
    val codigoMesa: String = "",
    val nomeJogador: String = "Convidado",
    val cartasJogador: List<Carta> = emptyList(),
    val cartasBanca: List<Carta> = emptyList(),
    val aposta: Int = 50,
    val fase: FaseRodada = FaseRodada.AGUARDANDO,
    val resultado: ResultadoRodada? = null,
    val cartasRestantes: Int = 52,
    val erro: String? = null
) {
    val temMesa: Boolean get() = codigoMesa.isNotBlank()
    val podeIniciar: Boolean get() = temMesa && fase == FaseRodada.AGUARDANDO
    val podeAlterarAposta: Boolean get() = temMesa &&
        (fase == FaseRodada.AGUARDANDO || fase == FaseRodada.ENCERRADA)
    val pontosJogador: Int get() = Pontuacao.calcular(cartasJogador)
    val pontosBanca: Int get() = Pontuacao.calcular(cartasBanca)
    val podeJogar: Boolean get() = fase == FaseRodada.JOGADOR
    val revelarBanca: Boolean get() = fase != FaseRodada.JOGADOR
}
