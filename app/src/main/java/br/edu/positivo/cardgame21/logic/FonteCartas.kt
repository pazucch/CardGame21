package br.edu.positivo.cardgame21.logic

interface FonteCartas {
    fun reiniciar()
    fun comprarCarta(): Carta?
    fun cartasRestantes(): Int
}
