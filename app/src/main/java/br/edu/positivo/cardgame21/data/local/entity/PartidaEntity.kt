package br.edu.positivo.cardgame21.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "partidas")
data class PartidaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val jogadorId: Long,
    val pontos: Int,
    val dataPartida: Long = System.currentTimeMillis()
)