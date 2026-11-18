package br.edu.positivo.cardgame21.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import br.edu.positivo.cardgame21.logic.ResultadoRodada

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
    val dataPartida: Long = System.currentTimeMillis(),
    @ColumnInfo(defaultValue = "'DESCONHECIDO'")
    val resultado: ResultadoRodada = ResultadoRodada.DESCONHECIDO,
    @ColumnInfo(defaultValue = "0")
    val aposta: Int = 0,
    @ColumnInfo(defaultValue = "0")
    val variacaoSaldo: Int = 0
)
