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
interface PartidaDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(jogador: PartidaEntity): Long

    @Query("SELECT * FROM jogadores")
    fun listartodos(): Flow<List<PartidaEntity>>

    @Update
    suspend fun atualizar(jogador: PartidaEntity)

    @Delete
    suspend fun deletar(jogador: PartidaEntity)

    @Query("SELECT * FROM jogadores WHERE id = :id")
    suspend fun buscaPorId(id: Long): PartidaEntity?
}