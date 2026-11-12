
package br.edu.positivo.cardgame21.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PontuacaoTest {

    @Test
    fun pontuacaoNormal() {
        val cartas = listOf(
            Carta(Naipe.COPAS, ValorCarta.REI),
            Carta(Naipe.ESPADAS, ValorCarta.SETE)
        )

        assertEquals(17, Pontuacao.calcular(cartas))
    }

    @Test
    fun pontuacaoEstourada() {
        val cartas = listOf(
            Carta(Naipe.COPAS, ValorCarta.REI),
            Carta(Naipe.ESPADAS, ValorCarta.DAMA),
            Carta(Naipe.PAUS, ValorCarta.CINCO)
        )

        assertEquals(25, Pontuacao.calcular(cartas))
        assertTrue(Pontuacao.estourou(cartas))
    }

    @Test
    fun asValendoOnze() {
        val cartas = listOf(
            Carta(Naipe.COPAS, ValorCarta.AS),
            Carta(Naipe.ESPADAS, ValorCarta.OITO)
        )

        assertEquals(19, Pontuacao.calcular(cartas))
    }

    @Test
    fun asValendoUm() {
        val cartas = listOf(
            Carta(Naipe.COPAS, ValorCarta.AS),
            Carta(Naipe.ESPADAS, ValorCarta.OITO),
            Carta(Naipe.PAUS, ValorCarta.CINCO)
        )

        assertEquals(14, Pontuacao.calcular(cartas))
    }

    @Test
    fun doisAses() {
        val cartas = listOf(
            Carta(Naipe.COPAS, ValorCarta.AS),
            Carta(Naipe.ESPADAS, ValorCarta.AS),
            Carta(Naipe.PAUS, ValorCarta.NOVE)
        )

        assertEquals(21, Pontuacao.calcular(cartas))
        assertTrue(Pontuacao.fezVinteEUm(cartas))
    }

    @Test
    fun naoEstourou() {
        val cartas = listOf(
            Carta(Naipe.COPAS, ValorCarta.DEZ),
            Carta(Naipe.ESPADAS, ValorCarta.NOVE)
        )

        assertFalse(Pontuacao.estourou(cartas))
    }

    @Test
    fun maoVazia() {
        val cartas = emptyList<Carta>()

        assertEquals(0, Pontuacao.calcular(cartas))
    }
}
