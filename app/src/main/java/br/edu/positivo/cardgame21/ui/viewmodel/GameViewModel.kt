package br.edu.positivo.cardgame21.ui.viewmodel

import androidx.lifecycle.ViewModel
import br.edu.positivo.cardgame21.logic.Baralho
import br.edu.positivo.cardgame21.logic.Carta
import br.edu.positivo.cardgame21.logic.FonteCartas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel(private val baralho: FonteCartas = Baralho()) : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState = _uiState.asStateFlow()

    init {
        distribuirRodada()
    }

    fun novaRodada() {
        if (_uiState.value.fase != FaseRodada.ENCERRADA) return
        distribuirRodada()
    }

    private fun distribuirRodada() {
        baralho.reiniciar()
        _uiState.value = GameUiState(aposta = _uiState.value.aposta)
        val jogador = mutableListOf<Carta>()
        val banca = mutableListOf<Carta>()
        repeat(2) {
            jogador.add(comprar() ?: return)
            banca.add(comprar() ?: return)
        }
        _uiState.value = _uiState.value.copy(
            cartasJogador = jogador.toList(), cartasBanca = banca.toList(),
            cartasRestantes = baralho.cartasRestantes()
        )
        if (_uiState.value.pontosJogador == 21) parar()
    }

    fun pedirCarta() {
        val estado = _uiState.value
        if (!estado.podeJogar) return
        val carta = comprar() ?: return
        _uiState.value = estado.copy(
            cartasJogador = estado.cartasJogador + carta,
            cartasRestantes = baralho.cartasRestantes()
        )
        when {
            _uiState.value.pontosJogador > 21 -> encerrar(ResultadoRodada.DERROTA)
            _uiState.value.pontosJogador == 21 -> parar()
        }
    }

    fun parar() {
        if (!_uiState.value.podeJogar) return
        _uiState.value = _uiState.value.copy(fase = FaseRodada.BANCA)
        // A banca para em qualquer 17, inclusive com Ás valendo 11.
        while (_uiState.value.pontosBanca < 17) {
            val carta = comprar() ?: return
            _uiState.value = _uiState.value.copy(
                cartasBanca = _uiState.value.cartasBanca + carta,
                cartasRestantes = baralho.cartasRestantes()
            )
        }
        val estado = _uiState.value
        encerrar(when {
            estado.pontosBanca > 21 || estado.pontosJogador > estado.pontosBanca -> ResultadoRodada.VITORIA
            estado.pontosJogador < estado.pontosBanca -> ResultadoRodada.DERROTA
            else -> ResultadoRodada.EMPATE
        })
    }

    private fun comprar(): Carta? {
        return baralho.comprarCarta() ?: run {
            _uiState.value = _uiState.value.copy(
                fase = FaseRodada.ENCERRADA,
                cartasRestantes = 0,
                erro = "O baralho acabou. Inicie uma nova rodada."
            )
            null
        }
    }

    private fun encerrar(resultado: ResultadoRodada) {
        _uiState.value = _uiState.value.copy(fase = FaseRodada.ENCERRADA, resultado = resultado)
    }
}
