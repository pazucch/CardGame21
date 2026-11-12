
package br.edu.positivo.cardgame21.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class BaralhoTest {

    @Test
    fun baralhoTem52Cartas() {
        val baralho = Baralho()

        assertEquals(52, baralho.cartasRestantes())
    }

    @Test
    fun comprarCartaReduzQuantidade() {
        val baralho = Baralho()

        val carta = baralho.comprarCarta()

        assertNotNull(carta)
        assertEquals(51, baralho.cartasRestantes())
    }

    @Test
    fun cartasNaoSeRepetem() {
        val baralho = Baralho()
        val cartas = mutableListOf<Carta>()

        repeat(52) {
            baralho.comprarCarta()?.let {
                cartas.add(it)
            }
        }

        assertEquals(52, cartas.size)
        assertEquals(52, cartas.toSet().size)
    }

    @Test
    fun baralhoVazio() {
        val baralho = Baralho()

        repeat(52) {
            baralho.comprarCarta()
        }

        assertEquals(0, baralho.cartasRestantes())
        assertNull(baralho.comprarCarta())
    }

    @Test
    fun reiniciarBaralho() {
        val baralho = Baralho()

        repeat(10) {
            baralho.comprarCarta()
        }

        assertEquals(42, baralho.cartasRestantes())

        baralho.reiniciar()

        assertEquals(52, baralho.cartasRestantes())
    }
}
