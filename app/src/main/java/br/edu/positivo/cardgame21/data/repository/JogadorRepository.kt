package br.edu.positivo.cardgame21.data.repository

import br.edu.positivo.cardgame21.data.local.dao.JogadorDao
import br.edu.positivo.cardgame21.data.local.entity.JogadorEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class JogadorRepository(
    private val jogadorDao: JogadorDao
) {
    fun listarJogadores(): Flow<Result<List<JogadorEntity>>> =
        jogadorDao.listarTodos()
            .map { lista -> Result.success(lista) }
            .catch { e -> emit(Result.failure(e)) }

    suspend fun buscarPorNome(nome: String): Result<JogadorEntity?> {
        return try {
            Result.success(jogadorDao.buscaPorNome(nome))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun inserirJogador(jogador: JogadorEntity): Result<Long> {
        return try {
            val id = jogadorDao.inserir(jogador)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun atualizarJogador(jogador: JogadorEntity): Result<Unit> {
        return try {
            jogadorDao.atualizar(jogador)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletarJogador(jogador: JogadorEntity): Result<Unit> {
        return try {
            jogadorDao.deletar(jogador)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
