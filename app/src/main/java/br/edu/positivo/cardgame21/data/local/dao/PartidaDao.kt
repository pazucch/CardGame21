package br.edu.positivo.cardgame21.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.edu.positivo.cardgame21.data.local.entity.PartidaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PartidaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(partida: PartidaEntity): Long

    @Query("SELECT * FROM partidas ORDER BY pontos DESC")
    fun listarPlacar(): Flow<List<PartidaEntity>>

    @Update
    suspend fun atualizar(partida: PartidaEntity)

    @Delete
    suspend fun deletar(partida: PartidaEntity)
}
