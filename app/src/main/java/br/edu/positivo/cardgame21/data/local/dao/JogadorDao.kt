package br.edu.positivo.cardgame21.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.edu.positivo.cardgame21.data.local.entity.JogadorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JogadorDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(jogador: JogadorEntity): Long

    @Query("SELECT * FROM jogadores")
    fun listartodos(): Flow<List<JogadorEntity>>

    @Update
    suspend fun atualizar(jogador: JogadorEntity)

    @Delete
    suspend fun deletar(jogador: JogadorEntity)

    @Query("SELECT * FROM jogadores WHERE id = :id")
    suspend fun buscaPorId(id: Long): JogadorEntity?
}