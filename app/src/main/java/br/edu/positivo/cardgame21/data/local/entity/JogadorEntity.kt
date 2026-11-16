package br.edu.positivo.cardgame21.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jogadores")
data class JogadorEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val isAdmin: Boolean = false
)