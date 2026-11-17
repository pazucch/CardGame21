package br.edu.positivo.cardgame21.data.remote

import retrofit2.http.GET

interface CartasApi {
    @GET("api/deck/new/draw/?count=52")
    suspend fun novoBaralho(): BaralhoDto
}

data class BaralhoDto(val success: Boolean, val cards: List<CartaDto>?)
data class CartaDto(val value: String?, val suit: String?)
