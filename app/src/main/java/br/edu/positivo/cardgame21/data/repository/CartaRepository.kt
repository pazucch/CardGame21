
package br.edu.positivo.cardgame21.data.repository

import br.edu.positivo.cardgame21.data.remote.api.DeckOfCardsApi
import br.edu.positivo.cardgame21.data.remote.toCarta
import br.edu.positivo.cardgame21.logic.Baralho
import br.edu.positivo.cardgame21.logic.Carta
import kotlinx.coroutines.CancellationException

enum class FonteCartas {
    REMOTA,
    LOCAL
}

class CartaRepository(
    private val api: DeckOfCardsApi
) {

    private val cartas = mutableListOf<Carta>()

    var fonteAtual = FonteCartas.LOCAL
        private set

    suspend fun iniciarBaralho(): FonteCartas {
        var novasCartas: List<Carta>
        var novaFonte: FonteCartas

        try {
            val deck = api.criarBaralho()
            check(deck.success)

            val resposta = api.comprarCartas(
                deckId = deck.deckId,
                quantidade = 52
            )

            check(resposta.success)

            val recebidas = resposta.cards.map {
                it.toCarta()
            }

            check(recebidas.size == 52)
            check(recebidas.distinct().size == 52)

            novasCartas = recebidas
            novaFonte = FonteCartas.REMOTA

        } catch (erro: CancellationException) {
            throw erro

        } catch (erro: Exception) {
            val baralhoLocal = Baralho()
            val locais = mutableListOf<Carta>()

            repeat(52) {
                baralhoLocal.comprarCarta()?.let {
                    locais.add(it)
                }
            }

            novasCartas = locais
            novaFonte = FonteCartas.LOCAL
        }

        cartas.clear()
        cartas.addAll(novasCartas)
        fonteAtual = novaFonte

        return fonteAtual
    }

    fun comprarCarta(): Carta? {
        if (cartas.isEmpty()) return null

        return cartas.removeAt(cartas.lastIndex)
    }

    fun cartasRestantes(): Int = cartas.size
}