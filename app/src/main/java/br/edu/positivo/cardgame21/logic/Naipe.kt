package br.edu.positivo.cardgame21.logic

// Os quatro naipes. O "︎" força o símbolo a aparecer como texto
enum class Naipe(val simbolo: String, val vermelho: Boolean) {
    ESPADAS("♠︎", false),
    COPAS("♥︎", true),
    OUROS("♦︎", true),
    PAUS("♣︎", false)
}
