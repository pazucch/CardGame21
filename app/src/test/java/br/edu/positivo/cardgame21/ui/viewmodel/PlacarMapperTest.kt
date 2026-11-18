package br.edu.positivo.cardgame21.ui.viewmodel

import br.edu.positivo.cardgame21.data.local.entity.JogadorEntity
import br.edu.positivo.cardgame21.data.local.entity.PartidaEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlacarMapperTest {

    private val maria = JogadorEntity(id = 1, nome = "maria")
    private val joao = JogadorEntity(id = 2, nome = "joão")

    private fun partida(id: Long, jogadorId: Long, pontos: Int, data: Long) =
        PartidaEntity(id = id, jogadorId = jogadorId, pontos = pontos, dataPartida = data)

    @Test
    fun listasVazias_geramRankingVazio() {
        assertTrue(montarRanking(emptyList(), emptyList()).isEmpty())
    }

    @Test
    fun ordenaPorPontosDecrescenteENumeraPosicoes() {
        val ranking = montarRanking(
            partidas = listOf(
                partida(1, 1, 15, 100),
                partida(2, 2, 21, 200),
                partida(3, 1, 18, 300)
            ),
            jogadores = listOf(maria, joao)
        )

        assertEquals(listOf(21, 18, 15), ranking.map { it.pontos })
        assertEquals(listOf(1, 2, 3), ranking.map { it.posicao })
        assertEquals(listOf("joão", "maria", "maria"), ranking.map { it.nome })
    }

    @Test
    fun empateDePontos_quemFezPrimeiroFicaNaFrente() {
        val ranking = montarRanking(
            partidas = listOf(
                partida(1, 1, 20, 300),
                partida(2, 2, 20, 100)
            ),
            jogadores = listOf(maria, joao)
        )

        assertEquals(listOf(2L, 1L), ranking.map { it.partidaId })
    }

    @Test
    fun jogadorInexistente_usaNomePadrao() {
        val ranking = montarRanking(
            partidas = listOf(partida(1, 99, 10, 100)),
            jogadores = listOf(maria)
        )

        assertEquals(NOME_JOGADOR_REMOVIDO, ranking.single().nome)
    }
}