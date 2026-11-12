
package br.edu.positivo.cardgame21.model

object Pontuacao {

    fun calcular(cartas: List<Carta>): Int {
        var total = 0
        var quantidadeAs = 0

        for (carta in cartas) {
            total += carta.pontos

            if (carta.valor == ValorCarta.AS) {
                quantidadeAs++
            }
        }

        while (total > 21 && quantidadeAs > 0) {
            total -= 10
            quantidadeAs--
        }

        return total
    }

    fun estourou(cartas: List<Carta>): Boolean {
        return calcular(cartas) > 21
    }

    fun fezVinteEUm(cartas: List<Carta>): Boolean {
        return calcular(cartas) == 21
    }
}
