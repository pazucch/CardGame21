package br.edu.positivo.cardgame21.ui.viewmodel

import br.edu.positivo.cardgame21.logic.Carta
import br.edu.positivo.cardgame21.logic.FonteCartas
import br.edu.positivo.cardgame21.logic.Naipe
import br.edu.positivo.cardgame21.logic.ValorCarta
import org.junit.Assert.*
import org.junit.Test

class GameViewModelTest {
    // Ordem: jogador, banca, jogador, banca, próximas compras.
    private fun jogo(vararg valores: Int): GameViewModel {
        val cartas = valores.map { pontos ->
            Carta(Naipe.COPAS, ValorCarta.entries.first { it.pontos == pontos })
        }
        return GameViewModel(object : FonteCartas {
            var indice = 0
            override fun reiniciar() { indice = 0 }
            override fun comprarCarta(): Carta? = cartas.getOrNull(indice)?.also { indice++ }
            override fun cartasRestantes(): Int = cartas.size - indice
        })
    }

    @Test fun distribuiDuasCartasPorMao() {
        val estado = jogo(10, 9, 8, 7, 2).uiState.value
        assertEquals(2, estado.cartasJogador.size)
        assertEquals(2, estado.cartasBanca.size)
        assertEquals(18, estado.pontosJogador)
        assertEquals(1, estado.cartasRestantes)
        assertTrue(estado.podeJogar)
        assertFalse(estado.revelarBanca)
    }

    @Test fun pedirCartaRecalculaAs() {
        val vm = jogo(11, 10, 5, 7, 10)
        vm.pedirCarta()
        assertEquals(16, vm.uiState.value.pontosJogador)
        assertTrue(vm.uiState.value.podeJogar)
    }

    @Test fun estouroEncerraEBloqueiaAcoes() {
        val vm = jogo(10, 10, 9, 7, 5, 2)
        vm.pedirCarta()
        val final = vm.uiState.value
        assertEquals(ResultadoRodada.DERROTA, final.resultado)
        assertTrue(final.revelarBanca)
        assertFalse(final.podeJogar)
        vm.pedirCarta()
        vm.parar()
        assertEquals(final, vm.uiState.value)
    }

    @Test fun bancaCompraAteDezessete() {
        val vm = jogo(10, 2, 8, 3, 4, 8, 10)
        vm.parar()
        assertEquals(17, vm.uiState.value.pontosBanca)
        assertEquals(4, vm.uiState.value.cartasBanca.size)
        assertEquals(ResultadoRodada.VITORIA, vm.uiState.value.resultado)
        assertEquals(1, vm.uiState.value.cartasRestantes)
    }

    @Test fun bancaParaNoDezesseteComAs() {
        val vm = jogo(10, 11, 6, 6, 10)
        vm.parar()
        assertEquals(2, vm.uiState.value.cartasBanca.size)
        assertEquals(ResultadoRodada.DERROTA, vm.uiState.value.resultado)
    }

    @Test fun estouroDaBancaDaVitoria() {
        val vm = jogo(10, 10, 8, 6, 10)
        vm.parar()
        assertEquals(26, vm.uiState.value.pontosBanca)
        assertEquals(ResultadoRodada.VITORIA, vm.uiState.value.resultado)
    }

    @Test fun pontosIguaisEmpatam() {
        val vm = jogo(10, 10, 8, 8)
        vm.parar()
        assertEquals(ResultadoRodada.EMPATE, vm.uiState.value.resultado)
    }

    @Test fun vinteEUmAoComprarPassaVezParaBanca() {
        val vm = jogo(10, 10, 5, 6, 6, 2)
        vm.pedirCarta()
        assertEquals(21, vm.uiState.value.pontosJogador)
        assertEquals(18, vm.uiState.value.pontosBanca)
        assertEquals(ResultadoRodada.VITORIA, vm.uiState.value.resultado)
    }

    @Test fun vinteEUmInicialResolveRodada() {
        val estado = jogo(11, 11, 10, 10).uiState.value
        assertEquals(FaseRodada.ENCERRADA, estado.fase)
        assertEquals(ResultadoRodada.EMPATE, estado.resultado)
    }

    @Test fun novaRodadaLimpaResultadoERestauraBaralho() {
        val vm = jogo(10, 10, 8, 7, 5)
        val inicial = vm.uiState.value
        vm.novaRodada()
        assertEquals(inicial, vm.uiState.value)
        vm.pedirCarta()
        vm.novaRodada()
        assertEquals(inicial, vm.uiState.value)
    }

    @Test fun baralhoEsgotadoEncerraSemInventarResultado() {
        val vm = jogo(10, 10, 8, 6)
        vm.parar()
        assertEquals(FaseRodada.ENCERRADA, vm.uiState.value.fase)
        assertNotNull(vm.uiState.value.erro)
        assertNull(vm.uiState.value.resultado)
    }

    @Test fun baralhoLocalComecaComQuarentaEOitoCartasRestantes() {
        val estado = GameViewModel().uiState.value
        // Se vier 21 inicial, a banca pode já ter comprado cartas.
        assertTrue(estado.cartasRestantes <= 48)
        assertEquals(2, estado.cartasJogador.size)
    }
}
