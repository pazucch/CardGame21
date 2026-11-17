
package br.edu.positivo.cardgame21.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DeckDto(
    val success: Boolean,

    @SerializedName("deck_id")
    val deckId: String,

    val remaining: Int
)

data class CardDto(
    val code: String,
    val value: String,
    val suit: String
)

data class DrawCardsDto(
    val success: Boolean,

    @SerializedName("deck_id")
    val deckId: String,

    val cards: List<CardDto>,
    val remaining: Int
)
