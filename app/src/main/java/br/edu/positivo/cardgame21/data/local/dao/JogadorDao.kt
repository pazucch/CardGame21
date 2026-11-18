package br.edu.positivo.cardgame21.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Transaction
import br.edu.positivo.cardgame21.data.local.entity.JogadorEntity
import br.edu.positivo.cardgame21.data.local.entity.PartidaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JogadorDao {
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
    
    @Query("""
    SELECT COUNT(*) FROM jogadores
    WHERE isAdmin = 1
""")
    suspend fun contarAdministradores(): Int

    @Transaction
    suspend fun inserirAdminInicial(
        nome: String,
        senhaHash: String
    ): Boolean {

        if (contarAdministradores() > 0) {
            return false
        }

        require(nome.isNotBlank()) {
            "Nome do administrador obrigatório"
        }

        require(senhaHash.isNotBlank()) {
            "Hash da senha obrigatório"
        }

        val administrador = JogadorEntity(
            nome = nome,
            senhaHash = senhaHash,
            isAdmin = true
        )

        inserir(administrador)

        return true
    }

    @Query("SELECT saldo FROM jogadores WHERE id = :id")
    suspend fun buscarSaldo(id: Long): Int?

    @Query("""
    UPDATE jogadores
    SET saldo = saldo + :variacao
    WHERE id = :id
      AND saldo + :variacao BETWEEN 0 AND 2147483647
    """)
    suspend fun alterarSaldo(id: Long, variacao: Int): Int

    @Query("SELECT COUNT(*) FROM partidas WHERE id = :id")
    suspend fun partidaExiste(id: Long): Int

    @Insert
    suspend fun inserirPartidaComSaldo(partida: PartidaEntity)

    @Transaction
    suspend fun concluirRodada(partida: PartidaEntity): Int {
        if (partidaExiste(partida.id) == 0) {
            if (partida.variacaoSaldo != 0) {
                check(alterarSaldo(partida.jogadorId, partida.variacaoSaldo) == 1) {
                    "Saldo insuficiente ou jogador inexistente."
                }
            }
            inserirPartidaComSaldo(partida)
        }

        return buscarSaldo(partida.jogadorId)
            ?: error("Jogador não encontrado.")
    }
}