
package br.edu.positivo.cardgame21.model

class Baralho {

    private val cartas = mutableListOf<Carta>()

    init {
        reiniciar()
    }

    fun reiniciar() {
        cartas.clear()

        for (naipe in Naipe.entries) {
            for (valor in ValorCarta.entries) {
                cartas.add(Carta(naipe, valor))
            }
        }

        embaralhar()
    }

    fun embaralhar() {
        cartas.shuffle()
    }

    fun comprarCarta(): Carta? {
        if (cartas.isEmpty()) {
            return null
        }

        return cartas.removeAt(cartas.lastIndex)
    }

    fun cartasRestantes(): Int {
        return cartas.size
    }

    fun estaVazio(): Boolean {
        return cartas.isEmpty()
    }
}
