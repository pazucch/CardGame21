package br.edu.positivo.cardgame21.logic

data class Carta(
    val naipe: Naipe,
    val valor: ValorCarta
) {
    val pontos: Int
        get() = valor.pontos
}
