package br.edu.positivo.cardgame21.ui.viewmodel

import br.edu.positivo.cardgame21.logic.Carta
import br.edu.positivo.cardgame21.logic.Pontuacao

enum class FaseRodada { AGUARDANDO, JOGADOR, BANCA, ENCERRADA }
typealias ResultadoRodada = br.edu.positivo.cardgame21.logic.ResultadoRodada

data class GameUiState(
    val erroCriarMesa: String? = null,
    val codigoMesa: String = "",
    val nomeJogador: String = "Convidado",
    val cartasJogador: List<Carta> = emptyList(),
    val cartasBanca: List<Carta> = emptyList(),
    val aposta: Int = 50,
    val saldo: Int = 1000,
    val fase: FaseRodada = FaseRodada.AGUARDANDO,
    val resultado: ResultadoRodada? = null,
    val cartasRestantes: Int = 52,
    val erro: String? = null,
    val carregando: Boolean = false,
    val identificandoJogador: Boolean = false,
    val salvando: Boolean = false,
    val partidaSalva: Boolean = false,
    val erroSalvar: String? = null,
    val aviso: String? = null,
    val baralhoLocal: Boolean = false
) {
    val temMesa: Boolean get() = codigoMesa.isNotBlank()
    val podeIniciar: Boolean
        get() = temMesa &&
                fase == FaseRodada.AGUARDANDO &&
                aposta <= saldo &&
                !ocupado
    val ocupado: Boolean get() = carregando || salvando || identificandoJogador
    val podeAlterarAposta: Boolean get() = temMesa && !ocupado && erroSalvar == null &&
        (fase == FaseRodada.AGUARDANDO || fase == FaseRodada.ENCERRADA)
    val pontosJogador: Int get() = Pontuacao.calcular(cartasJogador)
    val pontosBanca: Int get() = Pontuacao.calcular(cartasBanca)
    val podeJogar: Boolean get() = fase == FaseRodada.JOGADOR && !ocupado
    val revelarBanca: Boolean get() = fase != FaseRodada.JOGADOR
}
