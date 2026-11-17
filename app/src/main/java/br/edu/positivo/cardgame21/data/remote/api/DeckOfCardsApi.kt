
package br.edu.positivo.cardgame21.data.remote.api

import br.edu.positivo.cardgame21.data.remote.dto.DeckDto
import br.edu.positivo.cardgame21.data.remote.dto.DrawCardsDto

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DeckOfCardsApi {

    @GET("deck/new/shuffle/")
    suspend fun criarBaralho(
        @Query("deck_count") quantidade: Int = 1
    ): DeckDto

    @GET("deck/{deckId}/draw/")
    suspend fun comprarCartas(
        @Path("deckId") deckId: String,
        @Query("count") quantidade: Int
    ): DrawCardsDto
}
