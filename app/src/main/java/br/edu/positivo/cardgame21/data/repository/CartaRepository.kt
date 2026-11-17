package br.edu.positivo.cardgame21.data.repository

import br.edu.positivo.cardgame21.data.remote.CartasApi
import br.edu.positivo.cardgame21.logic.*
import kotlinx.coroutines.CancellationException

data class BaralhoPreparado(val cartas: List<Carta>, val local: Boolean)

class CartaRepository(private val api: CartasApi) {
    // Prepara as 52 cartas antes da rodada: uma queda de conexão depois disso
    // não troca de baralho nem reintroduz cartas que já foram distribuídas.
    suspend fun prepararBaralho(): BaralhoPreparado {
        return try {
            val resposta = api.novoBaralho()
            check(resposta.success)
            val cartas = requireNotNull(resposta.cards).map { dto ->
                val naipe = when (dto.suit) {
                    "SPADES" -> Naipe.ESPADAS
                    "HEARTS" -> Naipe.COPAS
                    "DIAMONDS" -> Naipe.OUROS
                    "CLUBS" -> Naipe.PAUS
                    else -> error("Naipe inválido")
                }
                val valor = when (dto.value) {
                    "ACE" -> ValorCarta.AS
                    "KING" -> ValorCarta.REI
                    "QUEEN" -> ValorCarta.DAMA
                    "JACK" -> ValorCarta.VALETE
                    else -> ValorCarta.entries.first { it.simbolo == dto.value }
                }
                Carta(naipe, valor)
            }
            check(cartas.size == 52 && cartas.toSet().size == 52)
            BaralhoPreparado(cartas, local = false)
        } catch (cancelamento: CancellationException) {
            throw cancelamento
        } catch (_: Exception) {
            val baralho = Baralho()
            BaralhoPreparado(List(52) { requireNotNull(baralho.comprarCarta()) }, local = true)
        }
    }
}
