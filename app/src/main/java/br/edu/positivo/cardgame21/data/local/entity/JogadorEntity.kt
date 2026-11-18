package br.edu.positivo.cardgame21.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "jogadores",
    indices = [Index(value = ["nome"], unique = true)]
)
data class JogadorEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val senhaHash: String = "",
    val isAdmin: Boolean = false,
    val saldo: Int = 1000
)
