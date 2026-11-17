package br.edu.positivo.cardgame21.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import br.edu.positivo.cardgame21.data.local.entity.JogadorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JogadorDao {
    // Sem REPLACE: nome repetido lança exceção (tratada no Repository).
    // Com REPLACE, o jogador antigo seria apagado e suas partidas junto (CASCADE).
    @Insert
    suspend fun inserir(jogador: JogadorEntity): Long

    @Query("SELECT * FROM jogadores")
    fun listarTodos(): Flow<List<JogadorEntity>>

    @Update
    suspend fun atualizar(jogador: JogadorEntity)

    @Delete
    suspend fun deletar(jogador: JogadorEntity)

    @Query("SELECT * FROM jogadores WHERE id = :id")
    suspend fun buscaPorId(id: Long): JogadorEntity?

    @Query("SELECT * FROM jogadores WHERE nome = :nome LIMIT 1")
    suspend fun buscaPorNome(nome: String): JogadorEntity?

    @Query("""
    SELECT * FROM jogadores
    WHERE nome LIKE '%' || :nome || '%'
    ORDER BY nome ASC
""")
    fun pesquisarPorNome(nome: String): Flow<List<JogadorEntity>>
}
