package br.edu.positivo.cardgame21.logic
enum class Naipe(val simbolo: String, val vermelho: Boolean) {
    ESPADAS("♠︎", false),
    COPAS("♥︎", true),
    OUROS("♦︎", true),
    PAUS("♣︎", false)
}
