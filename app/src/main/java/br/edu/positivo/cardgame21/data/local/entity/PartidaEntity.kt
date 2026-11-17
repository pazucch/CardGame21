package br.edu.positivo.cardgame21.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "partidas",
    foreignKeys = [
        ForeignKey(
            entity = JogadorEntity::class,
            parentColumns = ["id"],
            childColumns = ["jogadorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("jogadorId")]
)
data class PartidaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val jogadorId: Long,
    val pontos: Int,
    val dataPartida: Long = System.currentTimeMillis()
)
