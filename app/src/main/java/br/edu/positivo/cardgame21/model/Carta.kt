package br.edu.positivo.cardgame21.model

data class Carta(
    val naipe: Naipe,
    val valor: ValorCarta
) {
    val pontos: Int
        get() = valor.pontos
}
