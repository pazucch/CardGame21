
package br.edu.positivo.cardgame21.data.remote

import br.edu.positivo.cardgame21.data.remote.dto.CardDto
import br.edu.positivo.cardgame21.logic.Carta
import br.edu.positivo.cardgame21.logic.Naipe
import br.edu.positivo.cardgame21.logic.ValorCarta

fun CardDto.toCarta(): Carta {

    val naipeConvertido = when (suit) {
        "SPADES" -> Naipe.ESPADAS
        "HEARTS" -> Naipe.COPAS
        "DIAMONDS" -> Naipe.OUROS
        "CLUBS" -> Naipe.PAUS
        else -> error("Naipe desconhecido: $suit")
    }

    val valorConvertido = when (value) {
        "ACE" -> ValorCarta.AS
        "2" -> ValorCarta.DOIS
        "3" -> ValorCarta.TRES
        "4" -> ValorCarta.QUATRO
        "5" -> ValorCarta.CINCO
        "6" -> ValorCarta.SEIS
        "7" -> ValorCarta.SETE
        "8" -> ValorCarta.OITO
        "9" -> ValorCarta.NOVE
        "10" -> ValorCarta.DEZ
        "JACK" -> ValorCarta.VALETE
        "QUEEN" -> ValorCarta.DAMA
        "KING" -> ValorCarta.REI
        else -> error("Valor desconhecido: $value")
    }

    return Carta(
        naipe = naipeConvertido,
        valor = valorConvertido
    )
}
